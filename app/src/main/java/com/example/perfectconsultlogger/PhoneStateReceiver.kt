package com.example.perfectconsultlogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.CallLog
import android.telephony.TelephonyManager
import android.util.Log
import com.example.perfectconsultlogger.data.Database
import com.example.perfectconsultlogger.data.Settings
import java.util.*

class PhoneStateReceiver : BroadcastReceiver() {

    private val TAG = "PhoneStateReceiver"

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var database: Database? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if(it.action == "android.intent.action.PHONE_STATE" || it.action == "android.intent.action.NEW_OUTGOING_CALL") {
                database = context?.let { Database.getInstance(it) }

                if (lastState == TelephonyManager.CALL_STATE_IDLE) { //Phone call has ended

                }
            }
        }
    }

    private fun getCallDetails(context: Context): String {
        val sb = StringBuffer();
        val managedCursor = context.managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 2;");
        val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        val date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Log :");
        while (managedCursor.moveToNext()) {
            val phNumber = managedCursor.getString(number);
            val callType = managedCursor.getString(type);
            val callTimestampText = managedCursor.getString(date);
            val callDate = Date(callTimestampText.toLong())
            val callDuration = managedCursor.getString(duration);
            var callTypeText: String? = null
            when (Integer.parseInt(callType)) {
                CallLog.Calls.OUTGOING_TYPE -> callTypeText = "OUTGOING";
                CallLog.Calls.INCOMING_TYPE -> callTypeText = "INCOMING";
                CallLog.Calls.MISSED_TYPE -> callTypeText = "MISSED";
                CallLog.Calls.VOICEMAIL_TYPE -> callTypeText = "VOICEMAIL";
                CallLog.Calls.REJECTED_TYPE -> callTypeText = "REJECTED";
                CallLog.Calls.BLOCKED_TYPE -> callTypeText = "BLOCKED";
                CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> callTypeText = "EXTERNALLY_ANSWERED";
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + callTypeText + " \nCall Date:--- " + callDate + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
        }
        return sb.toString()
    }

}
