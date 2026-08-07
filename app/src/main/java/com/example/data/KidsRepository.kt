package com.example.data

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
import kotlinx.coroutines.flow.Flow

class KidsRepository(
    private val childProfileDao: ChildProfileDao,
    private val achievementDao: AchievementDao,
    private val progressDao: ProgressDao,
    private val drawingDao: DrawingDao,
    private val parentSettingsDao: ParentSettingsDao,
    private val customQuizDao: CustomQuizDao
) {
    val allProfiles: Flow<List<ChildProfileEntity>> = childProfileDao.getAllProfiles()
    val currentProfile: Flow<ChildProfileEntity?> = childProfileDao.getCurrentProfile()
    val parentSettings: Flow<ParentSettingsEntity?> = parentSettingsDao.getSettings()
    val customQuizzes: Flow<List<CustomQuizEntity>> = customQuizDao.getAllQuizzes()

    fun getAchievements(childId: Int): Flow<List<AchievementEntity>> = achievementDao.getAchievements(childId)
    fun getProgress(childId: Int): Flow<List<UserProgressEntity>> = progressDao.getProgressByChild(childId)
    fun getSavedDrawings(childId: Int): Flow<List<SavedDrawingEntity>> = drawingDao.getDrawingsByChild(childId)

    suspend fun createChildProfile(name: String, age: Int, avatar: String): Long {
        childProfileDao.clearCurrentProfile()
        val profile = ChildProfileEntity(
            name = name,
            age = age,
            avatar = avatar,
            coins = 100,
            level = 1,
            stars = 5,
            dailyStreak = 1,
            isCurrent = true
        )
        val id = childProfileDao.insertProfile(profile)
        // Add starter badge
        achievementDao.insertAchievement(
            AchievementEntity(
                childProfileId = id.toInt(),
                badgeKey = "welcome_star",
                title = "Welcome Superstar!",
                description = "Started learning adventure",
                icon = "🌟"
            )
        )
        return id
    }

    suspend fun selectProfile(id: Int) {
        childProfileDao.clearCurrentProfile()
        childProfileDao.setCurrentProfile(id)
    }

    suspend fun updateProfile(profile: ChildProfileEntity) {
        childProfileDao.updateProfile(profile)
    }

    suspend fun addCoinsAndStars(childId: Int, coinsToAdd: Int, starsToAdd: Int) {
        val profile = childProfileDao.getProfileById(childId) ?: return
        val newCoins = profile.coins + coinsToAdd
        val newStars = profile.stars + starsToAdd
        val newLevel = 1 + (newCoins / 100)
        childProfileDao.updateProfile(profile.copy(coins = newCoins, stars = newStars, level = newLevel))
    }

    suspend fun recordActivityCompleted(childId: Int, category: String, activityName: String, score: Int, timeSpentSec: Long) {
        progressDao.insertProgress(
            UserProgressEntity(
                childProfileId = childId,
                category = category,
                activityName = activityName,
                completedCount = 1,
                score = score,
                timeSpentSeconds = timeSpentSec
            )
        )
        // Award 15 coins & 2 stars per completed activity
        addCoinsAndStars(childId, 15, 2)
    }

    suspend fun saveDrawing(childId: Int, title: String, colorDataJson: String) {
        drawingDao.insertDrawing(
            SavedDrawingEntity(
                childProfileId = childId,
                title = title,
                colorDataJson = colorDataJson
            )
        )
        // Award 20 coins for artwork creation
        addCoinsAndStars(childId, 20, 3)
    }

    suspend fun deleteDrawing(id: Int) {
        drawingDao.deleteDrawing(id)
    }

    suspend fun updateParentSettings(settings: ParentSettingsEntity) {
        parentSettingsDao.insertSettings(settings)
    }

    suspend fun ensureDefaultParentSettings() {
        val existing = parentSettingsDao.getSettingsDirect()
        if (existing == null) {
            parentSettingsDao.insertSettings(
                ParentSettingsEntity(
                    id = 1,
                    parentPin = "1234",
                    screenTimeLimitMinutes = 30,
                    soundEnabled = true,
                    bgMusicEnabled = true,
                    voiceEnabled = true,
                    language = "en",
                    parentPhone = "",
                    isLoggedIn = false
                )
            )
        }
    }

    suspend fun addCustomQuiz(quiz: CustomQuizEntity) {
        customQuizDao.insertQuiz(quiz)
    }

    suspend fun deleteCustomQuiz(id: Int) {
        customQuizDao.deleteQuiz(id)
    }
}
