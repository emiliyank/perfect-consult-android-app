package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Room
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import kotlinx.coroutines.*
import java.util.*

class Database(context: Context) {

    private val TAG = "Database"

    private val database: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app-db")
        .fallbackToDestructiveMigration().build()

    fun insertCallLog(callLog: CallLog) = InsertTask(database).execute(callLog)

    fun getAll(context: Context, listener: DataListener<List<CallLog>>){
        RetrieveTask(listener, database).execute(context)
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

    private class InsertTask(database: AppDatabase): AsyncTask<CallLog, Void, Void>(){

        val database = database

        override fun doInBackground(vararg params: CallLog?): Void? {
            params[0]?.let { database.callLogDao().insert(it) }
            return null
        }



    }

    private class RetrieveTask(listener: DataListener<List<CallLog>>, database: AppDatabase) : AsyncTask<Context, Void, List<CallLog>>(){

        val listener = listener
        val database = database

        override fun doInBackground(vararg params: Context?): List<CallLog> {
            val allLogs = database.callLogDao().getAllLogs()
            return allLogs
        }

        override fun onPostExecute(logs: List<CallLog>?) {
            super.onPostExecute(logs)
            if (logs != null) {
                listener.onData(logs)
            }
        }

    }

    interface DataListener<T> {
        fun onData(data: T)
    }
}