package cv.brulinski.sebastian.repository

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cv.brulinski.sebastian.R.string
import cv.brulinski.sebastian.model.*
import cv.brulinski.sebastian.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class MainRepository {

    private enum class Type {
        PROFILE,
        BCG
    }

    private val myCv = MutableLiveData<MyCv>()

    fun getCv(): LiveData<MyCv> {
        fetchAllFromDatabase()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { cv ->
                    var dbNotEmpty = false
                    cv?.welcome?.timestamp?.let {
                        dbNotEmpty = it != -1L
                    }
                    if (dbNotEmpty) myCv.value = cv
                    else fetchCv()
                }
        return myCv
    }

    fun refreshAll() {
        fetchCv()
    }

    private fun HashMap<String, String>.fetchBitmaps(): Observable<HashMap<String, String>> {
        val observables = arrayListOf<Observable<Bitmap>>()
        values.forEach { url ->
            observables.add(downloadBitmap(url))
        }
        var counter = 0
        return Observable.create { emitter ->
            val map = HashMap<String, String>()
            Observable.merge(observables)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        emitter.onNext(map)
                        emitter.onComplete()
                    }
                    .subscribe({
                        it?.apply {
                            val key = keys.toList()[counter]
                            map[key] = toBase64String()
                        }
                        counter++
                    }, {
                        emitter.onError(it)
                    })

        }
    }

    private fun fetchCv() {
        fetchAllFromRemoteServer()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    it.insert()
                    myCv.value = it
                }
                .doOnError {
                    ctx.getString(string.data_fetching_error).toast()
                    it.printStackTrace()
                }.subscribe()
    }

    private fun fetchAllFromRemoteServer(): Observable<MyCv> {
        return Observable.create { emitter ->
            retrofit
                    .getAll()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ cv ->

                        val urlMap = HashMap<String, String>()
                        val errorImageUrl = string.error_image_url.string()
                        urlMap["profilePicture"] = cv.personalInfo?.profilePhotoUrl ?: errorImageUrl
                        urlMap["profileBcg"] = cv.personalInfo?.profileBcgUrl ?: errorImageUrl
                        cv.languages?.forEach {
                            urlMap[it.id] = it.imageUrl ?: errorImageUrl
                        }

                        urlMap.fetchBitmaps()
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext {
                                    it.apply {
                                        cv.personalInfo?.profilePictureBase64 = it["profilePicture"]
                                        cv.personalInfo?.profilePictureBcgBase64 = it["profileBcg"]
                                    }
                                    cv.languages?.forEach { language ->
                                        language.flagBase64 = it[language.id]
                                    }
                                    emitter.onNext(cv)
                                    emitter.onComplete()
                                }
                                .doOnError {
                                    emitter.onError(it)
                                }
                                .subscribe()
                    }, {
                        emitter.onError(it)
                        it.printStackTrace()
                    })
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
        }
        return Observable.create { emitter ->
            val cv = MyCv()
            Observable.merge(observers)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { objectFromDb ->
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
                                        }
                                    }
                            }
                        }
                    }
                    .doOnComplete {
                        emitter.onNext(cv)
                        emitter.onComplete()
                    }
                    .subscribe()
        }
    }

    private fun fetchFromDatabase(fetch: () -> LiveData<*>): Observable<Any?> {
        return Observable.create { emitter ->
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
        }
    }
}