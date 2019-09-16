package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Room
import android.content.Context
import kotlinx.coroutines.*

class Database(context: Context) {

    private val TAG = "Database"

    private var allLogs: List<CallLog>? = null
    private val database: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app-db")
        .fallbackToDestructiveMigration().build()

    fun insertCallLog(callLog: CallLog) = database.callLogDao().insert(callLog)

    fun getAll(listener: DataListener<List<CallLog>>){
        allLogs = database.callLogDao().getAllLogs()
        listener.onData(allLogs!!)
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