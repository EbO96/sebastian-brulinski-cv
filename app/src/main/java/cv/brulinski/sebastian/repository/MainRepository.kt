package cv.brulinski.sebastian.repository

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cv.brulinski.sebastian.R.string
import cv.brulinski.sebastian.annotations.Crypto
import cv.brulinski.sebastian.crypto.CryptoOperations
import cv.brulinski.sebastian.interfaces.BitmapLoadable
import cv.brulinski.sebastian.interfaces.CryptoClass
import cv.brulinski.sebastian.interfaces.OnFetchingStatuses
import cv.brulinski.sebastian.interfaces.OnGetCvObjects
import cv.brulinski.sebastian.model.*
import cv.brulinski.sebastian.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Provides all required data for application
 * @param listener listener used for listening for events like START, END or fetch ERROR
 */
@SuppressLint("CheckResult")
open class MainRepository<T : OnFetchingStatuses>(private val listener: T?) {

    private val myCv = MutableLiveData<MyCv>()
    private val disposables = ArrayList<Disposable>()
    private val cryptoOperations = CryptoOperations()

    companion object {
        val errorImageUrl by lazy { string.error_image_url.string() }
        var errorBitmap: Bitmap? = null
    }

    /**
     * Get CV from local database if exists
     * or from remote server when database is empty
     */
    fun getCv(): LiveData<MyCv> {

        val cvFetchDisposable = fetchAllFromDatabase()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ cv ->
                    val dbNotEmpty = cv?.welcome?.timestamp ?: -1L != -1L
                    //CV are in local database so we now decrypt it
                    if (dbNotEmpty) {
                        doCrypto(cv, false)?.let {
                            myCv.value = it
                        }
                    } else {
                        //We need to fetch CV from remote server
                        fetchCv()
                    }
                }, {
                    it.printStackTrace()
                })
        disposables.add(cvFetchDisposable)
        return myCv
    }

    /**
     * Fetch CV again
     */
    fun refreshAll() {
        fetchCv()
    }

    /**
     * Fetch CV from remote server.
     * This method also copying existing
     * bitmaps from old CV object into new CV object because
     * in app 'Settings' we can disable fetching bitmaps
     * so in way when it is disabled all bitmaps will be 'null'
     * @return MyCv as observable
     */
    private fun fetchAllFromRemoteServer(): Observable<MyCv> {
        return Observable.create { emitter ->
            val fetchAllDisposable = retrofit
                    .getAll()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ cv ->

                        //Everything went well
                        if (cv.status == 1) {
                            //Copy existing bitmaps from old CV into new CV object
                            copyExistingBitmaps(cv, PersonalInfo::class.java, Skill::class.java, Language::class.java)

                            //This block of code will execute always when app is opening first time
                            //or when in 'Settings' we will enable the option of being responsible for
                            //fetching bitmaps
                            if (settings.fetchGraphics || settings.firstLaunch) {
                                val urlMap = HashMap<String, String?>()
                                urlMap["profilePicture"] = cv.personalInfo?.profilePhotoUrl
                                urlMap["profileBcg"] = cv.personalInfo?.profileBcgUrl
                                cv.languages?.forEach {
                                    urlMap[it.id] = it.imageUrl
                                }
                                cv.skills?.forEach {
                                    urlMap[it.id] = it.iconUrl
                                }

                                //Fetch bitmaps in put into new CV object
                                val bitmapFetchDisposable = urlMap.fetchBitmaps()
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            it.apply {
                                                cv.personalInfo?.profilePictureBase64 = it["profilePicture"]
                                                cv.personalInfo?.profilePictureBcgBase64 = it["profileBcg"]
                                            }
                                            cv.languages?.forEach { language ->
                                                language.flagBase64 = it[language.id]
                                            }
                                            cv.skills?.forEach { skill ->
                                                skill.iconBase64 = it[skill.id]
                                            }
                                            emitter.onNext(cv)
                                            emitter.onComplete()
                                        }, {
                                            emitter.onError(it)
                                        })
                                disposables.add(bitmapFetchDisposable)
                            } else {
                                emitter.onNext(cv)
                                emitter.onComplete()
                            }
                        } else {
                            emitter.onError(Throwable("Fetching error"))
                        }

                    }, {
                        emitter.onError(it)
                        it.printStackTrace()
                    })
            disposables.add(fetchAllDisposable)
        }
    }

    /**
     * Fetch CV from remote server, encrypt and put into local database
     */
    private fun fetchCv() {
        listener?.onFetchStart()
        val cvFetchDisposable = fetchAllFromRemoteServer()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    settings.firstLaunch = false
                    listener?.onFetchEnd()
                    //Fetching completed. Encrypt CV and insert into local database
                    doAsync {
                        doCrypto(myCv.value, true)?.insert()
                    }
                }
                .subscribe({
                    //Before encrypting inform observers about new CV
                    myCv.value = it
                }, {
                    listener?.onFetchError(it)
                })
        disposables.add(cvFetchDisposable)
    }

    /**
     * Fetch bitmaps from url address
     * @property HashMap<String, String?> map of tag and bitmap url.
     * Tag is used to recognize fetched bitmap
     * @return HashMap<String, String> observable which is map of tag <-> Base64 representation of bitmap
     */
    private fun HashMap<String, String?>.fetchBitmaps(): Observable<HashMap<String, String>> {
        val observables = arrayListOf<Observable<Bitmap>>()
        values.forEach { url ->
            observables.add(downloadBitmap(url.validBitmapUrl()))
        }
        var counter = 0
        return Observable.create { emitter ->
            val map = HashMap<String, String>()
            val bitmapFetchDisposable = Observable.merge(observables)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        emitter.onNext(map)
                        emitter.onComplete()
                    }
                    .onErrorReturnItem(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
                    .subscribe({
                        it?.apply {
                            val key = keys.toList()[counter]
                            map[key] = toBase64String() //encode bitmap to Base64 string
                        }
                        counter++
                    }, {
                        emitter.onError(it)
                    })
            disposables.add(bitmapFetchDisposable)
        }
    }

    /**
     * Used to copying bitmaps from one CV object to another.
     * Classes which want to use this method must implement 'OnGetCvObject'
     */
    private fun <T : OnGetCvObjects> copyExistingBitmaps(t: T, vararg c: Class<*>) {
        c.forEach { clazz ->
            var remoteCv: List<BitmapLoadable>? = null
            val localCv = when {
                clazz.name == Skill::class.java.name -> {
                    remoteCv = t.getTypeSkills()
                    myCv.value?.getTypeSkills()
                }
                clazz.name == Language::class.java.name -> {
                    remoteCv = t.getTypeLanguages()
                    myCv.value?.getTypeLanguages()
                }
                clazz.name == PersonalInfo::class.java.name -> {
                    remoteCv = t.getTypePersonalInfo()
                    myCv.value?.getTypePersonalInfo()
                }
                else -> null
            }

            localCv?.let { listOfCvObjects ->

                val localIdObjectMap = listOfCvObjects.groupBy { it.getTypeId() }
                val remoteIdObjectMap = remoteCv?.groupBy { it.getTypeId() }

                localIdObjectMap.forEach { (id, localObject) ->
                    remoteIdObjectMap?.get(id)?.filter { remoteObject -> remoteObject.getTypeId() == id }?.let {
                        localObject.forEach { localObject ->
                            if (it.isNotEmpty()) {
                                it[0].setTypeBitmap(localObject.getTypeBitmap())
                                it[0].setTypeBitmapBase64(localObject.getTypeBitmapBase64())
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method is responsible for fetching CV from local SQL database.
     * Method use RxJava to wait for all database operations.
     * This is due to the fact that the data is in different tables in the database,
     * so we have to wait for all operations to be completed because these operations are asynchronous
     * @return MyCv as observable
     */
    private fun fetchAllFromDatabase(): Observable<MyCv> {
        //Create observables from every database operation
        val observers = arrayListOf<Observable<Any?>>().apply {
            add(fetchFromDatabase {
                database.getWelcome()
            })
            add(fetchFromDatabase {
                database.getPersonalInfo()
            })
            add(fetchFromDatabase {
                database.getCareer()
            })
            add(fetchFromDatabase {
                database.getLanguages()
            })
            add(fetchFromDatabase {
                database.getSkills()
            })
        }

        //Merge previously created database operations
        return Observable.create { emitter ->
            val cv = MyCv()
            val databaseFetchDisposable = Observable.merge(observers)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        emitter.onNext(cv)
                        emitter.onComplete()
                    }
                    .subscribe({ objectFromDb ->
                        when (objectFromDb) {
                            is Welcome -> {
                                cv.welcome = objectFromDb
                            }
                            is PersonalInfo -> {
                                cv.personalInfo = objectFromDb
                            }
                            is List<*> -> {
                                if (objectFromDb.isNotEmpty())
                                    objectFromDb.last()?.let {
                                        if (it is Career) {
                                            cv.career = objectFromDb as List<Career>
                                        } else if (it is Language) {
                                            cv.languages = objectFromDb as List<Language>
                                        } else if (it is Skill) {
                                            cv.skills = objectFromDb as List<Skill>
                                        }
                                    }
                            }
                        }
                    }, {
                        emitter.onError(it)
                    })
            disposables.add(databaseFetchDisposable)
        }
    }

    /**
     * Creating observable instance from database operation
     */
    private fun fetchFromDatabase(fetch: () -> LiveData<*>): Observable<Any?> {
        return Observable.create { emitter ->
            try {
                fetch().apply {
                    var obs: Observer<Any>? = null
                    val observer = Observer<Any> {
                        it?.let { emitter.onNext(it) }
                        emitter.onComplete()
                        obs?.let {
                            removeObserver(it)
                        }
                    }
                    obs = observer
                    observeForever(observer)
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    /**
     * Validation of bitmap URL. In case when
     * url is wrong then returns url to 'error' bitmap
     * @return valid URL
     */
    private fun (String?).validBitmapUrl(): String {
        return this.let { if (it.isNullOrBlank()) errorImageUrl else it } ?: errorImageUrl
    }

    private fun String.encrypt() = cryptoOperations.encrypt(this)
    private fun String.decrypt() = cryptoOperations.decrypt(this)

    private fun <T : CryptoClass> doCrypto(toEncrypt: T?, encrypt: Boolean): T? {
        if (toEncrypt is Any) {
            toEncrypt.javaClass.declaredFields.filter { it.isAnnotationPresent(Crypto::class.java) }.forEach {
                it.isAccessible = true
                val field = it.get(toEncrypt).javaClass.newInstance()
                if (field is Collection<*>) {
                    field.apply {
                        forEach {
                            it?.let { listElement ->
                                doCrypto(listElement as? CryptoClass, encrypt)
                            }
                        }
                    }
                } else {
                    field.javaClass.declaredFields.filter { it.isAnnotationPresent(Crypto::class.java) }.forEach {
                        it?.apply {
                            it.isAccessible = true
                            val changedField = (this.get(field) as? String)
                                    ?.let { if (encrypt) it.encrypt() else it.decrypt() }
                            this.set(field, changedField)
                        }
                    }
                }
            }
        }
        return toEncrypt
    }
}

