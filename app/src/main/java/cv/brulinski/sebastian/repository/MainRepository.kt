package cv.brulinski.sebastian.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cv.brulinski.sebastian.model.Welcome
import cv.brulinski.sebastian.utils.InsertWelcome
import cv.brulinski.sebastian.utils.database
import cv.brulinski.sebastian.utils.retrofit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainRepository {

    private val welcome = MutableLiveData<Welcome>()

    fun getWelcome(): LiveData<Welcome>? {
        database.getWelcome().observeForever {
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
}