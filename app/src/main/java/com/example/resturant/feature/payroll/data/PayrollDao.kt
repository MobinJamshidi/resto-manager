package com.example.resturant.feature.payroll.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PayrollDao {

    @Query("SELECT * FROM payroll_adjustments WHERE employeeId = :employeeId ORDER BY date DESC")
    fun getForEmployee(employeeId: Long): Flow<List<PayrollAdjustment>>

    @Insert
    suspend fun insert(adjustment: PayrollAdjustment)

    @Delete
    suspend fun delete(adjustment: PayrollAdjustment)
}