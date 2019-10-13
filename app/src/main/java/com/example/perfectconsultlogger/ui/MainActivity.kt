package com.example.perfectconsultlogger.ui

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.example.perfectconsultlogger.R
import com.example.perfectconsultlogger.data.Database
import kotlinx.android.synthetic.main.activity_main.*
import android.telephony.TelephonyManager
import android.annotation.SuppressLint
import android.content.Context
import android.provider.CallLog
import com.example.perfectconsultlogger.BuildConfig
import java.util.*

private const val PERMISSION_REQUEST_PROCESS_OUTGOING_CALLS = 0
private const val PERMISSION_REQUEST_READ_PHONE_STATE = 1
private const val PERMISSION_REQUEST_READ_CALL_LOG = 2
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var database: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Database.getInstance(this)
        setContentView(R.layout.activity_main)
        askForPermissions()
        btn_continue.setOnClickListener {
            database.setOwnerPhone(edt_phone.text.toString())
        }
        setupDebugOptions()
    }

    private fun askForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                PERMISSION_REQUEST_READ_PHONE_STATE
            )
        } else {
            retrievePhoneNumber()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.PROCESS_OUTGOING_CALLS),
                PERMISSION_REQUEST_PROCESS_OUTGOING_CALLS
            )
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CALL_LOG),
                PERMISSION_REQUEST_READ_CALL_LOG
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_READ_PHONE_STATE &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            retrievePhoneNumber()
        }
    }

    @SuppressLint("MissingPermission")
    private fun retrievePhoneNumber() {
        val tMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (!tMgr.line1Number.isNullOrBlank()) {
            database.setOwnerPhone(tMgr.line1Number)
            edt_phone.setText(tMgr.line1Number)
        }
    }

    private fun setupDebugOptions() {
        if (BuildConfig.DEBUG) {
            btn_export_database.visibility = View.VISIBLE
            btn_export_database.setOnClickListener {
//                exportDb()
                txt_database.setText(getCallDetails())
                scroll_database.visibility = View.VISIBLE
            }
        }
    }

    private fun exportDb() {
        database.getAll(this, object : Database.DataListener<List<com.example.perfectconsultlogger.data.CallLog>> {
            override fun onData(data: List<com.example.perfectconsultlogger.data.CallLog>) {
                val sb = StringBuffer()
                for (log in data) {
                    sb.append(log)
                    sb.append("\n")
                }
                txt_database.setText(sb.toString())
                scroll_database.visibility = View.VISIBLE
            }
        })
    }

    private fun getCallDetails(): String {
        val sb = StringBuffer();
        val managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 2;");
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
