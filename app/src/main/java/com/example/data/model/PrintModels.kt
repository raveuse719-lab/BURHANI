package com.example.data.model

data class PrintableFile(
    val name: String,
    val type: String, // "PDF", "JPG", "PNG", "DOCX", "XLSX", "TXT"
    val uriString: String? = null,
    val sizeBytes: Long = 1024L * 150L,
    val pagesCount: Int = 1,
    val isSample: Boolean = false,
    val sampleTextContent: String? = null
)

data class PrintSettings(
    val copies: Int = 1,
    val paperSize: String = "A4", // "A4", "A5", "Letter", "Legal"
    val colorMode: String = "Color", // "Color", "Black & White"
    val orientation: String = "Portrait", // "Portrait", "Landscape"
    val duplexMode: String = "Simplex", // "Simplex", "Duplex Long Edge", "Duplex Short Edge"
    val pageRange: String = "All", // "All", "1", "1-3"
    val fitToPage: String = "Fit to Page" // "Fit to Page", "Fill Page", "100% Scale"
)

data class PrintSpoolerProgress(
    val jobId: Int = 0,
    val fileName: String = "",
    val printerName: String = "",
    val printerIp: String = "",
    val currentStep: String = "Idle",
    val progressPercent: Float = 0f,
    val isPrinting: Boolean = false,
    val isCompleted: Boolean = false,
    val isFailed: Boolean = false,
    val errorMessage: String? = null
)

data class NetworkInfo(
    val isConnected: Boolean = true,
    val ssid: String = "Office_WiFi_5G",
    val localIp: String = "192.168.1.105",
    val subnetMask: String = "255.255.255.0",
    val gatewayIp: String = "192.168.1.1"
)
