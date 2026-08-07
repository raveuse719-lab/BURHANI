package com.example.data

import com.example.data.dao.ScanHistoryDao
import com.example.data.dao.TrustedDeviceDao
import com.example.data.dao.UserProfileDao
import com.example.data.entity.ScanDeviceHistoryEntity
import com.example.data.entity.ScanHistoryEntity
import com.example.data.entity.TrustedDeviceEntity
import com.example.data.entity.UserProfileEntity
import com.example.data.model.NetworkDevice
import com.example.data.model.PingResult
import com.example.data.model.SpeedTestResult
import com.example.data.model.SpeedTestState
import com.example.data.model.TpLinkExtender
import com.example.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

import com.example.util.SpeedTestEngine

class WifiInspectorRepository(
    private val userProfileDao: UserProfileDao,
    private val trustedDeviceDao: TrustedDeviceDao,
    private val scanHistoryDao: ScanHistoryDao
) {
    val userProfileFlow: Flow<UserProfileEntity?> = userProfileDao.getProfileFlow()
    val trustedDevicesFlow: Flow<List<TrustedDeviceEntity>> = trustedDeviceDao.getAllDevicesFlow()
    val scanHistoryFlow: Flow<List<ScanHistoryEntity>> = scanHistoryDao.getAllScanHistoryFlow()

    suspend fun initDefaultProfileIfNeeded() {
        withContext(Dispatchers.IO) {
            val existing = userProfileDao.getProfile()
            if (existing == null) {
                userProfileDao.insertOrUpdateProfile(
                    UserProfileEntity(
                        id = 1,
                        phoneNumber = "+1 555-0199",
                        displayName = "Guest Inspector",
                        isLoggedIn = false,
                        isGuestMode = true,
                        themePreference = "SYSTEM"
                    )
                )
            }
        }
    }

    suspend fun saveUserProfile(phoneNumber: String, name: String, isLoggedIn: Boolean) {
        withContext(Dispatchers.IO) {
            val current = userProfileDao.getProfile() ?: UserProfileEntity()
            userProfileDao.insertOrUpdateProfile(
                current.copy(
                    phoneNumber = phoneNumber,
                    displayName = name,
                    isLoggedIn = isLoggedIn,
                    isGuestMode = !isLoggedIn,
                    lastLoginTimestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun updateThemePreference(theme: String) {
        withContext(Dispatchers.IO) {
            userProfileDao.updateThemePreference(theme)
        }
    }

    suspend fun updateNotificationSettings(newDev: Boolean, disc: Boolean, off: Boolean) {
        withContext(Dispatchers.IO) {
            userProfileDao.updateNotificationSettings(newDev, disc, off)
        }
    }

    suspend fun markDeviceTrust(mac: String, ip: String, defaultName: String, vendor: String, isTrusted: Boolean) {
        withContext(Dispatchers.IO) {
            val existing = trustedDeviceDao.getDeviceByMac(mac)
            if (existing != null) {
                trustedDeviceDao.insertOrUpdateDevice(
                    existing.copy(
                        isTrusted = isTrusted,
                        isUnknownAlert = !isTrusted,
                        lastSeenTimestamp = System.currentTimeMillis()
                    )
                )
            } else {
                trustedDeviceDao.insertOrUpdateDevice(
                    TrustedDeviceEntity(
                        macAddress = mac,
                        ipAddress = ip,
                        originalName = defaultName,
                        vendor = vendor,
                        isTrusted = isTrusted,
                        isUnknownAlert = !isTrusted
                    )
                )
            }
        }
    }

    suspend fun renameDevice(mac: String, ip: String, defaultName: String, vendor: String, newName: String) {
        withContext(Dispatchers.IO) {
            val existing = trustedDeviceDao.getDeviceByMac(mac)
            if (existing != null) {
                trustedDeviceDao.insertOrUpdateDevice(
                    existing.copy(
                        customName = newName,
                        lastSeenTimestamp = System.currentTimeMillis()
                    )
                )
            } else {
                trustedDeviceDao.insertOrUpdateDevice(
                    TrustedDeviceEntity(
                        macAddress = mac,
                        ipAddress = ip,
                        customName = newName,
                        originalName = defaultName,
                        vendor = vendor
                    )
                )
            }
        }
    }

    suspend fun saveScanToHistory(
        ssid: String,
        gatewayIp: String,
        devices: List<NetworkDevice>
    ) {
        withContext(Dispatchers.IO) {
            val total = devices.size
            val newDevs = devices.count { it.isUnknownAlert || !it.isTrusted }
            val offDevs = devices.count { !it.isOnline }

            val historyId = scanHistoryDao.insertScanHistory(
                ScanHistoryEntity(
                    ssid = ssid,
                    gatewayIp = gatewayIp,
                    totalDevicesCount = total,
                    newDevicesCount = newDevs,
                    offlineDevicesCount = offDevs
                )
            ).toInt()

            val deviceEntities = devices.map { dev ->
                ScanDeviceHistoryEntity(
                    historyId = historyId,
                    ipAddress = dev.ipAddress,
                    macAddress = dev.macAddress,
                    deviceName = dev.displayTitle,
                    deviceType = dev.deviceType.name,
                    vendor = dev.manufacturer,
                    responseTimeMs = dev.responseTimeMs,
                    isOnline = dev.isOnline
                )
            }
            scanHistoryDao.insertScanDeviceHistory(deviceEntities)
        }
    }

    suspend fun clearScanHistory() {
        withContext(Dispatchers.IO) {
            scanHistoryDao.clearAllHistory()
            scanHistoryDao.clearAllDeviceHistory()
        }
    }

    fun runSpeedTestFlow(): Flow<SpeedTestResult> {
        return SpeedTestEngine.runSpeedTest()
    }
}
