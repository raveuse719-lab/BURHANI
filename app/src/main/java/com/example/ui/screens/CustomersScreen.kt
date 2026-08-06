package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.entity.Invoice
import com.example.data.entity.RepairJob
import com.example.ui.BurhaniViewModel
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.TechBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    viewModel: BurhaniViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val searchQuery by viewModel.customerSearch.collectAsState()
    val customers by viewModel.customersList.collectAsState()
    val repairJobs by viewModel.repairJobsList.collectAsState()
    val invoices by viewModel.invoicesList.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var customerToEdit by remember { mutableStateOf<Customer?>(null) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }
    var selectedCustomerForHistory by remember { mutableStateOf<Customer?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    customerToEdit = null
                    showAddDialog = true
                },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Add Customer") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_customer_fab")
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
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setCustomerSearch(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_search_input"),
                placeholder = { Text("Search by Name, Mobile, Email, GST...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setCustomerSearch("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (customers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.People,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "No customers registered yet." else "No customers matching '$searchQuery'.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(customers, key = { it.id }) { customer ->
                        val custRepairs = repairJobs.filter { it.customerId == customer.id }
                        val custInvoices = invoices.filter { it.customerId == customer.id }

                        Card(
                            onClick = { selectedCustomerForHistory = customer },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("customer_card_${customer.id}"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = TechBlue.copy(alpha = 0.15f),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = customer.name.take(1).uppercase(),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TechBlue
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = customer.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = customer.mobile,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Row {
                                        IconButton(onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${customer.mobile}"))
                                            context.startActivity(intent)
                                        }) {
                                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = TechBlue)
                                        }
                                        IconButton(onClick = {
                                            customerToEdit = customer
                                            showAddDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                if (customer.address.isNotBlank() || customer.gstNumber.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (customer.address.isNotBlank()) {
                                        Text(
                                            text = "Address: ${customer.address}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (customer.gstNumber.isNotBlank()) {
                                        Text(
                                            text = "GSTIN: ${customer.gstNumber}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TechBlue
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "${custRepairs.size} Repairs",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "${custInvoices.size} Invoices",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                    TextButton(onClick = { selectedCustomerForHistory = customer }) {
                                        Text("History →", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddDialog) {
        AddEditCustomerDialog(
            customer = customerToEdit,
            onDismiss = { showAddDialog = false },
            onSave = { updatedCust ->
                viewModel.saveCustomer(updatedCust)
                showAddDialog = false
            },
            onDelete = { custToDelete ->
                viewModel.deleteCustomer(custToDelete)
                showAddDialog = false
            }
        )
    }

    // Customer History Modal
    selectedCustomerForHistory?.let { customer ->
        val custRepairs = repairJobs.filter { it.customerId == customer.id }
        val custInvoices = invoices.filter { it.customerId == customer.id }

        AlertDialog(
            onDismissRequest = { selectedCustomerForHistory = null },
            title = {
                Column {
                    Text(text = customer.name, fontWeight = FontWeight.Bold)
                    Text(text = "Customer Service & Purchase History", style = MaterialTheme.typography.labelSmall)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("Repair Jobs (${custRepairs.size})", fontWeight = FontWeight.Bold, color = TechBlue)
                    }
                    if (custRepairs.isEmpty()) {
                        item { Text("No repair history for this customer.", style = MaterialTheme.typography.bodySmall) }
                    } else {
                        items(custRepairs) { job ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("${job.jobNo} • ${job.productName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("Status: ${job.status}", style = MaterialTheme.typography.bodySmall, color = TechBlue)
                                    Text("Issue: ${job.problemDescription}", style = MaterialTheme.typography.bodySmall)
                                    Text("Cost: ₹${job.repairCost}", fontWeight = FontWeight.Bold, color = GreenSuccess)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Invoices & Quotations (${custInvoices.size})", fontWeight = FontWeight.Bold, color = TechBlue)
                    }
                    if (custInvoices.isEmpty()) {
                        item { Text("No invoice history for this customer.", style = MaterialTheme.typography.bodySmall) }
                    } else {
                        items(custInvoices) { inv ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("${inv.invoiceNo} (${inv.type})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("Total: ₹${inv.totalAmount} • Payment: ${inv.paymentStatus}", style = MaterialTheme.typography.bodySmall, color = GreenSuccess)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedCustomerForHistory = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun AddEditCustomerDialog(
    customer: Customer?,
    onDismiss: () -> Unit,
    onSave: (Customer) -> Unit,
    onDelete: (Customer) -> Unit
) {
    var name by remember { mutableStateOf(customer?.name ?: "") }
    var mobile by remember { mutableStateOf(customer?.mobile ?: "") }
    var email by remember { mutableStateOf(customer?.email ?: "") }
    var address by remember { mutableStateOf(customer?.address ?: "") }
    var gstNumber by remember { mutableStateOf(customer?.gstNumber ?: "") }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (customer == null) "Add Customer" else "Edit Customer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (errorText.isNotBlank()) {
                    Text(errorText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer / Business Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("cust_name_input")
                )

                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it },
                    label = { Text("Mobile Number *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("cust_mobile_input")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = gstNumber,
                    onValueChange = { gstNumber = it },
                    label = { Text("GSTIN (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || mobile.isBlank()) {
                        errorText = "Please enter both Customer Name and Mobile Number."
                    } else {
                        onSave(
                            Customer(
                                id = customer?.id ?: 0L,
                                name = name.trim(),
                                mobile = mobile.trim(),
                                email = email.trim(),
                                address = address.trim(),
                                gstNumber = gstNumber.trim().uppercase(),
                                createdAt = customer?.createdAt ?: System.currentTimeMillis()
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("save_customer_btn")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (customer != null) {
                    TextButton(onClick = { onDelete(customer) }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
