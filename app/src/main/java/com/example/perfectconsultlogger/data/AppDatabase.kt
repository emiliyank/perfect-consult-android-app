package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [Settings::class], version = 4)
abstract  class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
}