package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Room
import android.content.Context
import android.os.AsyncTask

class Database(context: Context) {

    private val TAG = "Database"

    companion object {
        private var instance: Database? = null

        fun getInstance(context: Context): Database {
            if (instance == null) {
                instance = Database(context)
            }
            return instance as Database
        }
    }

    private val database: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app-db")
        .fallbackToDestructiveMigration().build()

    fun getLastSyncedCallTimestamp(listener: DataListener<Long>) {
        SettingRetrieveTask(Settings.OWNER_PHONE, database, object : DataListener<Settings?> {
            override fun onData(settings: Settings?) {
                listener.onData(settings?.value?.toLong() ?: 0L)
            }
        }).execute()
    }

    fun setLastSyncedCallTimestamp(value: Long) {
        val ownerPhone = Settings(Settings.OWNER_PHONE, value.toString())
        SettingsInsertTask(database).execute(ownerPhone)
    }

    fun getOwnerPhone(listener: DataListener<String>) {
        SettingRetrieveTask(Settings.OWNER_PHONE, database, object : DataListener<Settings?> {
            override fun onData(settings: Settings?) {
                listener.onData(settings?.value ?: "")
            }
        }).execute()
    }

    fun setOwnerPhone(value: String) {
        val ownerPhone = Settings(Settings.OWNER_PHONE, value)
        SettingsInsertTask(database).execute(ownerPhone)
    }

    private class SettingsInsertTask(val database: AppDatabase) : AsyncTask<Settings, Void, Void>() {
        override fun doInBackground(vararg params: Settings?): Void? {
            params[0]?.let { database.settingsDao().insert(it) }
            return null
        }
    }

    private class SettingRetrieveTask(
        val setting: String,
        val database: AppDatabase,
        val listener: DataListener<Settings?>
    ) : AsyncTask<Void, Void, Settings?>() {
        override fun doInBackground(vararg params: Void?): Settings? {
            return database.settingsDao().getSetting(setting)
        }

        override fun onPostExecute(phoneNumber: Settings?) {
            super.onPostExecute(phoneNumber)
            listener.onData(phoneNumber)
        }
    }

    interface DataListener<T> {
        fun onData(data: T)
    }
}