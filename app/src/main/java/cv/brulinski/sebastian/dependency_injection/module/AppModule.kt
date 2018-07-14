package cv.brulinski.sebastian.dependency_injection.module

import android.app.Application
import android.content.Context
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.dependency_injection.scope.AppContext
import cv.brulinski.sebastian.network.RetrofitApiCallbacks
import cv.brulinski.sebastian.network.RetrofitFetch
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Provides
    @AppContext
    @Singleton
    fun provideContext(): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideRetrofitApiCallbacks(): RetrofitApiCallbacks = RetrofitFetch.get(app.getString(R.string.firestore_url))
}