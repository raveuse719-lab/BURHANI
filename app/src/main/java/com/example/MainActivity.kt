package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.KidsViewModel
import com.example.ui.MainNavigationContainer
import com.example.ui.theme.KidsLearningTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KidsLearningTheme {
                val vm: KidsViewModel = viewModel()
                MainNavigationContainer(viewModel = vm)
            }
        }
    }
}
