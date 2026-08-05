package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.BurhaniViewModel
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedAlert
import com.example.ui.theme.TechBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: BurhaniViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val invoices by viewModel.invoicesList.collectAsState()
    val repairJobs by viewModel.repairJobsList.collectAsState()
    val products by viewModel.productsList.collectAsState()
    val customers by viewModel.customersList.collectAsState()

    val totalSalesRevenue = invoices.filter { it.type == "GST_INVOICE" }.sumOf { it.totalAmount }
    val totalGstCollected = invoices.filter { it.type == "GST_INVOICE" }.sumOf { it.cgstAmount + it.sgstAmount + it.igstAmount }
    val totalRepairBillRevenue = repairJobs.filter { it.status == "DELIVERED" || it.status == "READY" }.sumOf { it.repairCost }

    val stockValuationPurchase = products.sumOf { it.purchasePrice * it.stockQuantity }
    val stockValuationSelling = products.sumOf { it.sellingPrice * it.stockQuantity }
    val potentialProfitInStock = stockValuationSelling - stockValuationPurchase

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Business Reports & ERP Analytics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Burhani Infotech Financial & Operational Metrics", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                IconButton(onClick = {
                    val reportText = """
                        BURHANI INFOTECH - BUSINESS SUMMARY REPORT
                        ========================================
                        Total Sales Invoices Revenue: ₹${String.format("%.2f", totalSalesRevenue)}
                        Total GST Collected: ₹${String.format("%.2f", totalGstCollected)}
                        Total Service & Repair Revenue: ₹${String.format("%.2f", totalRepairBillRevenue)}
                        
                        STOCK VALUATION
                        ----------------------------------------
                        Total Cost Value of Inventory: ₹${String.format("%.2f", stockValuationPurchase)}
                        Total Retail Selling Value: ₹${String.format("%.2f", stockValuationSelling)}
                        Estimated Profit in Stock: ₹${String.format("%.2f", potentialProfitInStock)}
                        
                        REPAIR METRICS
                        ----------------------------------------
                        Total Repair Jobs: ${repairJobs.size}
                        Completed/Delivered: ${repairJobs.count { it.status == "DELIVERED" }}
                        Pending Jobs: ${repairJobs.count { it.status != "DELIVERED" }}
                    """.trimIndent()

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Burhani Infotech ERP Summary Report")
                        putExtra(Intent.EXTRA_TEXT, reportText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Export Report"))
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Export Report", tint = TechBlue)
                }
            }
        }

        // Sales & Revenue Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("report_sales_card"),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("1. Revenue & Sales Report", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechBlue)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("GST Invoices Revenue:")
                        Text("₹${String.format("%.2f", totalSalesRevenue)}", fontWeight = FontWeight.Bold, color = GreenSuccess)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Repair & Service Revenue:")
                        Text("₹${String.format("%.2f", totalRepairBillRevenue)}", fontWeight = FontWeight.Bold, color = GreenSuccess)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total GST Collected (Tax):")
                        Text("₹${String.format("%.2f", totalGstCollected)}", fontWeight = FontWeight.Bold, color = TechBlue)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Combined Total Revenue:", fontWeight = FontWeight.Bold)
                        Text("₹${String.format("%.2f", totalSalesRevenue + totalRepairBillRevenue)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = GreenSuccess)
                    }
                }
            }
        }

        // Stock Valuation Report
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("report_stock_valuation_card"),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("2. Stock Valuation & Margin Report", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechBlue)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Inventory Cost Value:")
                        Text("₹${String.format("%.2f", stockValuationPurchase)}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Inventory Selling Retail Value:")
                        Text("₹${String.format("%.2f", stockValuationSelling)}", fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estimated Gross Profit Margin:", fontWeight = FontWeight.Bold)
                        Text("₹${String.format("%.2f", potentialProfitInStock)}", fontWeight = FontWeight.Bold, color = GreenSuccess)
                    }
                }
            }
        }

        // Repair Operations Report
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("report_repairs_card"),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("3. Repair Operations Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechBlue)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Repair Jobs Received:")
                        Text("${repairJobs.size}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ready for Delivery:")
                        Text("${repairJobs.count { it.status == "READY" }}", fontWeight = FontWeight.Bold, color = GreenSuccess)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Waiting for Spare Parts:")
                        Text("${repairJobs.count { it.status == "WAITING_PARTS" }}", fontWeight = FontWeight.Bold, color = RedAlert)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivered to Customers:")
                        Text("${repairJobs.count { it.status == "DELIVERED" }}", fontWeight = FontWeight.Bold, color = TechBlue)
                    }
                }
            }
        }
    }
}
