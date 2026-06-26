package com.example.resturant.feature.finance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partners")
data class Partner(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)