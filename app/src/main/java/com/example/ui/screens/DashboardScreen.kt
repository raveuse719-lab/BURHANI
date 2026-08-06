package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.Product
import com.example.data.entity.RepairJob
import com.example.ui.AppNavTab
import com.example.ui.BurhaniViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: BurhaniViewModel,
    modifier: Modifier = Modifier,
    onNavigate: (AppNavTab) -> Unit = {},
    onOpenNewRepair: () -> Unit = {},
    onOpenNewInvoice: () -> Unit = {}
) {
    val context = LocalContext.current
    val customers by viewModel.customersList.collectAsState()
    val products by viewModel.productsList.collectAsState()
    val lowStockList by viewModel.lowStockList.collectAsState()
    val repairJobs by viewModel.repairJobsList.collectAsState()
    val invoices by viewModel.invoicesList.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    val pendingRepairsCount = repairJobs.count { it.status != "DELIVERED" && it.status != "RETURNED_NO_REPAIR" }
    val readyRepairs = repairJobs.filter { it.status == "READY" }
    val totalRevenue = invoices.filter { it.type == "GST_INVOICE" || it.type == "REPAIR_BILL" }.sumOf { it.totalAmount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Hero Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card"),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile?.businessName ?: "Burhani Infotech",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = profile?.tagline ?: "Sales, Service & Repair Management",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Computer,
                                    contentDescription = "Logo",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Logged Account (${user.role})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(user.username, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = GreenSuccess,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Total Revenue", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${String.format("%.0f", totalRevenue)}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = GreenSuccess)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Action Shortcuts Bar
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    QuickActionChip(
                        label = "New Repair Job",
                        icon = Icons.Default.Build,
                        containerColor = TechBlue,
                        contentColor = Color.White,
                        onClick = onOpenNewRepair
                    )
                }
                item {
                    QuickActionChip(
                        label = "Create Invoice",
                        icon = Icons.Default.ReceiptLong,
                        containerColor = GreenSuccess,
                        contentColor = Color.White,
                        onClick = onOpenNewInvoice
                    )
                }
                item {
                    QuickActionChip(
                        label = "Add Product",
                        icon = Icons.Default.Inventory2,
                        containerColor = AmberWarning,
                        contentColor = Color.White,
                        onClick = { onNavigate(AppNavTab.PRODUCTS) }
                    )
                }
                item {
                    QuickActionChip(
                        label = "Add Customer",
                        icon = Icons.Default.PersonAdd,
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White,
                        onClick = { onNavigate(AppNavTab.CUSTOMERS) }
                    )
                }
            }
        }

        // KPI Metric Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Active Repairs",
                        value = "$pendingRepairsCount Jobs",
                        subtitle = "${repairJobs.count { it.status == "RECEIVED" }} Received, ${repairJobs.count { it.status == "REPAIRING" }} Repairing",
                        icon = Icons.Default.HomeRepairService,
                        color = TechBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppNavTab.REPAIRS) }
                    )

                    MetricCard(
                        title = "Ready Delivery",
                        value = "${readyRepairs.size} Jobs",
                        subtitle = "Waiting customer pickup",
                        icon = Icons.Default.CheckCircle,
                        color = GreenSuccess,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppNavTab.REPAIRS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Low Stock Alert",
                        value = "${lowStockList.size} Items",
                        subtitle = if (lowStockList.isEmpty()) "Stock levels healthy" else "Needs reorder",
                        icon = Icons.Default.Warning,
                        color = if (lowStockList.isEmpty()) GreenSuccess else RedAlert,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppNavTab.PRODUCTS) }
                    )

                    MetricCard(
                        title = "Customers",
                        value = "${customers.size} Clients",
                        subtitle = "${invoices.size} Invoices issued",
                        icon = Icons.Default.People,
                        color = CyanAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppNavTab.CUSTOMERS) }
                    )
                }
            }
        }

        // Ready for Delivery - WhatsApp Notify Section
        if (readyRepairs.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCF8C6).copy(alpha = 0.9f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = Color(0xFF075E54)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ready for Delivery (${readyRepairs.size})",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF075E54),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            TextButton(onClick = { onNavigate(AppNavTab.REPAIRS) }) {
                                Text("View All", color = Color(0xFF075E54))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        readyRepairs.take(2).forEach { job ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${job.jobNo} • ${job.customerName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Text("${job.productName} (${job.serialNumber})", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        Text("Bill: ₹${String.format("%.0f", job.repairCost)}", fontWeight = FontWeight.Bold, color = GreenSuccess, style = MaterialTheme.typography.labelSmall)
                                    }

                                    Button(
                                        onClick = {
                                            val msg = "Hello ${job.customerName}, your device ${job.productName} (${job.jobNo}) is READY FOR DELIVERY at Burhani Infotech! Total Repair Bill: ₹${String.format("%.0f", job.repairCost)}. Thank you!"
                                            val url = "https://api.whatsapp.com/send?phone=91${job.customerMobile}&text=${Uri.encode(msg)}"
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("WhatsApp", fontSize = 12.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Low Stock Alert Banner
        if (lowStockList.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = RedAlert)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Low Stock Warnings (${lowStockList.size})", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                            TextButton(onClick = { onNavigate(AppNavTab.PRODUCTS) }) {
                                Text("Manage Stock", color = RedAlert)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        lowStockList.take(3).forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${p.name} (${p.category})", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                Text("${p.stockQuantity} in stock (Min: ${p.minStockLevel})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = RedAlert)
                            }
                        }
                    }
                }
            }
        }

        // Recent Repair Jobs List Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Repair Jobs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigate(AppNavTab.REPAIRS) }) {
                    Text("View All")
                }
            }
        }

        if (repairJobs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No repair jobs yet. Tap 'New Repair Job' to add one.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(repairJobs.take(3)) { job ->
                RepairJobCard(job = job, onClick = { onNavigate(AppNavTab.REPAIRS) })
            }
        }
    }
}

@Composable
fun QuickActionChip(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        shadowElevation = 2.dp,
        modifier = Modifier.testTag("action_chip_$label")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = contentColor)
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
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
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = color.copy(alpha = 0.15f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun RepairJobCard(
    job: RepairJob,
    onClick: () -> Unit
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
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("repair_job_card_${job.jobNo}"),
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
                        shape = RoundedCornerShape(4.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = job.jobNo,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = job.customerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusColor,
                ) {
                    Text(
                        text = job.status.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${job.productCategory}: ${job.productName} (${job.brand} ${job.modelNumber})",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (job.serialNumber.isNotBlank()) {
                Text(
                    text = "S/N: ${job.serialNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "Issue: ${job.problemDescription}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Accessories: ${job.accessoriesReceived.ifBlank { "None" }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Est. Bill: ₹${String.format("%.0f", job.repairCost)}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = GreenSuccess
                )
            }
        }
    }
}
