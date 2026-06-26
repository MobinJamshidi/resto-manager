package com.example.resturant.feature.finance.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PartnerDao {

    @Query("SELECT * FROM partners ORDER BY id ASC")
    fun getAll(): Flow<List<Partner>>

    @Insert
    suspend fun insert(partner: Partner): Long

    @Delete
    suspend fun delete(partner: Partner)
}