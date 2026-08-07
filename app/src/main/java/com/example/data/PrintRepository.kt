package com.example.data

import com.example.data.dao.AppSettingsDao
import com.example.data.dao.PrintJobDao
import com.example.data.dao.PrinterDao
import com.example.data.entity.AppSettingsEntity
import com.example.data.entity.PrintJobEntity
import com.example.data.entity.PrinterEntity
import kotlinx.coroutines.flow.Flow

class PrintRepository(
    private val printerDao: PrinterDao,
    private val printJobDao: PrintJobDao,
    private val appSettingsDao: AppSettingsDao
) {
    val allPrinters: Flow<List<PrinterEntity>> = printerDao.getAllPrintersFlow()
    val favoritePrinters: Flow<List<PrinterEntity>> = printerDao.getFavoritePrintersFlow()
    val allPrintJobs: Flow<List<PrintJobEntity>> = printJobDao.getAllPrintJobsFlow()
    val lastPrintJob: Flow<PrintJobEntity?> = printJobDao.getLastPrintJobFlow()
    val completedJobsCount: Flow<Int> = printJobDao.getCompletedJobsCountFlow()
    val appSettings: Flow<AppSettingsEntity?> = appSettingsDao.getSettingsFlow()

    suspend fun savePrinter(printer: PrinterEntity) {
        printerDao.insertOrUpdatePrinter(printer)
    }

    suspend fun savePrinters(printers: List<PrinterEntity>) {
        printerDao.insertPrinters(printers)
    }

    suspend fun toggleFavoritePrinter(printerId: String, currentFav: Boolean) {
        printerDao.updateFavoriteStatus(printerId, !currentFav)
    }

    suspend fun deletePrinter(printerId: String) {
        printerDao.deletePrinterById(printerId)
    }

    suspend fun recordPrintJob(job: PrintJobEntity): Long {
        return printJobDao.insertPrintJob(job)
    }

    suspend fun updatePrintJobStatus(jobId: Int, status: String, errorMsg: String? = null) {
        printJobDao.updateJobStatus(jobId, status, errorMsg)
    }

    suspend fun clearPrintHistory() {
        printJobDao.clearAllHistory()
    }

    suspend fun updateThemePreference(theme: String) {
        val current = appSettingsDao.getSettings() ?: AppSettingsEntity()
        appSettingsDao.insertOrUpdateSettings(current.copy(themePreference = theme))
    }

    suspend fun updateAppSettings(settings: AppSettingsEntity) {
        appSettingsDao.insertOrUpdateSettings(settings)
    }
}
