package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [CallLog::class, Settings::class], version = 3)
abstract  class AppDatabase : RoomDatabase() {
    abstract fun callLogDao(): CallLogDao
    abstract fun settingsDao(): SettingsDao
}