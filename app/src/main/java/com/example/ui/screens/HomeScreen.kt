package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ChildProfileEntity
import com.example.ui.theme.ColorAbc
import com.example.ui.theme.ColorAnimals
import com.example.ui.theme.ColorBirds
import com.example.ui.theme.ColorColors
import com.example.ui.theme.ColorDailyChallenge
import com.example.ui.theme.ColorDrawing
import com.example.ui.theme.ColorFruits
import com.example.ui.theme.ColorMemory
import com.example.ui.theme.ColorNumbers
import com.example.ui.theme.ColorPuzzle
import com.example.ui.theme.ColorQuiz
import com.example.ui.theme.ColorRhymes
import com.example.ui.theme.ColorShapes
import com.example.ui.theme.ColorVegetables
import com.example.ui.theme.ColorVehicles

data class CategoryItem(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val titleGu: String,
    val emoji: String,
    val color: Color
)

val mainCategories = listOf(
    CategoryItem("abc", "ABC Learning", "एबीसी सीखें", "ABC શીખો", "🔤", ColorAbc),
    CategoryItem("numbers", "Numbers 1-100", "संख्या 1-100", "નંબર 1-100", "🔢", ColorNumbers),
    CategoryItem("colors", "Colors", "रंग", "રંગો", "🎨", ColorColors),
    CategoryItem("shapes", "Shapes", "आकृतियां", "આકારો", "🔺", ColorShapes),
    CategoryItem("animals", "Animals", "जानवर", "પ્રાણીઓ", "🦁", ColorAnimals),
    CategoryItem("birds", "Birds", "पक्षी", "પક્ષીઓ", "🦚", ColorBirds),
    CategoryItem("fruits", "Fruits", "फल", "ફળો", "🍎", ColorFruits),
    CategoryItem("vegetables", "Vegetables", "सब्जियां", "શાકભાજી", "🥦", ColorVegetables),
    CategoryItem("vehicles", "Vehicles", "वाहनों", "વાહનો", "🚗", ColorVehicles),
    CategoryItem("drawing", "Drawing & Coloring", "ड्राइंग और रंग", "ડ્રોઇંગ અને કલરિંગ", "🖍️", ColorDrawing),
    CategoryItem("puzzles", "Puzzle Games", "पहेली खेल", "પઝલ રમતો", "🧩", ColorPuzzle),
    CategoryItem("memory", "Memory Games", "मेमोरी गेम", "મેમરી ગેમ્સ", "🃏", ColorMemory),
    CategoryItem("rhymes", "Rhymes Jukebox", "कविताएं", "બાળગીતો", "🎵", ColorRhymes),
    CategoryItem("quiz", "Quiz Fun", "क्विज", "ક્વિઝ", "❓", ColorQuiz),
    CategoryItem("challenge", "Daily Challenge", "दैनिक चुनौती", "દૈનિક ચેલેન્જ", "🌟", ColorDailyChallenge)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    profile: ChildProfileEntity?,
    language: String,
    dailyChallengeDone: Boolean,
    onCategoryClick: (String) -> Unit,
    onRewardsClick: () -> Unit,
    onParentModeClick: () -> Unit,
    onAdminClick: () -> Unit,
    onLanguageChange: (String) -> Unit,
    onClaimDailyReward: () -> Unit,
    onSpeak: (String) -> Unit
) {
    var showLangPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = when (profile?.avatar) {
                                        "lion" -> "🦁"
                                        "bear" -> "🐻"
                                        "rabbit" -> "🐰"
                                        "panda" -> "🐼"
                                        "fox" -> "🦊"
                                        "dino" -> "🦖"
                                        else -> "🐱"
                                    },
                                    fontSize = 22.sp
                                )
                            }
                        }
                        Column {
                            Text(
                                text = profile?.name ?: "Little Explorer",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Level ${profile?.level ?: 1} • ${profile?.coins ?: 100} 🪙",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                },
                actions = {
                    // Daily Login Claim
                    IconButton(
                        onClick = {
                            onClaimDailyReward()
                        },
                        modifier = Modifier.testTag("daily_reward_button")
                    ) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = "Daily Gift",
                            tint = Color(0xFFFFD600)
                        )
                    }

                    // Rewards Showcase
                    IconButton(
                        onClick = onRewardsClick,
                        modifier = Modifier.testTag("rewards_button")
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = "Rewards",
                            tint = Color.White
                        )
                    }

                    // Language Selector
                    IconButton(
                        onClick = { showLangPicker = !showLangPicker },
                        modifier = Modifier.testTag("language_button")
                    ) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = "Language",
                            tint = Color.White
                        )
                    }

                    // Parent Mode
                    IconButton(
                        onClick = onParentModeClick,
                        modifier = Modifier.testTag("parent_mode_button")
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Parent Control",
                            tint = Color.White
                        )
                    }

                    // Admin Panel
                    IconButton(
                        onClick = onAdminClick,
                        modifier = Modifier.testTag("admin_panel_button")
                    ) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Panel",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Language selection banner
                AnimatedVisibility(visible = showLangPicker) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Language:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            listOf("en" to "English 🇬🇧", "hi" to "हिंदी 🇮🇳", "gu" to "ગુજરાતી 🇮🇳").forEach { (code, label) ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (language == code) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable {
                                            onLanguageChange(code)
                                            showLangPicker = false
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (language == code) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Daily Challenge Banner
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6D00)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            onSpeak("Daily Challenge! Complete activities to earn 100 bonus coins!")
                            onCategoryClick("challenge")
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌟", fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = when (language) {
                                        "hi" -> "दैनिक चुनौती!"
                                        "gu" -> "દૈનિક ચેલેન્જ!"
                                        else -> "Daily Challenge!"
                                    },
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = if (dailyChallengeDone) "Completed! +100 Coins 🪙" else "Earn 100 Double Coins today! 🪙",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = if (dailyChallengeDone) "Done ✅" else "PLAY ▶️",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color(0xFFFF6D00),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Category Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(mainCategories) { cat ->
                        CategoryCard(
                            item = cat,
                            language = language,
                            onClick = {
                                val title = when (language) {
                                    "hi" -> cat.titleHi
                                    "gu" -> cat.titleGu
                                    else -> cat.titleEn
                                }
                                onSpeak(title)
                                onCategoryClick(cat.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    item: CategoryItem,
    language: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "cardScale"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = item.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .height(135.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable {
                isPressed = true
                onClick()
                isPressed = false
            }
            .testTag("category_${item.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 42.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when (language) {
                        "hi" -> item.titleHi
                        "gu" -> item.titleGu
                        else -> item.titleEn
                    },
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
