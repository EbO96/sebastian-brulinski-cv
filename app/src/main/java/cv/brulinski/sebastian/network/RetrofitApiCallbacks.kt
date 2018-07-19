package cv.brulinski.sebastian.network

import cv.brulinski.sebastian.model.Job
import cv.brulinski.sebastian.model.PersonalInfo
import cv.brulinski.sebastian.model.School
import cv.brulinski.sebastian.model.Welcome
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
}