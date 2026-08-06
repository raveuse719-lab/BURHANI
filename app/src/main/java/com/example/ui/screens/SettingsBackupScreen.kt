package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.entity.BusinessProfile
import com.example.data.entity.User
import com.example.ui.BurhaniViewModel
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.TechBlue
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBackupScreen(
    viewModel: BurhaniViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by viewModel.businessProfile.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val usersList by viewModel.usersList.collectAsState()
    
    val isDriveSyncEnabled by viewModel.isDriveSyncEnabled.collectAsState()
    val driveSyncFolder by viewModel.driveSyncFolder.collectAsState()
    val lastDriveSyncTime by viewModel.lastDriveSyncTime.collectAsState()

    val activeFirmCode by viewModel.activeFirmCode.collectAsState()
    val connectedDevices by viewModel.connectedDevices.collectAsState()
    val staffActivityLogs by viewModel.staffActivityLogs.collectAsState()

    val adminDriveEmail by viewModel.adminCentralDriveEmail.collectAsState()
    val registeredMobile by viewModel.registeredMobile.collectAsState()
    val registeredGmail by viewModel.registeredGmail.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinMessage by remember { mutableStateOf("") }

    var showAddUserDialog by remember { mutableStateOf(false) }
    var showJoinFirmDialog by remember { mutableStateOf(false) }
    var showOnboardingDialog by remember { mutableStateOf(false) }
    var showExportJsonDialog by remember { mutableStateOf(false) }
    var showImportJsonDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }
    var importJsonInput by remember { mutableStateOf("") }

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
            Text("Settings & Multi-Device Sync", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Multi-User Firm Connection, Staff Account Roles & Cloud Data Sync", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Multi-User & Multi-Mobile Firm Sync Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("multi_user_firm_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = BorderStroke(1.dp, TechBlue.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = TechBlue, modifier = Modifier.size(24.dp))
                            Column {
                                Text("Multi-User Firm Connection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechBlue)
                                Text("Work together under 1 firm from different mobile phones", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = GreenSuccess.copy(alpha = 0.15f)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(8.dp).background(GreenSuccess, shape = CircleShape))
                                Text("Sync Active", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = GreenSuccess)
                            }
                        }
                    }

                    // Firm Code Banner Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TechBlue.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("STORE / FIRM CONNECTION CODE", style = MaterialTheme.typography.labelSmall, color = TechBlue, fontWeight = FontWeight.Bold)
                                Text(activeFirmCode, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                                Text("${profile?.businessName ?: "Burhani Infotech"} • Multi-Device Store", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Firm Join Code", activeFirmCode)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Firm Code '$activeFirmCode' copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Firm Code", tint = TechBlue)
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.shareFirmCode(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                            modifier = Modifier.weight(1f).testTag("share_firm_code_btn")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Code (WhatsApp)")
                        }

                        OutlinedButton(
                            onClick = { showJoinFirmDialog = true },
                            modifier = Modifier.weight(1f).testTag("join_firm_btn")
                        ) {
                            Icon(Icons.Default.PhonelinkSetup, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Join Firm Code")
                        }
                    }

                    HorizontalDivider()

                    // Connected Staff Mobile Devices
                    Text("Connected Staff Mobiles (${connectedDevices.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = TechBlue)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        connectedDevices.forEach { dev ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.Smartphone,
                                            contentDescription = null,
                                            tint = if (dev.isOnline) GreenSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Column {
                                            Text(dev.deviceName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            Text("Staff: ${dev.staffName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    RoleBadge(role = dev.role)
                                }
                            }
                        }
                    }

                    // Live Audit Trail / Staff Activity Log
                    if (staffActivityLogs.isNotEmpty()) {
                        HorizontalDivider()
                        Text("Realtime Staff Activity Log (Multi-Mobile Trail)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = TechBlue)

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            staffActivityLogs.take(5).forEach { log ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.History, contentDescription = null, tint = TechBlue, modifier = Modifier.size(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${log.staffName} (${log.deviceName})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                        Text(log.action, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                    val timeAgo = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                                    Text(timeAgo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
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
                        Text("Active Login Account", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(user.username, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            RoleBadge(role = user.role)
                            Text("PIN Protected", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Button(
                        onClick = { showPinDialog = true },
                        modifier = Modifier.testTag("switch_user_btn")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Switch User")
                    }
                }
            }
        }

        // Google Drive Online Data Sync Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("google_drive_sync_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = TechBlue, modifier = Modifier.size(24.dp))
                            Text("Central Google Drive Cloud Sync", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechBlue)
                        }

                        Switch(
                            checked = isDriveSyncEnabled,
                            onCheckedChange = { viewModel.setDriveSyncEnabled(it) }
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TechBlue.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = TechBlue, modifier = Modifier.size(18.dp))
                                Text("Central Drive Account: $adminDriveEmail", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = TechBlue)
                            }
                            Text("Mobile No: $registeredMobile (OTP Verified) • Email: $registeredGmail", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("All sales, repairs & stock data automatically backs up to this central Google Drive account.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    if (isDriveSyncEnabled) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GreenSuccess.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = GreenSuccess)
                                Column {
                                    Text("Central Drive Sync: Active & Backing Up", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = GreenSuccess)
                                    val timeStr = lastDriveSyncTime?.let {
                                        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(it))
                                    } ?: "Never"
                                    Text("Last Drive Backup: $timeStr", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                viewModel.syncNowWithGoogleDrive { success, msg ->
                                    Toast.makeText(context, "Synced data to Central Google Drive ($adminDriveEmail)!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = isDriveSyncEnabled,
                            colors = ButtonDefaults.buttonColors(containerColor = TechBlue),
                            modifier = Modifier.weight(1f).testTag("sync_central_drive_btn")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Drive")
                        }

                        OutlinedButton(
                            onClick = { showOnboardingDialog = true },
                            modifier = Modifier.weight(1f).testTag("open_registration_dialog_btn")
                        ) {
                            Icon(Icons.Default.PhonelinkLock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("OTP Registration")
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = {
                                exportedJsonText = viewModel.exportDatabaseJson()
                                showExportJsonDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export JSON")
                        }

                        OutlinedButton(
                            onClick = {
                                importJsonInput = ""
                                showImportJsonDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Import JSON")
                        }
                    }
                }
            }
        }

        // User Accounts & Role Permissions Card (Admin, Engineer, Partner, Staff)
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("user_accounts_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("User Accounts & Roles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechBlue)
                            Text("Admin, Engineer, Partner & Staff Logins", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = { showAddUserDialog = true },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_new_user_account_btn")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Account")
                        }
                    }

                    HorizontalDivider()

                    usersList.forEach { u ->
                        UserAccountRow(
                            user = u,
                            isCurrent = u.id == user.id || u.username == user.username,
                            onSwitch = {
                                viewModel.switchUserDirectly(u)
                                Toast.makeText(context, "Switched active user to ${u.username}", Toast.LENGTH_SHORT).show()
                            },
                            onDelete = {
                                if (usersList.size <= 1) {
                                    Toast.makeText(context, "Cannot delete the only remaining account!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.deleteUser(u)
                                    Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }

        // Business Profile & Bank Details Editor
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
                    Text("Bank & Payment Information (For Invoices)", fontWeight = FontWeight.Bold, color = TechBlue)

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
                            Toast.makeText(context, "Business Profile Saved Successfully!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_profile_btn")
                    ) {
                        Text("Save Business Profile")
                    }
                }
            }
        }
    }

    // Dialog: Switch User / PIN Verification
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Account Security PIN") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Default Admin PIN: 1234 | Engineer PIN: 1111 | Partner PIN: 2222 | Staff PIN: 0000", style = MaterialTheme.typography.labelSmall, color = TechBlue)

                    if (pinMessage.isNotBlank()) {
                        Text(pinMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        label = { Text("Enter 4-Digit PIN") },
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
                    Text("Verify & Switch")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Add New User Account (Admin, Engineer, Partner, Staff)
    if (showAddUserDialog) {
        var newName by remember { mutableStateOf("") }
        var newPin by remember { mutableStateOf("") }
        var selectedRole by remember { mutableStateOf("ENGINEER") }

        AlertDialog(
            onDismissRequest = { showAddUserDialog = false },
            title = { Text("Add New Account") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Create login credentials for team members with role-based permissions.", style = MaterialTheme.typography.bodySmall)

                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Full Name / Account Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_username_input")
                    )

                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { if (it.length <= 4) newPin = it },
                        label = { Text("4-Digit PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_pin_input")
                    )

                    Text("Select Account Role:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ADMIN", "ENGINEER", "PARTNER", "STAFF").forEach { role ->
                            FilterChip(
                                selected = selectedRole == role,
                                onClick = { selectedRole = role },
                                label = { Text(role, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isBlank() || newPin.length < 4) {
                            Toast.makeText(context, "Please enter valid name and 4-digit PIN", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.saveUser(
                                User(
                                    username = "$newName ($selectedRole)",
                                    role = selectedRole,
                                    pin = newPin
                                )
                            )
                            showAddUserDialog = false
                            Toast.makeText(context, "Account Created for $newName!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("confirm_create_account_btn")
                ) {
                    Text("Create Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddUserDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Export Google Drive JSON Data
    if (showExportJsonDialog) {
        AlertDialog(
            onDismissRequest = { showExportJsonDialog = false },
            title = { Text("Google Drive JSON Backup Payload") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("This JSON string contains your complete ERP data (Customers, Products, Repairs, Users) ready for Google Drive.", style = MaterialTheme.typography.bodySmall)

                    OutlinedTextField(
                        value = exportedJsonText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("ERP Google Drive Backup", exportedJsonText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied Drive Backup JSON to Clipboard!", Toast.LENGTH_SHORT).show()
                        showExportJsonDialog = false
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportJsonDialog = false }) { Text("Close") }
            }
        )
    }

    // Dialog: Import Google Drive JSON Data
    if (showImportJsonDialog) {
        AlertDialog(
            onDismissRequest = { showImportJsonDialog = false },
            title = { Text("Restore Data from Google Drive JSON") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Paste the Google Drive JSON backup string to restore records into your local ERP database.", style = MaterialTheme.typography.bodySmall)

                    OutlinedTextField(
                        value = importJsonInput,
                        onValueChange = { importJsonInput = it },
                        placeholder = { Text("Paste JSON payload here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importJsonInput.isBlank()) {
                            Toast.makeText(context, "Please paste valid JSON text", Toast.LENGTH_SHORT).show()
                        } else {
                            val success = viewModel.restoreDatabaseFromJson(importJsonInput)
                            if (success) {
                                Toast.makeText(context, "Successfully Restored Data from Drive Backup!", Toast.LENGTH_LONG).show()
                                showImportJsonDialog = false
                            } else {
                                Toast.makeText(context, "Failed to parse JSON backup. Ensure valid format.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("Restore Database")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportJsonDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Join Existing Firm Code on another Mobile
    if (showJoinFirmDialog) {
        var inputCode by remember { mutableStateOf(activeFirmCode) }
        var staffNameInput by remember { mutableStateOf("") }
        var staffPinInput by remember { mutableStateOf("") }
        var selectedRole by remember { mutableStateOf("ENGINEER") }

        AlertDialog(
            onDismissRequest = { showJoinFirmDialog = false },
            title = { Text("Join Existing Store Firm") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter the Firm Connection Code provided by your store admin to connect this mobile phone under the firm.", style = MaterialTheme.typography.bodySmall)

                    OutlinedTextField(
                        value = inputCode,
                        onValueChange = { inputCode = it.uppercase() },
                        label = { Text("Firm Code (e.g. FIRM-BURHANI-7860)") },
                        leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("join_firm_code_input")
                    )

                    OutlinedTextField(
                        value = staffNameInput,
                        onValueChange = { staffNameInput = it },
                        label = { Text("Your Staff / Engineer Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("join_firm_staff_name_input")
                    )

                    OutlinedTextField(
                        value = staffPinInput,
                        onValueChange = { if (it.length <= 4) staffPinInput = it },
                        label = { Text("Set 4-Digit Security PIN") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("join_firm_pin_input")
                    )

                    Text("Select Your Account Role:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ENGINEER", "STAFF", "PARTNER", "ADMIN").forEach { role ->
                            FilterChip(
                                selected = selectedRole == role,
                                onClick = { selectedRole = role },
                                label = { Text(role, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputCode.isBlank() || staffNameInput.isBlank()) {
                            Toast.makeText(context, "Please enter Firm Code and Staff Name", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.joinFirmByCode(
                                code = inputCode,
                                staffName = staffNameInput.trim(),
                                role = selectedRole,
                                pin = staffPinInput
                            ) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                if (success) showJoinFirmDialog = false
                            }
                        }
                    },
                    modifier = Modifier.testTag("confirm_join_firm_btn")
                ) {
                    Text("Connect Phone to Firm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinFirmDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showOnboardingDialog) {
        OnboardingRegistrationDialog(
            viewModel = viewModel,
            onDismiss = { showOnboardingDialog = false }
        )
    }
}

@Composable
fun UserAccountRow(
    user: User,
    isCurrent: Boolean,
    onSwitch: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isCurrent) TechBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(getRoleColor(user.role).copy(alpha = 0.2f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (user.role) {
                            "ADMIN" -> Icons.Default.AdminPanelSettings
                            "ENGINEER" -> Icons.Default.Engineering
                            "PARTNER" -> Icons.Default.Handshake
                            else -> Icons.Default.Person
                        },
                        contentDescription = null,
                        tint = getRoleColor(user.role),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(user.username, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        if (isCurrent) {
                            Text("(Active)", style = MaterialTheme.typography.labelSmall, color = TechBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        RoleBadge(role = user.role)
                        Text("PIN: ${user.pin}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!isCurrent) {
                    IconButton(onClick = onSwitch) {
                        Icon(Icons.Default.Login, contentDescription = "Switch Account", tint = TechBlue)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Account", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun RoleBadge(role: String) {
    val color = getRoleColor(role)
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = role,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

fun getRoleColor(role: String): Color {
    return when (role) {
        "ADMIN" -> Color(0xFF673AB7) // Purple
        "ENGINEER" -> Color(0xFFFF9800) // Orange
        "PARTNER" -> Color(0xFF009688) // Teal
        else -> Color(0xFF607D8B) // Blue Grey
    }
}
