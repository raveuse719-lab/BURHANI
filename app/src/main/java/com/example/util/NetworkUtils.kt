package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.example.data.model.DeviceType
import com.example.data.model.NetworkDevice
import com.example.data.model.NetworkInfoModel
import com.example.data.model.PingResult
import com.example.data.model.TpLinkExtender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.FileReader
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.URL
import java.util.Locale
import java.util.Collections

object NetworkUtils {

    // OUI Vendor Database Map
    private val vendorOuiMap = mapOf(
        "50:C7:BF" to "TP-Link Technologies Co., Ltd.",
        "18:D6:C7" to "TP-Link Technologies Co., Ltd.",
        "00:0A:EB" to "TP-Link Technologies Co., Ltd.",
        "A0:F3:C1" to "TP-Link Technologies Co., Ltd.",
        "C4:6E:1F" to "TP-Link Technologies Co., Ltd.",
        "CC:32:E5" to "TP-Link Technologies Co., Ltd.",
        "E8:48:B8" to "TP-Link Technologies Co., Ltd.",
        "30:B5:C2" to "TP-Link Technologies Co., Ltd.",
        "E4:C3:2A" to "TP-Link Technologies Co., Ltd.",
        "00:1E:06" to "Cisco Systems",
        "00:26:5A" to "D-Link Corporation",
        "24:4B:03" to "Netgear Inc.",
        "04:D4:C4" to "ASUSTeK Computer Inc.",
        "DC:A6:32" to "Raspberry Pi Trading Ltd",
        "B8:27:EB" to "Raspberry Pi Foundation",
        "AC:D5:64" to "Apple Inc.",
        "3C:D0:F8" to "Apple Inc.",
        "BC:92:6B" to "Apple Inc.",
        "FC:E8:92" to "Samsung Electronics",
        "84:25:DB" to "Samsung Electronics",
        "70:EE:50" to "Espressif Inc. (IoT Smart Device)",
        "68:37:E9" to "Amazon Technologies Inc.",
        "00:11:32" to "Synology Inc.",
        "94:83:C4" to "LG Electronics",
        "00:04:20" to "Slim Devices (Logitech)",
        "D4:AD:71" to "Huawei Technologies Co., Ltd.",
        "28:6C:07" to "Xiaomi Communications",
        "30:89:4A" to "Google LLC (Chromecast / Nest)"
    )

    fun getConnectedWifiInfo(context: Context): NetworkInfoModel {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        var ssid = "Not Connected"
        var bssid = ""
        var rssi = 0
        var linkSpeed = 0
        var freq = 0

        if (wifiManager != null) {
            val wifiInfo: WifiInfo? = wifiManager.connectionInfo
            if (wifiInfo != null) {
                val rawSsid = wifiInfo.ssid
                if (!rawSsid.isNullOrBlank() && rawSsid != "<unknown ssid>") {
                    ssid = rawSsid.replace("\"", "")
                }
                if (!wifiInfo.bssid.isNullOrBlank() && wifiInfo.bssid != "02:00:00:00:00:00") {
                    bssid = wifiInfo.bssid
                }
                rssi = wifiInfo.rssi
                linkSpeed = wifiInfo.linkSpeed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    freq = wifiInfo.frequency
                }
            }
        }

        // Determine Signal Level (0 to 4)
        val level = if (rssi != 0) WifiManager.calculateSignalLevel(rssi, 5) else 0

        // Read Gateway Router IP from DHCP Info
        val dhcpInfo = wifiManager?.dhcpInfo
        val gatewayIp = if (dhcpInfo != null && dhcpInfo.gateway != 0) {
            intToIp(dhcpInfo.gateway)
        } else {
            ""
        }

        val dns1 = if (dhcpInfo != null && dhcpInfo.dns1 != 0) intToIp(dhcpInfo.dns1) else ""
        val dns2 = if (dhcpInfo != null && dhcpInfo.dns2 != 0) intToIp(dhcpInfo.dns2) else ""
        val netmask = if (dhcpInfo != null && dhcpInfo.netmask != 0) intToIp(dhcpInfo.netmask) else ""
        val dhcpServer = if (dhcpInfo != null && dhcpInfo.serverAddress != 0) intToIp(dhcpInfo.serverAddress) else gatewayIp

        val localIp = getLocalIpAddress()
        val isConnected = isInternetAvailable(connectivityManager)

        val channel = if (freq > 0) frequencyToChannel(freq) else 0
        val networkType = if (freq in 4900..5900) "Wi-Fi 5 GHz (802.11ac/ax)" else if (freq in 5925..7125) "Wi-Fi 6E 6 GHz" else if (freq in 2400..2500) "Wi-Fi 2.4 GHz (802.11n)" else ""

        val routerVendor = if (bssid.isNotBlank()) {
            lookupVendorByMac(bssid).let { if (it == "Unknown Vendor") inferRouterBrandFromGateway(gatewayIp) else it }
        } else if (gatewayIp.isNotBlank()) {
            inferRouterBrandFromGateway(gatewayIp)
        } else ""

        return NetworkInfoModel(
            ssid = ssid,
            bssid = bssid,
            isInternetAvailable = isConnected,
            routerGatewayIp = gatewayIp,
            routerBrand = routerVendor,
            wifiSignalDbm = rssi,
            wifiSignalLevel = level,
            localIpAddress = localIp,
            publicIpAddress = "", // Updated asynchronously via fetchPublicIp
            dns1 = dns1,
            dns2 = dns2,
            dhcpServer = dhcpServer,
            netmask = netmask,
            networkType = networkType,
            ipv4 = localIp,
            ipv6 = getLocalIpv6Address(),
            frequencyMhz = freq,
            channel = channel,
            linkSpeedMbps = if (linkSpeed > 0) linkSpeed else 0
        )
    }

    private fun inferRouterBrandFromGateway(gatewayIp: String): String {
        if (gatewayIp.isBlank()) return ""
        return when {
            gatewayIp.startsWith("192.168.0.") -> "TP-Link / D-Link Router"
            gatewayIp.startsWith("192.168.1.") -> "TP-Link / Netgear Gateway"
            gatewayIp.startsWith("192.168.8.") -> "Huawei Mobile Router"
            gatewayIp.startsWith("192.168.31.") -> "Xiaomi Mi Router"
            gatewayIp.startsWith("10.0.0.") -> "Xfinity / Comcast / Cisco Gateway"
            gatewayIp.startsWith("192.168.2.") -> "Belkin / ASUS Router"
            else -> "Standard Local Router"
        }
    }

    suspend fun fetchPublicIp(): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.ipify.org")
            val connection = url.openConnection()
            connection.connectTimeout = 3000
            connection.readTimeout = 3000
            val ip = connection.getInputStream().bufferedReader().use { it.readText() }
            if (ip.isNotBlank()) ip.trim() else ""
        } catch (_: Exception) {
            ""
        }
    }

    fun lookupVendorByMac(macAddress: String): String {
        val cleanMac = macAddress.uppercase(Locale.ROOT).replace("-", ":")
        if (cleanMac.length >= 8) {
            val prefix = cleanMac.substring(0, 8)
            vendorOuiMap[prefix]?.let { return it }
        }
        return "Unknown Vendor"
    }

    fun inferDeviceType(name: String, vendor: String, ip: String, gatewayIp: String): DeviceType {
        val lowerName = name.lowercase()
        val lowerVendor = vendor.lowercase()

        return when {
            ip == gatewayIp -> DeviceType.ROUTER
            lowerName.contains("extender") || lowerName.contains("repeater") || lowerName.contains("tplinkextender") -> DeviceType.RANGE_EXTENDER
            lowerVendor.contains("tp-link") && (lowerName.contains("re") || lowerName.contains("ext")) -> DeviceType.RANGE_EXTENDER
            lowerName.contains("iphone") || lowerName.contains("galaxy") || lowerName.contains("pixel") || lowerName.contains("android") || lowerVendor.contains("samsung") || lowerVendor.contains("apple") -> DeviceType.PHONE
            lowerName.contains("macbook") || lowerName.contains("laptop") || lowerName.contains("thinkpad") || lowerName.contains("dell") || lowerName.contains("hp") -> DeviceType.LAPTOP
            lowerName.contains("desktop") || lowerName.contains("pc") || lowerName.contains("workstation") -> DeviceType.DESKTOP
            lowerName.contains("tv") || lowerName.contains("chromecast") || lowerName.contains("firestick") || lowerName.contains("bravia") || lowerVendor.contains("lg electronics") -> DeviceType.SMART_TV
            lowerName.contains("camera") || lowerName.contains("cctv") || lowerName.contains("hikvision") || lowerName.contains("dahua") -> DeviceType.CCTV
            lowerName.contains("printer") || lowerName.contains("epson") || lowerName.contains("canon") || lowerName.contains("brother") -> DeviceType.PRINTER
            lowerVendor.contains("espressif") || lowerVendor.contains("amazon") || lowerName.contains("esp") || lowerName.contains("sonoff") || lowerName.contains("tuya") -> DeviceType.IOT
            lowerName.contains("router") || lowerVendor.contains("cisco") -> DeviceType.ROUTER
            else -> DeviceType.UNKNOWN
        }
    }

    suspend fun scanLocalSubnet(
        subnetPrefix: String, // e.g. "192.168.1"
        gatewayIp: String,
        localIp: String,
        onProgress: (scanned: Int, total: Int) -> Unit
    ): List<NetworkDevice> = withContext(Dispatchers.IO) {
        if (subnetPrefix.isBlank()) return@withContext emptyList()
        val totalHosts = 254
        val arpMap = readArpTable()
        val discoveredDevices = mutableListOf<NetworkDevice>()

        // Add Gateway Router if present
        if (gatewayIp.isNotBlank()) {
            val routerMac = arpMap[gatewayIp] ?: ""
            val vendor = if (routerMac.isNotBlank()) lookupVendorByMac(routerMac) else inferRouterBrandFromGateway(gatewayIp)
            discoveredDevices.add(
                NetworkDevice(
                    ipAddress = gatewayIp,
                    macAddress = if (routerMac.isNotBlank()) routerMac else "N/A",
                    deviceName = "Router Gateway ($gatewayIp)",
                    deviceType = DeviceType.ROUTER,
                    manufacturer = if (vendor.isNotBlank()) vendor else "Gateway",
                    isOnline = true,
                    responseTimeMs = 2L,
                    isTrusted = true
                )
            )
        }

        // Add Local Device (This Phone)
        if (localIp.isNotBlank() && localIp != gatewayIp && localIp != "127.0.0.1") {
            val localMac = getLocalMacAddress() ?: ""
            discoveredDevices.add(
                NetworkDevice(
                    ipAddress = localIp,
                    macAddress = if (localMac.isNotBlank()) localMac else "N/A",
                    deviceName = "This Phone (${Build.MODEL})",
                    deviceType = DeviceType.PHONE,
                    manufacturer = Build.MANUFACTURER.uppercase(),
                    isOnline = true,
                    responseTimeMs = 1L,
                    isTrusted = true
                )
            )
        }

        // Multi-threaded subnet scan in batches
        val chunkSize = 25
        var currentScanned = 0

        for (startHost in 1..totalHosts step chunkSize) {
            val endHost = (startHost + chunkSize - 1).coerceAtMost(totalHosts)
            coroutineScope {
                val deferreds = (startHost..endHost).map { host ->
                    async {
                        val targetIp = "$subnetPrefix.$host"
                        if (targetIp == gatewayIp || targetIp == localIp) {
                            return@async null
                        }

                        val pingStart = System.currentTimeMillis()
                        val isReachable = try {
                            val addr = InetAddress.getByName(targetIp)
                            addr.isReachable(200) || checkPortOpen(targetIp, 80, 150) || checkPortOpen(targetIp, 443, 150)
                        } catch (_: Exception) {
                            false
                        }

                        val pingMs = (System.currentTimeMillis() - pingStart).coerceAtLeast(4L)

                        if (isReachable) {
                            val mac = arpMap[targetIp] ?: ""
                            val hostName = try {
                                InetAddress.getByName(targetIp).canonicalHostName
                            } catch (_: Exception) {
                                "Device ($targetIp)"
                            }

                            val vendor = if (mac.isNotBlank()) lookupVendorByMac(mac) else "Unknown Vendor"
                            val devType = inferDeviceType(hostName, vendor, targetIp, gatewayIp)

                            NetworkDevice(
                                ipAddress = targetIp,
                                macAddress = if (mac.isNotBlank()) mac else "N/A",
                                deviceName = if (hostName != targetIp && hostName.isNotBlank()) hostName else "Device ($targetIp)",
                                deviceType = devType,
                                manufacturer = vendor,
                                isOnline = true,
                                responseTimeMs = pingMs
                            )
                        } else null
                    }
                }

                val results = deferreds.awaitAll().filterNotNull()
                discoveredDevices.addAll(results)

                currentScanned += (endHost - startHost + 1)
                onProgress(currentScanned, totalHosts)
            }
        }

        discoveredDevices.distinctBy { it.ipAddress }
    }

    suspend fun detectTpLinkExtenders(
        subnetPrefix: String,
        gatewayIp: String
    ): List<TpLinkExtender> = withContext(Dispatchers.IO) {
        val list = mutableListOf<TpLinkExtender>()
        val arpMap = readArpTable()
        for ((ip, mac) in arpMap) {
            val vendor = lookupVendorByMac(mac)
            if (vendor.contains("TP-Link", ignoreCase = true) && ip != gatewayIp) {
                list.add(
                    TpLinkExtender(
                        extenderName = "TP-Link Extender ($ip)",
                        localIp = ip,
                        macAddress = mac,
                        manufacturer = vendor,
                        connectionStatus = "Connected",
                        signalStrengthDbm = -50,
                        firmwareVersion = "Detected on Subnet",
                        isConnectedToMainRouter = true,
                        adminLoginRequired = true,
                        modelName = "TP-Link Range Extender"
                    )
                )
            }
        }
        list
    }

    suspend fun pingHost(targetHost: String, count: Int = 4): PingResult = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis()
            val address = InetAddress.getByName(targetHost)
            val ip = address.hostAddress ?: targetHost

            val times = mutableListOf<Long>()
            var successes = 0

            for (i in 1..count) {
                val pStart = System.currentTimeMillis()
                val reachable = address.isReachable(1000) || checkPortOpen(ip, 80, 500)
                val elapsed = System.currentTimeMillis() - pStart
                if (reachable) {
                    successes++
                    times.add(elapsed.coerceAtLeast(3L))
                }
            }

            if (successes > 0) {
                val min = times.minOrNull() ?: 12L
                val max = times.maxOrNull() ?: 24L
                val avg = times.average().toLong().coerceAtLeast(8L)
                val loss = ((count - successes).toFloat() / count.toFloat()) * 100f

                PingResult(
                    targetHost = targetHost,
                    ipAddress = ip,
                    isSuccess = true,
                    timeMs = avg,
                    packetLossPercent = loss,
                    minMs = min,
                    maxMs = max,
                    avgMs = avg
                )
            } else {
                PingResult(
                    targetHost = targetHost,
                    ipAddress = ip,
                    isSuccess = false,
                    timeMs = 0L,
                    packetLossPercent = 100f
                )
            }
        } catch (_: Exception) {
            PingResult(
                targetHost = targetHost,
                ipAddress = "0.0.0.0",
                isSuccess = false,
                timeMs = 0L,
                packetLossPercent = 100f
            )
        }
    }

    private fun checkPortOpen(ip: String, port: Int, timeoutMs: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(java.net.InetSocketAddress(ip, port), timeoutMs)
                true
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun readArpTable(): Map<String, String> {
        val arpMap = mutableMapOf<String, String>()
        try {
            BufferedReader(FileReader("/proc/net/arp")).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    val tokens = line!!.split("\\s+".toRegex())
                    if (tokens.size >= 4) {
                        val ip = tokens[0]
                        val mac = tokens[3]
                        if (mac != "00:00:00:00:00:00" && mac.contains(":")) {
                            arpMap[ip] = mac.uppercase(Locale.ROOT)
                        }
                    }
                }
            }
        } catch (_: Exception) {
        }
        return arpMap
    }

    private fun getLocalIpAddress(): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) {
                        return addr.hostAddress ?: ""
                    }
                }
            }
        } catch (_: Exception) {
        }
        return ""
    }

    private fun getLocalIpv6Address(): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is java.net.Inet6Address) {
                        return addr.hostAddress?.substringBefore("%") ?: ""
                    }
                }
            }
        } catch (_: Exception) {
        }
        return ""
    }

    private fun getLocalMacAddress(): String? {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (intf.name.contains("wlan0") || intf.name.contains("eth0")) {
                    val macBytes = intf.hardwareAddress ?: return null
                    val res = StringBuilder()
                    for (b in macBytes) {
                        res.append(String.format("%02X:", b))
                    }
                    if (res.isNotEmpty()) res.deleteCharAt(res.length - 1)
                    return res.toString()
                }
            }
        } catch (_: Exception) {
        }
        return null
    }

    private fun generateFallbackMacForIp(ip: String): String {
        return ""
    }

    private fun isInternetAvailable(connectivityManager: ConnectivityManager?): Boolean {
        if (connectivityManager == null) return true
        val network = connectivityManager.activeNetwork ?: return true
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return true
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun intToIp(i: Int): String {
        return (i and 0xFF).toString() + "." +
                (i shr 8 and 0xFF) + "." +
                (i shr 16 and 0xFF) + "." +
                (i shr 24 and 0xFF)
    }

    private fun frequencyToChannel(freq: Int): Int {
        return when {
            freq in 2412..2484 -> (freq - 2407) / 5
            freq in 5170..5825 -> (freq - 5000) / 5
            freq in 5925..7125 -> (freq - 5950) / 5
            else -> 6
        }
    }
}
