package cv.brulinski.sebastian.repository

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.model.PersonalInfo
import cv.brulinski.sebastian.model.Welcome
import cv.brulinski.sebastian.repository.MainRepository.Type.BCG
import cv.brulinski.sebastian.repository.MainRepository.Type.PROFILE
import cv.brulinski.sebastian.utils.database
import cv.brulinski.sebastian.utils.insertPersonalInfo
import cv.brulinski.sebastian.utils.insertWelcome
import cv.brulinski.sebastian.utils.retrofit
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

    private val welcome = MutableLiveData<Welcome>()
    private val personalInfo = MutableLiveData<PersonalInfo>()
    private val storage = FirebaseStorage.getInstance()
    private val profilePictureReference = storage.getReference("profile_picture/profile.jpg")
    private val profilePictureBcgReference = storage.getReference("profile_bcg/bcg.jpg")
    private val welcomeObserver = Observer<Welcome> {
        it?.let {
            welcome.value = it
        } ?: run {
            fetchWelcome()
        }
    }
    private val personalInfoObserver = Observer<PersonalInfo> {
        it?.let {
            personalInfo.value = it
            fetchProfileGraphics()
        } ?: run {
            fetchPersonalInfo()
        }
    }

    fun getWelcome(): LiveData<Welcome> {
        database.getWelcome().observeForever(welcomeObserver)
        return welcome
    }

    fun refreshWelcome() {
        fetchWelcome(true)
    }

    fun getPersonalInfo(): LiveData<PersonalInfo> {
        database.getPersonalInfo().observeForever(personalInfoObserver)
        return personalInfo
    }

    fun refreshPersonalInfo() {
        fetchPersonalInfo(true)
        fetchProfileGraphics(true)
    }

    private fun fetchProfileGraphics(refresh: Boolean = false) {
        ProfilePictureManager().apply {
            Type.values().forEach { type ->
                if (!loadFromDevice(type) || refresh)
                    type.getRef().downloadUrl.addOnSuccessListener {
                        Log.d("url", "$it")
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
                                            saveToDevice(type, this)
                                            notifyProfileGraphics(type, this)
                                        }
                                    }
                                })
                    }.addOnFailureListener {
                        it.printStackTrace()
                    }
            }
        }
    }

    private fun fetchWelcome(refresh: Boolean = false) {
        retrofit
                .getWelcome()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    insertWelcome(it.apply {
                        timestamp = Calendar.getInstance().timeInMillis
                    })
                    if (refresh)
                        welcome.value = it
                }, {
                    fetchError()
                    it.printStackTrace()
                })
    }

    private fun fetchPersonalInfo(refresh: Boolean = false) {
        retrofit
                .getPersonalInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    insertPersonalInfo(it.apply {
                        timestamp = Calendar.getInstance().timeInMillis
                        personalInfo.value?.let {
                            profilePicture = it.profilePicture
                            profileBcg = it.profileBcg
                        }
                    })
                    if (refresh)
                        personalInfo.value = it
                }, {
                    fetchError()
                    it.printStackTrace()
                })
    }

    private fun fetchError() {
        personalInfo.value = personalInfo.value?.apply {
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