package cv.brulinski.sebastian.network

import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.utils.toast
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
            .addInterceptor { chain ->
                if (App.token == null)
                    throw Exception("Unauthorized")
                val request = chain.request()
                val newRequest = request.newBuilder()
                        .addHeader("Authorization", "Bearer ${App.token}")
                        .build()
                chain.proceed(newRequest)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .dispatcher(Dispatcher())
            .build()
}