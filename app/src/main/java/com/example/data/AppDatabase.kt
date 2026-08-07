package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AchievementDao
import com.example.data.dao.ChildProfileDao
import com.example.data.dao.CustomQuizDao
import com.example.data.dao.DrawingDao
import com.example.data.dao.ParentSettingsDao
import com.example.data.dao.ProgressDao
import com.example.data.entity.AchievementEntity
import com.example.data.entity.ChildProfileEntity
import com.example.data.entity.CustomQuizEntity
import com.example.data.entity.ParentSettingsEntity
import com.example.data.entity.SavedDrawingEntity
import com.example.data.entity.UserProgressEntity

@Database(
    entities = [
        ChildProfileEntity::class,
        AchievementEntity::class,
        UserProgressEntity::class,
        SavedDrawingEntity::class,
        ParentSettingsEntity::class,
        CustomQuizEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun childProfileDao(): ChildProfileDao
    abstract fun achievementDao(): AchievementDao
    abstract fun progressDao(): ProgressDao
    abstract fun drawingDao(): DrawingDao
    abstract fun parentSettingsDao(): ParentSettingsDao
    abstract fun customQuizDao(): CustomQuizDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kids_learning_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
