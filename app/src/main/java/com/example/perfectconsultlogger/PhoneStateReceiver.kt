package com.example.perfectconsultlogger

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.CallLog
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import com.example.perfectconsultlogger.data.CallDetails
import com.example.perfectconsultlogger.data.Database
import com.example.perfectconsultlogger.data.remote.ApiWrapper
import com.example.perfectconsultlogger.data.remote.models.CallRequest
import java.util.*
import kotlin.collections.ArrayList

class PhoneStateReceiver : BroadcastReceiver() {

    private val TAG = "PhoneStateReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        val nonNullIntent = intent ?: return
        val nonNullContext = context ?: return
        val database = Database.getInstance(nonNullContext)
        val state = intent.extras.getString(TelephonyManager.EXTRA_STATE)
        if (nonNullIntent.action == "android.intent.action.PHONE_STATE" || nonNullIntent.action == "android.intent.action.NEW_OUTGOING_CALL") {
            if (state == TelephonyManager.EXTRA_STATE_IDLE) { //Phone call has ended
                if (ContextCompat.checkSelfPermission(
                        nonNullContext,
                        Manifest.permission.READ_CALL_LOG
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    syncUnsyncedCalls(database, nonNullContext)

                } else {
                    //TODO notify server for no permission granted
                }
            }
        }

    }

    private fun syncUnsyncedCalls(
        database: Database,
        nonNullContext: Context
    ) {
        database.getLastSyncedCallTimestamp(object : Database.DataListener<Long> {
            override fun onData(lastSyncedCallTimestamp: Long) {
                database.getOwnerPhone(object : Database.DataListener<String> {
                    override fun onData(phoneNumber: String) {
                        val unsyncedCalls = getUnsyncedCalls(nonNullContext, lastSyncedCallTimestamp)
                        var latestSyncedCallTimestamp = 0L
                        for (call in unsyncedCalls) {
                            syncCall(call, phoneNumber)
                            latestSyncedCallTimestamp = call.callStartTimestamp

                        }
                        database.setLastSyncedCallTimestamp(latestSyncedCallTimestamp)
                    }
                })
            }
        })
    }

    private fun syncCall(call: CallDetails, ownerNumber: String) {
        val apiWrapper = ApiWrapper()
        val otherNumber = call.phoneNumber
        val startTimestamp = call.callStartTimestamp
        val duration = call.callDuration
        val callType = call.callType
        val callRequest = CallRequest(
            ownerNumber,
            otherNumber,
            Date(startTimestamp).toString(),
            duration,
            callType
        )
        apiWrapper.createCallLogAsync(callRequest)
    }

    @SuppressLint("MissingPermission")
    private fun getUnsyncedCalls(context: Context, sinceTimestamp: Long): List<CallDetails> {
        val unsyncedCalls = ArrayList<CallDetails>()
        val managedCursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            android.provider.CallLog.Calls.DATE + " >= ?",
            arrayOf(sinceTimestamp.toString()),
            android.provider.CallLog.Calls.DATE
        );
        val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        val date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        while (managedCursor.moveToNext()) {
            var callTypeText = ""
            when (Integer.parseInt(managedCursor.getString(type))) {
                CallLog.Calls.OUTGOING_TYPE -> callTypeText = "OUTGOING";
                CallLog.Calls.INCOMING_TYPE -> callTypeText = "INCOMING";
                CallLog.Calls.MISSED_TYPE -> callTypeText = "MISSED";
                CallLog.Calls.VOICEMAIL_TYPE -> callTypeText = "VOICEMAIL";
                CallLog.Calls.REJECTED_TYPE -> callTypeText = "REJECTED";
                CallLog.Calls.BLOCKED_TYPE -> callTypeText = "BLOCKED";
                CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> callTypeText = "EXTERNALLY_ANSWERED";
            }
            unsyncedCalls.add(
                CallDetails(
                    managedCursor.getString(number),
                    callTypeText,
                    managedCursor.getString(date).toLong(),
                    managedCursor.getString(duration).toLong()
                )
            )
        }
        return unsyncedCalls
    }
}
