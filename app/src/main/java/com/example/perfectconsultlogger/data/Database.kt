package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Room
import android.content.Context
import android.os.AsyncTask

class Database(context: Context) {

    private val TAG = "Database"

    private val database: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app-db")
        .fallbackToDestructiveMigration().build()

    fun dropDatabase(){
        database.callLogDao().dropTable()
    }

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

    private class InsertTask(val database: AppDatabase): AsyncTask<CallLog, Void, Void>(){

        override fun doInBackground(vararg params: CallLog?): Void? {
            params[0]?.let { database.callLogDao().insert(it) }
            return null
        }

    }

    private class RetrieveTask(val listener: DataListener<List<CallLog>>, val database: AppDatabase) : AsyncTask<Context, Void, List<CallLog>>(){

        override fun doInBackground(vararg params: Context?): List<CallLog> {
            return database.callLogDao().getAllLogs()
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