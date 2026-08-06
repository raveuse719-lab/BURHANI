package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entity.Customer
import com.example.data.entity.WarrantyClaim
import com.example.ui.BurhaniViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantyScreen(
    viewModel: BurhaniViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val warrantyClaims by viewModel.warrantyClaimsList.collectAsState()
    val products by viewModel.productsList.collectAsState()
    val customers by viewModel.customersList.collectAsState()

    var showAddClaimDialog by remember { mutableStateOf(false) }
    var claimToDelete by remember { mutableStateOf<com.example.data.entity.WarrantyClaim?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddClaimDialog = true },
                icon = { Icon(Icons.Default.VerifiedUser, contentDescription = null) },
                text = { Text("New Warranty Claim") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_warranty_claim_fab")
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text("Warranty & Service Tracking", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Automatic warranty calculation & claim records", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(16.dp))

            // Products Warranty Overview
            Text("Products Active Warranties (${products.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechBlue)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(products) { prd ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${prd.name} (${prd.brand})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("Model: ${prd.modelNumber.ifBlank { "N/A" }} | Category: ${prd.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TechBlue.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${prd.warrantyMonths} Months Warranty",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TechBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Warranty Claims Log (${warrantyClaims.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechBlue)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (warrantyClaims.isEmpty()) {
                    item {
                        Text("No warranty claims submitted yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    items(warrantyClaims, key = { it.id }) { claim ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${claim.claimNo} • ${claim.customerName}", fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = when (claim.status) {
                                                "APPROVED" -> GreenSuccess
                                                "REPLACED" -> TechBlue
                                                "REJECTED" -> RedAlert
                                                else -> AmberWarning
                                            }
                                        ) {
                                            Text(
                                                text = claim.status,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        IconButton(onClick = { claimToDelete = claim }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete Claim", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                                Text("Product: ${claim.productName} (S/N: ${claim.serialNumber})", style = MaterialTheme.typography.bodySmall)
                                Text("Issue: ${claim.issueDescription}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    claimToDelete?.let { claim ->
        AlertDialog(
            onDismissRequest = { claimToDelete = null },
            title = { Text("Delete Warranty Claim?") },
            text = { Text("Are you sure you want to delete warranty claim ${claim.claimNo} for ${claim.customerName}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteWarrantyClaim(claim)
                        Toast.makeText(context, "Warranty claim ${claim.claimNo} deleted", Toast.LENGTH_SHORT).show()
                        claimToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { claimToDelete = null }) { Text("Cancel") }
            }
        )
    }

    if (showAddClaimDialog) {
        CreateWarrantyClaimDialog(
            customers = customers,
            onDismiss = { showAddClaimDialog = false },
            onSave = { claim ->
                viewModel.saveWarrantyClaim(claim)
                showAddClaimDialog = false
            }
        )
    }
}

@Composable
fun CreateWarrantyClaimDialog(
    customers: List<Customer>,
    onDismiss: () -> Unit,
    onSave: (WarrantyClaim) -> Unit
) {
    var customerName by remember { mutableStateOf(customers.firstOrNull()?.name ?: "") }
    var productName by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var issueDescription by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("SUBMITTED") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Warranty Claim") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Product Name / Model *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = serialNumber,
                    onValueChange = { serialNumber = it },
                    label = { Text("Serial Number *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = issueDescription,
                    onValueChange = { issueDescription = it },
                    label = { Text("Fault / Issue Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customerName.isNotBlank() && productName.isNotBlank()) {
                        val now = System.currentTimeMillis()
                        onSave(
                            WarrantyClaim(
                                claimNo = "",
                                customerId = 0L,
                                customerName = customerName.trim(),
                                productName = productName.trim(),
                                serialNumber = serialNumber.trim(),
                                purchaseDate = now - (60 * 86400000L),
                                warrantyExpiryDate = now + (300 * 86400000L),
                                issueDescription = issueDescription.trim(),
                                status = status
                            )
                        )
                    }
                }
            ) {
                Text("Submit Claim")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
