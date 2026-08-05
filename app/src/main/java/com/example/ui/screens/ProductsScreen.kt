package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.entity.Product
import com.example.ui.BurhaniViewModel
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedAlert
import com.example.ui.theme.TechBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: BurhaniViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.productSearch.collectAsState()
    val categoryFilter by viewModel.selectedCategoryFilter.collectAsState()
    val products by viewModel.productsList.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var productToAdjustStock by remember { mutableStateOf<Product?>(null) }
    var showBarcodeScannerDialog by remember { mutableStateOf(false) }

    val categories = listOf("ALL", "Printer", "Laptop", "Desktop", "CCTV", "Networking", "Cartridge", "Accessory")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { showBarcodeScannerDialog = true },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.testTag("scan_barcode_fab")
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Barcode")
                }

                ExtendedFloatingActionButton(
                    onClick = {
                        productToEdit = null
                        showAddDialog = true
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Product") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("add_product_fab")
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Search Bar & Scan Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setProductSearch(it) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("product_search_input"),
                    placeholder = { Text("Search product name, brand, barcode...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setProductSearch("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                IconButton(
                    onClick = { showBarcodeScannerDialog = true },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp))
                        .size(54.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = categoryFilter == cat,
                        onClick = { viewModel.setCategoryFilter(cat) },
                        label = { Text(cat) },
                        modifier = Modifier.testTag("cat_chip_$cat")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No products found in inventory.",
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
                    items(products, key = { it.id }) { product ->
                        val isLowStock = product.stockQuantity <= product.minStockLevel

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("product_card_${product.id}"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isLowStock) RedAlert else MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isLowStock) RedAlert.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = TechBlue.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${product.brand} • ${product.category}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = TechBlue,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = product.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )

                                        if (product.modelNumber.isNotBlank() || product.barcode.isNotBlank()) {
                                            Text(
                                                text = "Model: ${product.modelNumber.ifBlank { "N/A" }} | Barcode: ${product.barcode.ifBlank { "N/A" }}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(onClick = {
                                        productToEdit = product
                                        showAddDialog = true
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Selling Price", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "₹${product.sellingPrice}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GreenSuccess)
                                        Text(text = "Cost: ₹${product.purchasePrice} | Warranty: ${product.warrantyMonths}m", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isLowStock) RedAlert else TechBlue,
                                        modifier = Modifier.clickable { productToAdjustStock = product }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isLowStock) Icons.Default.Warning else Icons.Default.Inventory,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Stock: ${product.stockQuantity}",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelMedium
                                            )
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

    // Add / Edit Product Dialog
    if (showAddDialog) {
        AddEditProductDialog(
            product = productToEdit,
            onDismiss = { showAddDialog = false },
            onSave = { p ->
                viewModel.saveProduct(p)
                showAddDialog = false
            },
            onDelete = { p ->
                viewModel.deleteProduct(p)
                showAddDialog = false
            }
        )
    }

    // Adjust Stock Dialog
    productToAdjustStock?.let { p ->
        AdjustStockDialog(
            product = p,
            onDismiss = { productToAdjustStock = null },
            onConfirm = { qtyChange, reason ->
                viewModel.adjustStock(p.id, p.name, qtyChange, reason)
                productToAdjustStock = null
            }
        )
    }

    // Barcode Scanner Dialog Simulation
    if (showBarcodeScannerDialog) {
        BarcodeScannerDialog(
            onDismiss = { showBarcodeScannerDialog = false },
            onBarcodeFound = { barcode ->
                viewModel.setProductSearch(barcode)
                showBarcodeScannerDialog = false
            }
        )
    }
}

@Composable
fun AddEditProductDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var brand by remember { mutableStateOf(product?.brand ?: "HP") }
    var category by remember { mutableStateOf(product?.category ?: "Printer") }
    var modelNumber by remember { mutableStateOf(product?.modelNumber ?: "") }
    var serialNumber by remember { mutableStateOf(product?.serialNumber ?: "") }
    var barcode by remember { mutableStateOf(product?.barcode ?: "") }
    var purchasePrice by remember { mutableStateOf(product?.purchasePrice?.toString() ?: "0") }
    var sellingPrice by remember { mutableStateOf(product?.sellingPrice?.toString() ?: "0") }
    var warrantyMonths by remember { mutableStateOf(product?.warrantyMonths?.toString() ?: "12") }
    var stockQuantity by remember { mutableStateOf(product?.stockQuantity?.toString() ?: "5") }
    var minStockLevel by remember { mutableStateOf(product?.minStockLevel?.toString() ?: "2") }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Add New Product" else "Edit Product") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    if (errorText.isNotBlank()) {
                        Text(errorText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("product_name_input")
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Brand") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category") },
                            modifier = Modifier.weight(1f)
                        )
                    }
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
                            value = barcode,
                            onValueChange = { barcode = it },
                            label = { Text("Barcode") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = purchasePrice,
                            onValueChange = { purchasePrice = it },
                            label = { Text("Purchase Price (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = sellingPrice,
                            onValueChange = { sellingPrice = it },
                            label = { Text("Selling Price (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = stockQuantity,
                            onValueChange = { stockQuantity = it },
                            label = { Text("Stock Qty") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minStockLevel,
                            onValueChange = { minStockLevel = it },
                            label = { Text("Min Stock Alert") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = warrantyMonths,
                        onValueChange = { warrantyMonths = it },
                        label = { Text("Warranty Period (Months)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorText = "Product Name is required."
                    } else {
                        onSave(
                            Product(
                                id = product?.id ?: 0L,
                                name = name.trim(),
                                brand = brand.trim(),
                                category = category.trim(),
                                modelNumber = modelNumber.trim(),
                                serialNumber = serialNumber.trim(),
                                barcode = barcode.trim(),
                                purchasePrice = purchasePrice.toDoubleOrNull() ?: 0.0,
                                sellingPrice = sellingPrice.toDoubleOrNull() ?: 0.0,
                                warrantyMonths = warrantyMonths.toIntOrNull() ?: 12,
                                stockQuantity = stockQuantity.toIntOrNull() ?: 0,
                                minStockLevel = minStockLevel.toIntOrNull() ?: 2
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("save_product_btn")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (product != null) {
                    TextButton(onClick = { onDelete(product) }) {
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

@Composable
fun AdjustStockDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var qtyChangeText by remember { mutableStateOf("1") }
    var isAdding by remember { mutableStateOf(true) }
    var reason by remember { mutableStateOf("Stock adjustment") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adjust Stock: ${product.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Current Stock: ${product.stockQuantity}", fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isAdding,
                        onClick = { isAdding = true },
                        label = { Text("+ Add Stock (In)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isAdding,
                        onClick = { isAdding = false },
                        label = { Text("- Remove Stock (Out)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = qtyChangeText,
                    onValueChange = { qtyChangeText = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason / Note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val qty = qtyChangeText.toIntOrNull() ?: 0
                val delta = if (isAdding) qty else -qty
                onConfirm(delta, reason)
            }) {
                Text("Update Stock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun BarcodeScannerDialog(
    onDismiss: () -> Unit,
    onBarcodeFound: (String) -> Unit
) {
    var manualBarcode by remember { mutableStateOf("889296001122") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = TechBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Barcode Scanner")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .border(2.dp, TechBlue, RoundedCornerShape(12.dp)),
                    color = Color.Black.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Camera Barcode Viewfinder", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Text("Simulate Quick Scan / Enter Barcode:", style = MaterialTheme.typography.labelMedium)

                OutlinedTextField(
                    value = manualBarcode,
                    onValueChange = { manualBarcode = it },
                    label = { Text("Scanned Barcode Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("scanned_barcode_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onBarcodeFound(manualBarcode) },
                modifier = Modifier.testTag("apply_barcode_btn")
            ) {
                Text("Search Barcode")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
