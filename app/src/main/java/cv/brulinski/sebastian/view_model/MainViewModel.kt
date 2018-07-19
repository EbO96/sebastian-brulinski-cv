package cv.brulinski.sebastian.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cv.brulinski.sebastian.repository.MainRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository by lazy { MainRepository() }
    val welcome by lazy { repository.getWelcome() }
    val career by lazy { repository.getCareer() }
    val personalInfo by lazy { repository.getPersonalInfo() }

    fun refreshWelcome() {
        repository.refreshWelcome()
    }

    fun refreshPersonalInfo() {
        repository.refreshPersonalInfo()
    }

    fun refreshCareer() {
        repository.refreshCareer()
    }
}