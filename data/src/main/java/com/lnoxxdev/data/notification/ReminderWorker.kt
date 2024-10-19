package com.lnoxxdev.data.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.lnoxxdev.data.R

class ReminderWorker(private val appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {

    private val appNotificationManager = AppNotificationManager(appContext)

    override fun doWork(): Result {
        val text = inputData.getString(TEXT_KEY)
            ?: appContext.getString(R.string.notification_default_text)
        val id = inputData.getInt(ID_KEY, 669)
        appNotificationManager.showDefaultNotification(
            text,
            appContext.getString(R.string.notification_title),
            id
        )
        return Result.success()
    }

    companion object {
        const val TEXT_KEY = "REMINDER_NOTIFICATION_TEXT"
        const val ID_KEY = "REMINDER_NOTIFICATION_ID"
    }
}