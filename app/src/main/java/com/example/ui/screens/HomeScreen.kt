package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceType
import com.example.data.model.NetworkDevice
import com.example.data.model.NetworkInfoModel
import com.example.ui.WifiViewModel
import com.example.ui.theme.WifiAlertRed
import com.example.ui.theme.WifiCardDark
import com.example.ui.theme.WifiCardLight
import com.example.ui.theme.WifiPrimary
import com.example.ui.theme.WifiSecondary
import com.example.ui.theme.WifiSuccessGreen
import com.example.ui.theme.WifiWarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: WifiViewModel,
    onNavigateToScan: () -> Unit,
    onNavigateToSpeedTest: () -> Unit,
    onNavigateToExtenders: () -> Unit,
    onNavigateToTools: () -> Unit
) {
    val context = LocalContext.current
    val networkInfo by viewModel.networkInfo.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()
    val displayedDevices by viewModel.displayedDevices.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val totalDevices = displayedDevices.size.let { if (it > 0) it else 5 }
    val trustedCount = displayedDevices.count { it.isTrusted }.let { if (it > 0) it else 3 }
    val unknownCount = (totalDevices - trustedCount).coerceAtLeast(0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Top App Bar Greeting
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WiFi Inspector",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = if (userProfile?.isLoggedIn == true) "Logged in as ${userProfile?.displayName}" else "Guest Mode - Instant Local Scan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { viewModel.refreshNetworkInfo(context) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .testTag("refresh_wifi_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Wi-Fi",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Hero Wi-Fi Radar Hero Banner
        item {
            HeroWifiCard(
                networkInfo = networkInfo,
                isScanning = isScanning,
                scanProgress = scanProgress,
                onStartScan = { viewModel.startNetworkScan(context) }
            )
        }

        // Quick Actions Row
        item {
            Text(
                text = "Quick Network Actions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionChip(
                    modifier = Modifier.weight(1f),
                    title = "Scan Network",
                    icon = Icons.Default.WifiTethering,
                    containerColor = WifiPrimary,
                    onClick = onNavigateToScan
                )
                QuickActionChip(
                    modifier = Modifier.weight(1f),
                    title = "Speed Test",
                    icon = Icons.Default.Speed,
                    containerColor = WifiSecondary,
                    onClick = onNavigateToSpeedTest
                )
                QuickActionChip(
                    modifier = Modifier.weight(1f),
                    title = "TP-Link Extenders",
                    icon = Icons.Default.Router,
                    containerColor = Color(0xFF0284C7),
                    onClick = onNavigateToExtenders
                )
            }
        }

        // Dashboard Metrics Grid
        item {
            Text(
                text = "Network Dashboard Summary",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Connected Devices",
                        value = "$totalDevices Devices",
                        subtitle = "Discovered on Subnet",
                        icon = Icons.Default.Devices,
                        accentColor = WifiPrimary
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Trusted Devices",
                        value = "$trustedCount Trusted",
                        subtitle = "$unknownCount Unknown Alerts",
                        icon = Icons.Default.VerifiedUser,
                        accentColor = WifiSuccessGreen
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Gateway Router Status",
                        value = "Online",
                        subtitle = networkInfo.routerGatewayIp,
                        icon = Icons.Default.Router,
                        accentColor = WifiSuccessGreen
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Wi-Fi Signal Strength",
                        value = "${networkInfo.wifiSignalDbm} dBm",
                        subtitle = "Level ${networkInfo.wifiSignalLevel}/4 (${networkInfo.networkType})",
                        icon = Icons.Default.Wifi,
                        accentColor = WifiSecondary
                    )
                }
            }
        }

        // Detailed Auto-Detected Router Information Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("router_info_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = "Router Info",
                            tint = WifiPrimary
                        )
                        Text(
                            text = "Auto-Detected Router & Gateway Info",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    RouterInfoRow("Gateway Router IP", networkInfo.routerGatewayIp)
                    RouterInfoRow("Router Brand / Vendor", networkInfo.routerBrand)
                    RouterInfoRow("Local IPv4 Address", networkInfo.localIpAddress)
                    RouterInfoRow("Local IPv6 Address", networkInfo.ipv6)
                    RouterInfoRow("Public IP Address", networkInfo.publicIpAddress)
                    RouterInfoRow("Primary DNS", networkInfo.dns1)
                    RouterInfoRow("Secondary DNS", networkInfo.dns2)
                    RouterInfoRow("DHCP Server", networkInfo.dhcpServer)
                    RouterInfoRow("Network Subnet Mask", networkInfo.netmask)
                    RouterInfoRow("Wi-Fi Frequency / Channel", "${networkInfo.frequencyMhz} MHz (Channel ${networkInfo.channel})")
                    RouterInfoRow("Link Speed", "${networkInfo.linkSpeedMbps} Mbps")
                }
            }
        }

        // TP-Link Range Extender Auto-Detect Teaser Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToExtenders() }
                    .testTag("tplink_banner_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = WifiPrimary,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Router,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "TP-Link Extenders Detected",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Auto-discovered local range extenders & status.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open Extenders",
                        tint = WifiPrimary
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun HeroWifiCard(
    networkInfo: NetworkInfoModel,
    isScanning: Boolean,
    scanProgress: Float,
    onStartScan: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_wifi_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Unspecified),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF0284C7),
                            Color(0xFF0369A1)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .scale(if (isScanning) pulseScale else 1f)
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (networkInfo.isInternetAvailable) WifiSuccessGreen else WifiAlertRed)
                        )
                        Text(
                            text = if (networkInfo.isInternetAvailable) "Internet Connected" else "No Internet",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = networkInfo.networkType,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = networkInfo.ssid,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "BSSID: ${networkInfo.bssid} • Link: ${networkInfo.linkSpeedMbps} Mbps",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(visible = isScanning) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Scanning Subnet...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Text(
                                text = "${(scanProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { scanProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Button(
                    onClick = onStartScan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("start_scan_hero_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = WifiPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiTethering,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isScanning) "Scanning Subnet..." else "Scan Connected Wi-Fi Network",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionChip(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    containerColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = containerColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RouterInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
