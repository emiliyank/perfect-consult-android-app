package com.example.perfectconsultlogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.perfectconsultlogger.data.CallLog
import com.example.perfectconsultlogger.data.Database
import com.example.perfectconsultlogger.data.Settings


class PhoneStateReceiver : BroadcastReceiver() {

    private val TAG = "PhoneStateReceiver"

    private lateinit var ownerNumber: String

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var database: Database? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        database = context?.let { Database.getInstance(it) }
        checkCall(intent)
    }


    private fun checkCall(intent: Intent?) {
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
        var timeStamp = System.currentTimeMillis()
        var eventType = checkEventType(state)
        var isIncoming = checkIncoming(state)
        var targetNumber: String? = getTargetNumber(intent)


        database?.getOwnerPhone(object : Database.DataListener<Settings> {
            override fun onData(data: Settings) {
                ownerNumber = data.value
                val callLog = targetNumber?.let { CallLog(ownerNumber, it, timeStamp, eventType, isIncoming) }
                callLog?.let { addToDatabase(it) }
            }
        })

        lastState = intState(state)
        Log.d(TAG, lastState.toString())
    }

    private fun checkIncoming(state: String?): Boolean {
        if (lastState == TelephonyManager.CALL_STATE_IDLE) {
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                return true
            }
        }
        return false
    }


    private fun getTargetNumber(intent: Intent?): String? {
        //works only for incoming calls TODO: make it work for outgoing calls
        return intent?.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
    }

    private fun checkEventType(state: String?): String {
        if(lastState == TelephonyManager.CALL_STATE_IDLE){
            if(state == TelephonyManager.EXTRA_STATE_RINGING || state == TelephonyManager.EXTRA_STATE_OFFHOOK){
                return "start"
            }
        }
        return "end"
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
