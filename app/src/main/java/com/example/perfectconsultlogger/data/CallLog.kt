package com.example.perfectconsultlogger.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

public const val CALL_TYPE_START = "call type start"
public const val CALL_TYPE_END = "call type end"

@Entity(tableName = "call_logs")
class CallLog(
    @ColumnInfo(name = "owner_number") val owner_number: String,
    @ColumnInfo(name = "target_number") val target_number: String,
    @ColumnInfo(name = "time_stamp") val timeStamp: Long,
    @ColumnInfo(name = "call_event_type") val callEventType: String,
    @ColumnInfo(name = "is_incoming") val isIncoming: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}