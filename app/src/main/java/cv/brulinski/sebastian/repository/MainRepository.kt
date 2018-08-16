package cv.brulinski.sebastian.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cv.brulinski.sebastian.crypto.CryptoOperations
import cv.brulinski.sebastian.interfaces.RemoteRepository
import cv.brulinski.sebastian.model.MyCv

/**
 * Provides all required data for application
 * @param listener listener used for listening for events like START, END or fetch ERROR
 */
@SuppressLint("CheckResult")
class MainRepository<T : RemoteRepository>(private val listener: T?) : AppRepository {

    private val myCv = MutableLiveData<MyCv>()
    private val cryptoOperations = CryptoOperations()
    private val localRepository = LocalRepository(this@MainRepository) //Provides CV from local SQL database
    private val remoteRepository = RemoteRepository(this@MainRepository) //Provides Cv from remote server

    /**
     * Get CV from local database if exists
     * or from remote server when database is empty
     */
    fun getCv(): MutableLiveData<MyCv> {

        localRepository.getCv({
            myCv.value = it
        }, {
            fetchCvFromRemote()
        })

        return myCv
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
            myCv.value = it
            cv?.apply { invoke(it) }
            listener?.onFetchEnd()
        }, {
            listener?.onFetchError(null)
        })
    }

    /*
    Override methods
     */

    override fun getRepository(): MainRepository<T> = this

    override fun getMyCv(): MyCv? = myCv.value

    override fun getCrypto() = cryptoOperations
}

