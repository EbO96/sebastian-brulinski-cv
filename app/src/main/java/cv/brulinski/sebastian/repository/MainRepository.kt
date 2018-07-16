package cv.brulinski.sebastian.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cv.brulinski.sebastian.model.PersonalInfo
import cv.brulinski.sebastian.model.Welcome
import cv.brulinski.sebastian.utils.InsertPersonalInfo
import cv.brulinski.sebastian.utils.InsertWelcome
import cv.brulinski.sebastian.utils.database
import cv.brulinski.sebastian.utils.retrofit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class MainRepository {

    private val welcome = MutableLiveData<Welcome>()
    private val personalInfo = MutableLiveData<PersonalInfo>()

    fun getWelcome(): LiveData<Welcome> {
        database
                .getWelcome()
                .observeForever {
                    it?.let {
                        welcome.value = it
                    } ?: run {
                        fetchWelcome {
                            InsertWelcome().execute(it)
                        }
                    }
                }
        return welcome
    }

    fun getPersonalInfo(): LiveData<PersonalInfo> {
        database
                .getPersonalInfo()
                .observeForever {
                    it?.let {
                        personalInfo.value = it
                    } ?: run {
                        fetchPersonalInfo {
                            InsertPersonalInfo().execute(it)
                        }
                    }
                }
        return personalInfo
    }

    private fun fetchWelcome(result: (Welcome) -> Unit) {
        retrofit
                .getWelcome()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result(it)
                }, {
                    it.printStackTrace()
                })
    }

    private fun fetchPersonalInfo(result: (PersonalInfo) -> Unit) {
        retrofit
                .getPersonalInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result(it)
                }, {
                    it.printStackTrace()
                })
    }
}