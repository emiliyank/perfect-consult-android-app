package com.example.perfectconsultlogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.perfectconsultlogger.data.CallLog
import com.example.perfectconsultlogger.data.Database
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PhoneStateReceiver : BroadcastReceiver() {

    private val TAG = "PhoneStateReceiver"

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var database: Database? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        database = context?.let { Database.getInstance(it) }
        checkIncoming(intent)
    }


    private fun checkIncoming(intent: Intent?) {
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)

        if (lastState == TelephonyManager.CALL_STATE_IDLE) {
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                processIncomingCall(intent)
            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //TODO: extract outgoing call number
                val number = intent?.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                Log.d(TAG, "Outgoing number : $number " + intent?.action)
            }
        }
        Log.d(TAG, state)
        lastState = intState(state)
    }

    private fun processIncomingCall(intent: Intent?) {
        val number = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        //take care of date
        //TODO: check phone states and obtain start and end time
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)

        val callLog = number?.let { CallLog(it, formatted, formatted, true) }
        if (callLog != null) {
            database?.insertCallLog(callLog)
        }
        Log.d(TAG, "Incoming number : $number")
    }

    private fun intState(state: String?): Int {
        return when {
            state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) -> TelephonyManager.CALL_STATE_OFFHOOK
            state.equals(TelephonyManager.EXTRA_STATE_IDLE) -> TelephonyManager.CALL_STATE_IDLE
            state.equals(TelephonyManager.EXTRA_STATE_RINGING) -> TelephonyManager.CALL_STATE_RINGING
            else -> -1
        }
    }
}