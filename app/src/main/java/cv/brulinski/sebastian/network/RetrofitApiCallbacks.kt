package cv.brulinski.sebastian.network

import cv.brulinski.sebastian.model.Credit
import cv.brulinski.sebastian.model.MyCv
import cv.brulinski.sebastian.model.PersonalDataProcessing
import io.reactivex.Observable
import retrofit2.http.GET

interface RetrofitApiCallbacks {

    @GET("app/getAll")
    fun getAll(): Observable<MyCv>

    @GET("app1/getCredits")
    fun getCredits(): Observable<List<Credit>>

    @GET("app3/getPersonalDataProcessing")
    fun getPersonalDataProcessing(): Observable<PersonalDataProcessing>
}