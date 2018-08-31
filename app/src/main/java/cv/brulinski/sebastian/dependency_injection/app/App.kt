package cv.brulinski.sebastian.dependency_injection.app

import android.app.Application
import cv.brulinski.sebastian.crypto.CryptoOperations
import cv.brulinski.sebastian.dependency_injection.component.AppComponent
import cv.brulinski.sebastian.dependency_injection.component.DaggerAppComponent
import cv.brulinski.sebastian.dependency_injection.module.AppModule
import cv.brulinski.sebastian.model.MyCv
import cv.brulinski.sebastian.notification.MyNotificationChannel
import cv.brulinski.sebastian.service.FetchNewCvJob

class App : Application() {

    companion object {
        lateinit var component: AppComponent
        var token: String? = null
    }

    var cryptoOperations: CryptoOperations? = null

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)

        val myNotificationChannel = MyNotificationChannel(this)
        myNotificationChannel.newCvChannel(FetchNewCvJob.CHANNEL_ID)

        if (cryptoOperations == null)
            cryptoOperations = CryptoOperations()
    }

    enum class DataHolder {
        INSTANCE;

        var cv: MyCv? = null
    }
}