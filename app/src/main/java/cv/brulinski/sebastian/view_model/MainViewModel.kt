package cv.brulinski.sebastian.view_model

import android.app.Application
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import cv.brulinski.sebastian.repository.MainRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MainRepository()
    val welcome = repository.getWelcome()
    val personalInfo = repository.getPersonalInfo()
}