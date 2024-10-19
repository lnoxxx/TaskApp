package com.lnoxxdev.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import com.lnoxxdev.data.R

class AppNotificationManager(private val appContext: Context) {

    init {
        initReminderNotificationChannel()
    }

    fun showDefaultNotification(text: String, title: String, id: Int) {
        val notificationManager =
            appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder =
            NotificationCompat.Builder(appContext, CHANNEL_ID_REMINDER)
                .setContentText(text)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_notification).build()

        notificationManager.notify(id, notificationBuilder)
    }

    private fun initReminderNotificationChannel() {
        val name = appContext.getString(R.string.notification_channel_name)
        val descriptionText = appContext.getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH

        val mChannel = NotificationChannel(CHANNEL_ID_REMINDER, name, importance)
        mChannel.description = descriptionText
        val notificationManager =
            appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    companion object {
        const val CHANNEL_ID_REMINDER = "TaskAppReminders"
    }
}