package com.example.perfectconsultlogger.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "call_logs")
class CallLog(
    @ColumnInfo(name = "phone_number") val number: String,
    @ColumnInfo(name = "start_time") val startTime: String, @ColumnInfo(name = "end_time") val endTime: String,
    @ColumnInfo(name = "is_incoming") val isIncoming: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}