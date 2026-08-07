package com.example.data

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.example.data.entity.PrinterEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.InetAddress
import java.util.concurrent.ConcurrentHashMap

class NsdPrinterDiscoveryRepository(context: Context) {

    private val appContext = context.applicationContext
    private val nsdManager: NsdManager? = appContext.getSystemService(Context.NSD_SERVICE) as? NsdManager

    private val _discoveredPrinters = MutableStateFlow<List<PrinterEntity>>(emptyList())
    val discoveredPrinters: StateFlow<List<PrinterEntity>> = _discoveredPrinters.asStateFlow()

    private val _isDiscovering = MutableStateFlow(false)
    val isDiscovering: StateFlow<Boolean> = _isDiscovering.asStateFlow()

    private val printerMap = ConcurrentHashMap<String, PrinterEntity>()
    private val activeListeners = mutableListOf<NsdManager.DiscoveryListener>()

    // Standard printer service types registered on local Wi-Fi via mDNS / DNS-SD (Bonjour / AirPrint)
    private val printerServiceTypes = listOf(
        "_ipp._tcp.",
        "_ipps._tcp.",
        "_pdl-datastream._tcp.",
        "_printer._tcp."
    )

    fun startDiscovery() {
        if (nsdManager == null) {
            Log.e("NsdPrinterRepo", "NsdManager is not available on this device.")
            return
        }

        stopDiscovery()
        printerMap.clear()
        _discoveredPrinters.value = emptyList()
        _isDiscovering.value = true

        printerServiceTypes.forEach { serviceType ->
            val listener = createDiscoveryListener(serviceType)
            try {
                nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, listener)
                synchronized(activeListeners) {
                    activeListeners.add(listener)
                }
            } catch (e: Exception) {
                Log.e("NsdPrinterRepo", "Error starting discovery for $serviceType", e)
            }
        }
    }

    fun stopDiscovery() {
        if (nsdManager == null) return

        synchronized(activeListeners) {
            activeListeners.forEach { listener ->
                try {
                    nsdManager.stopServiceDiscovery(listener)
                } catch (e: Exception) {
                    Log.e("NsdPrinterRepo", "Error stopping service discovery", e)
                }
            }
            activeListeners.clear()
        }
        _isDiscovering.value = false
    }

    private fun createDiscoveryListener(serviceType: String): NsdManager.DiscoveryListener {
        return object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {
                Log.d("NsdPrinterRepo", "mDNS/DNS-SD discovery started for: $regType")
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                Log.d("NsdPrinterRepo", "Printer mDNS service found: ${serviceInfo.serviceName} (${serviceInfo.serviceType})")
                resolvePrinterService(serviceInfo)
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                Log.d("NsdPrinterRepo", "Printer mDNS service lost: ${serviceInfo.serviceName}")
                val nameKey = serviceInfo.serviceName
                printerMap.values.find { it.name == nameKey }?.let { target ->
                    printerMap.remove(target.id)
                    _discoveredPrinters.value = printerMap.values.toList()
                }
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.d("NsdPrinterRepo", "mDNS discovery stopped for: $serviceType")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e("NsdPrinterRepo", "Start discovery failed for $serviceType with code: $errorCode")
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e("NsdPrinterRepo", "Stop discovery failed for $serviceType with code: $errorCode")
            }
        }
    }

    private fun resolvePrinterService(serviceInfo: NsdServiceInfo) {
        nsdManager?.let { manager ->
            try {
                manager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                    override fun onResolveFailed(info: NsdServiceInfo, errorCode: Int) {
                        Log.e("NsdPrinterRepo", "Resolve failed for ${info.serviceName}: code $errorCode")
                    }

                    override fun onServiceResolved(resolvedInfo: NsdServiceInfo) {
                        Log.d("NsdPrinterRepo", "Printer service resolved: ${resolvedInfo.serviceName} at ${resolvedInfo.host}:${resolvedInfo.port}")
                        val printer = createPrinterEntityFromNsd(resolvedInfo)
                        if (printer != null) {
                            printerMap[printer.id] = printer
                            _discoveredPrinters.value = printerMap.values.toList()
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e("NsdPrinterRepo", "Exception resolving NSD service", e)
            }
        }
    }

    private fun createPrinterEntityFromNsd(info: NsdServiceInfo): PrinterEntity? {
        val host: InetAddress = info.host ?: return null
        val ipAddress = host.hostAddress ?: return null
        if (ipAddress.startsWith("127.") || ipAddress == "0.0.0.0") return null

        val port = if (info.port > 0) info.port else 9100
        val serviceName = info.serviceName.ifBlank { "mDNS AirPrint Printer" }
        val serviceType = info.serviceType ?: "_ipp._tcp."

        // Extract TXT record metadata
        val attributes = info.attributes
        val modelText = attributes["ty"]?.let { String(it) }
            ?: attributes["product"]?.let { String(it) }
            ?: attributes["note"]?.let { String(it) }

        val supportsColor = attributes["Color"]?.let { String(it).lowercase() in listOf("t", "true", "1", "y") } ?: true
        val supportsDuplex = attributes["Duplex"]?.let { String(it).lowercase() in listOf("t", "true", "1", "y") } ?: true

        val protocol = when {
            serviceType.contains("ipp", ignoreCase = true) -> "IPP / AirPrint (mDNS)"
            serviceType.contains("pdl", ignoreCase = true) -> "RAW Port $port (Bonjour)"
            serviceType.contains("printer", ignoreCase = true) -> "LPR / LPD (DNS-SD)"
            else -> "mDNS / DNS-SD Protocol"
        }

        val brand = when {
            serviceName.contains("HP", ignoreCase = true) || (modelText?.contains("HP", ignoreCase = true) == true) -> "HP"
            serviceName.contains("Epson", ignoreCase = true) || (modelText?.contains("Epson", ignoreCase = true) == true) -> "Epson"
            serviceName.contains("Canon", ignoreCase = true) || (modelText?.contains("Canon", ignoreCase = true) == true) -> "Canon"
            serviceName.contains("Brother", ignoreCase = true) || (modelText?.contains("Brother", ignoreCase = true) == true) -> "Brother"
            serviceName.contains("Xerox", ignoreCase = true) -> "Xerox"
            else -> "AirPrint Wi-Fi"
        }

        return PrinterEntity(
            id = "$ipAddress:$port",
            name = serviceName,
            brand = brand,
            model = modelText ?: "Wi-Fi Network Printer",
            ipAddress = ipAddress,
            port = port,
            protocol = protocol,
            status = "Online",
            signalMs = 6L,
            paperSizesSupported = "A4, Letter, Legal, Executive",
            supportsColor = supportsColor,
            supportsDuplex = supportsDuplex,
            inkLevelPercent = 92
        )
    }
}
