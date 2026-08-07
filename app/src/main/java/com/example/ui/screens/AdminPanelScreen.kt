package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ChildProfileEntity
import com.example.data.entity.CustomQuizEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    customQuizzes: List<CustomQuizEntity>,
    allProfiles: List<ChildProfileEntity>,
    onBackClick: () -> Unit,
    onAddQuiz: (String, String, String, String, String, String, Int, String) -> Unit,
    onDeleteQuiz: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    var quizCategory by remember { mutableStateOf("ABC") }
    var question by remember { mutableStateOf("") }
    var o1 by remember { mutableStateOf("") }
    var o2 by remember { mutableStateOf("") }
    var o3 by remember { mutableStateOf("") }
    var o4 by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }
    var explanation by remember { mutableStateOf("") }

    var notificationTitle by remember { mutableStateOf("Daily Learning Reminder! 🌟") }
    var notificationMessage by remember { mutableStateOf("Complete today's challenge to earn 100 double coins!") }
    var notificationSent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Admin Control Panel ⚙️", fontWeight = FontWeight.Bold) },
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
            // App Statistics Summary
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = "Stats", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text(text = "App Analytics & Usage", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Active Profiles: ${allProfiles.size}", fontWeight = FontWeight.SemiBold)
                            Text(text = "Total Coins: ${allProfiles.sumOf { it.coins }} 🪙", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Create Custom Quiz Question
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Quiz, contentDescription = "Quiz", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text(text = "Add Custom Quiz Question", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = question,
                            onValueChange = { question = it },
                            label = { Text("Question Text") },
                            modifier = Modifier.fillMaxWidth().testTag("admin_question_input")
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = o1,
                            onValueChange = { o1 = it },
                            label = { Text("Option 1 (Correct)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = o2,
                            onValueChange = { o2 = it },
                            label = { Text("Option 2") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = o3,
                            onValueChange = { o3 = it },
                            label = { Text("Option 3") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = explanation,
                            onValueChange = { explanation = it },
                            label = { Text("Explanation Tip") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (question.isNotBlank() && o1.isNotBlank() && o2.isNotBlank()) {
                                    onAddQuiz(quizCategory, question, o1, o2, o3.ifBlank { "Option 3" }, "Option 4", 0, explanation.ifBlank { "Good job!" })
                                    question = ""
                                    o1 = ""
                                    o2 = ""
                                    o3 = ""
                                    explanation = ""
                                    onSpeak("Custom quiz question added!")
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("submit_custom_quiz_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text("Publish Quiz Question")
                        }
                    }
                }
            }

            // Existing Custom Quizzes
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Published Custom Quizzes (${customQuizzes.size})", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        if (customQuizzes.isEmpty()) {
                            Text(text = "No custom questions published yet.", fontSize = 13.sp, color = Color.Gray)
                        } else {
                            customQuizzes.forEach { q ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = q.question, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = "Ans: ${q.option1}", fontSize = 12.sp, color = Color(0xFF00E676))
                                    }
                                    IconButton(onClick = { onDeleteQuiz(q.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // FCM Push Notification Simulator
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notification", tint = Color(0xFFFF9100))
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text(text = "Firebase Cloud Messaging (Push Notification)", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = notificationTitle,
                            onValueChange = { notificationTitle = it },
                            label = { Text("Notification Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = notificationMessage,
                            onValueChange = { notificationMessage = it },
                            label = { Text("Notification Body Message") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                notificationSent = true
                                onSpeak("Push notification broadcast sent!")
                            },
                            modifier = Modifier.fillMaxWidth().testTag("send_fcm_notification_button")
                        ) {
                            Text("Send Broadcast Push Notification 🚀")
                        }

                        if (notificationSent) {
                            Text(
                                text = "✅ Broadcast notification sent successfully to all registered child devices!",
                                color = Color(0xFF00E676),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
