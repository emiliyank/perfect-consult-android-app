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

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var database: Database? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        database = context?.let { Database.getInstance(it) }
        checkCall(intent)
    }

    private fun checkCall(intent: Intent?) {
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
        val timeStamp = System.currentTimeMillis()
        val eventType = checkEventType(state)
        val isIncoming = checkIncoming(state)
        val targetNumber: String? = getTargetNumber(intent)


        database?.getOwnerPhone(object : Database.DataListener<Settings> {
            override fun onData(data: Settings) {
                val ownerNumber = data.value
                val callLog = targetNumber?.let { CallLog(ownerNumber, it, timeStamp, eventType, isIncoming) }
                callLog?.let { addToDatabase(it) }
            }
        })

        if(state != null) {
            Log.d(TAG, "$state other side:$targetNumber")
        } else {
            Log.d(TAG, "State is NULL, other side:$targetNumber")
        }
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

}
