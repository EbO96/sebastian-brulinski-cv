package cv.brulinski.sebastian.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cv.brulinski.sebastian.repository.MainRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository by lazy { MainRepository() }
    val myCv by lazy { repository.getCv() }

    fun refreshAll() = repository.refreshAll()
}