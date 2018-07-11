package cv.brulinski.sebastian.dependency_injection.module

import android.app.Application
import android.content.Context
import cv.brulinski.sebastian.dependency_injection.scope.AppContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application){

    @Provides
    @AppContext
    @Singleton
    fun provideContext(): Context = app.applicationContext
}