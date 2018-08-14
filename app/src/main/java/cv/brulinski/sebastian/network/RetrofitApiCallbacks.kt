package cv.brulinski.sebastian.network

import cv.brulinski.sebastian.model.BaseResponse
import cv.brulinski.sebastian.model.MyCv
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitApiCallbacks {

    @GET("getAll")
    fun getAll(): Observable<MyCv>
}