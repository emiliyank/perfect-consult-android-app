package com.example.perfectconsultlogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.perfectconsultlogger.data.CallLog
import com.example.perfectconsultlogger.data.Database
import java.util.*


class PhoneStateReceiver : BroadcastReceiver() {

    private val TAG = "PhoneStateReceiver"

    private val owner_number = getOwnerNumber()

    private fun getOwnerNumber(): String {
        //TODO: obtain phone number
        return ""
    }

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var database: Database? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        database = context?.let { Database.getInstance(it) }
        checkCall(intent)
    }


    private fun checkCall(intent: Intent?) {
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
        var targetNumber: String? = null
        var timeStamp = 0L
        var eventType = checkEventType(state)
        var isIncoming = false

        lastState = intState(state)
        val callLog = targetNumber?.let { CallLog("", it, timeStamp, eventType, isIncoming) }
        callLog?.let { addToDatabase(it) }
    }

    private fun checkEventType(state: String?): String {
        //TODO: implement
        return ""
    }

    private fun addToDatabase(callLog: CallLog) = database?.insertCallLog(callLog)

    private fun intState(state: String?): Int {
        return when {
            state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) -> TelephonyManager.CALL_STATE_OFFHOOK
            state.equals(TelephonyManager.EXTRA_STATE_IDLE) -> TelephonyManager.CALL_STATE_IDLE
            state.equals(TelephonyManager.EXTRA_STATE_RINGING) -> TelephonyManager.CALL_STATE_RINGING
            else -> -1
        }
    }
}
