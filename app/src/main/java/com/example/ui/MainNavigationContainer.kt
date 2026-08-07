package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.screens.HistoryProfileScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.TpLinkExtenderScreen
import com.example.ui.theme.WifiPrimary

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Dashboard", Icons.Default.Home)
    object Scanner : BottomNavItem("scanner", "Scanner", Icons.Default.WifiTethering)
    object Extenders : BottomNavItem("extenders", "Extenders", Icons.Default.Router)
    object Tools : BottomNavItem("tools", "Tools", Icons.Default.Build)
    object History : BottomNavItem("history", "History", Icons.Default.History)
}

@Composable
fun MainNavigationContainer(
    viewModel: WifiViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Scanner,
        BottomNavItem.Extenders,
        BottomNavItem.Tools,
        BottomNavItem.History
    )

    Scaffold(
        bottomBar = {
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
                    onNavigateToScan = { navController.navigate(BottomNavItem.Scanner.route) },
                    onNavigateToSpeedTest = { navController.navigate(BottomNavItem.Tools.route) },
                    onNavigateToExtenders = { navController.navigate(BottomNavItem.Extenders.route) },
                    onNavigateToTools = { navController.navigate(BottomNavItem.Tools.route) }
                )
            }

            composable(BottomNavItem.Scanner.route) {
                ScannerScreen(viewModel = viewModel)
            }

            composable(BottomNavItem.Extenders.route) {
                TpLinkExtenderScreen(viewModel = viewModel)
            }

            composable(BottomNavItem.Tools.route) {
                ToolsScreen(viewModel = viewModel)
            }

            composable(BottomNavItem.History.route) {
                HistoryProfileScreen(viewModel = viewModel)
            }
        }
    }
}
