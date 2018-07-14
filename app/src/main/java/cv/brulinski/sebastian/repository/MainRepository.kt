package cv.brulinski.sebastian.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.leonardoxh.livedatacalladapter.Resource
import cv.brulinski.sebastian.model.Welcome
import cv.brulinski.sebastian.utils.database
import cv.brulinski.sebastian.utils.retrofit

class MainRepository {

    private val welcome = MutableLiveData<Welcome>()

    fun getWelcome(): LiveData<Welcome>? {
        database.getWelcome().value?.apply {
            welcome.value = this
        } ?: kotlin.run {
            fetchWelcome {
//                it?.value?.let {
//                    database.insertWelcome(it)
//                    welcome.value = it
//                }
            }
        }
        return welcome
    }

    private fun fetchWelcome(result: (LiveData<Welcome>?) -> Unit) {
        retrofit.getWelcome().observeForever {
            result(MutableLiveData<Welcome>().apply { value = it.resource })
        }
    }
}