package com.lnoxxdev.data.notification

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class ReminderManager(private val appContext: Context) {
    fun scheduleReminder(name: String, secondsDelay: Long, id: Int): String {
        val data =
            Data.Builder()
                .putString(ReminderWorker.TEXT_KEY, name)
                .putInt(ReminderWorker.ID_KEY, id)
                .build()
        val workRequest =
            OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(data)
                .setInitialDelay(secondsDelay, TimeUnit.SECONDS)
                .build()
        val workId = workRequest.id.toString()
        WorkManager.getInstance(appContext).enqueue(workRequest)
        return workId
    }
}