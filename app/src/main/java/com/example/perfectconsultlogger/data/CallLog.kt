package com.example.perfectconsultlogger.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "call_logs")
class CallLog(
    @ColumnInfo(name = "owner_number") val owner_number: String,
    @ColumnInfo(name = "target_number") val target_number: String,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "duration") val duration: String,
    @ColumnInfo(name = "is_incoming") val isIncoming: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}