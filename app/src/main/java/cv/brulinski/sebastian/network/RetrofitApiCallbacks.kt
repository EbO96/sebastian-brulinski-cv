package cv.brulinski.sebastian.network

import androidx.lifecycle.LiveData
import com.github.leonardoxh.livedatacalladapter.Resource
import cv.brulinski.sebastian.model.Welcome
import retrofit2.http.GET

interface RetrofitApiCallbacks {

    @GET("getWelcome")
    fun getWelcome(): LiveData<Resource<Welcome>>
}