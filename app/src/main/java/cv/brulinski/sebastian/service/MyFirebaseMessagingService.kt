package cv.brulinski.sebastian.service

import android.os.Bundle
import com.firebase.jobdispatcher.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Service to receive FCM
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if ((remoteMessage?.data?.get("newCv")?.toBoolean() == true))
            fetchNewCv(remoteMessage)
    }

    private fun fetchNewCv(remoteMessage: RemoteMessage) {

        val contentTitle = remoteMessage.data[FetchNewCvJob.TITLE_ARG]
        val contentText = remoteMessage.data[FetchNewCvJob.MESSAGE_ARG]
        val bundle = Bundle().apply {
            putString(FetchNewCvJob.TITLE_ARG, contentTitle)
            putString(FetchNewCvJob.MESSAGE_ARG, contentText)
        }

        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(applicationContext))
        val fetchCvJob = dispatcher.newJobBuilder()
                .setService(FetchNewCvJob::class.java)
                .setTag("fetch-new-cv")
                .setRecurring(false)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(0, 1))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setExtras(bundle)
                .build()
        dispatcher.mustSchedule(fetchCvJob)
    }

}