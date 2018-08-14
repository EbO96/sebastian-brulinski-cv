package cv.brulinski.sebastian.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cv.brulinski.sebastian.crypto.CryptoOperations
import cv.brulinski.sebastian.interfaces.OnFetchingStatuses
import cv.brulinski.sebastian.model.MyCv

/**
 * Provides all required data for application
 * @param listener listener used for listening for events like START, END or fetch ERROR
 */
@SuppressLint("CheckResult")
class MainRepository<T : OnFetchingStatuses>(private val listener: T?) : AppRepository {

    private val myCv = MutableLiveData<MyCv>()
    private val cryptoOperations = CryptoOperations()
    private val localRepository = LocalRepository(this@MainRepository) //Provides CV from local SQL database
    private val remoteRepository = RemoteRepository(this@MainRepository) //Provides Cv from remote server

    /**
     * Get CV from local database if exists
     * or from remote server when database is empty
     */
    fun getCv(): LiveData<MyCv> {

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

    /**
     * Fetch CV again
     */
    fun refreshAll() {
        fetchCvFromRemote()
    }

    /*
    Private methods
     */
    private fun fetchCvFromRemote() {
        listener?.onFetchStart()
        remoteRepository.fetchCv({
            listener?.onFetchEnd()
            myCv.value = it
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

