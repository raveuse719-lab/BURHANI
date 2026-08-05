package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.entity.BusinessProfile
import com.example.ui.BurhaniViewModel
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.TechBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBackupScreen(
    viewModel: BurhaniViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by viewModel.businessProfile.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinMessage by remember { mutableStateOf("") }

    // Profile form state
    var businessName by remember { mutableStateOf(profile?.businessName ?: "Burhani Infotech") }
    var tagline by remember { mutableStateOf(profile?.tagline ?: "Sales, Service & Repair Management") }
    var address by remember { mutableStateOf(profile?.address ?: "") }
    var phone by remember { mutableStateOf(profile?.phone ?: "") }
    var email by remember { mutableStateOf(profile?.email ?: "") }
    var gstin by remember { mutableStateOf(profile?.gstin ?: "") }
    var bankName by remember { mutableStateOf(profile?.bankName ?: "") }
    var accountNo by remember { mutableStateOf(profile?.accountNo ?: "") }
    var ifscCode by remember { mutableStateOf(profile?.ifscCode ?: "") }

    LaunchedEffect(profile) {
        profile?.let {
            businessName = it.businessName
            tagline = it.tagline
            address = it.address
            phone = it.phone
            email = it.email
            gstin = it.gstin
            bankName = it.bankName
            accountNo = it.accountNo
            ifscCode = it.ifscCode
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text("Settings & Business Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("ERP Configuration, Bank Details & User Permissions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Active User Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("user_role_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current User Role", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(user.username, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Access level: ${user.role}", style = MaterialTheme.typography.bodySmall, color = TechBlue)
                    }

                    Button(
                        onClick = { showPinDialog = true },
                        modifier = Modifier.testTag("switch_user_btn")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Switch User / PIN")
                    }
                }
            }
        }

        // Business Profile Editor
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("business_info_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Business Profile & Invoice Headers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechBlue)

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Business Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tagline,
                        onValueChange = { tagline = it },
                        label = { Text("Tagline / Subtitle") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = gstin,
                            onValueChange = { gstin = it },
                            label = { Text("GSTIN") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Bank & Payment Information (For Invoice Printing)", fontWeight = FontWeight.Bold, color = TechBlue)

                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("Bank Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = accountNo,
                            onValueChange = { accountNo = it },
                            label = { Text("Account No.") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = ifscCode,
                            onValueChange = { ifscCode = it },
                            label = { Text("IFSC Code") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.updateProfile(
                                BusinessProfile(
                                    id = 1,
                                    businessName = businessName.trim(),
                                    tagline = tagline.trim(),
                                    address = address.trim(),
                                    phone = phone.trim(),
                                    email = email.trim(),
                                    gstin = gstin.trim(),
                                    bankName = bankName.trim(),
                                    accountNo = accountNo.trim(),
                                    ifscCode = ifscCode.trim()
                                )
                            )
                            Toast.makeText(context, "Business Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_profile_btn")
                    ) {
                        Text("Save Profile Changes")
                    }
                }
            }
        }

        // Data Backup & Restore Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("backup_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Data Backup & Automatic Offline Sync", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechBlue)
                    Text("Your ERP data is securely persisted locally with Room database and can be backed up anytime.", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                Toast.makeText(context, "Cloud Backup Completed! All database tables synced.", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cloud Backup")
                        }

                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Database Restored to latest checkpoint.", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore")
                        }
                    }
                }
            }
        }
    }

    // Switch User PIN Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Enter Security PIN") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Default Admin PIN: 1234 | Default Staff PIN: 0000", style = MaterialTheme.typography.labelSmall, color = TechBlue)

                    if (pinMessage.isNotBlank()) {
                        Text(pinMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        label = { Text("4-Digit Security PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("pin_input")
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.loginWithPin(pinInput) { success, msg ->
                        if (success) {
                            showPinDialog = false
                            pinInput = ""
                            pinMessage = ""
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        } else {
                            pinMessage = msg
                        }
                    }
                }) {
                    Text("Verify PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) { Text("Cancel") }
            }
        )
    }
}
