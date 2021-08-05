package com.example.perfectconsultlogger

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.perfectconsultlogger.ui.MainActivity

class NotificationRequestWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val callLogService = CallLogsService()

    override fun doWork(): Result {
        Log.e("TTTT", "start manager")
        startService()
        return Result.success()
    }

    private fun startService() {
        if (!isMyServiceRunning(CallLogsService::class.java)) {
            callLogService.startService(context)
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