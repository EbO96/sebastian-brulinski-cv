package cv.brulinski.sebastian.dependency_injection

import android.app.Application
import android.content.Context
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @AppContext
    fun getContext(): Context

    fun inject(app: Application)
}