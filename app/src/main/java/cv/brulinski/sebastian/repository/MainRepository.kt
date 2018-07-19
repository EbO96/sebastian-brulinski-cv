package cv.brulinski.sebastian.repository

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.model.*
import cv.brulinski.sebastian.repository.MainRepository.Type.BCG
import cv.brulinski.sebastian.repository.MainRepository.Type.PROFILE
import cv.brulinski.sebastian.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.FileNotFoundException
import java.util.*

@SuppressLint("CheckResult")
class MainRepository {

    private enum class Type {
        PROFILE,
        BCG
    }

    companion object {
        val TAG = "MainRepository"
    }

    private val welcome = MutableLiveData<Welcome>()
    private val personalInfo = MutableLiveData<PersonalInfo>()
    private val education = MutableLiveData<Education>()
    private val jobExperience = MutableLiveData<JobExperience>()
    private val storage = FirebaseStorage.getInstance()
    private val profilePictureReference = storage.getReference("profile_picture/profile.jpg")
    private val profilePictureBcgReference = storage.getReference("profile_bcg/bcg.jpg")

    fun getWelcome(): LiveData<Welcome> {
        getDatabaseWelcome({
            if (it != welcome.value)
                welcome.value = it
        }, {
            fetchWelcome()
        })
        return welcome
    }

    fun refreshWelcome() {
        fetchWelcome()
    }

    fun getPersonalInfo(): LiveData<PersonalInfo> {
        getDatabasePersonalInfo({
            if (it != personalInfo.value) {
                personalInfo.value = it
                fetchProfileGraphics(true)
            } else fetchProfileGraphics()
        }, {
            fetchPersonalInfo()
        })
        return personalInfo
    }

    fun refreshPersonalInfo() {
        fetchPersonalInfo()
    }

    fun getEducation(): LiveData<Education> {
        getDatabaseSchools({
            if (it != education.value?.school)
                education.value = Education().apply {
                    school = it
                }
        }, {
            fetchSchools()
        })
        return education
    }

    fun refreshEducation() {
        fetchSchools()
    }

    fun getJobExperience(): LiveData<JobExperience> {
        getDatabaseJobs({
            if (it != jobExperience.value?.jobs)
                jobExperience.apply {
                    value = value?.apply {
                        timestamp = Calendar.getInstance().timeInMillis
                        jobs = it
                    }
                }
        }, {
            fetchJobs()
        })
        return jobExperience
    }

    fun refreshJonExperience(){
        fetchJobs()
    }

    private fun getDatabaseWelcome(welcome: (Welcome) -> Unit, empty: () -> Unit) {
        database.getWelcome().observeForever {
            it?.let { welcome(it) } ?: run { empty() }
        }
    }

    private fun getDatabasePersonalInfo(personalInfo: (PersonalInfo) -> Unit, empty: () -> Unit) {
        database.getPersonalInfo().observeForever {
            it?.let { personalInfo(it) } ?: run { empty() }
        }
    }

    private fun getDatabaseSchools(schools: (List<School>) -> Unit, empty: () -> Unit) {
        database.getSchools().observeForever {
            it?.let { if (it.isEmpty()) empty() else schools(it) } ?: run { empty() }
        }
    }

    private fun getDatabaseJobs(jobs: (List<Job>) -> Unit, empty: () -> Unit) {
        database.getJobs().observeForever {
            it?.let { if (it.isEmpty()) empty() else jobs(it) } ?: run { empty() }
        }
    }

    private fun fetchProfileGraphics(refresh: Boolean = false) {
        ProfilePictureManager().apply {
            Type.values().forEach { type ->
                if (!loadFromDevice(type) || refresh)
                    type.getRef().downloadUrl.addOnSuccessListener {
                        Picasso
                                .with(App.component.getContext())
                                .load(it)
                                .into(object : Target {
                                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                    }

                                    override fun onBitmapFailed(errorDrawable: Drawable?) {
                                    }

                                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                        bitmap?.apply {
                                            notifyProfileGraphics(type, this)
                                            saveToDevice(type, this)
                                        }
                                    }
                                })
                    }.addOnFailureListener {
                        it.printStackTrace()
                    }
            }
        }
    }

    private fun fetchWelcome() {
        retrofit.getWelcome()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.let {
                        it.timestamp = Calendar.getInstance().timeInMillis
                        welcome.value = it
                        it.insert()
                    } ?: run {
                        welcomeFetchingError()
                    }
                }, {
                    welcomeFetchingError()
                    it.printStackTrace()
                })
    }

    private fun fetchPersonalInfo() {
        retrofit.getPersonalInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.apply {
                        personalInfo.value = it.apply {
                            profilePicture = personalInfo.value?.profilePicture
                            profileBcg = personalInfo.value?.profileBcg
                        }
                        fetchProfileGraphics(true)
                        insert()
                    } ?: run {
                        personalInfoFetchingError()
                    }
                }, {
                    personalInfoFetchingError()
                    it.printStackTrace()
                })
    }

    private fun fetchSchools() {
        retrofit.getSchools()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.let {
                        education.value = Education().apply { school = it }
                        it.insertSchools()
                    } ?: run {
                        schoolsFetchingError()
                    }
                }, {
                    schoolsFetchingError()
                    it.printStackTrace()
                })
    }

    private fun fetchJobs() {
        retrofit.getJobs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.let {
                        jobExperience.apply {
                            value = value?.apply {
                                timestamp = Calendar.getInstance().timeInMillis
                                jobs = it
                            }
                        }
                        it.insertJobs()
                    } ?: run {
                        jobsFetchingError()
                    }
                }, {
                    jobsFetchingError()
                    it.printStackTrace()
                })
    }

    private fun personalInfoFetchingError() {
        personalInfo.value = personalInfo.value?.apply {
            timestamp = Calendar.getInstance().timeInMillis
        }
    }

    private fun welcomeFetchingError() {
        welcome.value = welcome.value?.apply {
            timestamp = Calendar.getInstance().timeInMillis
        }
    }

    private fun schoolsFetchingError() {
        education.value = education.value?.apply {
            timestamp = Calendar.getInstance().timeInMillis
        }
    }

    private fun jobsFetchingError() {
        jobExperience.value = jobExperience.value?.apply {
            timestamp = Calendar.getInstance().timeInMillis
        }
    }

    private fun Type.getRef() = when (this) {
        MainRepository.Type.PROFILE -> profilePictureReference
        MainRepository.Type.BCG -> profilePictureBcgReference
    }

    private inner class ProfilePictureManager {

        private val profilePhotoFileName = "profile.jpg"
        private val profileBcgFileName = "bcg.jpg"

        fun loadFromDevice(type: Type): Boolean {
            try {
                App.component.getContext().openFileInput(type.getFileName()).use {
                    notifyProfileGraphics(type, BitmapFactory.decodeStream(it))
                    return true
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return false
            }
        }

        fun saveToDevice(type: Type, bitmap: Bitmap) {
            App.component.getContext().openFileOutput(type.getFileName(), Context.MODE_PRIVATE).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        }

        fun notifyProfileGraphics(type: Type, graphic: Bitmap) {
            when (type) {
                MainRepository.Type.PROFILE -> notifyProfilePicture(graphic)
                MainRepository.Type.BCG -> notifyProfileBcg(graphic)
            }
        }

        private fun notifyProfilePicture(profile: Bitmap? = null) {
            personalInfo.apply {
                value = value?.apply {
                    profile?.apply {
                        profilePicture = profile
                    }
                }
            }
        }

        private fun notifyProfileBcg(bcg: Bitmap? = null) {
            personalInfo.apply {
                value = value?.apply {
                    bcg?.apply {
                        profileBcg = bcg
                    }
                }
            }
        }

        private fun Type.getFileName() = when (this) {
            PROFILE -> profilePhotoFileName
            BCG -> profileBcgFileName
        }
    }
}