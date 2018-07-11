package cv.brulinski.sebastian.dependency_injection.component

import android.app.Application
import android.content.Context
import cv.brulinski.sebastian.dependency_injection.scope.AppContext
import cv.brulinski.sebastian.dependency_injection.module.AppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @AppContext
    fun getContext(): Context

    fun inject(app: Application)
}