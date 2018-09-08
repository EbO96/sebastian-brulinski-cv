package cv.brulinski.sebastian.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cv.brulinski.sebastian.interfaces.RemoteRepository
import cv.brulinski.sebastian.model.Credit
import cv.brulinski.sebastian.model.MyCv
import cv.brulinski.sebastian.model.PersonalDataProcessing
import cv.brulinski.sebastian.utils.database
import cv.brulinski.sebastian.utils.doAsync

/**
 * Provides all required data for application
 * @param listener listener used for listening for events like START, END or fetch ERROR
 */
@SuppressLint("CheckResult")
class MainRepository<T : RemoteRepository>(private val listener: T?) : AppRepository {

    private val myCv = MutableLiveData<MyCv>()
    private val credits = MutableLiveData<List<Credit>>()
    private val localRepository = LocalRepository(this@MainRepository) //Provides CV from local SQL database
    private val remoteRepository = RemoteRepository(this@MainRepository) //Provides Cv from remote server

    /**
     * Get CV from local database if exists
     * or from remote server when database is empty
     */
    fun getCv(): MutableLiveData<MyCv> {
        localRepository.getCv({
            myCv.postValue(it.clone())
        }, {
            fetchCvFromRemote()
        })

        return myCv
    }

    fun getCredits(): MutableLiveData<List<Credit>> {
        localRepository.getCredits({
            credits.value = it
        }, {
            fetchCreditsFromRemote()
        })
        return credits
    }

    fun getPersonalDataProcessing(result: (PersonalDataProcessing?) -> Unit) {
        localRepository.getPersonalDataProcessing { personalDataProcessing ->
            if (personalDataProcessing == null)
                remoteRepository.getPersonalDataProcessing { personalDataProcessing ->
                    personalDataProcessing?.also {
                        doAsync {
                            database.insertPersonalDataProcessing(personalDataProcessing)
                        }
                    }
                    result(personalDataProcessing)
                }
            else result(personalDataProcessing)
        }
    }

    fun updatePersonalDataProcessing(personalDataProcessing: PersonalDataProcessing) {
        localRepository.updatePersonalDataProcessing(personalDataProcessing)
    }

    fun refreshCredits(credits: (List<Credit>?) -> Unit) {
        remoteRepository.getCredits { remoteCredits ->
            credits(remoteCredits)
        }
    }

    /*
    Public methods
     */

    fun registerForCvNotifications(register: Boolean, status: (Int) -> Unit) {
        remoteRepository.registerForCvNotifications(register, status)
    }

    /**
     * Fetch CV again
     */
    fun refreshAll(cv: ((MyCv) -> Unit)? = null) {
        fetchCvFromRemote(cv)
    }

    /*
    Private methods
     */
    private fun fetchCvFromRemote(cv: ((MyCv) -> Unit)? = null) {
        listener?.onFetchStart()
        remoteRepository.fetchCv({
            myCv.value = it.clone()
            cv?.apply { invoke(it) }
            listener?.onFetchEnd()
        }, {
            listener?.onFetchError(null)
        })
    }

    private fun fetchCreditsFromRemote() {
        remoteRepository.getCredits {
            doAsync {
                it?.also { database.insertCredits(it) }
            }
            credits.value = it
        }
    }

    /*
    Override methods
     */

    override fun getRepository(): MainRepository<T> = this

    override fun getMyCv(): MyCv? = myCv.value

}

