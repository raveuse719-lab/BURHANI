package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.Customer
import com.example.data.entity.RepairJob
import com.example.ui.BurhaniViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairsScreen(
    viewModel: BurhaniViewModel,
    modifier: Modifier = Modifier,
    initialOpenAdd: Boolean = false
) {
    val context = LocalContext.current
    val statusFilter by viewModel.repairStatusFilter.collectAsState()
    val repairJobs by viewModel.repairJobsList.collectAsState()
    val customers by viewModel.customersList.collectAsState()

    var showAddDialog by remember { mutableStateOf(initialOpenAdd) }
    var selectedJobForStatusUpdate by remember { mutableStateOf<RepairJob?>(null) }
    var selectedJobForTicketReceipt by remember { mutableStateOf<RepairJob?>(null) }

    val statusTabs = listOf(
        "ALL", "RECEIVED", "INSPECTION", "REPAIRING", "WAITING_PARTS", "READY", "DELIVERED", "RETURNED_NO_REPAIR"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Build, contentDescription = null) },
                text = { Text("New Repair Job") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_repair_fab")
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
            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Repair & Service Jobs", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${repairJobs.size} total repair records", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Filter Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(statusTabs) { status ->
                    val label = status.replace("_", " ")
                    FilterChip(
                        selected = statusFilter == status,
                        onClick = { viewModel.setRepairStatusFilter(status) },
                        label = { Text(label) },
                        modifier = Modifier.testTag("repair_chip_$status")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (repairJobs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.HomeRepairService,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No repair jobs matching category.",
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
                    items(repairJobs, key = { it.id }) { job ->
                        RepairJobDetailedCard(
                            job = job,
                            onUpdateStatus = { selectedJobForStatusUpdate = job },
                            onPrintTicket = { selectedJobForTicketReceipt = job },
                            onNotifyCustomer = {
                                val msg = "Burhani Infotech Alert: Dear ${job.customerName}, status of your ${job.productName} (${job.jobNo}) is now: ${job.status.replace("_", " ")}. Est. Cost: ₹${job.repairCost}. Thank you!"
                                val url = "https://api.whatsapp.com/send?phone=91${job.customerMobile}&text=${Uri.encode(msg)}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }

    // New Repair Job Dialog
    if (showAddDialog) {
        CreateRepairJobDialog(
            customers = customers,
            onDismiss = { showAddDialog = false },
            onSave = { job ->
                viewModel.saveRepairJob(job)
                showAddDialog = false
            }
        )
    }

    // Status Stepper Dialog
    selectedJobForStatusUpdate?.let { job ->
        UpdateRepairStatusDialog(
            job = job,
            onDismiss = { selectedJobForStatusUpdate = null },
            onConfirm = { newStatus, cost, parts, notes ->
                viewModel.saveRepairJob(
                    job.copy(
                        status = newStatus,
                        repairCost = cost,
                        sparePartsUsed = parts,
                        technicianNotes = if (notes.isNotBlank()) "${job.technicianNotes}\n[$newStatus]: $notes" else job.technicianNotes,
                        deliveryDate = if (newStatus == "DELIVERED") System.currentTimeMillis() else job.deliveryDate
                    )
                )
                selectedJobForStatusUpdate = null
            }
        )
    }

    // Job Ticket / Receipt Sheet Dialog
    selectedJobForTicketReceipt?.let { job ->
        JobTicketReceiptDialog(
            job = job,
            onDismiss = { selectedJobForTicketReceipt = null }
        )
    }
}

@Composable
fun RepairJobDetailedCard(
    job: RepairJob,
    onUpdateStatus: () -> Unit,
    onPrintTicket: () -> Unit,
    onNotifyCustomer: () -> Unit
) {
    val statusColor = when (job.status) {
        "RECEIVED" -> TechBlue
        "INSPECTION" -> CyanAccent
        "REPAIRING" -> AmberWarning
        "WAITING_PARTS" -> RedAlert
        "READY" -> GreenSuccess
        "DELIVERED" -> Color.Gray
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("repair_card_${job.jobNo}"),
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
                    Text(text = job.jobNo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = statusColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "•  ${job.customerName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = statusColor
                ) {
                    Text(
                        text = job.status.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${job.productCategory}: ${job.productName} (${job.brand} ${job.modelNumber})",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            if (job.serialNumber.isNotBlank()) {
                Text("Serial No: ${job.serialNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text("Problem: ${job.problemDescription}", style = MaterialTheme.typography.bodyMedium)

            if (job.accessoriesReceived.isNotBlank()) {
                Text("Accessories: ${job.accessoriesReceived}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (job.sparePartsUsed.isNotBlank()) {
                Text("Parts Used: ${job.sparePartsUsed}", style = MaterialTheme.typography.bodySmall, color = TechBlue)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Tech: ${job.assignedTechnician}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Bill: ₹${String.format("%.0f", job.repairCost)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GreenSuccess)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = onNotifyCustomer) {
                        Icon(Icons.Default.Share, contentDescription = "Notify WhatsApp", tint = Color(0xFF25D366))
                    }
                    IconButton(onClick = onPrintTicket) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = "Job Ticket", tint = TechBlue)
                    }
                    Button(
                        onClick = onUpdateStatus,
                        colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Update Status", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateRepairJobDialog(
    customers: List<Customer>,
    onDismiss: () -> Unit,
    onSave: (RepairJob) -> Unit
) {
    var selectedCustomer by remember { mutableStateOf(customers.firstOrNull()) }
    var customerNameInput by remember { mutableStateOf(customers.firstOrNull()?.name ?: "") }
    var customerMobileInput by remember { mutableStateOf(customers.firstOrNull()?.mobile ?: "") }

    var category by remember { mutableStateOf("Laptop") }
    var productName by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var modelNumber by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var problemDescription by remember { mutableStateOf("") }

    // Accessories Checklist
    var accAdapter by remember { mutableStateOf(true) }
    var accPowerCord by remember { mutableStateOf(false) }
    var accBag by remember { mutableStateOf(false) }
    var accCartridge by remember { mutableStateOf(false) }
    var accUsbCable by remember { mutableStateOf(false) }

    var assignedTechnician by remember { mutableStateOf("Abdeali Tech") }
    var estimatedCost by remember { mutableStateOf("1500") }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Repair Job Intake Sheet") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (errorText.isNotBlank()) {
                    item { Text(errorText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }

                item {
                    Text("1. Customer Details", fontWeight = FontWeight.Bold, color = TechBlue)
                }

                item {
                    OutlinedTextField(
                        value = customerNameInput,
                        onValueChange = { customerNameInput = it },
                        label = { Text("Customer Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("repair_cust_name")
                    )
                }

                item {
                    OutlinedTextField(
                        value = customerMobileInput,
                        onValueChange = { customerMobileInput = it },
                        label = { Text("Mobile Number *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("repair_cust_mobile")
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("2. Equipment Information", fontWeight = FontWeight.Bold, color = TechBlue)
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category (Laptop/Printer/CCTV)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Brand") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("Product Model / Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("repair_product_name")
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = modelNumber,
                            onValueChange = { modelNumber = it },
                            label = { Text("Model No.") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = serialNumber,
                            onValueChange = { serialNumber = it },
                            label = { Text("Serial Number") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = problemDescription,
                        onValueChange = { problemDescription = it },
                        label = { Text("Problem Reported *") },
                        modifier = Modifier.fillMaxWidth().testTag("repair_problem_desc")
                    )
                }

                item {
                    Text("3. Accessories Received", fontWeight = FontWeight.Bold, color = TechBlue)
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = accAdapter, onCheckedChange = { accAdapter = it })
                        Text("Adapter", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(12.dp))
                        Checkbox(checked = accPowerCord, onCheckedChange = { accPowerCord = it })
                        Text("Power Cable", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = accBag, onCheckedChange = { accBag = it })
                        Text("Laptop Bag", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(12.dp))
                        Checkbox(checked = accCartridge, onCheckedChange = { accCartridge = it })
                        Text("Ink Cartridge", style = MaterialTheme.typography.bodySmall)
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = assignedTechnician,
                            onValueChange = { assignedTechnician = it },
                            label = { Text("Technician") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = estimatedCost,
                            onValueChange = { estimatedCost = it },
                            label = { Text("Est. Cost (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customerNameInput.isBlank() || productName.isBlank() || problemDescription.isBlank()) {
                        errorText = "Please fill in Customer Name, Product Name, and Problem."
                    } else {
                        val accList = mutableListOf<String>()
                        if (accAdapter) accList.add("Power Adapter")
                        if (accPowerCord) accList.add("Power Cord")
                        if (accBag) accList.add("Bag")
                        if (accCartridge) accList.add("Ink Cartridge")
                        if (accUsbCable) accList.add("USB Cable")

                        onSave(
                            RepairJob(
                                jobNo = "", // Generated automatically
                                customerId = selectedCustomer?.id ?: 0L,
                                customerName = customerNameInput.trim(),
                                customerMobile = customerMobileInput.trim(),
                                productName = productName.trim(),
                                productCategory = category.trim(),
                                brand = brand.trim(),
                                modelNumber = modelNumber.trim(),
                                serialNumber = serialNumber.trim(),
                                problemDescription = problemDescription.trim(),
                                accessoriesReceived = accList.joinToString(", "),
                                assignedTechnician = assignedTechnician.trim(),
                                repairCost = estimatedCost.toDoubleOrNull() ?: 0.0,
                                status = "RECEIVED"
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("save_repair_btn")
            ) {
                Text("Create Repair Job")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun UpdateRepairStatusDialog(
    job: RepairJob,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(job.status) }
    var costText by remember { mutableStateOf(job.repairCost.toString()) }
    var partsUsedText by remember { mutableStateOf(job.sparePartsUsed) }
    var notesText by remember { mutableStateOf("") }

    val statuses = listOf(
        "RECEIVED", "INSPECTION", "REPAIRING", "WAITING_PARTS", "READY", "DELIVERED", "RETURNED_NO_REPAIR"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Job Status: ${job.jobNo}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select New Status:", fontWeight = FontWeight.Bold)

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(statuses) { st ->
                        FilterChip(
                            selected = selectedStatus == st,
                            onClick = { selectedStatus = st },
                            label = { Text(st.replace("_", " ")) }
                        )
                    }
                }

                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it },
                    label = { Text("Final / Est Repair Cost (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = partsUsedText,
                    onValueChange = { partsUsedText = it },
                    label = { Text("Spare Parts Replaced / Used") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Technician Diagnostic Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(selectedStatus, costText.toDoubleOrNull() ?: 0.0, partsUsedText, notesText)
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun JobTicketReceiptDialog(
    job: RepairJob,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = TechBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Job Card Ticket Sheet")
            }
        },
        text = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TechBlue, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("BURHANI INFOTECH", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechBlue)
                    Text("Service & Repair Center Ticket", style = MaterialTheme.typography.labelSmall)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("Job Ticket No: ${job.jobNo}", fontWeight = FontWeight.Bold)
                    Text("Customer: ${job.customerName} (${job.customerMobile})")
                    Text("Product: ${job.productName} (${job.brand} ${job.modelNumber})")
                    Text("Serial No: ${job.serialNumber.ifBlank { "N/A" }}")
                    Text("Problem: ${job.problemDescription}")
                    Text("Accessories: ${job.accessoriesReceived.ifBlank { "None" }}")
                    Text("Assigned Tech: ${job.assignedTechnician}")
                    Text("Est. Bill: ₹${job.repairCost}", fontWeight = FontWeight.Bold, color = GreenSuccess)

                    Spacer(modifier = Modifier.height(12.dp))

                    // QR Code Box Simulation
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.QrCode2, contentDescription = "QR Code", modifier = Modifier.size(64.dp), tint = Color.Black)
                                Text(job.jobNo, fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done / Print") }
        }
    )
}
