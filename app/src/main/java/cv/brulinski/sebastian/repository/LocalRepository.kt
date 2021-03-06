package cv.brulinski.sebastian.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.model.*
import cv.brulinski.sebastian.utils.database
import cv.brulinski.sebastian.utils.doAsync
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
                    val observer = Observer<Any> { any ->
                        any?.also { emitter.onNext(any) }
                        emitter.onComplete()
                        obs?.also { observer ->
                            removeObserver(observer)
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
                        doAsync {
                            val decryptedCv = App.component.getApp()
                                    .cryptoOperations
                                    ?.CryptoOperation(cv, MyCv::class.java)
                                    ?.start(false)
                            decryptedCv?.also { result(decryptedCv) }
                        }
                    } else {
                        //We need to fetch CV from remote server
                        empty(EMPTY)
                    }
                }, {
                    it.printStackTrace()
                })
    }

    @SuppressLint("CheckResult")
    fun getCredits(credits: (List<Credit>?) -> Unit, empty: (EMPTY) -> Unit) {
        val observable = fetchFromDatabase {
            database.getCredits()
        }

        observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    (it as? List<*>)?.also {
                        if (it.isNotEmpty() && it[0] is Credit) {
                            credits(it as List<Credit>)
                        } else empty(EMPTY)
                    } ?: run {
                        empty(EMPTY)
                    }
                }, {
                    empty(EMPTY)
                })
    }

    /**
     * Get personal data processing object from local SQL database
     * @see PersonalDataProcessing
     */
    fun getPersonalDataProcessing(result: (PersonalDataProcessing?) -> Unit) {

        val observable = fetchFromDatabase {
            database.getPersonalDataProcessing()
        }

        var personalDataProcessing: PersonalDataProcessing? = null
        observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    result(personalDataProcessing)
                }
                .subscribe({
                    personalDataProcessing = it as? PersonalDataProcessing
                }, {
                    result(null)
                })
    }

    /**
     * Update personal data processing in local databse
     */
    fun updatePersonalDataProcessing(personalDataProcessing: PersonalDataProcessing) {
        doAsync {
            database.insertPersonalDataProcessing(personalDataProcessing)
        }
    }
}