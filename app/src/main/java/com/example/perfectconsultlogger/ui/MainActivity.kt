package com.example.perfectconsultlogger.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.perfectconsultlogger.BuildConfig
import com.example.perfectconsultlogger.PushNotificationReceiver.Companion.NOTIFICATION_PHONE_NUMBER_PAYLOAD
import com.example.perfectconsultlogger.R
import com.example.perfectconsultlogger.data.Database
import com.example.perfectconsultlogger.data.remote.ApiWrapper
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


private const val PERMISSION_REQUEST_READ_PHONE_STATE = 1
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var database: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Database.getInstance(this)
        setContentView(R.layout.activity_main)
        askForPermissions()
        setLastCallTimestamp()
        setupDebugOptions()
        callClient()
    }

    private fun setLastCallTimestamp() {
        database.getLastSyncedCallTimestamp(object: Database.DataListener<Long> {
            override fun onData(data: Long) {
                if(data == 0L) {
                    database.setLastSyncedCallTimestamp(System.currentTimeMillis())
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        ApiWrapper.getInstance(this).logoutWithToken(object: ApiWrapper.Callback<Boolean> {
            override fun onDataReceived(data: Boolean) {
                if(data) {
                    database.deleteUserData()
                    finish()
                } else {
                    showError(getString(R.string.logout_unsuccessful))
                }
            }

            override fun onError(error: String) {
                showError(getString(R.string.logout_unsuccessful))
            }
        })
    }

    private fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    private fun askForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_CALL_LOG, Manifest.permission.INTERNET),
                PERMISSION_REQUEST_READ_PHONE_STATE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        askForPermissions()
    }

    private fun setupDebugOptions() {
        if (BuildConfig.DEBUG) {
            btn_export_database.visibility = View.VISIBLE
            btn_export_database.setOnClickListener {
                txt_database.setText(getCallDetails())
                scroll_database.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCallDetails(): String {
        val sb = StringBuffer();
        val managedCursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 2;");
        val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
        val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
        sb.append("Call Log :")
        while (managedCursor.moveToNext()) {
            val phNumber = managedCursor.getString(number)
            val callType = managedCursor.getString(type)
            val callTimestampText = managedCursor.getString(date)
            val callDate = Date(callTimestampText.toLong())
            val callDuration = managedCursor.getString(duration)
            var callTypeText: String? = null
            when (Integer.parseInt(callType)) {
                CallLog.Calls.OUTGOING_TYPE -> callTypeText = "OUTGOING"
                CallLog.Calls.INCOMING_TYPE -> callTypeText = "INCOMING"
                CallLog.Calls.MISSED_TYPE -> callTypeText = "MISSED"
                CallLog.Calls.VOICEMAIL_TYPE -> callTypeText = "VOICEMAIL"
                CallLog.Calls.REJECTED_TYPE -> callTypeText = "REJECTED"
                CallLog.Calls.BLOCKED_TYPE -> callTypeText = "BLOCKED"
                CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> callTypeText = "EXTERNALLY_ANSWERED"
            }
            sb.append("\nPhone Number:--- $phNumber \nCall Type:--- $callTypeText \nCall Date:--- $callDate \nCall duration in sec :--- $callDuration");
            sb.append("\n----------------------------------");
        }
        managedCursor.close()
        return sb.toString()
    }

    private fun callClient() {
        intent.extras?.getString(NOTIFICATION_PHONE_NUMBER_PAYLOAD)?.let {
            initiateCall(it)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initiateCall(phonenumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phonenumber")
        startActivity(intent)
    }
}
