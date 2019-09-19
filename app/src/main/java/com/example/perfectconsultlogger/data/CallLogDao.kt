package com.example.perfectconsultlogger.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface CallLogDao {

    @Query("DELETE FROM call_logs")
    fun dropTable()

    @Query("SELECT * FROM call_logs")
    fun getAllLogs() : List<CallLog>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(callLog: CallLog): Long

    @Query("DELETE FROM call_logs")
    fun deleteAll()
}