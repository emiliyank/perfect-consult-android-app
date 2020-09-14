package com.example.perfectconsultlogger

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val phoneNumber = intent?.getStringExtra(PushNotificationReceiver.EXTRA_PHONE_NUMBER)

        val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        context?.sendBroadcast(closeIntent)

        context?.let { dismissNotification(it) }

        phoneNumber?.let { it -> initiateCall(it, context) }
    }

    @SuppressLint("MissingPermission")
    private fun initiateCall(phoneNumber: String, context: Context?) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    private fun dismissNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(PushNotificationReceiver.NOTIFICATION_ID)
    }
}