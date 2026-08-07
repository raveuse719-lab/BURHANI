package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_profiles")
data class ChildProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val age: Int,
    val avatar: String, // e.g., "lion", "bear", "rabbit", "panda", "fox", "cat", "dog", "dino"
    val coins: Int = 100,
    val level: Int = 1,
    val stars: Int = 10,
    val dailyStreak: Int = 1,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val isCurrent: Boolean = false
)

@Entity(tableName = "user_achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childProfileId: Int,
    val badgeKey: String,
    val title: String,
    val description: String,
    val icon: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childProfileId: Int,
    val category: String,
    val activityName: String,
    val completedCount: Int = 1,
    val score: Int = 0,
    val timeSpentSeconds: Long = 0,
    val lastCompletedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_drawings")
data class SavedDrawingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childProfileId: Int,
    val title: String,
    val colorDataJson: String, // Store drawn path array / SVG data
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "parent_settings")
data class ParentSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val parentPin: String = "1234",
    val screenTimeLimitMinutes: Int = 30, // 0 means unlimited
    val screenTimeUsedTodaySeconds: Long = 0,
    val soundEnabled: Boolean = true,
    val bgMusicEnabled: Boolean = true,
    val voiceEnabled: Boolean = true,
    val language: String = "en", // "en", "hi", "gu"
    val parentPhone: String = "",
    val isLoggedIn: Boolean = false,
    val isSubscribed: Boolean = false
)

@Entity(tableName = "custom_quizzes")
data class CustomQuizEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // "ABC", "Numbers", "Animals", "Colors"
    val question: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correctIndex: Int,
    val explanation: String
)
