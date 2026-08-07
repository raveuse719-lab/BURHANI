package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "printers")
data class PrinterEntity(
    @PrimaryKey val id: String, // IP:Port or MAC
    val name: String,
    val ipAddress: String,
    val port: Int = 9100,
    val protocol: String = "RAW Port 9100", // "IPP", "AirPrint", "Mopria", "RAW Port 9100", "LPR/LPD", "PC Print Server"
    val isFavorite: Boolean = false,
    val status: String = "Online", // "Online", "Offline", "Busy", "Paper Out", "Ink Low"
    val signalMs: Long = 15L,
    val isPcServer: Boolean = false,
    val supportsColor: Boolean = true,
    val supportsDuplex: Boolean = true,
    val location: String = "Local Network",
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "print_jobs")
data class PrintJobEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val fileType: String, // "PDF", "JPG", "PNG", "DOCX", "XLSX", "TXT"
    val filePath: String = "",
    val fileSizeBytes: Long = 0L,
    val pagesCount: Int = 1,
    val copies: Int = 1,
    val paperSize: String = "A4", // "A4", "A5", "Letter", "Legal"
    val colorMode: String = "Color", // "Color", "Black & White"
    val duplexMode: String = "Simplex", // "Simplex", "Duplex Long Edge", "Duplex Short Edge"
    val orientation: String = "Portrait", // "Portrait", "Landscape"
    val fitToPage: String = "Fit to Page",
    val printerName: String,
    val printerIp: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Completed", // "Completed", "Printing", "Failed", "Cancelled"
    val errorMessage: String? = null
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val themePreference: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val defaultPaperSize: String = "A4",
    val defaultColorMode: String = "Color",
    val defaultCopies: Int = 1,
    val defaultDuplex: String = "Simplex",
    val autoConnectFavorite: Boolean = true,
    val pcServerPort: Int = 9100
)
