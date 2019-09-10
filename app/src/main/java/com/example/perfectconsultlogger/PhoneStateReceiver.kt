package com.example.perfectconsultlogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class PhoneStateReceiver : BroadcastReceiver() {

    private val TAG = "PhoneStateReceiver"

    private var lastState = TelephonyManager.CALL_STATE_IDLE

    override fun onReceive(context: Context?, intent: Intent?) {
        checkIncoming(intent)
    }


    private fun checkIncoming(intent: Intent?) {
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)

        if (lastState == TelephonyManager.CALL_STATE_IDLE) {
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                val number = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                Log.d(TAG, "Incoming number : $number")
            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                val number = intent?.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                Log.d(TAG, "Outgoing number : $number")
            }
        }
        Log.d(TAG, state)
        lastState = intState(state)
    }

    private fun intState(state: String?): Int {
        if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            return TelephonyManager.CALL_STATE_OFFHOOK
        } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            return TelephonyManager.CALL_STATE_IDLE
        } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            return TelephonyManager.CALL_STATE_RINGING
        }
        return -1
    }
}