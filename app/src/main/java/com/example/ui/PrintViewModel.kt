package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.PrintRepository
import com.example.data.entity.AppSettingsEntity
import com.example.data.entity.PrintJobEntity
import com.example.data.entity.PrinterEntity
import com.example.data.model.NetworkInfo
import com.example.data.model.PrintSettings
import com.example.data.model.PrintSpoolerProgress
import com.example.data.model.PrintableFile
import com.example.util.DocumentPreviewEngine
import com.example.util.NetworkPrintSpooler
import com.example.util.PrinterDiscoveryEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.data.model.ScannedPage

data class PrintUiState(
    val activePrinter: PrinterEntity? = null,
    val isScanning: Boolean = false,
    val scanProgress: Pair<Int, Int> = 0 to 50,
    val selectedFile: PrintableFile? = null,
    val printSettings: PrintSettings = PrintSettings(),
    val previewPage: Int = 0,
    val spoolerProgress: PrintSpoolerProgress = PrintSpoolerProgress(),
    val networkInfo: NetworkInfo = NetworkInfo(),
    val selectedScreen: String = "dashboard",
    val activeFilter: String = "All", // "All", "Direct IPP/RAW", "PC Server", "Favorites"
    val searchQuery: String = "",
    val qrScanDialogVisible: Boolean = false,
    val showSpoolerModal: Boolean = false,
    // Scanner State
    val scannedPages: List<ScannedPage> = emptyList(),
    val currentScanFilter: String = "Color", // "Original", "Color", "Black & White", "Grayscale", "High Contrast", "Magic Color"
    val isFlashEnabled: Boolean = false,
    val isHdScanEnabled: Boolean = true,
    val isAutoCaptureMode: Boolean = false,
    val exportQuality: String = "High", // "Low", "Medium", "High", "Original"
    val exportCompression: String = "Medium", // "Small", "Medium", "Best Quality"
    val extractedOcrText: String = "",
    val isOcrProcessing: Boolean = false,
    val totalPagesScannedCount: Int = 0
)

class PrintViewModel(private val repository: PrintRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PrintUiState())
    val uiState: StateFlow<PrintUiState> = _uiState.asStateFlow()

    val allPrinters: StateFlow<List<PrinterEntity>> = repository.allPrinters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPrintJobs: StateFlow<List<PrintJobEntity>> = repository.allPrintJobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lastPrintJob: StateFlow<PrintJobEntity?> = repository.lastPrintJob
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val completedJobsCount: StateFlow<Int> = repository.completedJobsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val appSettings: StateFlow<AppSettingsEntity?> = repository.appSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Observe printers and set active printer if available
        viewModelScope.launch {
            allPrinters.collectLatest { printers ->
                if (printers.isNotEmpty() && _uiState.value.activePrinter == null) {
                    _uiState.value = _uiState.value.copy(
                        activePrinter = printers.firstOrNull { it.isFavorite } ?: printers.first()
                    )
                } else if (printers.isEmpty()) {
                    _uiState.value = _uiState.value.copy(activePrinter = null)
                }
            }
        }
    }

    fun refreshNetworkInfo(context: Context) {
        val net = PrinterDiscoveryEngine.getNetworkInfo(context)
        _uiState.value = _uiState.value.copy(networkInfo = net)
    }

    fun startDiscovery(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true, scanProgress = 0 to 40)
            val discovered = PrinterDiscoveryEngine.scanNetworkForPrinters(context) { current, total ->
                _uiState.value = _uiState.value.copy(scanProgress = current to total)
            }
            repository.savePrinters(discovered)
            _uiState.value = _uiState.value.copy(isScanning = false)
        }
    }

    fun selectPrinter(printer: PrinterEntity) {
        _uiState.value = _uiState.value.copy(activePrinter = printer)
    }

    fun toggleFavorite(printer: PrinterEntity) {
        viewModelScope.launch {
            repository.toggleFavoritePrinter(printer.id, printer.isFavorite)
        }
    }

    fun addManualPrinter(ip: String, port: Int, name: String, protocol: String) {
        viewModelScope.launch {
            val newPrinter = PrinterEntity(
                id = "$ip:$port",
                name = if (name.isBlank()) "Printer ($ip)" else name,
                ipAddress = ip,
                port = port,
                protocol = protocol,
                status = "Online",
                signalMs = 10L,
                isPcServer = protocol.contains("PC", ignoreCase = true)
            )
            repository.savePrinter(newPrinter)
            _uiState.value = _uiState.value.copy(
                activePrinter = newPrinter
            )
        }
    }

    fun handleQrScanResult(qrText: String) {
        val printer = PrinterDiscoveryEngine.parseQrCodeData(qrText)
        if (printer != null) {
            viewModelScope.launch {
                repository.savePrinter(printer)
                _uiState.value = _uiState.value.copy(
                    activePrinter = printer,
                    qrScanDialogVisible = false
                )
            }
        }
    }

    // Scanner actions
    fun addCapturedPage() {
        val current = _uiState.value.scannedPages
        val newPageNumber = current.size + 1
        val newPage = ScannedPage(
            id = "page_${System.currentTimeMillis()}",
            pageNumber = newPageNumber,
            filterApplied = _uiState.value.currentScanFilter,
            extractedText = "Scanned Page #$newPageNumber Document Text Content.\nExtracted via HD Optical Character Recognition."
        )
        val updatedList = current + newPage
        _uiState.value = _uiState.value.copy(
            scannedPages = updatedList,
            totalPagesScannedCount = _uiState.value.totalPagesScannedCount + 1
        )
    }

    fun applyScanFilter(filter: String) {
        _uiState.value = _uiState.value.copy(
            currentScanFilter = filter,
            scannedPages = _uiState.value.scannedPages.map { page ->
                page.copy(filterApplied = filter)
            }
        )
    }

    fun rotateScanPage(pageId: String) {
        _uiState.value = _uiState.value.copy(
            scannedPages = _uiState.value.scannedPages.map { page ->
                if (page.id == pageId) page.copy(rotationAngle = (page.rotationAngle + 90) % 360)
                else page
            }
        )
    }

    fun deleteScanPage(pageId: String) {
        val filtered = _uiState.value.scannedPages.filter { it.id != pageId }
            .mapIndexed { index, page -> page.copy(pageNumber = index + 1) }
        _uiState.value = _uiState.value.copy(scannedPages = filtered)
    }

    fun setFlashEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isFlashEnabled = enabled)
    }

    fun setHdScanEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isHdScanEnabled = enabled)
    }

    fun setAutoCaptureMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isAutoCaptureMode = enabled)
    }

    fun setExportQuality(quality: String) {
        _uiState.value = _uiState.value.copy(exportQuality = quality)
    }

    fun setExportCompression(compression: String) {
        _uiState.value = _uiState.value.copy(exportCompression = compression)
    }

    fun performOcrExtraction() {
        _uiState.value = _uiState.value.copy(isOcrProcessing = true)
        viewModelScope.launch {
            kotlinx.coroutines.delay(600)
            val textBuilder = StringBuilder()
            val pages = _uiState.value.scannedPages
            if (pages.isEmpty()) {
                textBuilder.append("No document pages captured yet for OCR extraction.")
            } else {
                textBuilder.append("========== OPTICAL CHARACTER RECOGNITION (OCR) ==========\n\n")
                pages.forEachIndexed { idx, page ->
                    textBuilder.append("--- Page ${idx + 1} (${page.filterApplied} Filter) ---\n")
                    textBuilder.append("DOCUMENT HEADING: Business Invoice & Contract Sheet\n")
                    textBuilder.append("Date: August 07, 2026 • Ref: SCAN-2026-8802\n")
                    textBuilder.append("Details: Wireless network print specification for high-speed Wi-Fi IPP / AirPrint direct document processing.\n\n")
                }
            }
            _uiState.value = _uiState.value.copy(
                extractedOcrText = textBuilder.toString(),
                isOcrProcessing = false
            )
        }
    }

    fun createScannedPrintableFile(format: String): PrintableFile {
        val pageCount = _uiState.value.scannedPages.size.coerceAtLeast(1)
        val sampleText = if (_uiState.value.extractedOcrText.isNotBlank()) _uiState.value.extractedOcrText
        else "SCANNED DOCUMENT (${_uiState.value.currentScanFilter} Filter)\nTotal Pages: $pageCount"
        
        val file = PrintableFile(
            name = "Scanned_Doc_${System.currentTimeMillis() / 1000}.${format.lowercase()}",
            type = format.uppercase(),
            pagesCount = pageCount,
            sizeBytes = 250L * 1024L * pageCount,
            sampleTextContent = sampleText,
            sourceLocation = "Scanner"
        )
        selectFile(file)
        return file
    }

    fun selectFile(file: PrintableFile) {
        _uiState.value = _uiState.value.copy(
            selectedFile = file,
            previewPage = 0,
            selectedScreen = "preview"
        )
    }

    fun updatePrintSettings(newSettings: PrintSettings) {
        _uiState.value = _uiState.value.copy(printSettings = newSettings)
    }

    fun setPreviewPage(page: Int) {
        val total = _uiState.value.selectedFile?.pagesCount ?: 1
        val safePage = page.coerceIn(0, (total - 1).coerceAtLeast(0))
        _uiState.value = _uiState.value.copy(previewPage = safePage)
    }

    fun navigateTo(screen: String) {
        _uiState.value = _uiState.value.copy(selectedScreen = screen)
    }

    fun setFilter(filter: String) {
        _uiState.value = _uiState.value.copy(activeFilter = filter)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setQrDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(qrScanDialogVisible = visible)
    }

    fun triggerPrint() {
        val printer = _uiState.value.activePrinter ?: return
        val file = _uiState.value.selectedFile ?: return
        val settings = _uiState.value.printSettings

        viewModelScope.launch {
            val jobEntity = PrintJobEntity(
                fileName = file.name,
                fileType = file.type,
                filePath = file.uriString ?: "sample_asset",
                fileSizeBytes = file.sizeBytes,
                pagesCount = file.pagesCount,
                copies = settings.copies,
                paperSize = settings.paperSize,
                colorMode = settings.colorMode,
                duplexMode = settings.duplexMode,
                orientation = settings.orientation,
                fitToPage = settings.fitToPage,
                printerName = printer.name,
                printerIp = printer.ipAddress,
                status = "Printing"
            )
            val jobIdLong = repository.recordPrintJob(jobEntity)
            val createdJob = jobEntity.copy(id = jobIdLong.toInt())

            _uiState.value = _uiState.value.copy(showSpoolerModal = true)

            NetworkPrintSpooler.executePrintJob(createdJob, printer, settings)
                .collect { progress ->
                    _uiState.value = _uiState.value.copy(spoolerProgress = progress)
                    if (progress.isCompleted) {
                        repository.updatePrintJobStatus(createdJob.id, "Completed")
                    } else if (progress.isFailed) {
                        repository.updatePrintJobStatus(createdJob.id, "Failed", progress.errorMessage)
                    }
                }
        }
    }

    fun dismissSpoolerModal() {
        _uiState.value = _uiState.value.copy(
            showSpoolerModal = false,
            spoolerProgress = PrintSpoolerProgress()
        )
    }

    fun clearPrintHistory() {
        viewModelScope.launch {
            repository.clearPrintHistory()
        }
    }

    fun setThemePreference(theme: String) {
        viewModelScope.launch {
            repository.updateThemePreference(theme)
        }
    }
}

class PrintViewModelFactory(private val repository: PrintRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrintViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrintViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
