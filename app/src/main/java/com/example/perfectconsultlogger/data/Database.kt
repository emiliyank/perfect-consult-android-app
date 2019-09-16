package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Room
import android.content.Context
import android.os.AsyncTask
import kotlinx.coroutines.*
import java.util.*

class Database(context: Context) {

    private val TAG = "Database"

    private var allLogs: List<CallLog>? = null
    private val database: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app-db")
        .fallbackToDestructiveMigration().build()

    fun insertCallLog(callLog: CallLog) = database.callLogDao().insert(callLog)

    fun getAll(listener: DataListener<List<CallLog>>){
        object : AsyncTask<Context, Void, List<CallLog>>() {

            override fun doInBackground(vararg voids: Context): List<CallLog> {
                return database.callLogDao().getAllLogs()
            }

            override fun onPostExecute(logs: List<CallLog>) {
                super.onPostExecute(logs)
                listener.onData(logs)
            }
        }.execute()
    }

    companion object {
        private var instance: Database? = null

        fun getInstance(context: Context): Database {
            if (instance == null) {
                instance = Database(context)
            }
            return instance as Database
        }
    }

    interface DataListener<T> {
        fun onData(data: T)
    }
}