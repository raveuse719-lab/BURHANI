package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AppSettingsDao
import com.example.data.dao.PrintJobDao
import com.example.data.dao.PrinterDao
import com.example.data.entity.AppSettingsEntity
import com.example.data.entity.PrintJobEntity
import com.example.data.entity.PrinterEntity

@Database(
    entities = [
        PrinterEntity::class,
        PrintJobEntity::class,
        AppSettingsEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun printerDao(): PrinterDao
    abstract fun printJobDao(): PrintJobDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bi_wifi_print_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
