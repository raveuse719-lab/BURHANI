package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.screens.*
import com.example.ui.theme.TechBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationContainer(
    viewModel: BurhaniViewModel
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val lowStockList by viewModel.lowStockList.collectAsState()
    val repairJobs by viewModel.repairJobsList.collectAsState()

    val pendingRepairsCount = repairJobs.count { it.status != "DELIVERED" && it.status != "RETURNED_NO_REPAIR" }

    var openNewRepairDirectly by remember { mutableStateOf(false) }
    var openNewInvoiceDirectly by remember { mutableStateOf(false) }

    var showNavDrawer by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = when (currentTab) {
                                AppNavTab.DASHBOARD -> "Burhani Infotech ERP"
                                AppNavTab.CUSTOMERS -> "Customer Directory"
                                AppNavTab.PRODUCTS -> "Inventory & Stock"
                                AppNavTab.REPAIRS -> "Repair & Service Jobs"
                                AppNavTab.INVOICES -> "Billing & Quotations"
                                AppNavTab.WARRANTY -> "Warranty Tracking"
                                AppNavTab.REPORTS -> "Reports & Analytics"
                                AppNavTab.SUPPLIERS -> "Supplier Wholesalers"
                                AppNavTab.SETTINGS -> "Settings & Profile"
                            },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "User: ${user.username}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TechBlue
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { showNavDrawer = true },
                        modifier = Modifier.testTag("open_menu_btn")
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    if (lowStockList.isNotEmpty()) {
                        BadgedBox(
                            badge = { Badge { Text("${lowStockList.size}") } }
                        ) {
                            IconButton(onClick = { viewModel.selectTab(AppNavTab.PRODUCTS) }) {
                                Icon(Icons.Default.Warning, contentDescription = "Low Stock Alert", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    IconButton(onClick = { viewModel.selectTab(AppNavTab.SETTINGS) }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == AppNavTab.DASHBOARD,
                    onClick = { viewModel.selectTab(AppNavTab.DASHBOARD) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )
                NavigationBarItem(
                    selected = currentTab == AppNavTab.REPAIRS,
                    onClick = { viewModel.selectTab(AppNavTab.REPAIRS) },
                    icon = {
                        if (pendingRepairsCount > 0) {
                            BadgedBox(badge = { Badge { Text("$pendingRepairsCount") } }) {
                                Icon(Icons.Default.Build, contentDescription = "Repairs")
                            }
                        } else {
                            Icon(Icons.Default.Build, contentDescription = "Repairs")
                        }
                    },
                    label = { Text("Repairs") },
                    modifier = Modifier.testTag("nav_tab_repairs")
                )
                NavigationBarItem(
                    selected = currentTab == AppNavTab.INVOICES,
                    onClick = { viewModel.selectTab(AppNavTab.INVOICES) },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Billing") },
                    label = { Text("Billing") },
                    modifier = Modifier.testTag("nav_tab_billing")
                )
                NavigationBarItem(
                    selected = currentTab == AppNavTab.PRODUCTS,
                    onClick = { viewModel.selectTab(AppNavTab.PRODUCTS) },
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Inventory") },
                    label = { Text("Inventory") },
                    modifier = Modifier.testTag("nav_tab_inventory")
                )
                NavigationBarItem(
                    selected = currentTab == AppNavTab.CUSTOMERS,
                    onClick = { viewModel.selectTab(AppNavTab.CUSTOMERS) },
                    icon = { Icon(Icons.Default.People, contentDescription = "Clients") },
                    label = { Text("Clients") },
                    modifier = Modifier.testTag("nav_tab_clients")
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    AppNavTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.selectTab(it) },
                        onOpenNewRepair = {
                            openNewRepairDirectly = true
                            viewModel.selectTab(AppNavTab.REPAIRS)
                        },
                        onOpenNewInvoice = {
                            openNewInvoiceDirectly = true
                            viewModel.selectTab(AppNavTab.INVOICES)
                        }
                    )
                    AppNavTab.CUSTOMERS -> CustomersScreen(viewModel = viewModel)
                    AppNavTab.PRODUCTS -> ProductsScreen(viewModel = viewModel)
                    AppNavTab.REPAIRS -> {
                        RepairsScreen(viewModel = viewModel, initialOpenAdd = openNewRepairDirectly)
                        openNewRepairDirectly = false
                    }
                    AppNavTab.INVOICES -> {
                        InvoicesScreen(viewModel = viewModel, initialOpenAdd = openNewInvoiceDirectly)
                        openNewInvoiceDirectly = false
                    }
                    AppNavTab.WARRANTY -> WarrantyScreen(viewModel = viewModel)
                    AppNavTab.REPORTS -> ReportsScreen(viewModel = viewModel)
                    AppNavTab.SUPPLIERS -> SuppliersScreen(viewModel = viewModel)
                    AppNavTab.SETTINGS -> SettingsBackupScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Modal Navigation Drawer Modal Sheet
    if (showNavDrawer) {
        ModalBottomSheet(
            onDismissRequest = { showNavDrawer = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Burhani Infotech ERP Menu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TechBlue
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                DrawerMenuItem(
                    label = "Dashboard",
                    icon = Icons.Default.Dashboard,
                    selected = currentTab == AppNavTab.DASHBOARD,
                    onClick = { viewModel.selectTab(AppNavTab.DASHBOARD); showNavDrawer = false }
                )
                DrawerMenuItem(
                    label = "Customer Management",
                    icon = Icons.Default.People,
                    selected = currentTab == AppNavTab.CUSTOMERS,
                    onClick = { viewModel.selectTab(AppNavTab.CUSTOMERS); showNavDrawer = false }
                )
                DrawerMenuItem(
                    label = "Product & Inventory Management",
                    icon = Icons.Default.Inventory2,
                    selected = currentTab == AppNavTab.PRODUCTS,
                    onClick = { viewModel.selectTab(AppNavTab.PRODUCTS); showNavDrawer = false }
                )
                DrawerMenuItem(
                    label = "Repair & Service Management",
                    icon = Icons.Default.Build,
                    selected = currentTab == AppNavTab.REPAIRS,
                    onClick = { viewModel.selectTab(AppNavTab.REPAIRS); showNavDrawer = false }
                )
                DrawerMenuItem(
                    label = "GST Billing & Quotations",
                    icon = Icons.Default.ReceiptLong,
                    selected = currentTab == AppNavTab.INVOICES,
                    onClick = { viewModel.selectTab(AppNavTab.INVOICES); showNavDrawer = false }
                )
                DrawerMenuItem(
                    label = "Warranty Management",
                    icon = Icons.Default.VerifiedUser,
                    selected = currentTab == AppNavTab.WARRANTY,
                    onClick = { viewModel.selectTab(AppNavTab.WARRANTY); showNavDrawer = false }
                )
                DrawerMenuItem(
                    label = "Reports & Analytics",
                    icon = Icons.Default.BarChart,
                    selected = currentTab == AppNavTab.REPORTS,
                    onClick = { viewModel.selectTab(AppNavTab.REPORTS); showNavDrawer = false }
                )
                DrawerMenuItem(
                    label = "Supplier Wholesalers",
                    icon = Icons.Default.LocalShipping,
                    selected = currentTab == AppNavTab.SUPPLIERS,
                    onClick = { viewModel.selectTab(AppNavTab.SUPPLIERS); showNavDrawer = false }
                )
                DrawerMenuItem(
                    label = "Settings, Profile & Backup",
                    icon = Icons.Default.Settings,
                    selected = currentTab == AppNavTab.SETTINGS,
                    onClick = { viewModel.selectTab(AppNavTab.SETTINGS); showNavDrawer = false }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) TechBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth().testTag("drawer_item_$label")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) TechBlue else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) TechBlue else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
