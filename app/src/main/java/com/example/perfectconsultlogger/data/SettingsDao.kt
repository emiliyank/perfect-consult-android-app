package com.example.perfectconsultlogger.data

import androidx.room.*

@Dao
interface SettingsDao {

    @Query("DELETE FROM settings")
    fun dropTable()

    @Query("SELECT * FROM settings")
    fun getAllSettings(): List<Settings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(setting: Settings): Long

    @Update(onConflict = OnConflictStrategy.ROLLBACK)
    fun update(setting: Settings): Int

    @Query("DELETE FROM settings")
    fun deleteAll()

    @Query("DELETE FROM settings WHERE `key`!=:notifTokenSettingName")
    fun deleteAllButNotificationToken(notifTokenSettingName: String)

    @Query("SELECT * FROM settings WHERE `key`=:settingName")
    fun getSetting(settingName: String): Settings
}
