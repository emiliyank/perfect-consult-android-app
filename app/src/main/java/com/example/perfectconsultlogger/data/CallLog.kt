package com.example.perfectconsultlogger.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "call_logs")
class CallLog(@PrimaryKey @ColumnInfo(name = "id") val id : Int, @ColumnInfo(name = "phone_number") val number : String,
              @ColumnInfo(name = "start_time") val startTime : Date, @ColumnInfo(name = "end_time") val endTime : Date,
              @ColumnInfo(name = "is_incoming") val isIncoming : Boolean) {
}