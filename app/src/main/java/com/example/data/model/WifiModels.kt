package com.example.data.model

enum class DeviceType(val displayName: String, val defaultIconName: String) {
    PHONE("Phone", "phone_android"),
    LAPTOP("Laptop", "laptop"),
    DESKTOP("Desktop", "desktop_windows"),
    CCTV("CCTV Camera", "videocam"),
    SMART_TV("Smart TV", "tv"),
    PRINTER("Printer", "print"),
    ROUTER("Router Gateway", "router"),
    RANGE_EXTENDER("Range Extender", "settings_input_antenna"),
    IOT("IoT Device", "developer_board"),
    UNKNOWN("Unknown Device", "help_outline")
}

data class NetworkInfoModel(
    val ssid: String = "Scanning...",
    val bssid: String = "--:--:--:--:--:--",
    val isInternetAvailable: Boolean = true,
    val routerGatewayIp: String = "192.168.1.1",
    val routerBrand: String = "Auto Detect...",
    val wifiSignalDbm: Int = -55,
    val wifiSignalLevel: Int = 4, // 0..4
    val localIpAddress: String = "192.168.1.100",
    val publicIpAddress: String = "Fetching...",
    val dns1: String = "8.8.8.8",
    val dns2: String = "8.8.4.4",
    val dhcpServer: String = "192.168.1.1",
    val netmask: String = "255.255.255.0",
    val networkType: String = "Wi-Fi (5 GHz)",
    val ipv4: String = "192.168.1.100",
    val ipv6: String = "fe80::1%wlan0",
    val frequencyMhz: Int = 5180,
    val channel: Int = 36,
    val linkSpeedMbps: Int = 866
)

data class NetworkDevice(
    val ipAddress: String,
    val macAddress: String,
    val deviceName: String,
    val deviceType: DeviceType = DeviceType.UNKNOWN,
    val manufacturer: String = "Unknown Vendor",
    val isOnline: Boolean = true,
    val responseTimeMs: Long = 12L,
    val signalDbm: Int? = null,
    val isTrusted: Boolean = false,
    val isUnknownAlert: Boolean = false,
    val customName: String? = null,
    val lastSeen: Long = System.currentTimeMillis()
) {
    val displayTitle: String
        get() = customName?.takeIf { it.isNotBlank() } ?: deviceName
}

data class TpLinkExtender(
    val extenderName: String,
    val localIp: String,
    val macAddress: String,
    val manufacturer: String = "TP-Link Technologies Co., Ltd.",
    val connectionStatus: String = "Connected - Excellent Signal",
    val signalStrengthDbm: Int = -48,
    val firmwareVersion: String = "v1.4.2 Build 20231120 (Admin Login Required for Deep Config)",
    val isConnectedToMainRouter: Boolean = true,
    val adminLoginRequired: Boolean = true,
    val modelName: String = "TP-Link RE200 / RE305 Extender"
)

data class PingResult(
    val targetHost: String,
    val ipAddress: String,
    val isSuccess: Boolean,
    val timeMs: Long,
    val ttl: Int = 64,
    val packetLossPercent: Float = 0f,
    val minMs: Long = 0L,
    val maxMs: Long = 0L,
    val avgMs: Long = 0L
)

enum class SpeedTestState {
    IDLE, SELECTING_SERVER, PINGING, DOWNLOADING, UPLOADING, COMPLETED, ERROR
}

data class SpeedTestResult(
    val downloadMbps: Float = 0f,
    val uploadMbps: Float = 0f,
    val pingMs: Long = 0L,
    val jitterMs: Long = 0L,
    val packetLossPercent: Float = 0f,
    val serverName: String = "",
    val serverLocation: String = "",
    val errorMessage: String? = null,
    val testState: SpeedTestState = SpeedTestState.IDLE,
    val progress: Float = 0f // 0.0 to 1.0
)

data class NetworkQualityScore(
    val grade: String = "A+",
    val ratingText: String = "Excellent for 4K Streaming & Ultra-Low Latency Gaming",
    val latencyScore: Int = 95,
    val speedScore: Int = 90,
    val securityScore: Int = 98,
    val stabilityScore: Int = 94
)

enum class NotificationType {
    NEW_DEVICE, DEVICE_DISCONNECTED, INTERNET_LOST, SECURITY_ALERT, INFO
}

data class AppNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: NotificationType = NotificationType.INFO
)
