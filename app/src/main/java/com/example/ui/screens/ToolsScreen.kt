package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiChannel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.NetworkInfoModel
import com.example.data.model.NetworkQualityScore
import com.example.data.model.PingResult
import com.example.data.model.SpeedTestResult
import com.example.data.model.SpeedTestState
import com.example.ui.WifiViewModel
import com.example.ui.theme.WifiAlertRed
import com.example.ui.theme.WifiPrimary
import com.example.ui.theme.WifiSecondary
import com.example.ui.theme.WifiSuccessGreen
import com.example.ui.theme.WifiWarningAmber

@Composable
fun ToolsScreen(
    viewModel: WifiViewModel
) {
    val networkInfo by viewModel.networkInfo.collectAsState()
    val pingResult by viewModel.pingResult.collectAsState()
    val isPinging by viewModel.isPinging.collectAsState()
    val speedTestResult by viewModel.speedTestResult.collectAsState()
    val networkQuality by viewModel.networkQuality.collectAsState()

    var selectedToolTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Speed Test", "Ping Tool", "Channel Info", "Quality Check")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Network Tools Suite",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tool Tab Switcher
        TabRow(
            selectedTabIndex = selectedToolTabIndex,
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            indicator = {},
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedToolTabIndex == index,
                    onClick = { selectedToolTabIndex = index },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedToolTabIndex == index) WifiPrimary else Color.Transparent)
                        .testTag("tool_tab_$index"),
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedToolTabIndex == index) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedToolTabIndex) {
            0 -> SpeedTestTab(speedTestResult = speedTestResult, onStartSpeedTest = { viewModel.runSpeedTest() })
            1 -> PingToolTab(pingResult = pingResult, isPinging = isPinging, gatewayIp = networkInfo.routerGatewayIp, onRunPing = { viewModel.runPing(it) })
            2 -> ChannelInfoTab(networkInfo = networkInfo)
            3 -> QualityCheckTab(networkQuality = networkQuality, networkInfo = networkInfo)
        }
    }
}

@Composable
fun SpeedTestTab(
    speedTestResult: com.example.data.model.SpeedTestResult,
    onStartSpeedTest: () -> Unit
) {
    val animatedDownSpeed by animateFloatAsState(
        targetValue = speedTestResult.downloadMbps,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "downSpeed"
    )

    val animatedUpSpeed by animateFloatAsState(
        targetValue = speedTestResult.uploadMbps,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "upSpeed"
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("speed_test_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Internet Speed Test",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    if (speedTestResult.serverName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = WifiPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${speedTestResult.serverName} • ${speedTestResult.serverLocation}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Speed Test Animated Arc Gauge
                    Box(
                        modifier = Modifier.size(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val gaugeAngle = (speedTestResult.progress * 240f).coerceIn(0f, 240f)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = Color.LightGray.copy(alpha = 0.3f),
                                startAngle = 150f,
                                sweepAngle = 240f,
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round),
                                size = Size(size.width, size.height)
                            )
                            drawArc(
                                color = WifiPrimary,
                                startAngle = 150f,
                                sweepAngle = gaugeAngle,
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round),
                                size = Size(size.width, size.height)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format("%.1f", if (speedTestResult.testState == SpeedTestState.UPLOADING) animatedUpSpeed else animatedDownSpeed),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WifiPrimary
                                )
                            )
                            Text(
                                text = "Mbps",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = when (speedTestResult.testState) {
                            SpeedTestState.IDLE -> "Tap start to test speed"
                            SpeedTestState.SELECTING_SERVER -> "Selecting nearest low-latency server..."
                            SpeedTestState.PINGING -> "Measuring ping latency & jitter..."
                            SpeedTestState.DOWNLOADING -> "Testing Download Speed..."
                            SpeedTestState.UPLOADING -> "Testing Upload Speed..."
                            SpeedTestState.COMPLETED -> "Speed Test Complete"
                            SpeedTestState.ERROR -> "Test Failed"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (speedTestResult.testState == SpeedTestState.ERROR) WifiAlertRed else MaterialTheme.colorScheme.onSurface
                    )

                    if (speedTestResult.testState == SpeedTestState.ERROR && !speedTestResult.errorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = speedTestResult.errorMessage ?: "Speed test failed",
                            style = MaterialTheme.typography.bodySmall,
                            color = WifiAlertRed
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Detailed Metrics Grid: DOWNLOAD, UPLOAD, PING, JITTER, PACKET LOSS
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("DOWNLOAD", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${String.format("%.1f", animatedDownSpeed)} Mbps",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = WifiPrimary
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("UPLOAD", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${String.format("%.1f", animatedUpSpeed)} Mbps",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = WifiSecondary
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("PING", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${speedTestResult.pingMs} ms",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = WifiSuccessGreen
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("JITTER", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${speedTestResult.jitterMs} ms",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("LOSS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${String.format("%.1f", speedTestResult.packetLossPercent)} %",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (speedTestResult.packetLossPercent > 0f) WifiAlertRed else WifiSuccessGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val canStartTest = speedTestResult.testState == SpeedTestState.IDLE ||
                            speedTestResult.testState == SpeedTestState.COMPLETED ||
                            speedTestResult.testState == SpeedTestState.ERROR

                    Button(
                        onClick = onStartSpeedTest,
                        enabled = canStartTest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("run_speed_test_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (speedTestResult.testState == SpeedTestState.ERROR) "Retry Speed Test" else "Start Speed Test")
                    }
                }
            }
        }
    }
}

@Composable
fun PingToolTab(
    pingResult: com.example.data.model.PingResult?,
    isPinging: Boolean,
    gatewayIp: String,
    onRunPing: (String) -> Unit
) {
    var targetHostInput by remember { mutableStateOf(gatewayIp) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ICMP Ping Diagnostic Tool",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = targetHostInput,
                        onValueChange = { targetHostInput = it },
                        label = { Text("Target IP / Host (e.g., google.com or 192.168.1.1)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ping_host_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onRunPing(targetHostInput) },
                        enabled = !isPinging,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_ping_button")
                    ) {
                        Text(text = if (isPinging) "Pinging Host..." else "Ping Target")
                    }
                }
            }
        }

        pingResult?.let { res ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (res.isSuccess) Icons.Default.CheckCircle else Icons.Default.Assessment,
                                contentDescription = null,
                                tint = if (res.isSuccess) WifiSuccessGreen else Color.Red
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ping Results for ${res.targetHost}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ToolDetailRow("Resolved IP", res.ipAddress)
                        ToolDetailRow("Average Latency", "${res.timeMs} ms")
                        ToolDetailRow("Min Latency", "${res.minMs} ms")
                        ToolDetailRow("Max Latency", "${res.maxMs} ms")
                        ToolDetailRow("Packet Loss", "${res.packetLossPercent}%")
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelInfoTab(networkInfo: com.example.data.model.NetworkInfoModel) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.WifiChannel, contentDescription = null, tint = WifiPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Wi-Fi Channel & Frequency Analyzer",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ToolDetailRow("Current Frequency", "${networkInfo.frequencyMhz} MHz")
                    ToolDetailRow("Current Channel Number", "Channel ${networkInfo.channel}")
                    ToolDetailRow("Network Frequency Band", networkInfo.networkType)
                    ToolDetailRow("Signal RSSI", "${networkInfo.wifiSignalDbm} dBm")
                    ToolDetailRow("Channel Rating", "Grade A (Low Interference)")
                }
            }
        }
    }
}

@Composable
fun QualityCheckTab(
    networkQuality: com.example.data.model.NetworkQualityScore,
    networkInfo: com.example.data.model.NetworkInfoModel
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Network Overall Quality Grade",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = WifiPrimary,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = networkQuality.grade,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                            Text(
                                text = networkQuality.ratingText,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    QualityMetricBar("Latency Rating", networkQuality.latencyScore)
                    QualityMetricBar("Bandwidth Speed Rating", networkQuality.speedScore)
                    QualityMetricBar("Security Status", networkQuality.securityScore)
                    QualityMetricBar("Signal Stability", networkQuality.stabilityScore)
                }
            }
        }
    }
}

@Composable
fun QualityMetricBar(label: String, score: Int) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Text(text = "$score / 100", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score.toFloat() / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = WifiPrimary
        )
    }
}

@Composable
fun ToolDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
    }
}
