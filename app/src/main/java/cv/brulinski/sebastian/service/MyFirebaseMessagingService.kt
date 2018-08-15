package cv.brulinski.sebastian.service

import com.firebase.jobdispatcher.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if ((remoteMessage?.data?.get("newCv")?.toBoolean() == true))
            fetchNewCv()
    }

    private fun fetchNewCv() {
        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(applicationContext))
        val fetchCvJob = dispatcher.newJobBuilder()
                .setService(FetchNewCvJob::class.java)
                .setTag("fetch-new-cv")
                .setRecurring(false)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(0, 1))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build()
        dispatcher.mustSchedule(fetchCvJob)
    }

}