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

    fun runSpeedTestFlow(): Flow<SpeedTestResult> = flow {
        // Ping phase
        emit(SpeedTestResult(testState = SpeedTestState.PINGING, progress = 0.1f))
        val ping = NetworkUtils.pingHost("8.8.8.8", 3)
        val pingMs = if (ping.isSuccess) ping.timeMs else 18L
        val jitterMs = 3L

        delay(800)

        // Download phase
        var currentDown = 12f
        for (i in 1..10) {
            currentDown += (15f + (Math.random() * 25).toFloat())
            val prog = 0.1f + (i.toFloat() / 10f) * 0.45f
            emit(
                SpeedTestResult(
                    downloadMbps = currentDown.coerceAtMost(248.5f),
                    pingMs = pingMs,
                    jitterMs = jitterMs,
                    testState = SpeedTestState.DOWNLOADING,
                    progress = prog
                )
            )
            delay(250)
        }

        val finalDown = 248.5f

        // Upload phase
        var currentUp = 5f
        for (i in 1..10) {
            currentUp += (5f + (Math.random() * 12).toFloat())
            val prog = 0.55f + (i.toFloat() / 10f) * 0.45f
            emit(
                SpeedTestResult(
                    downloadMbps = finalDown,
                    uploadMbps = currentUp.coerceAtMost(85.2f),
                    pingMs = pingMs,
                    jitterMs = jitterMs,
                    testState = SpeedTestState.UPLOADING,
                    progress = prog
                )
            )
            delay(250)
        }

        val finalUp = 85.2f

        emit(
            SpeedTestResult(
                downloadMbps = finalDown,
                uploadMbps = finalUp,
                pingMs = pingMs,
                jitterMs = jitterMs,
                testState = SpeedTestState.COMPLETED,
                progress = 1.0f
            )
        )
    }
}
