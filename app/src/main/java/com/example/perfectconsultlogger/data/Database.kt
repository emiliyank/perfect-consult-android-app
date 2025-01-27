package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Room
import android.content.Context
import android.os.AsyncTask
import java.util.concurrent.Executors

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
        SettingRetrieveTask(Settings.LAST_SYNCED_CALL_TIMESTAMP, database, object : DataListener<Settings?> {
            override fun onData(settings: Settings?) {
                listener.onData(settings?.value?.toLong() ?: 0L)
            }
        }).execute()
    }

    fun setLastSyncedCallTimestamp(value: Long) {
        val timestamp = Settings(Settings.LAST_SYNCED_CALL_TIMESTAMP, value.toString())
        SettingsInsertTask(database).execute(timestamp)
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

    fun getUserToken(listener: DataListener<String>) {
        SettingRetrieveTask(Settings.USER_TOKEN, database, object : DataListener<Settings?> {
            override fun onData(settings: Settings?) {
                listener.onData(settings?.value ?: "")
            }
        }).execute()
    }

    fun deleteUserData() {
        SettingsDeleteTask(database).execute()
    }

    fun setUserToken(value: String) {
        val ownerPhone = Settings(Settings.USER_TOKEN, value)
        SettingsInsertTask(database).execute(ownerPhone)
    }

    fun setNotificationToken(notificationToken: String) {
        val ownerPhone = Settings(Settings.NOTIFICATION_TOKEN, notificationToken)
        SettingsInsertTask(database).execute(ownerPhone)
    }

    fun getNotificationToken(listener: DataListener<String>) {
        SettingRetrieveTask(Settings.NOTIFICATION_TOKEN, database, object : DataListener<Settings?> {
            override fun onData(settings: Settings?) {
                listener.onData(settings?.value ?: "")
            }
        }).execute()
    }

    private class SettingsInsertTask(val database: AppDatabase) : AsyncTask<Settings, Void, Void>() {
        override fun doInBackground(vararg params: Settings?): Void? {
            params[0]?.let { database.settingsDao().insert(it) }
            return null
        }
    }

    private class SettingsDeleteTask(val database: AppDatabase) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            database.settingsDao().deleteAllButNotificationToken(Settings.NOTIFICATION_TOKEN)
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

        override fun onPostExecute(setting: Settings?) {
            super.onPostExecute(setting)
            listener.onData(setting)
        }
    }

    interface DataListener<T> {
        fun onData(data: T)
    }
}
