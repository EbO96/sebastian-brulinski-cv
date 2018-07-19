package cv.brulinski.sebastian.network

import cv.brulinski.sebastian.model.*
import io.reactivex.Observable
import retrofit2.http.GET

interface RetrofitApiCallbacks {

    @GET("getWelcome")
    fun getWelcome(): Observable<Welcome>

    @GET("getPersonalInfo")
    fun getPersonalInfo(): Observable<PersonalInfo>

    @GET("getSchools")
    fun getSchools(): Observable<List<School>>

    @GET("getJobs")
    fun getJobs(): Observable<List<Job>>

    @GET("getCareer")
    fun getCareer(): Observable<Career>
}