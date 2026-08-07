package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.AchievementEntity
import com.example.data.entity.ChildProfileEntity
import com.example.data.entity.CustomQuizEntity
import com.example.data.entity.ParentSettingsEntity
import com.example.data.entity.SavedDrawingEntity
import com.example.data.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildProfileDao {
    @Query("SELECT * FROM child_profiles ORDER BY id ASC")
    fun getAllProfiles(): Flow<List<ChildProfileEntity>>

    @Query("SELECT * FROM child_profiles WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentProfile(): Flow<ChildProfileEntity?>

    @Query("SELECT * FROM child_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Int): ChildProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ChildProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: ChildProfileEntity)

    @Query("UPDATE child_profiles SET isCurrent = 0")
    suspend fun clearCurrentProfile()

    @Query("UPDATE child_profiles SET isCurrent = 1 WHERE id = :id")
    suspend fun setCurrentProfile(id: Int)

    @Query("DELETE FROM child_profiles WHERE id = :id")
    suspend fun deleteProfile(id: Int)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM user_achievements WHERE childProfileId = :childId ORDER BY unlockedAt DESC")
    fun getAchievements(childId: Int): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM user_progress WHERE childProfileId = :childId ORDER BY lastCompletedAt DESC")
    fun getProgressByChild(childId: Int): Flow<List<UserProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgressEntity)
}

@Dao
interface DrawingDao {
    @Query("SELECT * FROM saved_drawings WHERE childProfileId = :childId ORDER BY createdAt DESC")
    fun getDrawingsByChild(childId: Int): Flow<List<SavedDrawingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrawing(drawing: SavedDrawingEntity)

    @Query("DELETE FROM saved_drawings WHERE id = :id")
    suspend fun deleteDrawing(id: Int)
}

@Dao
interface ParentSettingsDao {
    @Query("SELECT * FROM parent_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<ParentSettingsEntity?>

    @Query("SELECT * FROM parent_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): ParentSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: ParentSettingsEntity)

    @Update
    suspend fun updateSettings(settings: ParentSettingsEntity)
}

@Dao
interface CustomQuizDao {
    @Query("SELECT * FROM custom_quizzes ORDER BY id DESC")
    fun getAllQuizzes(): Flow<List<CustomQuizEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: CustomQuizEntity)

    @Query("DELETE FROM custom_quizzes WHERE id = :id")
    suspend fun deleteQuiz(id: Int)
}
