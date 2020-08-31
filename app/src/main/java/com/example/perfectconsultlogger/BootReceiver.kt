package com.example.perfectconsultlogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.perfectconsultlogger.data.Database

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val nonNullIntent = intent ?: return
        val nonNullContext = context ?: return
        val database = Database.getInstance(nonNullContext)
        val callLogService = CallLogsService()
        if (nonNullIntent.action == "android.intent.action.BOOT_COMPLETED") {
            database.isServiceRunning(object : Database.DataListener<String> {
                override fun onData(data: String) {
                    if (data.toBoolean()) {
                        callLogService.startService(nonNullContext)
                    }
                }
            })
        }
    }
}