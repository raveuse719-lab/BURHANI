package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.DeviceType
import com.example.data.model.NetworkDevice
import com.example.ui.WifiViewModel
import com.example.ui.theme.WifiAlertRed
import com.example.ui.theme.WifiPrimary
import com.example.ui.theme.WifiSuccessGreen
import com.example.ui.theme.WifiWarningAmber

@Composable
fun ScannerScreen(
    viewModel: WifiViewModel
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isScanning by viewModel.isScanning.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()
    val devices by viewModel.displayedDevices.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    var deviceToRename by remember { mutableStateOf<NetworkDevice?>(null) }
    var selectedDeviceDetails by remember { mutableStateOf<NetworkDevice?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Local Network Scanner",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${devices.size} Discovered Devices",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { viewModel.startNetworkScan(context) },
                enabled = !isScanning,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("scan_network_top_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isScanning) "Scanning..." else "Scan Now")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("scanner_search_input"),
            placeholder = { Text("Search by IP, Name, MAC, Vendor...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val filters = listOf(
                "ALL" to "All (${devices.size})",
                "TRUSTED" to "Trusted",
                "UNKNOWN" to "Unknown",
                "EXTENDERS" to "Range Extenders",
                "ONLINE" to "Online"
            )
            items(filters) { (key, label) ->
                FilterChip(
                    selected = selectedFilter == key,
                    onClick = { viewModel.setFilter(key) },
                    label = { Text(label) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WifiPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Scan Progress Indicator
        AnimatedVisibility(visible = isScanning) {
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { scanProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = WifiPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Scanning subnet 192.168.1.1 through .254 (${(scanProgress * 100).toInt()}%)...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Device List
        if (devices.isEmpty() && !isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No devices found matching filters",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.startNetworkScan(context) }) {
                        Text("Start Network Scan")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(devices, key = { it.ipAddress + it.macAddress }) { device ->
                    DeviceCardItem(
                        device = device,
                        onTrustToggle = { viewModel.toggleDeviceTrust(device) },
                        onRenameClick = { deviceToRename = device },
                        onCopyMac = {
                            clipboardManager.setText(AnnotatedString(device.macAddress))
                            Toast.makeText(context, "Copied MAC Address to Clipboard", Toast.LENGTH_SHORT).show()
                        },
                        onDetailsClick = { selectedDeviceDetails = device }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    // Rename Dialog
    deviceToRename?.let { dev ->
        var newNameInput by remember { mutableStateOf(dev.displayTitle) }
        AlertDialog(
            onDismissRequest = { deviceToRename = null },
            title = { Text("Rename Connected Device") },
            text = {
                Column {
                    Text("Enter a friendly custom label for ${dev.ipAddress}:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newNameInput,
                        onValueChange = { newNameInput = it },
                        label = { Text("Device Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("rename_device_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.renameDevice(dev, newNameInput)
                        deviceToRename = null
                        Toast.makeText(context, "Saved Device Label", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("save_rename_button")
                ) {
                    Text("Save Label")
                }
            },
            dismissButton = {
                TextButton(onClick = { deviceToRename = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Device Details Dialog
    selectedDeviceDetails?.let { dev ->
        AlertDialog(
            onDismissRequest = { selectedDeviceDetails = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = getDeviceIcon(dev.deviceType), contentDescription = null, tint = WifiPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = dev.displayTitle)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Device Type: ${dev.deviceType.displayName}")
                    Text("IP Address: ${dev.ipAddress}")
                    Text("MAC Address: ${dev.macAddress}")
                    Text("Manufacturer: ${dev.manufacturer}")
                    Text("Ping Response: ${dev.responseTimeMs} ms")
                    Text("Status: ${if (dev.isOnline) "Active Online" else "Offline"}")
                    Text("Trust State: ${if (dev.isTrusted) "Trusted Device" else "Unknown / Untrusted"}")
                }
            },
            confirmButton = {
                Button(onClick = { selectedDeviceDetails = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun DeviceCardItem(
    device: NetworkDevice,
    onTrustToggle: () -> Unit,
    onRenameClick: () -> Unit,
    onCopyMac: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailsClick() }
            .testTag("device_card_${device.ipAddress.replace(".", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (device.isTrusted) WifiPrimary.copy(alpha = 0.15f) else WifiWarningAmber.copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = getDeviceIcon(device.deviceType),
                                contentDescription = null,
                                tint = if (device.isTrusted) WifiPrimary else WifiWarningAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = device.displayTitle,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = "${device.ipAddress} • ${device.manufacturer}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                // Trust Status Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (device.isTrusted) WifiSuccessGreen.copy(alpha = 0.15f) else WifiWarningAmber.copy(alpha = 0.15f),
                    modifier = Modifier.clickable { onTrustToggle() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (device.isTrusted) Icons.Default.VerifiedUser else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (device.isTrusted) WifiSuccessGreen else WifiWarningAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (device.isTrusted) "Trusted" else "Unknown",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (device.isTrusted) WifiSuccessGreen else WifiWarningAmber
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub info row: MAC Address + Latency
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MAC: ${device.macAddress}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onCopyMac,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy MAC",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Text(
                    text = "${device.responseTimeMs} ms ping",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = WifiPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onRenameClick, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Rename",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onTrustToggle) {
                    Text(text = if (device.isTrusted) "Mark Unknown" else "Mark Trusted")
                }
            }
        }
    }
}

fun getDeviceIcon(type: DeviceType): ImageVector {
    return when (type) {
        DeviceType.PHONE -> Icons.Default.PhoneAndroid
        DeviceType.LAPTOP -> Icons.Default.Laptop
        DeviceType.DESKTOP -> Icons.Default.DesktopWindows
        DeviceType.CCTV -> Icons.Default.Videocam
        DeviceType.SMART_TV -> Icons.Default.Tv
        DeviceType.PRINTER -> Icons.Default.Print
        DeviceType.ROUTER -> Icons.Default.Router
        DeviceType.RANGE_EXTENDER -> Icons.Default.SettingsInputAntenna
        DeviceType.IOT -> Icons.Default.DeveloperBoard
        DeviceType.UNKNOWN -> Icons.Default.HelpOutline
    }
}
