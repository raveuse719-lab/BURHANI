package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.PrinterEntity
import com.example.data.model.PrintSettings
import com.example.data.model.PrintableFile
import com.example.ui.PrintViewModel
import com.example.ui.theme.WifiPrimary
import com.example.ui.theme.WifiSuccessGreen
import com.example.util.DocumentPreviewEngine
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.util.AndroidSystemPrintEngine

@Composable
fun PrintPreviewScreen(
    viewModel: PrintViewModel,
    onNavigateToDiscovery: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val activePrinter = uiState.activePrinter
    val file = uiState.selectedFile
    val settings = uiState.printSettings
    val currentPage = uiState.previewPage

    if (file == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No document selected for printing.")
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Top Header & Selected Printer Bar
            item {
                ActivePrinterBanner(
                    printer = activePrinter,
                    onChangePrinter = onNavigateToDiscovery
                )
            }

            // Document Canvas Page Preview
            item {
                DocumentCanvasPreview(
                    file = file,
                    settings = settings,
                    currentPage = currentPage,
                    onNextPage = { viewModel.setPreviewPage(currentPage + 1) },
                    onPrevPage = { viewModel.setPreviewPage(currentPage - 1) }
                )
            }

            // Print Settings Form
            item {
                PrintOptionsPanel(
                    settings = settings,
                    onSettingsChange = { newSettings -> viewModel.updatePrintSettings(newSettings) }
                )
            }

            item { Spacer(modifier = Modifier.height(110.dp)) }
        }

        // Bottom Fixed PRINT Floating Actions Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            tonalElevation = 12.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Primary System Print Button (Triggers Android PrintManager Spooler)
                Button(
                    onClick = {
                        AndroidSystemPrintEngine.printViaSystemSpooler(context, file, settings)
                        viewModel.triggerPrint()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("trigger_system_print_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WifiPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "System Direct Print",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PRINT NOW (${settings.copies} Copy • ${settings.paperSize})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                // Secondary Wi-Fi Network RAW Print Button
                OutlinedButton(
                    onClick = { viewModel.triggerPrint() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("trigger_network_raw_print_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        contentDescription = "Wi-Fi Socket Print",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Wi-Fi IP Socket Print (${activePrinter?.ipAddress ?: "Direct IP"})",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun ActivePrinterBanner(
    printer: PrinterEntity?,
    onChangePrinter: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_printer_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = WifiPrimary,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Print, contentDescription = null, tint = Color.White)
                    }
                }

                Column {
                    Text(
                        text = printer?.name ?: "No Printer Selected",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (printer != null) "IP: ${printer.ipAddress} • ${printer.protocol}" else "Tap change to select target printer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            OutlinedButton(
                onClick = onChangePrinter,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Change", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DocumentCanvasPreview(
    file: PrintableFile,
    settings: PrintSettings,
    currentPage: Int,
    onNextPage: () -> Unit,
    onPrevPage: () -> Unit
) {
    val totalPages = file.pagesCount
    val pageLines = DocumentPreviewEngine.getPagePreviewLines(file, currentPage)

    val paperAspectRatio = if (settings.orientation == "Landscape") 1.414f else 0.707f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Document Preview",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${file.name} (${file.type})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Simulated Paper Sheet Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(paperAspectRatio)
                    .shadow(8.dp, shape = RoundedCornerShape(4.dp))
                    .background(
                        color = if (settings.colorMode == "Black & White") Color(0xFFF1F5F9) else Color.White,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        1.dp,
                        if (settings.colorMode == "Black & White") Color.Gray else WifiPrimary,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(12.dp)
            ) {
                if (!file.uriString.isNullOrBlank()) {
                    // Render Real Photo / Image Document Preview
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val colorFilter = if (settings.colorMode == "Black & White") {
                            ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                        } else null

                        val contentScale = when (settings.fitToPage) {
                            "Fill Page" -> ContentScale.Crop
                            "100% Scale" -> ContentScale.None
                            else -> ContentScale.Fit
                        }

                        AsyncImage(
                            model = file.uriString,
                            contentDescription = "Selected Photo / Document Preview",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = contentScale,
                            colorFilter = colorFilter
                        )

                        // Top Overlay Badge
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.65f)
                        ) {
                            Text(
                                text = "${file.type} • Page ${currentPage + 1}/$totalPages",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                } else {
                    // Render Text / Sample Document Preview Sheet
                    Column {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = file.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (settings.colorMode == "Black & White") Color.DarkGray else WifiPrimary
                                )
                            )
                            Text(
                                text = "Page ${currentPage + 1}/$totalPages",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.LightGray)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Text Content Lines
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            pageLines.forEach { line: String ->
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (settings.colorMode == "Black & White") Color.Black else Color(0xFF0F172A)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Page Navigation Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = onPrevPage,
                    enabled = currentPage > 0
                ) {
                    Icon(Icons.Default.ArrowBackIos, contentDescription = "Prev Page")
                }

                Text(
                    text = "Page ${currentPage + 1} of $totalPages",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                IconButton(
                    onClick = onNextPage,
                    enabled = currentPage < totalPages - 1
                ) {
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = "Next Page")
                }
            }
        }
    }
}

@Composable
fun PrintOptionsPanel(
    settings: PrintSettings,
    onSettingsChange: (PrintSettings) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Print Settings & Configuration",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Number of Copies Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Number of Copies",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (settings.copies > 1) {
                                onSettingsChange(settings.copy(copies = settings.copies - 1))
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Minus")
                    }

                    Text(
                        text = "${settings.copies}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    IconButton(
                        onClick = {
                            if (settings.copies < 99) {
                                onSettingsChange(settings.copy(copies = settings.copies + 1))
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Plus")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Paper Size Chips
            Text(
                text = "Paper Size",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("A4", "A5", "Letter", "Legal").forEach { size ->
                    FilterChip(
                        selected = settings.paperSize == size,
                        onClick = { onSettingsChange(settings.copy(paperSize = size)) },
                        label = { Text(size) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WifiPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Color Mode Toggle
            Text(
                text = "Color Mode",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Color", "Black & White").forEach { mode ->
                    FilterChip(
                        selected = settings.colorMode == mode,
                        onClick = { onSettingsChange(settings.copy(colorMode = mode)) },
                        label = { Text(mode) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WifiPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Orientation
            Text(
                text = "Page Orientation",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Portrait", "Landscape").forEach { orient ->
                    FilterChip(
                        selected = settings.orientation == orient,
                        onClick = { onSettingsChange(settings.copy(orientation = orient)) },
                        label = { Text(orient) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WifiPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Duplex Printing
            Text(
                text = "Duplex Printing (Two-Sided)",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Simplex", "Duplex Long Edge", "Duplex Short Edge").forEach { dup ->
                    FilterChip(
                        selected = settings.duplexMode == dup,
                        onClick = { onSettingsChange(settings.copy(duplexMode = dup)) },
                        label = { Text(dup, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WifiPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fit to Page Scaling
            Text(
                text = "Scaling & Fit",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Fit to Page", "Fill Page", "100% Scale").forEach { fit ->
                    FilterChip(
                        selected = settings.fitToPage == fit,
                        onClick = { onSettingsChange(settings.copy(fitToPage = fit)) },
                        label = { Text(fit, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WifiPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}
