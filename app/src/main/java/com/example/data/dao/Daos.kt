package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.ScanDeviceHistoryEntity
import com.example.data.entity.ScanHistoryEntity
import com.example.data.entity.TrustedDeviceEntity
import com.example.data.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = 1")
    fun getProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = 1")
    suspend fun getProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profiles SET themePreference = :theme WHERE id = 1")
    suspend fun updateThemePreference(theme: String)

    @Query("UPDATE user_profiles SET alertNewDevice = :newDev, alertDisconnect = :disc, alertOffline = :off WHERE id = 1")
    suspend fun updateNotificationSettings(newDev: Boolean, disc: Boolean, off: Boolean)
}

@Dao
interface TrustedDeviceDao {
    @Query("SELECT * FROM trusted_devices ORDER BY lastSeenTimestamp DESC")
    fun getAllDevicesFlow(): Flow<List<TrustedDeviceEntity>>

    @Query("SELECT * FROM trusted_devices WHERE macAddress = :mac LIMIT 1")
    suspend fun getDeviceByMac(mac: String): TrustedDeviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDevice(device: TrustedDeviceEntity)

    @Query("DELETE FROM trusted_devices WHERE macAddress = :mac")
    suspend fun deleteDeviceByMac(mac: String)
}

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllScanHistoryFlow(): Flow<List<ScanHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanHistory(history: ScanHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanDeviceHistory(devices: List<ScanDeviceHistoryEntity>)

    @Query("SELECT * FROM scan_device_history WHERE historyId = :historyId")
    suspend fun getDevicesForHistory(historyId: Int): List<ScanDeviceHistoryEntity>

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Int)

    @Query("DELETE FROM scan_history")
    suspend fun clearAllHistory()

    @Query("DELETE FROM scan_device_history")
    suspend fun clearAllDeviceHistory()
}
