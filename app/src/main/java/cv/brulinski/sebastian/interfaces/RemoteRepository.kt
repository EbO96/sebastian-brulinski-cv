package cv.brulinski.sebastian.interfaces

interface RemoteRepository {
    fun onFetchStart()
    fun onFetchEnd()
    fun onFetchError(error: Throwable?)
}