package cv.brulinski.sebastian.service

import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import com.firebase.jobdispatcher.JobService
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.interfaces.RemoteRepository
import cv.brulinski.sebastian.model.MyCv
import cv.brulinski.sebastian.utils.MAIN_ACTIVITY
import cv.brulinski.sebastian.utils.log
import cv.brulinski.sebastian.view_model.MainViewModel

/**
 * This service is used to fetching new CV is foreground or background.
 * This Job is trigger by FCM notification
 */
class FetchNewCvJob : JobService(), RemoteRepository {

    private val viewModel = MainViewModel<RemoteRepository>(null, this)
    private var cv: MyCv? = null //This is updated version of cv
    //Notification
    private val NOTIFICATION_ID = 1
    private var contentTitle = ""
    private var contentText = ""
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    companion object {
        //Channel
        const val CHANNEL_ID = "NEW CV CHANNEL"
        const val TITLE_ARG = "title"
        const val MESSAGE_ARG = "message"
    }

    override fun onStopJob(job: com.firebase.jobdispatcher.JobParameters?): Boolean {
        return false // Answers the question: "Should this job be retried?"
    }

    override fun onStartJob(job: com.firebase.jobdispatcher.JobParameters?): Boolean {
        job?.extras?.apply {
            contentTitle = getString(TITLE_ARG)
            contentText = getString(MESSAGE_ARG)
        }
        viewModel.apply {
            var observer: Observer<MyCv>? = null
            observer = Observer {
                observer?.let {
                    refreshAll {
                        cv = it
                    }
                    myCv.removeObserver(it)
                }
            }
            myCv.observeForever(observer)
        }
        return false // Answers the question: "Is there still work going on?"
    }

    private fun createNotification() {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_receipt)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle())
                .setAutoCancel(true)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onFetchStart() {
        MAIN_ACTIVITY.log("fetch start")
    }

    override fun onFetchEnd() {
        //Send broadcast to inform observers about new CV version
        cv?.let { cv ->
            Intent().apply {
                action = MainViewModel.UPDATED_CV_IN_BACKGROUND
                App.DataHolder.INSTANCE.cv = cv
                application.sendBroadcast(this)
            }
        }
        MAIN_ACTIVITY.log("fetch end")
        createNotification()
    }

    override fun onFetchError(error: Throwable?) {
        MAIN_ACTIVITY.log("fetch error")
    }
}