package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ScanHistoryDao
import com.example.data.dao.TrustedDeviceDao
import com.example.data.dao.UserProfileDao
import com.example.data.entity.ScanDeviceHistoryEntity
import com.example.data.entity.ScanHistoryEntity
import com.example.data.entity.TrustedDeviceEntity
import com.example.data.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        TrustedDeviceEntity::class,
        ScanHistoryEntity::class,
        ScanDeviceHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun trustedDeviceDao(): TrustedDeviceDao
    abstract fun scanHistoryDao(): ScanHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wifi_inspector_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
