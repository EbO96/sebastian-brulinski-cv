package cv.brulinski.sebastian.view_model

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.interfaces.RemoteRepository
import cv.brulinski.sebastian.model.*
import cv.brulinski.sebastian.repository.MainRepository

/**
 * Role of this class is being bridge between repository and views
 * @see MainRepository
 */
class MainViewModel<T : RemoteRepository> constructor(private val activity: AppCompatActivity?, listener: T) : AndroidViewModel(App.component.getApp()) {

    //Data for fragments
    private val welcome: MutableLiveData<Welcome> = MutableLiveData()
    private val personalInfo: MutableLiveData<PersonalInfo> = MutableLiveData()
    private val career: MutableLiveData<List<Career>> = MutableLiveData()
    private val languages: MutableLiveData<List<Language>> = MutableLiveData()
    private val skills: MutableLiveData<List<Skill>> = MutableLiveData()
    //Repository
    private val repository by lazy { MainRepository(listener) }
    //Repository CV
    val myCv by lazy { repository.getCv() }

    init {
        activity?.let {
            myCv.observe(activity, Observer {
                //Get CV parts and inform each fragment associated with this about update
                it?.apply {
                    welcome?.let { welcome ->
                        this@MainViewModel.welcome.value = welcome
                    }
                    personalInfo?.let { personalInfo ->
                        this@MainViewModel.personalInfo.value = personalInfo
                    }
                    career?.let { career ->
                        this@MainViewModel.career.value = career
                    }
                    languages?.let { languages ->
                        this@MainViewModel.languages.value = languages
                    }
                    skills?.let { skills ->
                        this@MainViewModel.skills.value = skills
                    }
                }
            })
        }
    }

    fun refreshAll() = repository.refreshAll()

    fun getWelcome(block: (Welcome) -> Unit) {
        activity?.let {
            welcome.observe(it, Observer {
                it?.let { block(it) }
            })
        }
    }

    fun getPersonalInfo(block: (PersonalInfo) -> Unit) {
        activity?.let {
            personalInfo.observe(it, Observer {
                it?.let { block(it) }
            })
        }
    }

    fun getCareer(block: (List<Career>) -> Unit) {
        activity?.let {
            career.observe(it, Observer {
                it?.let { block(it) }
            })
        }
    }

    fun getLanguages(block: (List<Language>) -> Unit) {
        activity?.let {
            languages.observe(it, Observer {
                it?.let { block(it) }
            })
        }
    }

    fun getSkills(block: (List<Skill>) -> Unit) {
        activity?.let {
            skills.observe(it, Observer {
                it?.let { block(it) }
            })
        }
    }

    fun registerForCvNotifications(register: Boolean, status: (Int) -> Unit) {
        repository.registerForCvNotifications(register, status)
    }
}