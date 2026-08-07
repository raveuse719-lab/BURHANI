package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Scanner
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScannedPage
import com.example.ui.PrintViewModel
import com.example.ui.theme.WifiPrimary

@Composable
fun DocumentScannerScreen(
    viewModel: PrintViewModel,
    onNavigateToPreview: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uiState by viewModel.uiState.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) } // 0: AirScan, 1: Camera, 2: Pages, 3: OCR & Print
    var showScanPreviewDialog by remember { mutableStateOf(false) }
    var previewPageIndex by remember { mutableStateOf(0) }

    val scannedPages = uiState.scannedPages
    val currentFilter = uiState.currentScanFilter
    val isFlash = uiState.isFlashEnabled
    val isHd = uiState.isHdScanEnabled
    val isAutoCapture = uiState.isAutoCaptureMode

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Scanner Title Bar with Scan Preview Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "HD Document Scanner",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Auto edge detection, perspective crop, multi-page PDF & OCR",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = WifiPrimary.copy(alpha = 0.15f),
                modifier = Modifier
                    .clickable {
                        if (scannedPages.isEmpty()) {
                            viewModel.addCapturedPage()
                        }
                        previewPageIndex = 0
                        showScanPreviewDialog = true
                    }
                    .testTag("scan_preview_header_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Scan Preview",
                        tint = WifiPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Scan Preview",
                        style = MaterialTheme.typography.labelSmall.copy(color = WifiPrimary, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector (eSCL AirScan, Camera, Pages, OCR & Print)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("AirScan (eSCL)") },
                icon = { Icon(Icons.Default.Scanner, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Camera") },
                icon = { Icon(Icons.Default.Camera, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                text = { Text("Pages (${scannedPages.size})") },
                icon = { Icon(Icons.Default.PictureInPicture, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTabIndex == 3,
                onClick = { selectedTabIndex = 3 },
                text = { Text("OCR & Print") },
                icon = { Icon(Icons.Default.TextSnippet, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTabIndex) {
            0 -> {
                // eSCL Network AirScan Protocol Screen
                val activePrinter = uiState.activePrinter
                val scannerIp = activePrinter?.ipAddress ?: uiState.networkInfo.gatewayIp.ifBlank { "192.168.1.120" }

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Scanner Connection Status Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = WifiPrimary.copy(alpha = 0.15f),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Scanner, contentDescription = null, tint = WifiPrimary)
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = activePrinter?.name ?: "Wi-Fi Network Scanner ($scannerIp)",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "eSCL 2.0 AirScan Protocol • Port ${activePrinter?.port ?: 8080}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = WifiPrimary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "Online",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }

                    // Scan Parameters Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = WifiPrimary)
                                Text(
                                    text = "eSCL Hardware Scan Parameters",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Input Source Selector
                            Text("Input Source", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val sources = listOf("Platen" to "Flatbed Glass", "ADF" to "Auto Feeder", "ADFDuplex" to "ADF Duplex")
                                sources.forEach { (key, label) ->
                                    FilterChip(
                                        selected = uiState.esclScanInputSource == key,
                                        onClick = { viewModel.setEsclScanInputSource(key) },
                                        label = { Text(label) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = WifiPrimary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Color Mode Selector
                            Text("Color Mode", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val colors = listOf("RGB24" to "Full Color", "Grayscale8" to "Grayscale", "BlackAndWhite1" to "B&W LineArt")
                                colors.forEach { (key, label) ->
                                    FilterChip(
                                        selected = uiState.esclScanColorMode == key,
                                        onClick = { viewModel.setEsclScanColorMode(key) },
                                        label = { Text(label) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = WifiPrimary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Resolution Selector
                            Text("Resolution (DPI)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val dpis = listOf(150, 300, 600)
                                dpis.forEach { dpi ->
                                    FilterChip(
                                        selected = uiState.esclScanResolution == dpi,
                                        onClick = { viewModel.setEsclScanResolution(dpi) },
                                        label = { Text("$dpi DPI") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = WifiPrimary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Start Scan Action Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Execute Network AirScan",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Communicates directly with $scannerIp via eSCL HTTP REST endpoints to pull multi-page document PDFs.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (uiState.isEsclScanning) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = uiState.esclScanStatusMessage,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { uiState.esclScanProgressValue },
                                        modifier = Modifier.fillMaxWidth().height(8.dp),
                                        color = WifiPrimary,
                                        trackColor = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.performEsclNetworkScan(context) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("escl_scan_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = WifiPrimary)
                                ) {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Initiate eSCL Network Scan", fontWeight = FontWeight.Bold)
                                }
                            }

                            if (uiState.lastEsclScannedPdfFile != null) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669))
                                        Column {
                                            Text(
                                                text = "Scanned PDF Ready: ${uiState.lastEsclScannedPdfFile?.name}",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                            )
                                            Text(
                                                text = "Multi-page document successfully captured via eSCL AirScan protocol.",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF065F46))
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.createScannedPrintableFile("PDF")
                                            onNavigateToPreview()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = WifiPrimary)
                                    ) {
                                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Print Direct")
                                    }

                                    OutlinedButton(
                                        onClick = { selectedTabIndex = 2 },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("View Pages")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Viewfinder / Camera Simulation View
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Document Boundary Box Overlay (Edge Detection Visualizer)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (isHd) WifiPrimary else Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = WifiPrimary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Position Document inside Frame",
                                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Auto Edge Detection & Shadow Removal Active",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                                )
                            }
                        }

                        // Top Controls Bar (Flash, HD, Auto Capture)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .align(Alignment.TopCenter),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { viewModel.setFlashEnabled(!isFlash) }) {
                                Icon(
                                    imageVector = if (isFlash) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Flash",
                                    tint = if (isFlash) Color(0xFFF59E0B) else Color.White
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isHd) WifiPrimary else Color.Gray,
                                modifier = Modifier.clickable { viewModel.setHdScanEnabled(!isHd) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Hd, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("HD Scan", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isAutoCapture) Color(0xFF10B981) else Color.DarkGray,
                                modifier = Modifier.clickable { viewModel.setAutoCaptureMode(!isAutoCapture) }
                            ) {
                                Text(
                                    text = if (isAutoCapture) "Auto Capture" else "Manual",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        // Bottom Shutter Controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Gallery Import
                            IconButton(
                                onClick = {
                                    viewModel.addCapturedPage()
                                    Toast.makeText(context, "Added page from Gallery", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.Image, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.size(32.dp))
                            }

                            // Capture Shutter Button
                            Surface(
                                shape = CircleShape,
                                color = WifiPrimary,
                                modifier = Modifier
                                    .size(68.dp)
                                    .clickable {
                                        viewModel.addCapturedPage()
                                        Toast.makeText(context, "Page Captured! (${scannedPages.size + 1} pages total)", Toast.LENGTH_SHORT).show()
                                    }
                                    .testTag("scan_shutter_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .border(3.dp, Color.White, CircleShape)
                                    )
                                }
                            }

                            // View Pages Badge Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = WifiPrimary.copy(alpha = 0.3f),
                                modifier = Modifier.clickable { selectedTabIndex = 2 }
                            ) {
                                Text(
                                    text = "${scannedPages.size} Pages >",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Page Management & Filters
                Column {
                    Text(
                        text = "Scan Filters & Color Correction",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Filters Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val filters = listOf("Original", "Color", "Black & White", "Grayscale", "High Contrast", "Magic Color")
                        items(filters) { filter ->
                            FilterChip(
                                selected = currentFilter == filter,
                                onClick = { viewModel.applyScanFilter(filter) },
                                label = { Text(filter) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = WifiPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Scanned Document Pages (${scannedPages.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (scannedPages.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Camera, contentDescription = null, modifier = Modifier.size(44.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No pages captured yet.", fontWeight = FontWeight.Bold)
                                Text("Switch to 'Camera' tab or tap 'Add Page' below.", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(onClick = { viewModel.addCapturedPage() }) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Capture Page")
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            scannedPages.forEachIndexed { index, page ->
                                ScannedPageCard(
                                    page = page,
                                    onPreview = {
                                        previewPageIndex = index
                                        showScanPreviewDialog = true
                                    },
                                    onRotate = { viewModel.rotateScanPage(page.id) },
                                    onDelete = { viewModel.deleteScanPage(page.id) }
                                )
                            }

                            Button(
                                onClick = { viewModel.addCapturedPage() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add More Pages")
                            }
                        }
                    }
                }
            }

            3 -> {
                // Export & OCR Extraction
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // OCR Text Extraction Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WifiPrimary)
                                    Text(
                                        text = "OCR Text Extraction Engine",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                Button(
                                    onClick = { viewModel.performOcrExtraction() },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    if (uiState.isOcrProcessing) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Text("Run OCR")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (uiState.extractedOcrText.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = uiState.extractedOcrText,
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(uiState.extractedOcrText))
                                            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy Text")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            Toast.makeText(context, "Text ready to share", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Share Text")
                                    }
                                }
                            } else {
                                Text(
                                    text = "Tap 'Run OCR' to extract readable text from scanned document pages.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Direct Print & Export Buttons
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Export & Direct Wireless Print",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Convert ${scannedPages.size.coerceAtLeast(1)} scanned page(s) to PDF and send directly to active printer.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.createScannedPrintableFile("PDF")
                                        onNavigateToPreview()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = WifiPrimary)
                                ) {
                                    Icon(Icons.Default.Print, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Print PDF Direct", fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        viewModel.createScannedPrintableFile("JPG")
                                        Toast.makeText(context, "Scanned document saved as JPG", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Save JPG / PNG")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showScanPreviewDialog) {
            ScanPreviewDialog(
                scannedPages = scannedPages,
                initialPageIndex = previewPageIndex,
                currentFilter = currentFilter,
                onApplyFilter = { viewModel.applyScanFilter(it) },
                onRotatePage = { pageId -> viewModel.rotateScanPage(pageId) },
                onDeletePage = { pageId -> viewModel.deleteScanPage(pageId) },
                onRunOcr = { viewModel.performOcrExtraction() },
                onPrintDirect = {
                    viewModel.createScannedPrintableFile("PDF")
                    showScanPreviewDialog = false
                    onNavigateToPreview()
                },
                onDismiss = { showScanPreviewDialog = false }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ScannedPageCard(
    page: ScannedPage,
    onPreview: () -> Unit,
    onRotate: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPreview() }
            .testTag("scanned_page_card_${page.pageNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = WifiPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "P.${page.pageNumber}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WifiPrimary)
                        )
                    }
                }

                Column {
                    Text(
                        text = "Page #${page.pageNumber}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Filter: ${page.filterApplied} • Rotation: ${page.rotationAngle}°",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onPreview) {
                    Icon(Icons.Default.Visibility, contentDescription = "Preview Page", tint = WifiPrimary)
                }
                IconButton(onClick = onRotate) {
                    Icon(Icons.Default.RotateRight, contentDescription = "Rotate", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun ScanPreviewDialog(
    scannedPages: List<ScannedPage>,
    initialPageIndex: Int,
    currentFilter: String,
    onApplyFilter: (String) -> Unit,
    onRotatePage: (String) -> Unit,
    onDeletePage: (String) -> Unit,
    onRunOcr: () -> Unit,
    onPrintDirect: () -> Unit,
    onDismiss: () -> Unit
) {
    var currentPageIndex by remember { mutableStateOf(initialPageIndex.coerceIn(0, (scannedPages.size - 1).coerceAtLeast(0))) }
    var zoomScale by remember { mutableStateOf(1.0f) }

    val currentPage = scannedPages.getOrNull(currentPageIndex) ?: ScannedPage(
        id = "sample_page",
        pageNumber = 1,
        imagePath = null,
        rotationAngle = 0,
        filterApplied = "Original"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = WifiPrimary)
                        Column {
                            Text(
                                text = "Scan Document Preview",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (scannedPages.isNotEmpty()) "Page ${currentPageIndex + 1} of ${scannedPages.size}" else "Sample Page Preview",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Preview")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Canvas Area with Document Preview, Edge Corner Detection Indicators, Zoom & Navigation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    // Document Sheet Canvas Simulation
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.82f * zoomScale)
                            .height(380.dp * zoomScale)
                            .border(2.dp, WifiPrimary, RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Document Text Simulation Lines & Filter Effect
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Document Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(120.dp)
                                            .height(14.dp)
                                            .background(if (currentFilter == "Black & White") Color.Black else WifiPrimary.copy(alpha = 0.8f))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(10.dp)
                                            .background(Color.Gray.copy(alpha = 0.5f))
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Document Body Lines
                                repeat(12) { idx ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(if (idx % 3 == 0) 0.6f else 0.95f)
                                            .height(8.dp)
                                            .background(
                                                when (currentFilter) {
                                                    "Black & White" -> Color.Black
                                                    "Grayscale" -> Color.DarkGray
                                                    "High Contrast" -> Color.Blue
                                                    "Magic Color" -> Color(0xFF0284C7)
                                                    else -> Color.DarkGray.copy(alpha = 0.7f)
                                                }
                                            )
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // Stamp / Signature Box Simulation
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .border(1.dp, Color.Red, RoundedCornerShape(4.dp))
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("PASSED", fontSize = 8.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Edge Corner Detection Handle Overlays
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.TopStart)
                                    .background(WifiPrimary, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.TopEnd)
                                    .background(WifiPrimary, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.BottomStart)
                                    .background(WifiPrimary, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.BottomEnd)
                                    .background(WifiPrimary, CircleShape)
                            )
                        }
                    }

                    // Left Chevron Page Prev
                    if (currentPageIndex > 0) {
                        IconButton(
                            onClick = { currentPageIndex -= 1 },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Page", tint = Color.White)
                        }
                    }

                    // Right Chevron Page Next
                    if (currentPageIndex < scannedPages.size - 1) {
                        IconButton(
                            onClick = { currentPageIndex += 1 },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Page", tint = Color.White)
                        }
                    }

                    // Floating Zoom Controls Bar
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { zoomScale = (zoomScale - 0.2f).coerceAtLeast(0.6f) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        Text(
                            text = "${(zoomScale * 100).toInt()}%",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = { zoomScale = (zoomScale + 0.2f).coerceAtMost(1.8f) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scan Filter Pills Carousel
                Text("Color Filters", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val filters = listOf("Original", "Color", "Black & White", "Grayscale", "High Contrast", "Magic Color")
                    items(filters) { filter ->
                        FilterChip(
                            selected = currentFilter == filter,
                            onClick = { onApplyFilter(filter) },
                            label = { Text(filter, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WifiPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons (Rotate, Run OCR, Print Direct)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onRotatePage(currentPage.id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.RotateRight, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rotate", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onRunOcr,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Run OCR", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onPrintDirect,
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WifiPrimary)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Print PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
