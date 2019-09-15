package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [CallLog::class], version = 1)
abstract  class AppDatabase : RoomDatabase() {
    abstract fun callLogDao(): CallLogDao
}