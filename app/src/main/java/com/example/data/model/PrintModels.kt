package com.example.data.model

data class PrintableFile(
    val name: String,
    val type: String, // "PDF", "JPG", "PNG", "DOCX", "XLSX", "PPTX", "TXT"
    val uriString: String? = null,
    val sizeBytes: Long = 1024L * 150L,
    val pagesCount: Int = 1,
    val isSample: Boolean = false,
    val sampleTextContent: String? = null,
    val sourceLocation: String = "Local Storage" // "Internal Storage", "Camera", "Scanner", "Google Drive", "OneDrive", "Dropbox", "SD Card"
)

data class PrintSettings(
    val copies: Int = 1,
    val paperSize: String = "A4", // "A3", "A4", "A5", "A6", "Letter", "Legal", "Executive", "B4", "B5", "Envelope", "Custom"
    val colorMode: String = "Color", // "Color", "Black & White"
    val orientation: String = "Portrait", // "Portrait", "Landscape"
    val duplexMode: String = "Simplex", // "Simplex", "Duplex Long Edge", "Duplex Short Edge"
    val pageRange: String = "All", // "All", "1", "1-3"
    val fitToPage: String = "Fit to Page", // "Fit to Page", "Actual Size", "Fill Page", "Custom %"
    val margins: String = "Standard", // "None", "Standard", "Wide", "Narrow"
    val printQuality: String = "Normal", // "Draft", "Normal", "Best Quality"
    val printDensity: String = "Medium" // "Light", "Medium", "Dark"
)

data class ScannedPage(
    val id: String,
    val pageNumber: Int,
    val imagePath: String? = null,
    val filterApplied: String = "Original", // "Original", "Color", "Black & White", "Grayscale", "High Contrast", "Magic Color"
    val rotationAngle: Int = 0,
    val extractedText: String = ""
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
    val gatewayIp: String = "192.168.1.1",
    val wifiStrengthDbm: Int = -48
)

