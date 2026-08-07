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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
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
import androidx.compose.runtime.mutableStateListOf
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

data class MemoryCard(
    val id: Int,
    val content: String,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryGameScreen(
    language: String,
    onBackClick: () -> Unit,
    onSpeak: (String) -> Unit,
    onCompleted: (String, Int) -> Unit
) {
    var themeCategory by remember { mutableStateOf("animals") } // "animals", "numbers", "shapes"
    var moveCount by remember { mutableIntStateOf(0) }
    var matchedPairs by remember { mutableIntStateOf(0) }

    val cards = remember(themeCategory) {
        val emojis = when (themeCategory) {
            "numbers" -> listOf("1️⃣", "2️⃣", "3️⃣", "4️⃣")
            "shapes" -> listOf("🔴", "🟦", "🔺", "⭐")
            else -> listOf("🦁", "🐘", "🐵", "🐶")
        }
        val cardList = (emojis + emojis).shuffled().mapIndexed { index, symbol ->
            MemoryCard(id = index, content = symbol)
        }
        mutableStateListOf(*cardList.toTypedArray())
    }

    var selectedFirstIndex by remember { mutableIntStateOf(-1) }
    var isProcessing by remember { mutableStateOf(false) }

    fun resetGame() {
        moveCount = 0
        matchedPairs = 0
        selectedFirstIndex = -1
        isProcessing = false
        val emojis = when (themeCategory) {
            "numbers" -> listOf("1️⃣", "2️⃣", "3️⃣", "4️⃣")
            "shapes" -> listOf("🔴", "🟦", "🔺", "⭐")
            else -> listOf("🦁", "🐘", "🐵", "🐶")
        }
        val shuffled = (emojis + emojis).shuffled()
        cards.indices.forEach { i ->
            cards[i] = MemoryCard(id = i, content = shuffled[i])
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Memory Card Matching 🃏", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { resetGame() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = Color.White)
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
            // Memory Category Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("animals" to "🦁 Animals", "numbers" to "🔢 Numbers", "shapes" to "🔺 Shapes").forEach { (cat, label) ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (themeCategory == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                themeCategory = cat
                                resetGame()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (themeCategory == cat) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Move Count Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Flips: $moveCount", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Matches: $matchedPairs / 4", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
            }

            // Memory Cards Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (matchedPairs == 4) {
                    Card(
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD600)),
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Celebration, contentDescription = "Won", tint = Color.Black, modifier = Modifier.size(72.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "MEMORY CHAMPION! 🏆", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color.Black)
                            Text(text = "Completed in $moveCount flips! +30 Coins 🪙", color = Color.Black, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(onClick = { resetGame() }) {
                                Text("Play Again 🔄")
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(cards) { index, card ->
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (card.isFlipped || card.isMatched) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        if (isProcessing || card.isFlipped || card.isMatched) return@clickable

                                        // Flip card
                                        cards[index] = card.copy(isFlipped = true)

                                        if (selectedFirstIndex == -1) {
                                            selectedFirstIndex = index
                                        } else {
                                            moveCount++
                                            val firstCard = cards[selectedFirstIndex]

                                            if (firstCard.content == card.content) {
                                                // Match found!
                                                cards[selectedFirstIndex] = firstCard.copy(isMatched = true)
                                                cards[index] = card.copy(isMatched = true, isFlipped = true)
                                                matchedPairs++
                                                selectedFirstIndex = -1
                                                onSpeak("Match found!")

                                                if (matchedPairs == 4) {
                                                    onCompleted("memory", 30)
                                                }
                                            } else {
                                                // Not a match - flip back
                                                isProcessing = true
                                                onSpeak("Try again!")
                                                cards[selectedFirstIndex] = firstCard.copy(isFlipped = false)
                                                cards[index] = card.copy(isFlipped = false)
                                                selectedFirstIndex = -1
                                                isProcessing = false
                                            }
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    if (card.isFlipped || card.isMatched) {
                                        Text(text = card.content, fontSize = 38.sp)
                                    } else {
                                        Text(text = "❓", fontSize = 32.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
