package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CustomQuizEntity
import com.example.data.model.KidsDataProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    customQuizzes: List<CustomQuizEntity>,
    language: String,
    onBackClick: () -> Unit,
    onSpeak: (String) -> Unit,
    onCompleted: (String, Int) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("ABC") } // "ABC", "Numbers", "Animals", "Colors"
    var currentQuestionIdx by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selectedAnswerIndex by remember { mutableIntStateOf(-1) }
    var isAnswered by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    val defaultQuestions = KidsDataProvider.quizQuestions
    val activeQuestions = remember(selectedCategory, customQuizzes) {
        val filteredDefault = defaultQuestions.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        val filteredCustom = customQuizzes.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        val customMapped = filteredCustom.map { cq ->
            com.example.data.model.QuizQuestion(
                id = cq.id,
                category = cq.category,
                questionEn = cq.question,
                questionHi = cq.question,
                questionGu = cq.question,
                options = listOf(cq.option1, cq.option2, cq.option3, cq.option4),
                correctIndex = cq.correctIndex,
                explanationEn = cq.explanation
            )
        }
        if ((filteredDefault + customMapped).isEmpty()) defaultQuestions else (filteredDefault + customMapped)
    }

    val currentQ = activeQuestions.getOrElse(currentQuestionIdx) { activeQuestions.first() }

    fun resetQuiz() {
        currentQuestionIdx = 0
        score = 0
        selectedAnswerIndex = -1
        isAnswered = false
        isFinished = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Kids Quiz Fun ❓", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { resetQuiz() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart Quiz", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Category Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("ABC", "Numbers", "Animals", "Colors").forEach { cat ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (selectedCategory == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                selectedCategory = cat
                                resetQuiz()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (selectedCategory == cat) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Score Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Question ${currentQuestionIdx + 1} / ${activeQuestions.size}", fontWeight = FontWeight.Bold)
                Text(text = "Score: $score 🌟", fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF9100), fontSize = 16.sp)
            }

            // Quiz Content Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isFinished) {
                    Card(
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Celebration, contentDescription = "Completed", tint = Color(0xFFFFD600), modifier = Modifier.size(80.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "QUIZ COMPLETED! 🎉", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                            Text(text = "You scored $score points!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Earned +${score * 10} Coins 🪙", color = MaterialTheme.colorScheme.primary, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(onClick = { resetQuiz() }) {
                                Text("Play Again 🔄")
                            }
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = when (language) {
                                    "hi" -> currentQ.questionHi
                                    "gu" -> currentQ.questionGu
                                    else -> currentQ.questionEn
                                },
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )

                            // Options List
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                currentQ.options.forEachIndexed { oIdx, option ->
                                    val isSelected = selectedAnswerIndex == oIdx
                                    val isCorrect = oIdx == currentQ.correctIndex

                                    val btnColor = when {
                                        !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
                                        isCorrect -> Color(0xFF00E676)
                                        isSelected -> Color(0xFFFF5252)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = btnColor,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(20.dp))
                                            .clickable(!isAnswered) {
                                                selectedAnswerIndex = oIdx
                                                isAnswered = true
                                                if (isCorrect) {
                                                    score += 10
                                                    onSpeak("Correct! Fantastic job!")
                                                } else {
                                                    onSpeak("Oops! The correct answer is ${currentQ.options[currentQ.correctIndex]}")
                                                }
                                            }
                                            .testTag("quiz_option_$oIdx")
                                    ) {
                                        Text(
                                            text = option,
                                            modifier = Modifier.padding(16.dp),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            color = if (isAnswered && (isCorrect || isSelected)) Color.White else MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            // Explanation & Next Button
                            if (isAnswered) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = currentQ.explanationEn,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            if (currentQuestionIdx < activeQuestions.size - 1) {
                                                currentQuestionIdx++
                                                selectedAnswerIndex = -1
                                                isAnswered = false
                                            } else {
                                                isFinished = true
                                                onCompleted("quiz", score * 10)
                                            }
                                        },
                                        modifier = Modifier.testTag("next_question_button")
                                    ) {
                                        Text(if (currentQuestionIdx < activeQuestions.size - 1) "Next Question ▶️" else "Finish Quiz 🏁")
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {
                                        val qText = when (language) {
                                            "hi" -> currentQ.questionHi
                                            "gu" -> currentQ.questionGu
                                            else -> currentQ.questionEn
                                        }
                                        onSpeak(qText)
                                    }
                                ) {
                                    Text("🔊 Read Question")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
