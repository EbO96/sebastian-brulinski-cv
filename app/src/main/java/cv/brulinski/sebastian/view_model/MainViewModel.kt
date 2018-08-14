package cv.brulinski.sebastian.view_model

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cv.brulinski.sebastian.interfaces.OnFetchingStatuses
import cv.brulinski.sebastian.model.*
import cv.brulinski.sebastian.repository.MainRepository

/**
 * Role of this class is being bridge between repository and views
 * @see MainRepository
 */
class MainViewModel<T : OnFetchingStatuses> constructor(private val activity: AppCompatActivity, listener: T) : AndroidViewModel(activity.application) {

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

    fun refreshAll() = repository.refreshAll()

    fun getWelcome(block: (Welcome) -> Unit) {
        welcome.observe(activity, Observer {
            it?.let { block(it) }
        })
    }

    fun getPersonalInfo(block: (PersonalInfo) -> Unit) {
        personalInfo.observe(activity, Observer {
            it?.let { block(it) }
        })
    }

    fun getCareer(block: (List<Career>) -> Unit) {
        career.observe(activity, Observer {
            it?.let { block(it) }
        })
    }

    fun getLanguages(block: (List<Language>) -> Unit) {
        languages.observe(activity, Observer {
            it?.let { block(it) }
        })
    }

    fun getSkills(block: (List<Skill>) -> Unit) {
        skills.observe(activity, Observer {
            it?.let { block(it) }
        })
    }
}