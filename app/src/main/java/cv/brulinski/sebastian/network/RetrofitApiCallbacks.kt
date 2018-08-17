package cv.brulinski.sebastian.network

import cv.brulinski.sebastian.model.Credit
import cv.brulinski.sebastian.model.MyCv
import io.reactivex.Observable
import retrofit2.http.GET

interface RetrofitApiCallbacks {

    @GET("getAll")
    fun getAll(): Observable<MyCv>

    @GET("getCredits")
    fun getCredits(): Observable<List<Credit>>
}