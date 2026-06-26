package com.example.resturant.feature.attendance.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance",
    indices = [Index(value = ["employeeId", "dayKey"], unique = true)]
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: Long,
    val dayKey: String,
    val isPresent: Boolean,
    val entryTime: String = "",
    val exitTime: String = ""
)