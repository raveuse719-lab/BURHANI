package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.AppSettingsEntity
import com.example.data.entity.PrintJobEntity
import com.example.data.entity.PrinterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrinterDao {
    @Query("SELECT * FROM printers ORDER BY isFavorite DESC, addedTimestamp DESC")
    fun getAllPrintersFlow(): Flow<List<PrinterEntity>>

    @Query("SELECT * FROM printers WHERE isFavorite = 1")
    fun getFavoritePrintersFlow(): Flow<List<PrinterEntity>>

    @Query("SELECT * FROM printers WHERE id = :id LIMIT 1")
    suspend fun getPrinterById(id: String): PrinterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrinter(printer: PrinterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrinters(printers: List<PrinterEntity>)

    @Query("UPDATE printers SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFav: Boolean)

    @Query("UPDATE printers SET status = :status, signalMs = :signalMs WHERE id = :id")
    suspend fun updatePrinterStatus(id: String, status: String, signalMs: Long)

    @Query("DELETE FROM printers WHERE id = :id")
    suspend fun deletePrinterById(id: String)
}

@Dao
interface PrintJobDao {
    @Query("SELECT * FROM print_jobs ORDER BY timestamp DESC")
    fun getAllPrintJobsFlow(): Flow<List<PrintJobEntity>>

    @Query("SELECT * FROM print_jobs ORDER BY timestamp DESC LIMIT 1")
    fun getLastPrintJobFlow(): Flow<PrintJobEntity?>

    @Query("SELECT COUNT(*) FROM print_jobs WHERE status = 'Completed'")
    fun getCompletedJobsCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrintJob(job: PrintJobEntity): Long

    @Query("UPDATE print_jobs SET status = :status, errorMessage = :errorMsg WHERE id = :id")
    suspend fun updateJobStatus(id: Int, status: String, errorMsg: String? = null)

    @Query("DELETE FROM print_jobs WHERE id = :id")
    suspend fun deleteJobById(id: Int)

    @Query("DELETE FROM print_jobs")
    suspend fun clearAllHistory()
}

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettingsFlow(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: AppSettingsEntity)

    @Query("UPDATE app_settings SET themePreference = :theme WHERE id = 1")
    suspend fun updateThemePreference(theme: String)
}
