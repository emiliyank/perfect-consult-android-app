package com.example.perfectconsultlogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.perfectconsultlogger.data.Database
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {

   private val repeatingTaskHelper = RepeatingTaskHelper()

    override fun onReceive(context: Context?, intent: Intent?) {
        val nonNullIntent = intent ?: return
        val nonNullContext = context ?: return
        if (nonNullIntent.action == "android.intent.action.BOOT_COMPLETED") {
            repeatingTaskHelper.scheduleRepeatingTasks()
        }
    }
}