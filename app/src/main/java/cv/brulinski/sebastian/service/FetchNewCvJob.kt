package cv.brulinski.sebastian.service

import android.content.Intent
import androidx.lifecycle.Observer
import com.firebase.jobdispatcher.JobService
import com.google.gson.Gson
import cv.brulinski.sebastian.interfaces.RemoteRepository
import cv.brulinski.sebastian.model.MyCv
import cv.brulinski.sebastian.utils.MAIN_ACTIVITY
import cv.brulinski.sebastian.utils.log
import cv.brulinski.sebastian.view_model.MainViewModel

class FetchNewCvJob : JobService(), RemoteRepository {

    private val viewModel = MainViewModel<RemoteRepository>(null, this)

    override fun onStopJob(job: com.firebase.jobdispatcher.JobParameters?): Boolean {
        return false // Answers the question: "Should this job be retried?"
    }

    override fun onStartJob(job: com.firebase.jobdispatcher.JobParameters?): Boolean {
        viewModel.apply {

            var observer: Observer<MyCv>? = null
            observer = Observer {
                observer?.let {
                    refreshAll()
                    myCv.removeObserver(it)
                }
            }
            myCv.observeForever(observer)
        }
        return false // Answers the question: "Is there still work going on?"
    }

    private fun createNotification() {

    }

    override fun onFetchStart() {
        MAIN_ACTIVITY.log("fetch start")
    }

    override fun onFetchEnd() {
        viewModel.myCv.value?.let { cv ->
            Intent().apply {
                action = MainViewModel.UPDATED_CV_IN_BACKGROUND
                val jsonCv = Gson().toJson(cv)
                //TODO all bitmaps to base64
//                putExtra("cv", jsonCv)//TODO error - to long string?
                application.sendBroadcast(this)
            }
        }

        createNotification()
        MAIN_ACTIVITY.log("fetch end")
    }

    override fun onFetchError(error: Throwable?) {
        MAIN_ACTIVITY.log("fetch error")
    }
}