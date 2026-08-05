package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BurhaniViewModel
import com.example.ui.MainNavigationContainer
import com.example.ui.theme.BurhaniTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BurhaniTheme {
                val vm: BurhaniViewModel = viewModel()
                MainNavigationContainer(viewModel = vm)
            }
        }
    }
}
