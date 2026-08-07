package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entity.ScanHistoryEntity
import com.example.ui.WifiViewModel
import com.example.ui.theme.WifiAlertRed
import com.example.ui.theme.WifiPrimary
import com.example.ui.theme.WifiSuccessGreen
import com.example.ui.theme.WifiWarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryProfileScreen(
    viewModel: WifiViewModel
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val scanHistory by viewModel.scanHistory.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    var showOtpDialog by remember { mutableStateOf(false) }

    var alertNewDevice by remember { mutableStateOf(userProfile?.alertNewDevice ?: true) }
    var alertDisconnect by remember { mutableStateOf(userProfile?.alertDisconnect ?: true) }
    var alertOffline by remember { mutableStateOf(userProfile?.alertOffline ?: true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }

        // User Auth Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_profile_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = WifiPrimary,
                                modifier = Modifier.size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = if (userProfile?.isLoggedIn == true) (userProfile?.displayName ?: "User") else "Guest Session",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (userProfile?.isLoggedIn == true) (userProfile?.phoneNumber ?: "") else "Mobile OTP Login Optional",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (userProfile?.isLoggedIn == true) {
                                    viewModel.logoutOrGuest()
                                    Toast.makeText(context, "Logged out to Guest Mode", Toast.LENGTH_SHORT).show()
                                } else {
                                    showOtpDialog = true
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("auth_action_button")
                        ) {
                            Text(text = if (userProfile?.isLoggedIn == true) "Logout" else "OTP Login")
                        }
                    }
                }
            }
        }

        // Notification Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = WifiPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Alert & Notification Settings",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingSwitchRow("Alert when new device connects", alertNewDevice) {
                        alertNewDevice = it
                        viewModel.updateNotificationSettings(alertNewDevice, alertDisconnect, alertOffline)
                    }

                    SettingSwitchRow("Alert when trusted device disconnects", alertDisconnect) {
                        alertDisconnect = it
                        viewModel.updateNotificationSettings(alertNewDevice, alertDisconnect, alertOffline)
                    }

                    SettingSwitchRow("Alert when internet connection lost", alertOffline) {
                        alertOffline = it
                        viewModel.updateNotificationSettings(alertNewDevice, alertDisconnect, alertOffline)
                    }
                }
            }
        }

        // Scan History Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.History, contentDescription = null, tint = WifiPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scan History Logs",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (scanHistory.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearHistory() }) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear History")
                    }
                }
            }
        }

        // History Items
        if (scanHistory.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No prior scan history recorded yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(scanHistory) { history ->
                HistoryCardItem(history = history)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // OTP Auth Dialog
    if (showOtpDialog) {
        OtpLoginDialog(
            onDismiss = { showOtpDialog = false },
            onLoginSuccess = { phone, name ->
                viewModel.loginWithOtp(phone, name)
                showOtpDialog = false
                Toast.makeText(context, "Logged in via Firebase OTP!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun HistoryCardItem(history: ScanHistoryEntity) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(history.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${history.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = history.ssid,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Total Devices: ${history.totalDevicesCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "New: ${history.newDevicesCount}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = WifiWarningAmber
                )
                Text(
                    text = "Gateway: ${history.gatewayIp}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun OtpLoginDialog(
    onDismiss: () -> Unit,
    onLoginSuccess: (phone: String, name: String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("+1 555-0199") }
    var userName by remember { mutableStateOf("Inspector Admin") }
    var otpCode by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (!otpSent) "Firebase Mobile OTP Login" else "Enter 6-Digit OTP") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!otpSent) {
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Your Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("auth_name_input")
                    )
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Mobile Phone Number") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("auth_phone_input")
                    )
                } else {
                    Text("OTP sent via SMS to $phoneNumber. For instant demo use 123456.")
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = { Text("6-Digit OTP Code") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("auth_otp_input")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!otpSent) {
                        otpSent = true
                    } else {
                        onLoginSuccess(phoneNumber, userName)
                    }
                },
                modifier = Modifier.testTag("auth_submit_button")
            ) {
                Text(if (!otpSent) "Send OTP" else "Verify OTP & Login")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
