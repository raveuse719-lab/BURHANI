package com.example

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.WifiInspectorRepository
import com.example.ui.MainNavigationContainer
import com.example.ui.WifiViewModel
import com.example.ui.WifiViewModelFactory
import com.example.ui.theme.WifiInspectorTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request runtime permissions for Location & Wi-Fi scan on startup
        requestWifiPermissions()

        val database = AppDatabase.getInstance(this)
        val repository = WifiInspectorRepository(
            userProfileDao = database.userProfileDao(),
            trustedDeviceDao = database.trustedDeviceDao(),
            scanHistoryDao = database.scanHistoryDao()
        )
        val factory = WifiViewModelFactory(repository)

        setContent {
            WifiInspectorTheme {
                val vm: WifiViewModel = viewModel(factory = factory)
                val context = androidx.compose.ui.platform.LocalContext.current
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    vm.refreshNetworkInfo(context)
                }
                MainNavigationContainer(viewModel = vm)
            }
        }
    }

    private fun requestWifiPermissions() {
        val permissions = mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CHANGE_WIFI_STATE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        val missing = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 101)
        }
    }
}
