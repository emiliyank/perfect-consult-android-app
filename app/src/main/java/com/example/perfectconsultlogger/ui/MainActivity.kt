package com.example.perfectconsultlogger.ui

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.example.perfectconsultlogger.R
import com.example.perfectconsultlogger.data.CallLog
import com.example.perfectconsultlogger.data.Database
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import android.content.Context.TELEPHONY_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.telephony.TelephonyManager
import android.annotation.SuppressLint
import android.content.Context

private const val PERMISSION_REQUEST_PROCESS_OUTGOING_CALLS = 0
private const val PERMISSION_REQUEST_READ_PHONE_STATE = 1
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
        if(!tMgr.line1Number.isNullOrBlank()) {
            database.setOwnerPhone(tMgr.line1Number)
            edt_phone.setText(tMgr.line1Number)
        }
    }
}
