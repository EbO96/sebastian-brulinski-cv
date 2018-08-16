package cv.brulinski.sebastian.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class MyNotificationChannel(private val context: Context) {

    companion object {
        private const val NEW_CV_CHANNEL_NAME = "Aktualizacja CV"
        private const val NEW_CV_CHANNEL_DESCRIPTION = "Powiadomienie o dostępności nowej wersji CV"
    }

    fun newCvChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(channelId, NEW_CV_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = NEW_CV_CHANNEL_DESCRIPTION
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(this)
            }
        }
    }
}