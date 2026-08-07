package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Scanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.DocumentScannerScreen
import com.example.ui.screens.FilePickerScreen
import com.example.ui.screens.HistoryProfileScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PcPrintServerScreen
import com.example.ui.screens.PrintPreviewScreen
import com.example.ui.screens.PrintSpoolerModal
import com.example.ui.screens.PrinterDiscoveryScreen
import com.example.ui.screens.SettingsScreen

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Dashboard", Icons.Default.Home)
    object Printers : BottomNavItem("printers", "Printers", Icons.Default.Devices)
    object Scan : BottomNavItem("scan", "Scan", Icons.Default.Scanner)
    object Files : BottomNavItem("files", "Documents", Icons.Default.FolderOpen)
    object PcServer : BottomNavItem("pc_server", "PC Server", Icons.Default.Computer)
    object History : BottomNavItem("history", "History", Icons.Default.History)
    object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun MainNavigationContainer(
    viewModel: PrintViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val uiState by viewModel.uiState.collectAsState()

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Printers,
        BottomNavItem.Scan,
        BottomNavItem.Files,
        BottomNavItem.History,
        BottomNavItem.Settings
    )

    Scaffold(
        bottomBar = {
            if (currentRoute != "preview") {
                NavigationBar(
                    tonalElevation = 8.dp
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(text = item.title)
                            },
                            modifier = Modifier.testTag("nav_item_${item.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToDiscovery = { navController.navigate(BottomNavItem.Printers.route) },
                    onNavigateToFilePicker = { navController.navigate(BottomNavItem.Files.route) },
                    onNavigateToPcServer = { navController.navigate(BottomNavItem.PcServer.route) },
                    onNavigateToPreview = { navController.navigate("preview") },
                    onNavigateToHistory = { navController.navigate(BottomNavItem.History.route) }
                )
            }

            composable(BottomNavItem.Printers.route) {
                PrinterDiscoveryScreen(
                    viewModel = viewModel,
                    onNavigateToPreview = { navController.navigate("preview") }
                )
            }

            composable(BottomNavItem.Scan.route) {
                DocumentScannerScreen(
                    viewModel = viewModel,
                    onNavigateToPreview = { navController.navigate("preview") }
                )
            }

            composable(BottomNavItem.Files.route) {
                FilePickerScreen(
                    viewModel = viewModel,
                    onNavigateToPreview = { navController.navigate("preview") }
                )
            }

            composable(BottomNavItem.History.route) {
                HistoryProfileScreen(
                    viewModel = viewModel
                )
            }

            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel
                )
            }

            composable(BottomNavItem.PcServer.route) {
                PcPrintServerScreen(
                    viewModel = viewModel,
                    onNavigateToPreview = { navController.navigate("preview") }
                )
            }

            composable("preview") {
                PrintPreviewScreen(
                    viewModel = viewModel,
                    onNavigateToDiscovery = { navController.navigate(BottomNavItem.Printers.route) }
                )
            }
        }

        if (uiState.showSpoolerModal) {
            PrintSpoolerModal(
                spoolerProgress = uiState.spoolerProgress,
                onDismiss = { viewModel.dismissSpoolerModal() }
            )
        }
    }
}

