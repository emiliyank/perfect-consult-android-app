package com.example.perfectconsultlogger

import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit


private const val REPEAT_INTERVAL = 60L

class RepeatingTaskHelper {

    fun scheduleRepeatingTasks() {
        val periodicRefreshRequest = PeriodicWorkRequest.Builder(
            NotificationRequestWorker::class.java, // Your worker class
            REPEAT_INTERVAL,
            TimeUnit.MINUTES
        ).build()
        WorkManager.getInstance().enqueue(periodicRefreshRequest)
    }
}