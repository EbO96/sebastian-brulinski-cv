package cv.brulinski.sebastian.interfaces

interface OnFetchingStatuses {
    fun onFetchStart()
    fun onFetchEnd()
    fun onFetchError(error: Throwable?)
}