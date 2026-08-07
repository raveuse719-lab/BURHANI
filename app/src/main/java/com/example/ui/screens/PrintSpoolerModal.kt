package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrintSpoolerProgress
import com.example.ui.theme.WifiAlertRed
import com.example.ui.theme.WifiPrimary
import com.example.ui.theme.WifiSuccessGreen

@Composable
fun PrintSpoolerModal(
    spoolerProgress: PrintSpoolerProgress,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!spoolerProgress.isPrinting) onDismiss()
        },
        modifier = Modifier.testTag("spooler_progress_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = when {
                        spoolerProgress.isCompleted -> WifiSuccessGreen
                        spoolerProgress.isFailed -> WifiAlertRed
                        else -> WifiPrimary
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when {
                                spoolerProgress.isCompleted -> Icons.Default.CheckCircle
                                spoolerProgress.isFailed -> Icons.Default.Error
                                else -> Icons.Default.Print
                            },
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                Text(
                    text = when {
                        spoolerProgress.isCompleted -> "Print Job Completed!"
                        spoolerProgress.isFailed -> "Printing Failed"
                        else -> "Sending to Printer..."
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${spoolerProgress.fileName} → ${spoolerProgress.printerName}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )

                if (spoolerProgress.isPrinting) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                        CircularProgressIndicator(
                            progress = { spoolerProgress.progressPercent },
                            modifier = Modifier.size(72.dp),
                            color = WifiPrimary,
                            strokeWidth = 6.dp
                        )
                        Text(
                            text = "${(spoolerProgress.progressPercent * 100).toInt()}%",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    LinearProgressIndicator(
                        progress = { spoolerProgress.progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = WifiPrimary
                    )
                }

                Text(
                    text = spoolerProgress.currentStep,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (spoolerProgress.isCompleted) {
                    Text(
                        text = "Document output transmitted cleanly to network printer raster buffer.",
                        style = MaterialTheme.typography.bodySmall.copy(color = WifiSuccessGreen),
                        textAlign = TextAlign.Center
                    )
                } else if (spoolerProgress.isFailed) {
                    Text(
                        text = spoolerProgress.errorMessage ?: "Printer connection timed out. Verify printer is powered on and connected to same Wi-Fi.",
                        style = MaterialTheme.typography.bodySmall.copy(color = WifiAlertRed),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            if (!spoolerProgress.isPrinting) {
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done")
                }
            }
        }
    )
}
