package com.example.resturant.feature.payroll.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PayrollAdjustment::class], version = 1, exportSchema = false)
abstract class PayrollDatabase : RoomDatabase() {

    abstract fun payrollDao(): PayrollDao

    companion object {
        @Volatile
        private var INSTANCE: PayrollDatabase? = null

        fun getInstance(context: Context): PayrollDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PayrollDatabase::class.java,
                    "payroll.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}