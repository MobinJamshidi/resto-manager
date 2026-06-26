package com.example.resturant.feature.finance.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {

    @Query("SELECT * FROM finance_records WHERE section = :section ORDER BY date DESC, id DESC")
    fun getBySection(section: Int): Flow<List<FinanceRecord>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM finance_records WHERE section = :section AND type = 'DEBT_GIVEN'")
    fun debtTotalForSection(section: Int): Flow<Double>

    @Query("SELECT * FROM finance_records WHERE section = :section AND type = 'DEBT_GIVEN' AND date >= :start AND date < :end ORDER BY date ASC")
    fun debtsBetween(section: Int, start: Long, end: Long): Flow<List<FinanceRecord>>

    @Insert
    suspend fun insert(record: FinanceRecord): Long

    @Update
    suspend fun update(record: FinanceRecord)

    @Delete
    suspend fun delete(record: FinanceRecord)
}