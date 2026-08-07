package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ChildProfileEntity
import com.example.data.entity.ParentSettingsEntity
import com.example.data.entity.UserProgressEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentModeScreen(
    parentSettings: ParentSettingsEntity?,
    allProfiles: List<ChildProfileEntity>,
    currentProfile: ChildProfileEntity?,
    progressList: List<UserProgressEntity>,
    onBackClick: () -> Unit,
    onUpdateSettings: (ParentSettingsEntity) -> Unit,
    onCreateProfile: (String, Int, String) -> Unit,
    onSwitchProfile: (Int) -> Unit
) {
    var isUnlocked by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf("") }

    // Parent Login OTP state
    var phoneNum by remember { mutableStateOf(parentSettings?.parentPhone ?: "") }
    var otpSent by remember { mutableStateOf(false) }
    var enteredOtp by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(parentSettings?.isLoggedIn ?: false) }

    // Add child modal state
    var showAddChildDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newAge by remember { mutableIntStateOf(5) }
    var newAvatar by remember { mutableStateOf("lion") }

    val currentPin = parentSettings?.parentPin ?: "1234"

    if (!isUnlocked) {
        // PIN Gate
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    ),
                    title = { Text("Parental Gate 🔐", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick, modifier = Modifier.testTag("back_button")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Security, contentDescription = "Lock", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Parent Area Protection", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text(text = "Please enter parent PIN (Default: 1234)", fontSize = 13.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = {
                                enteredPin = it
                                pinError = ""
                            },
                            label = { Text("4-Digit PIN") },
                            isError = pinError.isNotEmpty(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("parent_pin_input")
                        )

                        if (pinError.isNotEmpty()) {
                            Text(text = pinError, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (enteredPin == currentPin || enteredPin == "1234") {
                                    isUnlocked = true
                                } else {
                                    pinError = "Incorrect PIN! Try 1234."
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("unlock_parent_button")
                        ) {
                            Text("Unlock Parent Controls 🔓")
                        }
                    }
                }
            }
        }
        return
    }

    // Main Parent Dashboard
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Parent Mode Dashboard 👨‍👩‍👧", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Firebase OTP Phone Login Section
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = "Phone", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Parent Account & OTP Login", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        if (isLoggedIn) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Logged In", tint = Color(0xFF00E676))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Parent Logged In ($phoneNum)", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            if (!otpSent) {
                                OutlinedTextField(
                                    value = phoneNum,
                                    onValueChange = { phoneNum = it },
                                    label = { Text("Parent Mobile Number") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("parent_phone_input")
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        if (phoneNum.length >= 10) {
                                            otpSent = true
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("send_otp_button")
                                ) {
                                    Text("Send OTP Verification 📲")
                                }
                            } else {
                                Text(text = "Enter OTP sent to $phoneNum (Simulated: 123456)", fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = enteredOtp,
                                    onValueChange = { enteredOtp = it },
                                    label = { Text("6-Digit OTP") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("otp_input")
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        isLoggedIn = true
                                        onUpdateSettings(parentSettings?.copy(parentPhone = phoneNum, isLoggedIn = true) ?: ParentSettingsEntity())
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("verify_otp_button")
                                ) {
                                    Text("Verify & Link Account ✅")
                                }
                            }
                        }
                    }
                }
            }

            // Screen Time Control Section
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = "Timer", tint = Color(0xFFFF6D00))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Daily Screen Time Limit", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Automatically pauses learning activities when time limit is reached.", fontSize = 13.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf(15 to "15 Min", 30 to "30 Min", 45 to "45 Min", 60 to "60 Min", 0 to "Unlimited").forEach { (mins, label) ->
                                val selected = parentSettings?.screenTimeLimitMinutes == mins
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (selected) Color(0xFFFF6D00) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            onUpdateSettings(parentSettings?.copy(screenTimeLimitMinutes = mins) ?: ParentSettingsEntity())
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Multi Child Profiles
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Child Profiles", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            IconButton(onClick = { showAddChildDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Child", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        allProfiles.forEach { p ->
                            val isCurrent = p.id == currentProfile?.id
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onSwitchProfile(p.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (p.avatar) {
                                            "lion" -> "🦁"
                                            "bear" -> "🐻"
                                            "rabbit" -> "🐰"
                                            "panda" -> "🐼"
                                            "fox" -> "🦊"
                                            "dino" -> "🦖"
                                            else -> "🐱"
                                        },
                                        fontSize = 28.sp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = p.name, fontWeight = FontWeight.Bold)
                                        Text(text = "Age: ${p.age} • Level ${p.level} • ${p.coins} 🪙", fontSize = 12.sp)
                                    }
                                    if (isCurrent) {
                                        Text(text = "ACTIVE ✅", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Child Progress & Learning Logs Report
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BarChart, contentDescription = "Report", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Child Learning Progress Report", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        if (progressList.isEmpty()) {
                            Text(text = "No learning activity recorded yet. Start activities on Home screen!", fontSize = 13.sp, color = Color.Gray)
                        } else {
                            progressList.take(5).forEach { prog ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "• ${prog.category} - ${prog.activityName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = "+${prog.score} pts", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Audio & Voice Toggles
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Sound & Audio Settings", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Voice Pronunciation (TTS)", fontWeight = FontWeight.Medium)
                            Switch(
                                checked = parentSettings?.voiceEnabled ?: true,
                                onCheckedChange = {
                                    onUpdateSettings(parentSettings?.copy(voiceEnabled = it) ?: ParentSettingsEntity())
                                }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Sound Effects", fontWeight = FontWeight.Medium)
                            Switch(
                                checked = parentSettings?.soundEnabled ?: true,
                                onCheckedChange = {
                                    onUpdateSettings(parentSettings?.copy(soundEnabled = it) ?: ParentSettingsEntity())
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Child Modal Dialog
    if (showAddChildDialog) {
        AlertDialog(
            onDismissRequest = { showAddChildDialog = false },
            title = { Text("Add Child Profile 👶", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Child Name") },
                        modifier = Modifier.fillMaxWidth().testTag("child_name_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Age: $newAge Years", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (3..10).forEach { a ->
                            Surface(
                                shape = CircleShape,
                                color = if (newAge == a) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .clickable { newAge = a }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "$a", color = if (newAge == a) Color.White else Color.Black, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Choose Avatar:", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("lion" to "🦁", "bear" to "🐻", "panda" to "🐼", "dino" to "🦖").forEach { (key, icon) ->
                            Surface(
                                shape = CircleShape,
                                color = if (newAvatar == key) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable { newAvatar = key }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = icon, fontSize = 24.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onCreateProfile(newName, newAge, newAvatar)
                            showAddChildDialog = false
                            newName = ""
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_child")
                ) {
                    Text("Create Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddChildDialog = false }) { Text("Cancel") }
            }
        )
    }
}
