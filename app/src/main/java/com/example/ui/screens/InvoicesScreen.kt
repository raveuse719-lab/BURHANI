package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.entity.Invoice
import com.example.data.entity.Product
import com.example.ui.BurhaniViewModel
import com.example.ui.InvoiceLineItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    viewModel: BurhaniViewModel,
    modifier: Modifier = Modifier,
    initialOpenAdd: Boolean = false
) {
    val context = LocalContext.current
    val invoices by viewModel.invoicesList.collectAsState()
    val customers by viewModel.customersList.collectAsState()
    val products by viewModel.productsList.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()

    var showCreateInvoiceDialog by remember { mutableStateOf(initialOpenAdd) }
    var selectedInvoiceForPreview by remember { mutableStateOf<Invoice?>(null) }
    var invoiceToDelete by remember { mutableStateOf<Invoice?>(null) }
    var invoiceTypeFilter by remember { mutableStateOf("ALL") }

    val filteredInvoices = invoices.filter {
        if (invoiceTypeFilter == "ALL") true else it.type == invoiceTypeFilter
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateInvoiceDialog = true },
                icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                text = { Text("Create Bill / Quote") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("create_invoice_fab")
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
            // Title & Filter Chips
            Text("Billing & Quotation System", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Manage GST Invoices, Quotations & Repair Receipts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = invoiceTypeFilter == "ALL",
                    onClick = { invoiceTypeFilter = "ALL" },
                    label = { Text("All (${invoices.size})") }
                )
                FilterChip(
                    selected = invoiceTypeFilter == "GST_INVOICE",
                    onClick = { invoiceTypeFilter = "GST_INVOICE" },
                    label = { Text("GST Invoices") }
                )
                FilterChip(
                    selected = invoiceTypeFilter == "QUOTATION",
                    onClick = { invoiceTypeFilter = "QUOTATION" },
                    label = { Text("Quotations") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredInvoices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Receipt,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No invoices found.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredInvoices, key = { it.id }) { invoice ->
                        Card(
                            onClick = { selectedInvoiceForPreview = invoice },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("invoice_card_${invoice.invoiceNo}"),
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
                                            shape = RoundedCornerShape(6.dp),
                                            color = TechBlue.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = invoice.type.replace("_", " "),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = TechBlue,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = invoice.invoiceNo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (invoice.paymentStatus == "PAID") GreenSuccess else AmberWarning
                                    ) {
                                        Text(
                                            text = invoice.paymentStatus,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(text = "Customer: ${invoice.customerName}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                Text(text = "Mobile: ${invoice.customerMobile} ${if (invoice.customerGst.isNotBlank()) "| GST: ${invoice.customerGst}" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Total Amount", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("₹${String.format("%.2f", invoice.totalAmount)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GreenSuccess)
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = { invoiceToDelete = invoice },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete Bill", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Delete", fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = { selectedInvoiceForPreview = invoice },
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("View & Print", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Invoice Confirmation Dialog
    invoiceToDelete?.let { inv ->
        AlertDialog(
            onDismissRequest = { invoiceToDelete = null },
            title = { Text("Delete Bill / Invoice?") },
            text = { Text("Are you sure you want to permanently delete bill ${inv.invoiceNo} for ${inv.customerName} (₹${inv.totalAmount})?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteInvoice(inv)
                        Toast.makeText(context, "Bill ${inv.invoiceNo} deleted successfully", Toast.LENGTH_SHORT).show()
                        invoiceToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { invoiceToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // Create Invoice Dialog
    if (showCreateInvoiceDialog) {
        CreateInvoiceDialog(
            customers = customers,
            products = products,
            onDismiss = { showCreateInvoiceDialog = false },
            onSave = { invoice, items ->
                viewModel.saveInvoice(invoice, items)
                showCreateInvoiceDialog = false
            }
        )
    }

    // Invoice Preview & Print Modal
    selectedInvoiceForPreview?.let { invoice ->
        InvoicePrintPreviewDialog(
            invoice = invoice,
            profile = profile,
            lineItems = viewModel.parseInvoiceItems(invoice.itemsJson),
            onDismiss = { selectedInvoiceForPreview = null },
            onShareWhatsApp = {
                val msg = "Invoice from ${profile?.businessName ?: "Burhani Infotech"}:\nInvoice No: ${invoice.invoiceNo}\nCustomer: ${invoice.customerName}\nTotal Amount: ₹${invoice.totalAmount}\nThank you for doing business with us!"
                val url = "https://api.whatsapp.com/send?phone=91${invoice.customerMobile}&text=${Uri.encode(msg)}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun CreateInvoiceDialog(
    customers: List<Customer>,
    products: List<Product>,
    onDismiss: () -> Unit,
    onSave: (Invoice, List<InvoiceLineItem>) -> Unit
) {
    var invoiceType by remember { mutableStateOf("GST_INVOICE") }
    var selectedCustomer by remember { mutableStateOf(customers.firstOrNull()) }
    var customerNameInput by remember { mutableStateOf(customers.firstOrNull()?.name ?: "") }
    var customerMobileInput by remember { mutableStateOf(customers.firstOrNull()?.mobile ?: "") }
    var customerGstInput by remember { mutableStateOf(customers.firstOrNull()?.gstNumber ?: "") }
    var customerAddressInput by remember { mutableStateOf(customers.firstOrNull()?.address ?: "") }

    var lineItems = remember { mutableStateListOf<InvoiceLineItem>() }

    // Line item input state
    var selectedProductForLine by remember { mutableStateOf<Product?>(null) }
    var customItemName by remember { mutableStateOf("") }
    var itemQty by remember { mutableStateOf("1") }
    var itemUnitPrice by remember { mutableStateOf("0") }

    var taxRateText by remember { mutableStateOf("18.0") }
    var discountText by remember { mutableStateOf("0") }
    var paymentStatus by remember { mutableStateOf("PAID") }
    var paymentMethod by remember { mutableStateOf("UPI") }
    var errorText by remember { mutableStateOf("") }

    // Calculations
    val subtotal = lineItems.sumOf { it.lineTotal }
    val taxRate = taxRateText.toDoubleOrNull() ?: 0.0
    val taxAmount = (subtotal * taxRate) / 100.0
    val cgst = taxAmount / 2.0
    val sgst = taxAmount / 2.0
    val discount = discountText.toDoubleOrNull() ?: 0.0
    val grandTotal = (subtotal + taxAmount) - discount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create $invoiceType") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (errorText.isNotBlank()) {
                    item { Text(errorText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }

                item {
                    Text("1. Invoice Type", fontWeight = FontWeight.Bold, color = TechBlue)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = invoiceType == "GST_INVOICE",
                            onClick = { invoiceType = "GST_INVOICE" },
                            label = { Text("GST Invoice") }
                        )
                        FilterChip(
                            selected = invoiceType == "QUOTATION",
                            onClick = { invoiceType = "QUOTATION" },
                            label = { Text("Quotation") }
                        )
                        FilterChip(
                            selected = invoiceType == "REPAIR_BILL",
                            onClick = { invoiceType = "REPAIR_BILL" },
                            label = { Text("Repair Bill") }
                        )
                    }
                }

                item {
                    Text("2. Customer Information", fontWeight = FontWeight.Bold, color = TechBlue)
                    OutlinedTextField(
                        value = customerNameInput,
                        onValueChange = { customerNameInput = it },
                        label = { Text("Customer Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("inv_cust_name")
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customerMobileInput,
                            onValueChange = { customerMobileInput = it },
                            label = { Text("Mobile *") },
                            modifier = Modifier.weight(1f).testTag("inv_cust_mobile")
                        )
                        OutlinedTextField(
                            value = customerGstInput,
                            onValueChange = { customerGstInput = it },
                            label = { Text("GSTIN") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text("3. Add Line Items (${lineItems.size})", fontWeight = FontWeight.Bold, color = TechBlue)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Select Item from Inventory or Custom Service:", style = MaterialTheme.typography.labelMedium)

                            OutlinedTextField(
                                value = customItemName,
                                onValueChange = { customItemName = it },
                                label = { Text("Item Name / Service Description") },
                                modifier = Modifier.fillMaxWidth().testTag("inv_item_name_input")
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = itemQty,
                                    onValueChange = { itemQty = it },
                                    label = { Text("Qty") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = itemUnitPrice,
                                    onValueChange = { itemUnitPrice = it },
                                    label = { Text("Unit Price (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        if (customItemName.isNotBlank()) {
                                            val q = itemQty.toIntOrNull() ?: 1
                                            val p = itemUnitPrice.toDoubleOrNull() ?: 0.0
                                            lineItems.add(
                                                InvoiceLineItem(
                                                    productId = selectedProductForLine?.id,
                                                    name = customItemName.trim(),
                                                    qty = q,
                                                    unitPrice = p,
                                                    lineTotal = q * p
                                                )
                                            )
                                            customItemName = ""
                                            itemUnitPrice = "0"
                                            selectedProductForLine = null
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.CenterVertically).testTag("add_item_to_list_btn")
                                ) {
                                    Text("Add")
                                }
                            }

                            // Quick pick from products
                            if (products.isNotEmpty()) {
                                Text("Quick Pick Product:", style = MaterialTheme.typography.labelSmall)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(products.take(5)) { prd ->
                                        SuggestionChip(
                                            onClick = {
                                                selectedProductForLine = prd
                                                customItemName = prd.name
                                                itemUnitPrice = prd.sellingPrice.toString()
                                            },
                                            label = { Text(prd.name.take(15) + ".. (₹${prd.sellingPrice})") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Added Line Items Table
                if (lineItems.isNotEmpty()) {
                    items(lineItems) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                Text("${item.qty} x ₹${item.unitPrice} = ₹${item.lineTotal}", style = MaterialTheme.typography.labelSmall, color = TechBlue)
                            }
                            IconButton(onClick = { lineItems.remove(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = RedAlert, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                item {
                    Text("4. Taxes & Summary", fontWeight = FontWeight.Bold, color = TechBlue)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = taxRateText,
                            onValueChange = { taxRateText = it },
                            label = { Text("GST Rate (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = discountText,
                            onValueChange = { discountText = it },
                            label = { Text("Discount (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Subtotal:")
                                Text("₹${String.format("%.2f", subtotal)}")
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("GST (${taxRate}%):")
                                Text("₹${String.format("%.2f", taxAmount)}")
                            }
                            if (discount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Discount:")
                                    Text("- ₹${String.format("%.2f", discount)}", color = RedAlert)
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Grand Total:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("₹${String.format("%.2f", grandTotal)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = GreenSuccess)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customerNameInput.isBlank() || lineItems.isEmpty()) {
                        errorText = "Please enter Customer Name and add at least 1 line item."
                    } else {
                        onSave(
                            Invoice(
                                invoiceNo = "", // Generated automatically
                                type = invoiceType,
                                customerId = selectedCustomer?.id ?: 0L,
                                customerName = customerNameInput.trim(),
                                customerMobile = customerMobileInput.trim(),
                                customerGst = customerGstInput.trim(),
                                customerAddress = customerAddressInput.trim(),
                                subtotal = subtotal,
                                taxRate = taxRate,
                                cgstAmount = cgst,
                                sgstAmount = sgst,
                                discount = discount,
                                totalAmount = grandTotal,
                                paymentStatus = paymentStatus,
                                paymentMethod = paymentMethod
                            ),
                            lineItems
                        )
                    }
                },
                modifier = Modifier.testTag("save_invoice_btn")
            ) {
                Text("Generate Invoice")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun InvoicePrintPreviewDialog(
    invoice: Invoice,
    profile: com.example.data.entity.BusinessProfile?,
    lineItems: List<InvoiceLineItem>,
    onDismiss: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    var isHalfPageFormat by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(invoice.invoiceNo, fontWeight = FontWeight.Bold)
                Row {
                    FilterChip(
                        selected = !isHalfPageFormat,
                        onClick = { isHalfPageFormat = false },
                        label = { Text("A4") }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    FilterChip(
                        selected = isHalfPageFormat,
                        onClick = { isHalfPageFormat = true },
                        label = { Text("A5 / Half") }
                    )
                }
            }
        },
        text = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TechBlue, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(if (isHalfPageFormat) 10.dp else 16.dp)
                ) {
                    // Header
                    Text(profile?.businessName ?: "BURHANI INFOTECH", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechBlue)
                    Text(profile?.tagline ?: "Computer, Printer & Service Center", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                    Text("Address: ${profile?.address ?: ""}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("Phone: ${profile?.phone ?: ""} | GSTIN: ${profile?.gstin ?: ""}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

                    // Invoice details & Customer details
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Billed To:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                            Text(invoice.customerName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                            Text("Mob: ${invoice.customerMobile}", fontSize = 11.sp, color = Color.DarkGray)
                            if (invoice.customerGst.isNotBlank()) {
                                Text("GSTIN: ${invoice.customerGst}", fontSize = 11.sp, color = TechBlue)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Invoice No: ${invoice.invoiceNo}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TechBlue)
                            Text("Type: ${invoice.type.replace("_", " ")}", fontSize = 11.sp, color = Color.DarkGray)
                            Text("Status: ${invoice.paymentStatus}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GreenSuccess)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Line Items Table
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(modifier = Modifier.padding(6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Item Description", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black, modifier = Modifier.weight(2f))
                            Text("Qty", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black, modifier = Modifier.weight(0.5f))
                            Text("Price", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black, modifier = Modifier.weight(1f))
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black, modifier = Modifier.weight(1f))
                        }
                    }

                    lineItems.forEach { item ->
                        Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.name, fontSize = 11.sp, color = Color.Black, modifier = Modifier.weight(2f))
                            Text("${item.qty}", fontSize = 11.sp, color = Color.Black, modifier = Modifier.weight(0.5f))
                            Text("₹${item.unitPrice}", fontSize = 11.sp, color = Color.Black, modifier = Modifier.weight(1f))
                            Text("₹${item.lineTotal}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black, modifier = Modifier.weight(1f))
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

                    // Grand Totals
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Bank: ${profile?.bankName ?: ""}", fontSize = 10.sp, color = Color.Gray)
                            Text("A/C: ${profile?.accountNo ?: ""}", fontSize = 10.sp, color = Color.Gray)
                            Text("IFSC: ${profile?.ifscCode ?: ""}", fontSize = 10.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Subtotal: ₹${String.format("%.2f", invoice.subtotal)}", fontSize = 11.sp, color = Color.Black)
                            Text("CGST (9%): ₹${String.format("%.2f", invoice.cgstAmount)}", fontSize = 10.sp, color = Color.DarkGray)
                            Text("SGST (9%): ₹${String.format("%.2f", invoice.sgstAmount)}", fontSize = 10.sp, color = Color.DarkGray)
                            if (invoice.discount > 0) {
                                Text("Discount: -₹${String.format("%.2f", invoice.discount)}", fontSize = 10.sp, color = RedAlert)
                            }
                            Text("TOTAL: ₹${String.format("%.2f", invoice.totalAmount)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TechBlue)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onShareWhatsApp, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share WhatsApp")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
