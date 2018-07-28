package cv.brulinski.sebastian.dependency_injection.app

import android.app.Application
import androidx.lifecycle.MutableLiveData
import cv.brulinski.sebastian.dependency_injection.component.AppComponent
import cv.brulinski.sebastian.dependency_injection.component.DaggerAppComponent
import cv.brulinski.sebastian.dependency_injection.module.AppModule

class App : Application() {

    enum class FetchDataStatus {
        START,
        END,
        ERROR
    }

    companion object {
        lateinit var component: AppComponent
        var startFetchingData = MutableLiveData<FetchDataStatus>()
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)
    }


}