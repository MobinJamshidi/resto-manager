package com.example.resturant.feature.employee.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class Position(val label: String) {
    CHEF("Chef"), WAITER("Waiter"), CASHIER("Cashier"),
    DISHWASHER("Dishwasher"), CLEANER("Cleaner"), DELIVERY("Delivery"), MANAGER("Manager")
}

enum class MaritalStatus(val label: String) {
    SINGLE("Single"), MARRIED("Married")
}

enum class GuaranteeType(val label: String) {
    PROMISSORY_NOTE("Promissory note"), CHECK("Check"),
    CASH_DEPOSIT("Cash deposit"), GUARANTOR("Guarantor"), NONE("None")
}

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val age: Int,
    val phoneNumber: String,
    val emergencyPhone: String,
    val position: Position,
    val nationalId: String,
    val maritalStatus: MaritalStatus,
    val address: String,
    val salary: Double,
    val dailyHours: Int = 0,
    val startDate: Long,
    val workExperience: String,
    val hasHealthCard: Boolean,
    val healthCardExpiration: Long? = null,
    val guaranteeType: GuaranteeType
)

class EmployeeConverters {
    @TypeConverter fun fromPosition(p: Position): String = p.name
    @TypeConverter fun toPosition(v: String): Position = Position.valueOf(v)
    @TypeConverter fun fromMarital(m: MaritalStatus): String = m.name
    @TypeConverter fun toMarital(v: String): MaritalStatus = MaritalStatus.valueOf(v)
    @TypeConverter fun fromGuarantee(g: GuaranteeType): String = g.name
    @TypeConverter fun toGuarantee(v: String): GuaranteeType = GuaranteeType.valueOf(v)
}