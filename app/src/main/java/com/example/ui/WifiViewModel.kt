package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.WifiInspectorRepository
import com.example.data.entity.ScanHistoryEntity
import com.example.data.entity.TrustedDeviceEntity
import com.example.data.entity.UserProfileEntity
import com.example.data.model.AppNotification
import com.example.data.model.DeviceType
import com.example.data.model.NetworkDevice
import com.example.data.model.NetworkInfoModel
import com.example.data.model.NetworkQualityScore
import com.example.data.model.NotificationType
import com.example.data.model.PingResult
import com.example.data.model.SpeedTestResult
import com.example.data.model.SpeedTestState
import com.example.data.model.TpLinkExtender
import com.example.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WifiViewModel(
    private val repository: WifiInspectorRepository
) : ViewModel() {

    private val _networkInfo = MutableStateFlow(NetworkInfoModel())
    val networkInfo: StateFlow<NetworkInfoModel> = _networkInfo.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val _rawDiscoveredDevices = MutableStateFlow<List<NetworkDevice>>(emptyList())
    val rawDiscoveredDevices: StateFlow<List<NetworkDevice>> = _rawDiscoveredDevices.asStateFlow()

    private val _tpLinkExtenders = MutableStateFlow<List<TpLinkExtender>>(emptyList())
    val tpLinkExtenders: StateFlow<List<TpLinkExtender>> = _tpLinkExtenders.asStateFlow()

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val savedTrustedDevices: StateFlow<List<TrustedDeviceEntity>> = repository.trustedDevicesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scanHistory: StateFlow<List<ScanHistoryEntity>> = repository.scanHistoryFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("ALL") // ALL, TRUSTED, UNKNOWN, EXTENDERS, ONLINE
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _pingResult = MutableStateFlow<PingResult?>(null)
    val pingResult: StateFlow<PingResult?> = _pingResult.asStateFlow()

    private val _isPinging = MutableStateFlow(false)
    val isPinging: StateFlow<Boolean> = _isPinging.asStateFlow()

    private val _speedTestResult = MutableStateFlow(SpeedTestResult())
    val speedTestResult: StateFlow<SpeedTestResult> = _speedTestResult.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _networkQuality = MutableStateFlow<NetworkQualityScore?>(null)
    val networkQuality: StateFlow<NetworkQualityScore?> = _networkQuality.asStateFlow()

    // Combined Devices List (Merging scanned raw devices with saved custom names & trusted state in Room)
    val displayedDevices: StateFlow<List<NetworkDevice>> = combine(
        _rawDiscoveredDevices,
        savedTrustedDevices,
        _searchQuery,
        _selectedFilter
    ) { rawList, savedList, query, filter ->
        val savedMap = savedList.associateBy { it.macAddress }

        val merged = rawList.map { dev ->
            val saved = savedMap[dev.macAddress]
            if (saved != null) {
                dev.copy(
                    customName = if (saved.customName.isNotBlank()) saved.customName else null,
                    isTrusted = saved.isTrusted,
                    isUnknownAlert = saved.isUnknownAlert
                )
            } else {
                dev
            }
        }

        merged.filter { dev ->
            val matchesQuery = query.isBlank() ||
                    dev.displayTitle.contains(query, ignoreCase = true) ||
                    dev.ipAddress.contains(query, ignoreCase = true) ||
                    dev.macAddress.contains(query, ignoreCase = true) ||
                    dev.manufacturer.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "TRUSTED" -> dev.isTrusted
                "UNKNOWN" -> !dev.isTrusted || dev.isUnknownAlert
                "EXTENDERS" -> dev.deviceType == DeviceType.RANGE_EXTENDER
                "ONLINE" -> dev.isOnline
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initDefaultProfileIfNeeded()
        }
    }

    fun refreshNetworkInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val info = NetworkUtils.getConnectedWifiInfo(context)
            _networkInfo.value = info

            // Fetch public IP asynchronously
            val publicIp = NetworkUtils.fetchPublicIp()
            _networkInfo.value = _networkInfo.value.copy(publicIpAddress = publicIp)
        }
    }

    fun startNetworkScan(context: Context) {
        if (_isScanning.value) return

        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.value = true
            _scanProgress.value = 0.05f

            val currentInfo = NetworkUtils.getConnectedWifiInfo(context)
            _networkInfo.value = currentInfo

            val prefix = currentInfo.routerGatewayIp.substringBeforeLast(".")
            if (prefix.isBlank()) {
                _isScanning.value = false
                return@launch
            }

            val discovered = NetworkUtils.scanLocalSubnet(
                subnetPrefix = prefix,
                gatewayIp = currentInfo.routerGatewayIp,
                localIp = currentInfo.localIpAddress
            ) { scanned, total ->
                _scanProgress.value = (scanned.toFloat() / total.toFloat()).coerceIn(0.1f, 0.95f)
            }

            _rawDiscoveredDevices.value = discovered
            _scanProgress.value = 1.0f
            _isScanning.value = false

            // Automatically detect TP-Link range extenders
            detectTpLinkExtenders(prefix, currentInfo.routerGatewayIp)

            // Save scan history to Room
            repository.saveScanToHistory(
                ssid = currentInfo.ssid,
                gatewayIp = currentInfo.routerGatewayIp,
                devices = discovered
            )

            // Check if new unknown device joined and trigger notification
            val unknownCount = discovered.count { !it.isTrusted }
            if (unknownCount > 0) {
                addNotification(
                    title = "Network Scan Completed",
                    message = "Found ${discovered.size} total devices (${unknownCount} unknown/new devices detected).",
                    type = NotificationType.NEW_DEVICE
                )
            }
        }
    }

    fun detectTpLinkExtenders(prefix: String, gatewayIp: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val extenders = NetworkUtils.detectTpLinkExtenders(prefix, gatewayIp)
            _tpLinkExtenders.value = extenders
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun toggleDeviceTrust(device: NetworkDevice) {
        viewModelScope.launch {
            val newTrust = !device.isTrusted
            repository.markDeviceTrust(
                mac = device.macAddress,
                ip = device.ipAddress,
                defaultName = device.deviceName,
                vendor = device.manufacturer,
                isTrusted = newTrust
            )

            addNotification(
                title = if (newTrust) "Device Trusted" else "Marked as Unknown",
                message = "${device.displayTitle} is now ${if (newTrust) "Trusted" else "Marked as Unknown"}.",
                type = if (newTrust) NotificationType.INFO else NotificationType.SECURITY_ALERT
            )
        }
    }

    fun renameDevice(device: NetworkDevice, newName: String) {
        viewModelScope.launch {
            repository.renameDevice(
                mac = device.macAddress,
                ip = device.ipAddress,
                defaultName = device.deviceName,
                vendor = device.manufacturer,
                newName = newName
            )
        }
    }

    fun runPing(host: String) {
        if (host.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            _isPinging.value = true
            val res = NetworkUtils.pingHost(host, count = 4)
            _pingResult.value = res
            _isPinging.value = false
        }
    }

    fun runSpeedTest() {
        if (_speedTestResult.value.testState != SpeedTestState.IDLE &&
            _speedTestResult.value.testState != SpeedTestState.COMPLETED
        ) {
            return
        }

        viewModelScope.launch {
            repository.runSpeedTestFlow().collect { res ->
                _speedTestResult.value = res
                if (res.testState == SpeedTestState.COMPLETED) {
                    calculateQualityFromResults(res)
                }
            }
        }
    }

    private fun calculateQualityFromResults(speedResult: SpeedTestResult) {
        val down = speedResult.downloadMbps
        val ping = speedResult.pingMs
        val speedScore = (down * 2f).toInt().coerceIn(10, 100)
        val latencyScore = if (ping > 0) (100 - ping).toInt().coerceIn(10, 100) else 50
        val signal = _networkInfo.value.wifiSignalDbm
        val stabilityScore = if (signal != 0) (100 + signal).coerceIn(20, 100) else 70
        val securityScore = 95
        val avg = (speedScore + latencyScore + stabilityScore + securityScore) / 4
        val grade = when {
            avg >= 90 -> "A+"
            avg >= 80 -> "A"
            avg >= 70 -> "B"
            avg >= 60 -> "C"
            else -> "D"
        }
        _networkQuality.value = NetworkQualityScore(
            grade = grade,
            ratingText = "Grade $grade based on measured ${down.toInt()} Mbps download speed and ${ping}ms latency",
            latencyScore = latencyScore,
            speedScore = speedScore,
            securityScore = securityScore,
            stabilityScore = stabilityScore
        )
    }

    fun loginWithOtp(phoneNumber: String, name: String) {
        viewModelScope.launch {
            repository.saveUserProfile(phoneNumber = phoneNumber, name = name, isLoggedIn = true)
            addNotification(
                title = "Authentication Successful",
                message = "Logged in as $name ($phoneNumber).",
                type = NotificationType.INFO
            )
        }
    }

    fun logoutOrGuest() {
        viewModelScope.launch {
            repository.saveUserProfile(phoneNumber = "", name = "User", isLoggedIn = false)
        }
    }

    fun updateThemePreference(theme: String) {
        viewModelScope.launch {
            repository.updateThemePreference(theme)
        }
    }

    fun updateNotificationSettings(newDev: Boolean, disc: Boolean, off: Boolean) {
        viewModelScope.launch {
            repository.updateNotificationSettings(newDev, disc, off)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearScanHistory()
            addNotification(
                title = "Scan History Cleared",
                message = "All historical network scan logs have been removed.",
                type = NotificationType.INFO
            )
        }
    }

    private fun addNotification(title: String, message: String, type: NotificationType) {
        val newNotif = AppNotification(
            title = title,
            message = message,
            type = type
        )
        _notifications.value = listOf(newNotif) + _notifications.value
    }
}

class WifiViewModelFactory(
    private val repository: WifiInspectorRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WifiViewModel::class.java)) {
            return WifiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
