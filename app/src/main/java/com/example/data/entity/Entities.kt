package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val phoneNumber: String = "",
    val displayName: String = "Guest User",
    val isLoggedIn: Boolean = false,
    val isGuestMode: Boolean = true,
    val themePreference: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val alertNewDevice: Boolean = true,
    val alertDisconnect: Boolean = true,
    val alertOffline: Boolean = true,
    val lastLoginTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "trusted_devices")
data class TrustedDeviceEntity(
    @PrimaryKey val macAddress: String,
    val ipAddress: String,
    val customName: String = "",
    val originalName: String = "Unknown Device",
    val deviceType: String = "Unknown", // Phone, Laptop, Desktop, CCTV, Smart TV, Printer, Router, Range Extender, IoT, Unknown
    val vendor: String = "Unknown Vendor",
    val isTrusted: Boolean = false,
    val isUnknownAlert: Boolean = false,
    val notes: String = "",
    val lastSeenTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val ssid: String,
    val gatewayIp: String,
    val totalDevicesCount: Int,
    val newDevicesCount: Int,
    val offlineDevicesCount: Int
)

@Entity(tableName = "scan_device_history")
data class ScanDeviceHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val historyId: Int,
    val ipAddress: String,
    val macAddress: String,
    val deviceName: String,
    val deviceType: String,
    val vendor: String,
    val responseTimeMs: Long,
    val isOnline: Boolean
)
