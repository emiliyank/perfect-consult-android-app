package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Room
import android.content.Context

class Database(context: Context) {

    private val TAG = "Database"

    private var allLogs : List<CallLog>? = null
    private val database: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app-db")
        .fallbackToDestructiveMigration().build()

    fun insertCallLog(callLog: CallLog){
        database.callLogDao().insert(callLog)
    }

    companion object{
        private var instance: Database? = null

        fun getInstance(context: Context): Database {
            if (instance == null) {
                instance = Database(context)
            }
            return instance as Database
        }
    }
}