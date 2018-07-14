package cv.brulinski.sebastian.network

import com.github.leonardoxh.livedatacalladapter.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class RetrofitFetch {

    companion object {
        fun get(baseUrl: String): RetrofitApiCallbacks = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
                .build()
                .create(RetrofitApiCallbacks::class.java)
    }
}
