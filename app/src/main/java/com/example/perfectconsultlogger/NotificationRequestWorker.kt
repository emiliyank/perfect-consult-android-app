package com.example.perfectconsultlogger

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val DO_WORK = "do_work"
private const val IF_NOT_RUNNING_START_SERVICE = "if_not_service_running_start"

class NotificationRequestWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val callLogService = CallLogsService()
    private val pushNotificationReceiver = PushNotificationReceiver()

    override fun doWork(): Result {
        Log.e("TTTT", "start manager")
        startService()
        pushNotificationReceiver.firebaseAnalyticsLogEven(DO_WORK)
        return Result.success()
    }

    private fun startService() {
        if (!isMyServiceRunning(CallLogsService::class.java)) {
            callLogService.startService(context)
            pushNotificationReceiver.firebaseAnalyticsLogEven(IF_NOT_RUNNING_START_SERVICE)
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}