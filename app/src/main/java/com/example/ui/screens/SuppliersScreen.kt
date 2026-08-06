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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entity.Supplier
import com.example.ui.BurhaniViewModel
import com.example.ui.theme.TechBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliersScreen(
    viewModel: BurhaniViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val suppliers by viewModel.suppliersList.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var supplierToEdit by remember { mutableStateOf<Supplier?>(null) }
    var supplierToDelete by remember { mutableStateOf<Supplier?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    supplierToEdit = null
                    showAddDialog = true
                },
                icon = { Icon(Icons.Default.LocalShipping, contentDescription = null) },
                text = { Text("Add Supplier") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_supplier_fab")
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
            Text("Supplier Management", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Hardware, Printer & Component Wholesalers", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(16.dp))

            if (suppliers.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No suppliers added yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(suppliers, key = { it.id }) { sup ->
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("supplier_card_${sup.id}"),
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
                                    Column {
                                        Text(sup.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("Contact: ${sup.contactPerson} (${sup.mobile})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Row {
                                        IconButton(onClick = {
                                            supplierToEdit = sup
                                            showAddDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }
                                        IconButton(onClick = {
                                            supplierToDelete = sup
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete Supplier", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }

                                if (sup.gstNumber.isNotBlank() || sup.address.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Address: ${sup.address}", style = MaterialTheme.typography.bodySmall)
                                    Text("GSTIN: ${sup.gstNumber}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = TechBlue)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    supplierToDelete?.let { sup ->
        AlertDialog(
            onDismissRequest = { supplierToDelete = null },
            title = { Text("Delete Supplier?") },
            text = { Text("Are you sure you want to delete supplier '${sup.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSupplier(sup)
                        Toast.makeText(context, "Supplier '${sup.name}' deleted", Toast.LENGTH_SHORT).show()
                        supplierToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { supplierToDelete = null }) { Text("Cancel") }
            }
        )
    }

    if (showAddDialog) {
        AddEditSupplierDialog(
            supplier = supplierToEdit,
            onDismiss = { showAddDialog = false },
            onSave = { s ->
                viewModel.saveSupplier(s)
                showAddDialog = false
            },
            onDelete = { s ->
                viewModel.deleteSupplier(s)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddEditSupplierDialog(
    supplier: Supplier?,
    onDismiss: () -> Unit,
    onSave: (Supplier) -> Unit,
    onDelete: (Supplier) -> Unit
) {
    var name by remember { mutableStateOf(supplier?.name ?: "") }
    var contactPerson by remember { mutableStateOf(supplier?.contactPerson ?: "") }
    var mobile by remember { mutableStateOf(supplier?.mobile ?: "") }
    var email by remember { mutableStateOf(supplier?.email ?: "") }
    var address by remember { mutableStateOf(supplier?.address ?: "") }
    var gstNumber by remember { mutableStateOf(supplier?.gstNumber ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (supplier == null) "Add Supplier" else "Edit Supplier") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Supplier Company Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contactPerson,
                    onValueChange = { contactPerson = it },
                    label = { Text("Contact Person") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it },
                    label = { Text("Mobile Number") },
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
                    label = { Text("GSTIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) {
                    onSave(
                        Supplier(
                            id = supplier?.id ?: 0L,
                            name = name.trim(),
                            contactPerson = contactPerson.trim(),
                            mobile = mobile.trim(),
                            email = email.trim(),
                            address = address.trim(),
                            gstNumber = gstNumber.trim()
                        )
                    )
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (supplier != null) {
                    TextButton(onClick = { onDelete(supplier) }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}
