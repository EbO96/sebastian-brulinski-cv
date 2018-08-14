package cv.brulinski.sebastian.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import cv.brulinski.sebastian.model.*
import cv.brulinski.sebastian.utils.database
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Local SQL database repository
 * @property appRepository is interface to communicate with MainRepository
 * @see MainRepository
 */
open class LocalRepository(private val appRepository: AppRepository) {

    object EMPTY

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
        }
    }

    @SuppressLint("CheckResult")
    fun getCv(result: (MyCv) -> Unit, empty: (EMPTY) -> Unit) {
        fetchAllFromDatabase()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ cv ->
                    val dbNotEmpty = cv?.welcome?.timestamp ?: -1L != -1L
                    //CV are in local database so we now decrypt it
                    if (dbNotEmpty) {
                        appRepository.getCrypto().doCrypto(cv, false)?.let {
                            result(it)
                        }
                    } else {
                        //We need to fetch CV from remote server
                        empty(EMPTY)
                    }
                }, {
                    it.printStackTrace()
                })
    }
}