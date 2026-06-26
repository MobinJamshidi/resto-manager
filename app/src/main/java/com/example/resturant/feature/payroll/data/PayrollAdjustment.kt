package com.example.resturant.feature.payroll.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payroll_adjustments")
data class PayrollAdjustment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: Long,
    val amount: Double,
    val note: String,
    val date: Long
)