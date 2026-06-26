package com.example.resturant.feature.finance.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Partner::class], version = 1, exportSchema = false)
abstract class PartnerDatabase : RoomDatabase() {

    abstract fun partnerDao(): PartnerDao

    companion object {
        @Volatile
        private var INSTANCE: PartnerDatabase? = null

        fun getInstance(context: Context): PartnerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PartnerDatabase::class.java,
                    "partners.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}