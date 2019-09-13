package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Room
import android.content.Context

class Database(context: Context) {

    private val TAG = "Database"

    private var instance: Database? = null
    private val database: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app-db")
        .fallbackToDestructiveMigration().build()


    fun getInstance(context: Context): Database {
        if (instance == null) {
            instance = Database(context)
        }
        return instance as Database
    }
}