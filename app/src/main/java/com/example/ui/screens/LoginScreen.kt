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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BurhaniViewModel
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.TechBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: BurhaniViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val businessProfile by viewModel.businessProfile.collectAsState()
    val isFirstSetupDone by viewModel.isFirstSetupDone.collectAsState()
    val adminDriveEmail by viewModel.adminCentralDriveEmail.collectAsState()

    var mobileInput by remember { mutableStateOf("9726149451") }
    var otpInput by remember { mutableStateOf("786910") }
    var isOtpSent by remember { mutableStateOf(false) }

    // Setup Form States (shown if first login)
    var showSetupForm by remember { mutableStateOf(false) }

    var firmNameInput by remember { mutableStateOf(businessProfile?.businessName ?: "BI Service ERP") }
    var ownerNameInput by remember { mutableStateOf(businessProfile?.ownerName ?: "Abdeali Makda") }
    var setupMobileInput by remember { mutableStateOf(mobileInput) }
    var emailInput by remember { mutableStateOf(businessProfile?.email ?: adminDriveEmail) }
    var gstInput by remember { mutableStateOf(businessProfile?.gstin ?: "") }
    var addressInput by remember { mutableStateOf(businessProfile?.address ?: "Shop No. 4, Tech Plaza, Station Road") }
    var cityInput by remember { mutableStateOf(businessProfile?.city ?: "Surat") }
    var stateInput by remember { mutableStateOf(businessProfile?.state ?: "Gujarat") }
    var pincodeInput by remember { mutableStateOf(businessProfile?.pincode ?: "395003") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("login_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App Logo Header
                Surface(
                    shape = CircleShape,
                    color = TechBlue.copy(alpha = 0.15f),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "BI Service ERP Logo",
                            tint = TechBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Text(
                    text = "BI Service ERP",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TechBlue,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (!showSetupForm) "Multi-User Sales, Repair & Inventory Management" else "First-Time Business & Store Setup",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                if (!showSetupForm) {
                    // STEP 1: MOBILE NUMBER & OTP LOGIN
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TechBlue.copy(alpha = 0.08f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = TechBlue)
                                Column {
                                    Text(
                                        text = "Secure Mobile OTP Authentication",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TechBlue
                                    )
                                    Text(
                                        text = "Default Admin Mobile: 9726149451",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = mobileInput,
                            onValueChange = { mobileInput = it },
                            label = { Text("Mobile Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_mobile_input"),
                            shape = RoundedCornerShape(14.dp)
                        )

                        if (!isOtpSent) {
                            Button(
                                onClick = {
                                    if (mobileInput.isBlank()) {
                                        Toast.makeText(context, "Please enter mobile number", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.sendMobileOtp(mobileInput) { sentOtp ->
                                            isOtpSent = true
                                            otpInput = sentOtp
                                            Toast.makeText(context, "OTP Sent: $sentOtp", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("send_otp_btn"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TechBlue)
                            ) {
                                Icon(Icons.Default.Sms, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Get Verification OTP", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        } else {
                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { otpInput = it },
                                label = { Text("Enter 6-Digit OTP") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_otp_input"),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Button(
                                onClick = {
                                    viewModel.verifyMobileOtp(otpInput) { success, requiresSetup ->
                                        if (success) {
                                            if (requiresSetup) {
                                                setupMobileInput = mobileInput
                                                showSetupForm = true
                                                Toast.makeText(context, "OTP Verified! Please complete store setup.", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Welcome to BI Service ERP!", Toast.LENGTH_SHORT).show()
                                                onLoginSuccess()
                                            }
                                        } else {
                                            Toast.makeText(context, "Invalid OTP! Enter 786910 or 123456", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("verify_and_login_btn"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Verify OTP & Open App", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            TextButton(
                                onClick = {
                                    viewModel.sendMobileOtp(mobileInput) { newOtp ->
                                        otpInput = newOtp
                                        Toast.makeText(context, "Resent OTP: $newOtp", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Resend OTP", color = TechBlue)
                            }
                        }
                    }
                } else {
                    // STEP 2: FIRST-TIME STORE SETUP FORM
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GreenSuccess.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = GreenSuccess)
                                Text(
                                    text = "First Login Setup — Enter your business profile details below.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenSuccess
                                )
                            }
                        }

                        OutlinedTextField(
                            value = firmNameInput,
                            onValueChange = { firmNameInput = it },
                            label = { Text("Firm / Store Name *") },
                            leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("setup_firm_name"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = ownerNameInput,
                            onValueChange = { ownerNameInput = it },
                            label = { Text("Owner Name *") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("setup_owner_name"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = setupMobileInput,
                            onValueChange = { setupMobileInput = it },
                            label = { Text("Mobile Number *") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("setup_mobile"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address (Google Drive Account) *") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("setup_email"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = gstInput,
                            onValueChange = { gstInput = it },
                            label = { Text("GST Number (Optional)") },
                            leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("setup_gst"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = addressInput,
                            onValueChange = { addressInput = it },
                            label = { Text("Business Address *") },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("setup_address"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = cityInput,
                                onValueChange = { cityInput = it },
                                label = { Text("City") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("setup_city"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = stateInput,
                                onValueChange = { stateInput = it },
                                label = { Text("State") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("setup_state"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = pincodeInput,
                                onValueChange = { pincodeInput = it },
                                label = { Text("PIN Code") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("setup_pincode"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (firmNameInput.isBlank() || ownerNameInput.isBlank() || setupMobileInput.isBlank()) {
                                    Toast.makeText(context, "Please fill in Firm Name, Owner Name & Mobile Number", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.completeInitialStoreSetup(
                                        firmName = firmNameInput.trim(),
                                        ownerName = ownerNameInput.trim(),
                                        mobile = setupMobileInput.trim(),
                                        email = emailInput.trim(),
                                        gstNumber = gstInput.trim(),
                                        address = addressInput.trim(),
                                        city = cityInput.trim(),
                                        state = stateInput.trim(),
                                        pincode = pincodeInput.trim()
                                    ) { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                        onLoginSuccess()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("submit_setup_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TechBlue)
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Setup & Open Dashboard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
