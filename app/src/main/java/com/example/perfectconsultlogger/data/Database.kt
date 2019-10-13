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
        database.settingsDao().dropTable()
    }

    fun insertCallLog(callLog: CallLog) = CallLogInsertTask(database).execute(callLog)

    fun getAll(context: Context, listener: DataListener<List<CallLog>>){
        CallLogRetrieveTask(listener, database).execute(context)
    }

    fun getOwnerPhone(listener: DataListener<Settings>){
        PhoneNumberRetrieveTask(listener, database).execute()
    }

    fun setOwnerPhone(value: String){
        val ownerPhone = Settings("phone", value)
        SettingsInsertTask(database).execute(ownerPhone)
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

    private class SettingsInsertTask(val database: AppDatabase): AsyncTask<Settings, Void, Void>(){
        override fun doInBackground(vararg params: Settings?): Void? {
            params[0]?.let { database.settingsDao().insert(it) }
            return null
        }


    }

    private class PhoneNumberRetrieveTask(val listener: DataListener<Settings>, val database: AppDatabase) : AsyncTask<Void, Void, Settings>(){
        override fun doInBackground(vararg params: Void?): Settings {
            return database.settingsDao().getOwnerPhone()
        }

        override fun onPostExecute(phoneNumber: Settings) {
            super.onPostExecute(phoneNumber)
            listener.onData(phoneNumber)
        }

    }

    private class CallLogInsertTask(val database: AppDatabase): AsyncTask<CallLog, Void, Void>(){

        override fun doInBackground(vararg params: CallLog?): Void? {
            params[0]?.let { database.callLogDao().insert(it) }
            return null
        }

    }

    private class CallLogRetrieveTask(val listener: DataListener<List<CallLog>>, val database: AppDatabase) : AsyncTask<Context, Void, List<CallLog>>(){

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