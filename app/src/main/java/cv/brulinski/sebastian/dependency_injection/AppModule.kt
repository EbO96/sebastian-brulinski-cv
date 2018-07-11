package cv.brulinski.sebastian.dependency_injection

import android.app.Application
import android.content.Context
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