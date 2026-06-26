package com.example.resturant.feature.finance.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class RecordType(val label: String) {
    EXPENSE("خرج‌کرد"),
    DEBT_GIVEN("بدهی پرداختی"),
    WITHDRAWAL("برداشت از حساب"),
    INSTALLMENT("اقساط")
}

@Entity(tableName = "finance_records")
data class FinanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val section: Int,
    val type: RecordType,
    val amount: Double,
    val date: Long,
    val description: String = "",
    val totalInstallment: Double? = null,
    val months: Int? = null,
    val remaining: Double? = null
)

class RecordTypeConverter {
    @TypeConverter
    fun fromType(type: RecordType): String = type.name

    @TypeConverter
    fun toType(value: String): RecordType = RecordType.valueOf(value)
}