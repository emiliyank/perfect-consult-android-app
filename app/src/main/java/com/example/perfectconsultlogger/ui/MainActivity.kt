package com.example.perfectconsultlogger.ui

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.example.perfectconsultlogger.R
import com.example.perfectconsultlogger.data.CallLog
import com.example.perfectconsultlogger.data.Database
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_PROCESS_OUTGOING_CALLS = 0
    private val PERMISSION_REQUEST_READ_PHONE_STATE = 1

    private lateinit var adapter: CallLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askForPermissions()

        GlobalScope.launch {
            Database.getInstance(applicationContext).getAll(object : Database.DataListener<List<CallLog>> {
                override fun onData(data: List<CallLog>) {
                    adapter = CallLogAdapter(data)
                }
            })
        }
    }

    private fun askForPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                PERMISSION_REQUEST_READ_PHONE_STATE
            )

        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.PROCESS_OUTGOING_CALLS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.PROCESS_OUTGOING_CALLS),
                PERMISSION_REQUEST_PROCESS_OUTGOING_CALLS
            )

        }
    }
}
