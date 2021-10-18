package com.example.perfectconsultlogger

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.CallLog
import android.text.format.DateFormat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.perfectconsultlogger.data.CallDetails
import com.example.perfectconsultlogger.data.Database
import com.example.perfectconsultlogger.data.remote.ApiWrapper
import com.example.perfectconsultlogger.data.remote.models.CallRequest
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class CallLogsService : Service() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, TAG)
            .setContentText(NOTIFICATION_TEXT)
            .setSmallIcon(R.drawable.ic_pc_logo)
            .setSound(null)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    fun startService(cnt: Context) {
        context = cnt
        val startIntent = Intent(context, CallLogsService::class.java)
        startIntent.putExtra(INTENT_EXTRA, NOTIFICATION_TEXT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.let { ContextCompat.startForegroundService(it, startIntent) }
            firebaseAnalyticsLogEven(START_SERVICE_FOREGROUND)
        } else {
            context?.startService(startIntent)
            firebaseAnalyticsLogEven(START_SERVICE)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        database = context?.let { Database.getInstance(it) }
        database?.let { syncCallLog(it) }
        setIsServiceRunning(true.toString())
        firebaseAnalyticsLogEven(ON_START_COMAND)
        return START_STICKY
    }

    fun stopService(context: Context) {
        handler.post(object : Runnable {
            override fun run() {
                handler.removeCallbacks(this)
            }
        })
        val stopIntent = Intent(context, CallLogsService::class.java)
        context.stopService(stopIntent)
        setIsServiceRunning(false.toString())
        firebaseAnalyticsLogEven(STOP_SERVICE)
    }

    private fun setIsServiceRunning(value: String) {
        database?.setIsServiceRunning(value)
    }

    override fun onDestroy() {
        getSystemService(NotificationManager::class.java).also {
            it.cancel(NOTIFICATION_ID)
        }
        firebaseAnalyticsLogEven(DESTROY_SERVICE)
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                TAG, "Foreground Service",
                NotificationManager.IMPORTANCE_LOW
            )
            serviceChannel.setSound(null, null)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun syncCallLog(database: Database) {
        handler.post(object : Runnable {
            override fun run() {
                Log.e(TAG, "check for unsync calls")
                context?.let { syncUnsyncedCalls(database, it) }
                handler.postDelayed(this, TIMER_SCHEDULE)
            }
        })
        firebaseAnalyticsLogEven(SYNC_CALL_LOG)
    }

    // TODO: 15.10.2021 г. log custom evenet
    private fun syncUnsyncedCalls(
        database: Database,
        nonNullContext: Context
    ) {
        database.getLastSyncedCallTimestamp(object : Database.DataListener<Long> {
            override fun onData(lastSyncedCallTimestamp: Long) {
                database.getUserToken(object : Database.DataListener<String> {
                    override fun onData(apiToken: String) {
                        val unsyncedCalls =
                            getUnsyncedCalls(nonNullContext, lastSyncedCallTimestamp)
                        var latestSyncedCallTimestamp = lastSyncedCallTimestamp
                        for (call in unsyncedCalls) {
                            syncCall(call, apiToken, nonNullContext)
                            latestSyncedCallTimestamp = call.callStartTimestamp
                        }

                        database.updateLastSyncedCallTimestamp(latestSyncedCallTimestamp)
                    }
                })
            }
        })
        firebaseAnalyticsLogEven(SYNC_UNSYNCED_CALLS)
    }

    private fun syncCall(call: CallDetails, apiToken: String, context: Context) {
        val apiWrapper = ApiWrapper(context)
        val otherNumber = call.phoneNumber
        val duration = call.callDuration
        val callType = call.callType
        val startTimestamp = call.callStartTimestamp
        val startDate = DateFormat.format("yyyy-MM-dd HH:mm:ss", Date(startTimestamp)).toString()

        val callRequest = CallRequest(
            apiToken,
            otherNumber,
            startDate,
            duration,
            callType
        )
        Log.e(TAG, "${callRequest.phoneNumber} ${callRequest.startTime} ${callRequest.duration}")

        apiWrapper.createCallLogAsync(callRequest)
        firebaseAnalyticsLogEven(SYNG_CALL)
    }

    @SuppressLint("MissingPermission")
    private fun getUnsyncedCalls(context: Context, sinceTimestamp: Long): List<CallDetails> {
        val unsyncedCalls = ArrayList<CallDetails>()
        val managedCursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            android.provider.CallLog.Calls.DATE + " > ?",
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
        firebaseAnalyticsLogEven(GET_UNSYNCED_CALLS)
        return unsyncedCalls
    }

    fun firebaseAnalyticsLogEven(event: String) {
        val bundle = Bundle()
        firebaseAnalytics = Firebase.analytics
        bundle.putString(
            FirebaseAnalytics.Param.METHOD,
            event
        )
        firebaseAnalytics.logEvent(event, bundle)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val TAG = "CallLogsService"
        private const val NOTIFICATION_TEXT = "Perfect Consult is running..."
        private const val NOTIFICATION_ID = 1
        private const val INTENT_EXTRA = "inputExtra"
        private const val ACTION_NOTIFICATION = "ACTION_NOTIFICATION"
        private const val TIMER_SCHEDULE = 60 * 1000L
        private const val START_SERVICE = "start_service"
        private const val START_SERVICE_FOREGROUND = "start_service_foreground"
        private const val ON_START_COMAND = "on_start_comand"
        private const val STOP_SERVICE = "stop_service"
        private const val DESTROY_SERVICE = "destroy_service"
        private const val SYNC_CALL_LOG = "sync_call_log"
        private const val SYNC_UNSYNCED_CALLS = "sync_unsynced_calls"
        private const val SYNG_CALL = "syng_call"
        private const val GET_UNSYNCED_CALLS = "get_unsynced_calls"
        private var context: Context? = null
        val handler = Handler()
        private var database: Database? = null
    }

}