package cv.brulinski.sebastian.network

import cv.brulinski.sebastian.model.*
import io.reactivex.Observable
import retrofit2.http.GET

interface RetrofitApiCallbacks {

    @GET("getAll")
    fun getAll(): Observable<MyCv>
}