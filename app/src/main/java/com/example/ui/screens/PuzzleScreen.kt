package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KidsDataProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleScreen(
    language: String,
    onBackClick: () -> Unit,
    onSpeak: (String) -> Unit,
    onCompleted: (String, Int) -> Unit
) {
    var gameType by remember { mutableStateOf("jigsaw") } // "jigsaw", "match", "shadow", "pattern"
    var difficulty by remember { mutableStateOf("Easy") } // "Easy", "Medium", "Hard"
    var isWon by remember { mutableStateOf(false) }

    // Jigsaw state
    val puzzleData = KidsDataProvider.puzzleData
    var activePuzzleIndex by remember { mutableIntStateOf(0) }
    var tileBoard = remember { mutableStateListOf("🦁", "🐘", "🚗", "🍎", "☀️", "🐶", "🐱", "🐥", "🐬") }
    var selectedFirstTileIndex by remember { mutableIntStateOf(-1) }

    // Shadow state
    var selectedShadowTarget by remember { mutableStateOf("") }
    var shadowTargetAnimal by remember { mutableStateOf("🦁") }

    // Pattern state
    var patternSelectedOption by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Puzzle Games 🧩", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            // Puzzle Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf(
                    "jigsaw" to "🧩 Jigsaw",
                    "match" to "🖼️ Match",
                    "shadow" to "👥 Shadow",
                    "pattern" to "🔄 Pattern"
                ).forEach { (type, label) ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (gameType == type) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                gameType = type
                                isWon = false
                                selectedFirstTileIndex = -1
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (gameType == type) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Difficulty Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Difficulty: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                listOf("Easy", "Medium", "Hard").forEach { diff ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (difficulty == diff) Color(0xFFFF6D00) else Color.Transparent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { difficulty = diff }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = diff,
                            color = if (difficulty == diff) Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Game Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isWon) {
                    Card(
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676)),
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Celebration, contentDescription = "Won", tint = Color.White, modifier = Modifier.size(72.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "PUZZLE SOLVED! 🌟", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color.White)
                            Text(text = "You earned +25 Coins! 🪙", color = Color.White, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    isWon = false
                                    tileBoard.shuffle()
                                }
                            ) {
                                Text("Play Next Puzzle ▶️")
                            }
                        }
                    }
                } else {
                    when (gameType) {
                        "jigsaw" -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Tap two tiles to swap and match the grid!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                val gridCols = if (difficulty == "Hard") 4 else 3
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(gridCols),
                                    modifier = Modifier.size(280.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(tileBoard.indices.toList()) { idx ->
                                        val isSelected = selectedFirstTileIndex == idx
                                        Card(
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) Color(0xFFFFD600) else MaterialTheme.colorScheme.primaryContainer
                                            ),
                                            modifier = Modifier
                                                .size(70.dp)
                                                .clickable {
                                                    if (selectedFirstTileIndex == -1) {
                                                        selectedFirstTileIndex = idx
                                                    } else {
                                                        // Swap tiles
                                                        val temp = tileBoard[selectedFirstTileIndex]
                                                        tileBoard[selectedFirstTileIndex] = tileBoard[idx]
                                                        tileBoard[idx] = temp
                                                        selectedFirstTileIndex = -1
                                                        onSpeak("Tile swapped!")

                                                        // Check victory condition
                                                        if (tileBoard[0] == "🦁" && tileBoard[1] == "🐘") {
                                                            isWon = true
                                                            onSpeak("Hooray! Puzzle solved!")
                                                            onCompleted("puzzles", 25)
                                                        }
                                                    }
                                                }
                                        ) {
                                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                                Text(text = tileBoard[idx], fontSize = 32.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        "shadow" -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Find the matching animal shadow!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(20.dp))
                                Card(
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                                    modifier = Modifier.size(130.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Text(text = "❓ 🦁", fontSize = 54.sp, color = Color.Gray)
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    listOf("🐘 Elephant", "🦁 Lion", "🦒 Giraffe").forEach { option ->
                                        Button(
                                            onClick = {
                                                if (option.contains("Lion")) {
                                                    isWon = true
                                                    onSpeak("Correct! It is a Lion!")
                                                    onCompleted("puzzles", 25)
                                                } else {
                                                    onSpeak("Oops! Try again!")
                                                }
                                            }
                                        ) {
                                            Text(text = option)
                                        }
                                    }
                                }
                            }
                        }

                        "pattern" -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Complete the Pattern!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🍎", fontSize = 40.sp)
                                    Text(text = "🍌", fontSize = 40.sp)
                                    Text(text = "🍎", fontSize = 40.sp)
                                    Text(text = "❓", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(text = "What comes next?", fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    listOf("🍎 Apple", "🍌 Banana", "🍇 Grapes").forEach { choice ->
                                        Button(
                                            onClick = {
                                                if (choice.contains("Banana")) {
                                                    isWon = true
                                                    onSpeak("Bingo! Apple Banana Apple Banana!")
                                                    onCompleted("puzzles", 25)
                                                } else {
                                                    onSpeak("Not quite! Try another choice!")
                                                }
                                            }
                                        ) {
                                            Text(text = choice)
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            // Match Picture
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Match the Picture to its Name!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(text = "🚗", fontSize = 80.sp)
                                Spacer(modifier = Modifier.height(20.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    listOf("Bicycle 🚲", "Car 🚗", "Airplane ✈️").forEach { opt ->
                                        Button(
                                            onClick = {
                                                if (opt.contains("Car")) {
                                                    isWon = true
                                                    onSpeak("Awesome match! Car!")
                                                    onCompleted("puzzles", 25)
                                                } else {
                                                    onSpeak("Try again!")
                                                }
                                            },
                                            modifier = Modifier.width(200.dp)
                                        ) {
                                            Text(text = opt)
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
}
