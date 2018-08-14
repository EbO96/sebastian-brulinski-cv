package cv.brulinski.sebastian.service

import androidx.lifecycle.Observer
import com.firebase.jobdispatcher.JobService
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
        createNotification()
        MAIN_ACTIVITY.log("fetch end")
    }

    override fun onFetchError(error: Throwable?) {
        MAIN_ACTIVITY.log("fetch error")
    }
}