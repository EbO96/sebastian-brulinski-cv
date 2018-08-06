package cv.brulinski.sebastian.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cv.brulinski.sebastian.interfaces.OnFetchingStatuses
import cv.brulinski.sebastian.repository.MainRepository

class MainViewModel<T : OnFetchingStatuses> constructor(application: Application, listener: T) : AndroidViewModel(application) {

    private val repository by lazy { MainRepository(listener) }
    val myCv by lazy { repository.getCv() }

    fun refreshAll() = repository.refreshAll()
}