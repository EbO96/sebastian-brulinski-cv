package cv.brulinski.sebastian.repository

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import cv.brulinski.sebastian.activity.SplashActivity
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.model.Language
import cv.brulinski.sebastian.model.MyCv
import cv.brulinski.sebastian.repository.MainRepository.Type.BCG
import cv.brulinski.sebastian.repository.MainRepository.Type.PROFILE
import cv.brulinski.sebastian.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.FileNotFoundException

@SuppressLint("CheckResult")
class MainRepository {

    private enum class Type {
        PROFILE,
        BCG
    }

    private val myCv = MutableLiveData<MyCv>()
    private val storage = FirebaseStorage.getInstance()
    private val profilePictureReference = storage.getReference("profile_picture/profile.jpg")
    private val profilePictureBcgReference = storage.getReference("profile_bcg/bcg.jpg")

    private var getDatabaseCvCounter = 0
    private val databaseCv = MyCv()

    fun getCv(): LiveData<MyCv> {
        if (getPrefsValue(SplashActivity.firstLaunch) == true)
            fetchCv()
        else
            getDatabaseCv()
        return myCv
    }

    fun refreshAll() {
        fetchCv(true)
    }

    private fun getDatabaseCv() {
        database.getWelcome().observeForever {
            databaseCv.welcome = it
            isAllDatabaseGetCompleted()
        }
        database.getPersonalInfo().observeForever {
            databaseCv.personalInfo = it
            isAllDatabaseGetCompleted()
        }

        database.getCareer().observeForever {
            databaseCv.career = it
            isAllDatabaseGetCompleted()
        }

        database.getLanguages().observeForever {
            databaseCv.languages = it
            isAllDatabaseGetCompleted()
        }
    }

    private fun isAllDatabaseGetCompleted() {
        getDatabaseCvCounter++
        if (getDatabaseCvCounter == 4) {
            myCv.value = databaseCv
            getDatabaseCvCounter = 0
            fetchProfileGraphics()
        }
    }

    private fun fetchProfileGraphics(refresh: Boolean = false) {
        ProfilePictureManager().apply {
            Type.values().forEach { type ->
                if (!loadFromDevice(type) || refresh)
                    type.getRef().downloadUrl.addOnSuccessListener {
                        fetchBitmap(it.toString()).subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    it?.let {
                                        notifyProfileGraphics(type, it)
                                        saveToDevice(type, it)
                                    }
                                }
                    }.addOnFailureListener {
                        it.printStackTrace()
                    }
            }
        }
    }

    private fun List<Language>.fetchFlags(result: (List<Language>) -> Unit) {
        val observables = ArrayList<Observable<Bitmap>>()
        forEach {
            observables.add(fetchBitmap(it.imageUrl))
        }
        val flags = arrayListOf<Bitmap?>()
        Observable.merge(observables)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    flags.add(it)
                }.doOnComplete {
                    withIndex().forEach {
                        it.value.flag = flags[it.index]
                    }
                    result(this)
                }
                .subscribe()
    }

    private fun notifyProfileGraphics(type: MainRepository.Type, bitmap: Bitmap?) {
        bitmap?.let {
            when (type) {
                MainRepository.Type.PROFILE -> {
                    myCv.apply {
                        value?.personalInfo?.profilePicture = bitmap
                        postValue(value)
                    }
                }
                MainRepository.Type.BCG -> {
                    myCv.apply {
                        value?.personalInfo?.profileBcg = bitmap
                        postValue(value)
                    }
                }
            }
        }
    }

    private fun fetchCv(refresh: Boolean = false) {
        retrofit
                .getAll()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ cv ->
                    false.putPrefsValue(SplashActivity.firstLaunch)
                    fetchProfileGraphics(refresh)
                    cv.insert()
                }, {
                    it.printStackTrace()
                })
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

        private fun Type.getFileName() = when (this) {
            PROFILE -> profilePhotoFileName
            BCG -> profileBcgFileName
        }
    }
}