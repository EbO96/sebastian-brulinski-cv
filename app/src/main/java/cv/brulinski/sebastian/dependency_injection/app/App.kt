package cv.brulinski.sebastian.dependency_injection.app

import android.app.Application
import cv.brulinski.sebastian.dependency_injection.component.AppComponent
import cv.brulinski.sebastian.dependency_injection.module.AppModule
import cv.brulinski.sebastian.dependency_injection.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)
    }
}