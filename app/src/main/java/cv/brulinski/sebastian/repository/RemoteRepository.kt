package cv.brulinski.sebastian.repository

import android.graphics.Bitmap
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.interfaces.BitmapLoadable
import cv.brulinski.sebastian.interfaces.OnFetchingStatuses
import cv.brulinski.sebastian.interfaces.OnGetCvObjects
import cv.brulinski.sebastian.model.Language
import cv.brulinski.sebastian.model.MyCv
import cv.brulinski.sebastian.model.PersonalInfo
import cv.brulinski.sebastian.model.Skill
import cv.brulinski.sebastian.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Remote repository
 * @property appRepository is interface to communicate with MainRepository
 * @see MainRepository
 */
class RemoteRepository(private val appRepository: AppRepository) {

    companion object {
        var errorBitmap: Bitmap? = null
        val errorImageUrl by lazy { R.string.error_image_url.string() ?: "" }
    }

    object ERROR

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
                    appRepository.getMyCv()?.getTypeSkills()
                }
                clazz.name == Language::class.java.name -> {
                    remoteCv = t.getTypeLanguages()
                    appRepository.getMyCv()?.getTypeLanguages()
                }
                clazz.name == PersonalInfo::class.java.name -> {
                    remoteCv = t.getTypePersonalInfo()
                    appRepository.getMyCv()?.getTypePersonalInfo()
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
     * Validation of bitmap URL. In case when
     * url is wrong then returns url to 'error' bitmap
     * @return valid URL
     */
    private fun (String?).validBitmapUrl(): String {
        return this.let { if (it.isNullOrBlank()) errorImageUrl else it }
                ?: errorImageUrl
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
        }
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
        }
    }

    fun fetchCv(result: (MyCv) -> Unit, error: (ERROR) -> Unit) {
        fetchAllFromRemoteServer()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    settings.firstLaunch = false
                    //Fetching completed. Encrypt CV and insert into local database
                    doAsync {
                        appRepository.apply {
                            getMyCv()?.let { cv ->
                                getCrypto()
                                        .doCrypto(cv, true)
                                        ?.insert()
                            }
                        }
                    }
                }
                .subscribe({
                    //Before encrypting inform observers about new CV
                    result(it)
                }, {
                    error(ERROR)
                })
    }
}