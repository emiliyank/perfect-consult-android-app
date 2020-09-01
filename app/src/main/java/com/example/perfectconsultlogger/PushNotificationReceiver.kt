package com.example.perfectconsultlogger

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
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
            initiateCall(it)
        }
//        Check if message contains a data payload.
//        if (remoteMessage.data.size > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.data)
//        }
    }

    @SuppressLint("MissingPermission")
    private fun initiateCall(phonenumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phonenumber")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        p0?.let { Database.getInstance(this).setNotificationToken(it) }
        p0?.let { ApiWrapper.getInstance(this).sendNotificationToken(it) }
    }

    companion object {

        const val NOTIFICATION_PHONE_NUMBER_PAYLOAD = "clientPhone"
    }
}
