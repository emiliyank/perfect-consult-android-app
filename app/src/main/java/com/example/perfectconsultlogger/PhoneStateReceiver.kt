package com.example.perfectconsultlogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.perfectconsultlogger.data.CallLog
import com.example.perfectconsultlogger.data.Database
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
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

        if (lastState == TelephonyManager.CALL_STATE_IDLE) {
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                processIncomingCall(intent)
            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //TODO: extract outgoing call target_number
                val number = intent?.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            }
        }
        Log.d(TAG, state)
        lastState = intState(state)
    }

    private fun processIncomingCall(intent: Intent?) {
        val targetNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        val startTime = getStartTime()
        val duration = getDuration(intent)
        val callLog = targetNumber?.let { CallLog("", it, startTime, duration.toString(), true) }
        if (callLog != null) {
            database?.insertCallLog(callLog)
        }
    }

    private fun getDuration(intent: Intent?): Int {
        var state: String?
        val delay = 1000
        val period = 1000
        val timer = Timer()
        var durationInSeconds = 0

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
                Log.d(TAG,"getDuration: $state")

                if(state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    durationInSeconds++
                }
            }
        }, delay.toLong(), period.toLong())
        return durationInSeconds
    }

    private fun getStartTime(): String {
        //TODO: check phone states and obtain start and end time
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return current.format(formatter)
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
