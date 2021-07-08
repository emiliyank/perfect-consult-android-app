package com.example.perfectconsultlogger

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.perfectconsultlogger.data.Database
import com.example.perfectconsultlogger.data.remote.ApiWrapper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val TAG = "PushNotificationReceiver"

class PushNotificationReceiver : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage!!.from!!)

        remoteMessage?.data?.get(NOTIFICATION_PHONE_NUMBER_PAYLOAD)?.let {
            if (isAppInForegrounded()) {
                val snoozeIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$it"))
                startActivity(snoozeIntent)
            } else {
                startNotification(it)
            }
        }
    }

    private fun isAppInForegrounded(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        p0?.let { Database.getInstance(this).setNotificationToken(it) }
        p0?.let { ApiWrapper.getInstance(this).sendNotificationToken(it) }
    }

    private fun getPendingIntent(phoneNumber: String): PendingIntent {
        val snoozeIntent = Intent(this, NotificationReceiver::class.java).apply {
            action = ACTION_NOTIFICATION
            putExtra(EXTRA_PHONE_NUMBER, phoneNumber)
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        return pendingIntent
    }

    private fun startNotification(phoneNumber: String) {
        val pendingIntent = getPendingIntent(phoneNumber)
        val notificationBuilder =
            NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_pc_logo)
                .setContentTitle(TITLE)
                .setContentText(MESSAGE)
                .setStyle(NotificationCompat.BigTextStyle().bigText(MESSAGE))
                .setSound(null)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(pendingIntent, true)
                .addAction(R.drawable.ic_pc_logo, ACTION_NAME, pendingIntent)


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"
        const val NOTIFICATION_PHONE_NUMBER_PAYLOAD = "clientPhone"
        const val NOTIFICATION_ID = 0
        private const val TITLE = "Perfect Consult"
        private const val MESSAGE = "Кликнете, за да направите обаждане"
        private const val ACTION_NAME = "ПОЗВЪНЯВАНЕ"
        private const val ACTION_NOTIFICATION = "ACTION_NOTIFICATION"
    }
}
