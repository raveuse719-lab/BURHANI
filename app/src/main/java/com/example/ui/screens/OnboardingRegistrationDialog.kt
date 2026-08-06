package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.BurhaniViewModel
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.TechBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingRegistrationDialog(
    viewModel: BurhaniViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val businessProfile by viewModel.businessProfile.collectAsState()
    val adminDriveEmail by viewModel.adminCentralDriveEmail.collectAsState()
    val isOtpVerifiedState by viewModel.isMobileOtpVerified.collectAsState()
    val generatedOtp by viewModel.generatedOtp.collectAsState()

    var mobileInput by remember { mutableStateOf(businessProfile?.phone ?: "+91 98765 43210") }
    var emailInput by remember { mutableStateOf(businessProfile?.email ?: adminDriveEmail) }
    var firmNameInput by remember { mutableStateOf(businessProfile?.businessName ?: "Burhani Infotech & Services") }
    var ownerNameInput by remember { mutableStateOf("Abdeali Makda") }
    var gstInput by remember { mutableStateOf(businessProfile?.gstin ?: "24ABCDE1234F1Z5") }
    var addressInput by remember { mutableStateOf(businessProfile?.address ?: "Main Market, Station Road") }

    var otpInput by remember { mutableStateOf("786910") }
    var isOtpSent by remember { mutableStateOf(true) }
    var isVerified by remember { mutableStateOf(isOtpVerifiedState) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("onboarding_dialog_surface"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
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
                            shape = CircleShape,
                            color = TechBlue.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = TechBlue)
                            }
                        }
                        Column {
                            Text(
                                "App Registration & Drive Sync",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TechBlue
                            )
                            Text(
                                "Setup Mobile OTP Login & Central Cloud Backup",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Central Google Drive Auto-Sync Info Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = TechBlue.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, TechBlue.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = TechBlue, modifier = Modifier.size(32.dp))
                            Column {
                                Text(
                                    "CENTRAL GOOGLE DRIVE DATA SYNC ACTIVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TechBlue
                                )
                                Text(
                                    "Target Admin Account: $adminDriveEmail",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "When anyone uses this app, all sales, stock & repair data automatically backs up to this central Google Drive.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Card 1: Mobile Number & OTP Verification
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("otp_verification_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("1. Mobile Number & OTP Login", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = TechBlue)
                                if (isVerified) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = GreenSuccess.copy(alpha = 0.15f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.Verified, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(16.dp))
                                            Text("OTP Verified", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = GreenSuccess)
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = mobileInput,
                                onValueChange = {
                                    mobileInput = it
                                    isVerified = false
                                },
                                label = { Text("Mobile Number (with country code)") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("mobile_number_input")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = otpInput,
                                    onValueChange = { otpInput = it },
                                    label = { Text("6-Digit OTP") },
                                    placeholder = { Text("e.g. 786910") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("otp_number_input")
                                )

                                Button(
                                    onClick = {
                                        viewModel.sendMobileOtp(mobileInput) { sentOtp ->
                                            isOtpSent = true
                                            otpInput = sentOtp
                                            Toast.makeText(context, "OTP $sentOtp sent to $mobileInput via SMS!", Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TechBlue),
                                    modifier = Modifier.testTag("send_otp_btn")
                                ) {
                                    Text("Send OTP")
                                }

                                Button(
                                    onClick = {
                                        viewModel.verifyMobileOtp(otpInput) { verified ->
                                            if (verified) {
                                                isVerified = true
                                                Toast.makeText(context, "Mobile Number Verified Successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Invalid OTP! Try 786910 or 123456", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                    modifier = Modifier.testTag("verify_otp_btn")
                                ) {
                                    Text("Verify")
                                }
                            }
                        }
                    }

                    // Card 2: Firm & User Details
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("firm_details_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("2. Default Firm & Admin Profile Data", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = TechBlue)

                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Gmail Address (Google Drive Backup Target)") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("gmail_address_input")
                            )

                            OutlinedTextField(
                                value = firmNameInput,
                                onValueChange = { firmNameInput = it },
                                label = { Text("Firm / Store Name") },
                                leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("firm_name_input")
                            )

                            OutlinedTextField(
                                value = ownerNameInput,
                                onValueChange = { ownerNameInput = it },
                                label = { Text("Owner / Admin Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("owner_name_input")
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = gstInput,
                                    onValueChange = { gstInput = it },
                                    label = { Text("GST Number") },
                                    leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("gst_number_input")
                                )

                                OutlinedTextField(
                                    value = addressInput,
                                    onValueChange = { addressInput = it },
                                    label = { Text("Store Address") },
                                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("store_address_input")
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer Actions
                Button(
                    onClick = {
                        viewModel.completeOnboardingRegistration(
                            mobile = mobileInput,
                            email = emailInput,
                            firmName = firmNameInput,
                            ownerName = ownerNameInput,
                            gstNumber = gstInput,
                            address = addressInput
                        ) { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("complete_registration_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TechBlue)
                ) {
                    Icon(Icons.Default.CloudSync, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Register & Connect to Central Google Drive", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
