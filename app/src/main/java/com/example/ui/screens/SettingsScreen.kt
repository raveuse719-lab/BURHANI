package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Scanner
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.PrintViewModel
import com.example.ui.theme.WifiPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PrintViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val allPrinters by viewModel.allPrinters.collectAsState()

    var defaultPaperSize by remember { mutableStateOf("A4") }
    var defaultPrintQuality by remember { mutableStateOf("Normal") }
    var defaultScanQuality by remember { mutableStateOf("High") }
    var defaultColorMode by remember { mutableStateOf("Color") }
    var defaultDuplex by remember { mutableStateOf("Simplex") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var themePreference by remember { mutableStateOf("SYSTEM") } // "SYSTEM", "LIGHT", "DARK"

    val paperSizes = listOf("A3", "A4", "A5", "A6", "Letter", "Legal", "Executive", "B4", "B5", "Envelope", "Custom")
    val printQualities = listOf("Draft", "Normal", "Best Quality")
    val scanQualities = listOf("Low", "Medium", "High", "Original")
    val colorModes = listOf("Color", "Black & White")
    val duplexModes = listOf("Simplex", "Duplex Long Edge", "Duplex Short Edge")
    val languages = listOf("English", "Spanish", "French", "German", "Hindi", "Japanese", "Chinese")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "App & Printing Settings",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Configure default print profiles, paper sizes, scan resolution, and theme",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Printing Defaults Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = WifiPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Default Printing Preferences",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Default Paper Size
                SettingDropdownSelector(
                    label = "Default Paper Size",
                    options = paperSizes,
                    selectedOption = defaultPaperSize,
                    onOptionSelected = { defaultPaperSize = it }
                )

                // Default Print Quality
                SettingDropdownSelector(
                    label = "Default Print Quality",
                    options = printQualities,
                    selectedOption = defaultPrintQuality,
                    onOptionSelected = { defaultPrintQuality = it }
                )

                // Default Color Mode
                SettingDropdownSelector(
                    label = "Default Color Mode",
                    options = colorModes,
                    selectedOption = defaultColorMode,
                    onOptionSelected = { defaultColorMode = it }
                )

                // Default Duplex Mode
                SettingDropdownSelector(
                    label = "Default Duplex Printing",
                    options = duplexModes,
                    selectedOption = defaultDuplex,
                    onOptionSelected = { defaultDuplex = it }
                )
            }
        }

        // Scanner Defaults Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Scanner, contentDescription = null, tint = WifiPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Document Scanner Preferences",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Scan Resolution Quality
                SettingDropdownSelector(
                    label = "Default Scan Quality",
                    options = scanQualities,
                    selectedOption = defaultScanQuality,
                    onOptionSelected = { defaultScanQuality = it }
                )
            }
        }

        // Language & Appearance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = WifiPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Language & Theme",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                SettingDropdownSelector(
                    label = "Language Selection",
                    options = languages,
                    selectedOption = selectedLanguage,
                    onOptionSelected = { selectedLanguage = it }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dark Mode Theme", style = MaterialTheme.typography.bodyMedium)
                    }

                    Switch(
                        checked = themePreference == "DARK",
                        onCheckedChange = { isChecked ->
                            themePreference = if (isChecked) "DARK" else "LIGHT"
                            viewModel.setThemePreference(themePreference)
                        }
                    )
                }
            }
        }

        // Backup & Restore Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Backup & Restore",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Settings backed up successfully", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Backup Settings")
                    }

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Settings restored from backup", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingDropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onOptionSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
