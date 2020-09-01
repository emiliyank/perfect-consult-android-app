package com.example.perfectconsultlogger.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Settings::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
}