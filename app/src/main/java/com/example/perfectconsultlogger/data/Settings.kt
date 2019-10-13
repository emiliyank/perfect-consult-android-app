package com.example.perfectconsultlogger.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "settings")
class Settings(
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "value") val value: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}