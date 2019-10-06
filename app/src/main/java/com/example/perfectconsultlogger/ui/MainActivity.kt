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


class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_PROCESS_OUTGOING_CALLS = 0
    private val PERMISSION_REQUEST_READ_PHONE_STATE = 1
    private val TAG = "MainActivity"

    lateinit var ownerPhone: String
    lateinit var database: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Database.getInstance(this)
        setContentView(R.layout.activity_main)
        askForPermissions()
        btn_continue.setOnClickListener {
            ownerPhone = edt_phone.text.toString()
            database.setOwnerPhone(ownerPhone)
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
