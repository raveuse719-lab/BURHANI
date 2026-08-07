package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.VolumeUp
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LearningScreen(
    category: String,
    language: String,
    onBackClick: () -> Unit,
    onSpeak: (String) -> Unit,
    onCompleted: (String, Int) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val categoryTitle = when (category) {
        "abc" -> if (language == "hi") "एबीसी अक्षर" else if (language == "gu") "ABC અક્ષરો" else "Alphabet A-Z"
        "numbers" -> if (language == "hi") "संख्या 1-100" else if (language == "gu") "નંબર 1-100" else "Numbers 1-100"
        "colors" -> if (language == "hi") "रंग सीखें" else if (language == "gu") "રંગો શીખો" else "Learn Colors"
        "shapes" -> if (language == "hi") "आकृतियां" else if (language == "gu") "આકારો" else "Learn Shapes"
        "animals" -> if (language == "hi") "जानवर और आवाजें" else if (language == "gu") "પ્રાણીઓ અને અવાજ" else "Animals & Sounds"
        "birds" -> if (language == "hi") "पक्षी और आवाजें" else if (language == "gu") "પક્ષીઓ અને અવાજ" else "Birds & Sounds"
        "fruits" -> if (language == "hi") "फल और सब्जियां" else if (language == "gu") "ફળો અને શાકભાજી" else "Fruits & Vegetables"
        "vegetables" -> if (language == "hi") "सब्जियां" else if (language == "gu") "શાકભાજી" else "Vegetables"
        else -> if (language == "hi") "वाहनों के नाम" else if (language == "gu") "વાહનોના નામ" else "Vehicles & Sounds"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text(text = categoryTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("back_button")
                    ) {
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
            when (category) {
                "abc" -> {
                    val list = KidsDataProvider.abcList
                    val item = list[selectedIndex]

                    // Top quick scroll bar
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(list.indices.toList()) { idx ->
                            val itm = list[idx]
                            Surface(
                                shape = CircleShape,
                                color = if (idx == selectedIndex) Color(itm.colorHex) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        selectedIndex = idx
                                        val word = when (language) {
                                            "hi" -> "${itm.letter} से ${itm.wordHi}"
                                            "gu" -> "${itm.letter} થી ${itm.wordGu}"
                                            else -> "${itm.letter} for ${itm.wordEn}"
                                        }
                                        onSpeak(word)
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = itm.letter,
                                        fontWeight = FontWeight.Bold,
                                        color = if (idx == selectedIndex) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Main Learning Display Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(item.colorHex)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    val word = when (language) {
                                        "hi" -> "${item.letter} से ${item.wordHi}"
                                        "gu" -> "${item.letter} થી ${item.wordGu}"
                                        else -> "${item.letter} for ${item.wordEn}"
                                    }
                                    onSpeak(word)
                                    onCompleted("abc", 10)
                                }
                                .testTag("abc_main_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.letter,
                                    fontSize = 100.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )

                                Text(
                                    text = item.icon,
                                    fontSize = 90.sp
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = when (language) {
                                            "hi" -> item.wordHi
                                            "gu" -> item.wordGu
                                            else -> item.wordEn
                                        },
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "Tap to Listen", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.White.copy(alpha = 0.25f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Lightbulb, contentDescription = "Fun Fact", tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = when (language) {
                                                "hi" -> item.funFactHi
                                                "gu" -> item.funFactGu
                                                else -> item.funFactEn
                                            },
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Navigation Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (selectedIndex > 0) selectedIndex--
                            },
                            enabled = selectedIndex > 0,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", modifier = Modifier.size(40.dp))
                        }

                        Text(
                            text = "${selectedIndex + 1} / ${list.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        IconButton(
                            onClick = {
                                if (selectedIndex < list.size - 1) selectedIndex++
                            },
                            enabled = selectedIndex < list.size - 1,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next", modifier = Modifier.size(40.dp))
                        }
                    }
                }

                "numbers" -> {
                    val numbersList = KidsDataProvider.getNumbersList()
                    val numItem = numbersList[selectedIndex]

                    // Numbers Grid View / Picker
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(numbersList.indices.toList()) { idx ->
                            Surface(
                                shape = CircleShape,
                                color = if (idx == selectedIndex) Color(numItem.colorHex) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        selectedIndex = idx
                                        onSpeak("${numbersList[idx].number}")
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${idx + 1}",
                                        fontWeight = FontWeight.Bold,
                                        color = if (idx == selectedIndex) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    // Main Number Display Card
                    Card(
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(numItem.colorHex)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp)
                            .clickable {
                                onSpeak("${numItem.number}. ${numItem.wordEn}")
                                onCompleted("numbers", 10)
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${numItem.number}",
                                fontSize = 110.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            // Display emojis counted according to number (max 10 displayed nicely)
                            val displayCount = if (numItem.number <= 12) numItem.number else 10
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(5),
                                modifier = Modifier.height(120.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                items((1..displayCount).toList()) {
                                    Text(
                                        text = numItem.emoji,
                                        fontSize = 28.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = when (language) {
                                        "hi" -> numItem.wordHi
                                        "gu" -> numItem.wordGu
                                        else -> numItem.wordEn
                                    },
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Speak", tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Tap to Voice Count", color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    // Bottom Stepper
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (selectedIndex > 0) selectedIndex-- },
                            enabled = selectedIndex > 0
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", modifier = Modifier.size(40.dp))
                        }

                        Text(
                            text = "Number ${numItem.number} of 100",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        IconButton(
                            onClick = { if (selectedIndex < numbersList.size - 1) selectedIndex++ },
                            enabled = selectedIndex < numbersList.size - 1
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next", modifier = Modifier.size(40.dp))
                        }
                    }
                }

                "colors" -> {
                    val colorsList = KidsDataProvider.colorsList
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(colorsList) { c ->
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(c.hexValue)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                modifier = Modifier
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .clickable {
                                        val name = when (language) {
                                            "hi" -> c.nameHi
                                            "gu" -> c.nameGu
                                            else -> c.nameEn
                                        }
                                        onSpeak("$name color. ${c.exampleObjectEn}")
                                        onCompleted("colors", 10)
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = c.exampleEmoji, fontSize = 42.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = when (language) {
                                            "hi" -> c.nameHi
                                            "gu" -> c.nameGu
                                            else -> c.nameEn
                                        },
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = if (c.hexValue == 0xFFFFFFFF) Color.Black else Color.White
                                    )
                                    Text(
                                        text = when (language) {
                                            "hi" -> c.exampleObjectHi
                                            "gu" -> c.exampleObjectGu
                                            else -> c.exampleObjectEn
                                        },
                                        fontSize = 12.sp,
                                        color = if (c.hexValue == 0xFFFFFFFF) Color.DarkGray else Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }

                "shapes" -> {
                    val shapesList = KidsDataProvider.shapesList
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(shapesList) { s ->
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                modifier = Modifier
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .clickable {
                                        val name = when (language) {
                                            "hi" -> s.nameHi
                                            "gu" -> s.nameGu
                                            else -> s.nameEn
                                        }
                                        onSpeak("$name shape. Example: ${s.exampleObjectEn}")
                                        onCompleted("shapes", 10)
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = s.icon, fontSize = 48.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = when (language) {
                                            "hi" -> s.nameHi
                                            "gu" -> s.nameGu
                                            else -> s.nameEn
                                        },
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = when (language) {
                                            "hi" -> s.exampleObjectHi
                                            "gu" -> s.exampleObjectGu
                                            else -> s.exampleObjectEn
                                        },
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    // Animals, Birds, Fruits, Vegetables, Vehicles
                    val itemList = when (category) {
                        "animals" -> KidsDataProvider.animalsList.map { Triple(it.nameEn, it.nameHi, Triple(it.nameGu, it.imageEmoji, it.soundText)) }
                        "birds" -> KidsDataProvider.birdsList.map { Triple(it.nameEn, it.nameHi, Triple(it.nameGu, it.imageEmoji, it.soundText)) }
                        "fruits", "vegetables" -> KidsDataProvider.fruitsVegList.map { Triple(it.nameEn, it.nameHi, Triple(it.nameGu, it.imageEmoji, if (it.isFruit) "Fruit" else "Vegetable")) }
                        else -> KidsDataProvider.vehiclesList.map { Triple(it.nameEn, it.nameHi, Triple(it.nameGu, it.imageEmoji, it.soundText)) }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(itemList) { item ->
                            val nameEn = item.first
                            val nameHi = item.second
                            val nameGu = item.third.first
                            val emoji = item.third.second
                            val sound = item.third.third

                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                modifier = Modifier
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .clickable {
                                        val speakName = when (language) {
                                            "hi" -> nameHi
                                            "gu" -> nameGu
                                            else -> nameEn
                                        }
                                        onSpeak("$speakName. $sound!")
                                        onCompleted(category, 10)
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = emoji, fontSize = 52.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = when (language) {
                                            "hi" -> nameHi
                                            "gu" -> nameGu
                                            else -> nameEn
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = sound,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
