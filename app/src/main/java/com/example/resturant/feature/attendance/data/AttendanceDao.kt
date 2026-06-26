package com.example.resturant.feature.attendance.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM attendance WHERE dayKey = :dayKey")
    fun getForDay(dayKey: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId")
    fun getForEmployee(employeeId: Long): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: AttendanceRecord)

    @Query("DELETE FROM attendance WHERE employeeId = :employeeId")
    suspend fun clearForEmployee(employeeId: Long)
}