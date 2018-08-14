package cv.brulinski.sebastian.dependency_injection.component

import android.app.Application
import android.content.Context
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.dependency_injection.module.AppModule
import cv.brulinski.sebastian.dependency_injection.scope.AppContext
import cv.brulinski.sebastian.model.AppSettings
import cv.brulinski.sebastian.network.RetrofitApiCallbacks
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @AppContext
    fun getContext(): Context

    fun getApp(): App

    fun getRetrofitApiCallbacks(): RetrofitApiCallbacks

    fun getAppSettings(): AppSettings

    fun inject(app: App)
}