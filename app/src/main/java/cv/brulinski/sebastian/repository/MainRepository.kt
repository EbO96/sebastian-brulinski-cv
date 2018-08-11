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

    fun getCv(): LiveData<MyCv> {

        val cvFetchDisposable = fetchAllFromDatabase()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ cv ->
                    var dbNotEmpty = false
                    cv?.welcome?.timestamp?.let {
                        dbNotEmpty = it != -1L
                    }
                    if (dbNotEmpty) {
                        doCrypto(cv, false)?.let {
                            myCv.value = it
                        }
                    } else {
                        fetchCv()
                    }
                }, {
                    it.printStackTrace()
                })
        disposables.add(cvFetchDisposable)
        return myCv
    }

    fun refreshAll() {
        fetchCv()
    }

    private fun HashMap<String, String?>.fetchBitmaps(): Observable<HashMap<String, String>> {
        val observables = arrayListOf<Observable<Bitmap>>()
        values.forEach { url ->
            observables.add(downloadBitmap(url.checkUrl()))
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
                            map[key] = toBase64String()
                        }
                        counter++
                    }, {
                        emitter.onError(it)
                    })
            disposables.add(bitmapFetchDisposable)
        }
    }

    private fun fetchCv() {
        listener?.onFetchStart()
        val cvFetchDisposable = fetchAllFromRemoteServer()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    settings.firstLaunch = false
                    listener?.onFetchEnd()
                    doAsync {
                        doCrypto(myCv.value, true)?.insert()
                    }
                }
                .subscribe({
                    myCv.value = it
                }, {
                    listener?.onFetchError(it)
                })
        disposables.add(cvFetchDisposable)
    }

    private fun fetchAllFromRemoteServer(): Observable<MyCv> {
        return Observable.create { emitter ->
            val fetchAllDisposable = retrofit
                    .getAll()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ cv ->

                        if (cv.status == 1) {
                            copyExistingBitmaps(cv, PersonalInfo::class.java, Skill::class.java, Language::class.java)

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

    private fun fetchAllFromDatabase(): Observable<MyCv> {
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

    private fun (String?).checkUrl(): String {
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

