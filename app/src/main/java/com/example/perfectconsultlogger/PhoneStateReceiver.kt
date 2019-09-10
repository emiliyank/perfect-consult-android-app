package com.example.perfectconsultlogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PhoneStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("PhoneStateReceiver", "receiver start")
    }
}