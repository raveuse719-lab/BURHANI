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

data class PrintUiState(
    val activePrinter: PrinterEntity? = null,
    val isScanning: Boolean = false,
    val scanProgress: Pair<Int, Int> = 0 to 40,
    val selectedFile: PrintableFile? = null,
    val printSettings: PrintSettings = PrintSettings(),
    val previewPage: Int = 0,
    val spoolerProgress: PrintSpoolerProgress = PrintSpoolerProgress(),
    val networkInfo: NetworkInfo = NetworkInfo(),
    val selectedScreen: String = "dashboard",
    val activeFilter: String = "All", // "All", "Direct IPP/RAW", "PC Server", "Favorites"
    val searchQuery: String = "",
    val qrScanDialogVisible: Boolean = false,
    val manualIpDialogVisible: Boolean = false,
    val showSpoolerModal: Boolean = false
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
                name = if (name.isBlank()) "Manual Printer ($ip)" else name,
                ipAddress = ip,
                port = port,
                protocol = protocol,
                status = "Online",
                signalMs = 10L,
                isPcServer = protocol.contains("PC", ignoreCase = true)
            )
            repository.savePrinter(newPrinter)
            _uiState.value = _uiState.value.copy(
                activePrinter = newPrinter,
                manualIpDialogVisible = false
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

    fun setManualIpDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(manualIpDialogVisible = visible)
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
