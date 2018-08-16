package cv.brulinski.sebastian.view_model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.interfaces.RemoteRepository
import cv.brulinski.sebastian.model.*
import cv.brulinski.sebastian.repository.MainRepository
import cv.brulinski.sebastian.utils.MAIN_ACTIVITY
import cv.brulinski.sebastian.utils.log

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
    //Broadcast
    private var myBroadcastReceiver: MyBroadcastReceiver? = null
    private var broadcastIntentFilter: IntentFilter? = null

    companion object {
        //MyBroadcastReceiver actions
        const val UPDATED_CV_IN_BACKGROUND = "updated cv in background"
    }

    init {
        activity?.apply {
            //Observer CV
            myCv.observe(activity, Observer {
                //Get CV parts and inform each fragment associated with this about update
                it?.apply {
                    welcome?.let { welcome ->
                        this@MainViewModel.welcome.postValue(welcome)
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

    fun refreshAll(cv: ((MyCv) -> Unit)? = null) = repository.refreshAll(cv)

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

    fun registerMyBroadcastReceiver() {
        activity?.apply {
            //Initialize broadcast receiver only when activity isn't null
            myBroadcastReceiver = MyBroadcastReceiver()
            broadcastIntentFilter = IntentFilter().apply {
                addAction(UPDATED_CV_IN_BACKGROUND)
            }
            registerReceiver(myBroadcastReceiver, broadcastIntentFilter)
        }
    }

    fun unregisterMyBroadcastReceiver() {
        try {
            myBroadcastReceiver?.let {
                activity?.unregisterReceiver(it)
            }
        } catch (e: IllegalArgumentException) {
            MAIN_ACTIVITY.log("${e.message}")
        }
    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                UPDATED_CV_IN_BACKGROUND -> {
                    App.DataHolder.INSTANCE.cv?.let { cv ->
                        myCv.postValue(cv)
                    }
                }
            }
        }
    }
}