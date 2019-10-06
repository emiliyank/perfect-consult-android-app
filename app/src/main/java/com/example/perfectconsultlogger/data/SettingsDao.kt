package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface SettingsDao {

    @Query("DELETE FROM settings")
    fun dropTable()

    @Query("SELECT * FROM settings")
    fun getAllSettings() : List<Settings>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(setting: Settings): Long

    @Query("DELETE FROM settings")
    fun deleteAll()

    @Query("SELECT * FROM settings WHERE `key`='phone'")
    fun getOwnerPhone(): Settings
}