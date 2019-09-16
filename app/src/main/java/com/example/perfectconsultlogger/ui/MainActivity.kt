package com.example.perfectconsultlogger.ui

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.perfectconsultlogger.R
import com.example.perfectconsultlogger.data.CallLog
import com.example.perfectconsultlogger.data.Database
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_PROCESS_OUTGOING_CALLS = 0
    private val PERMISSION_REQUEST_READ_PHONE_STATE = 1

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var logAdapter: CallLogAdapter
    private lateinit var recyclerView: RecyclerView
    private var allLogs: List<CallLog> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askForPermissions()

        Database.getInstance(applicationContext).getAll(object : Database.DataListener<List<CallLog>> {
            override fun onData(data: List<CallLog>) {
                allLogs = data
            }
        })

        logAdapter = CallLogAdapter(allLogs)
        viewManager = LinearLayoutManager(this)
        recyclerView = findViewById<RecyclerView>(R.id.rec_call_log).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = logAdapter

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
