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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KidsDataProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RhymesScreen(
    language: String,
    onBackClick: () -> Unit,
    onSpeak: (String) -> Unit,
    onCompleted: (String, Int) -> Unit
) {
    var selectedLangFilter by remember { mutableStateOf(language) }
    val allRhymes = KidsDataProvider.rhymesList
    val filteredRhymes = remember(selectedLangFilter) {
        allRhymes.filter { it.language == selectedLangFilter || selectedLangFilter == "all" }
    }

    var activeRhymeIndex by remember { mutableIntStateOf(0) }
    val activeRhyme = filteredRhymes.getOrElse(activeRhymeIndex) { filteredRhymes.firstOrNull() ?: allRhymes.first() }

    var activeLineIndex by remember { mutableIntStateOf(-1) }
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Rhymes Jukebox 🎵", fontWeight = FontWeight.Bold) },
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
            // Language Tab Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf(
                    "en" to "English 🇬🇧",
                    "hi" to "हिंदी 🇮🇳",
                    "gu" to "ગુજરાતી 🇮🇳"
                ).forEach { (code, label) ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (selectedLangFilter == code) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                selectedLangFilter = code
                                activeRhymeIndex = 0
                                activeLineIndex = -1
                                isPlaying = false
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (selectedLangFilter == code) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Rhyme Playlist Quick Selector
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(filteredRhymes) { idx, rhyme ->
                    val isSelected = idx == activeRhymeIndex
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                activeRhymeIndex = idx
                                activeLineIndex = -1
                                isPlaying = false
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${rhyme.icon} ${rhyme.titleEn}",
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Active Rhyme Player Card with Lyrics Karaoke Mode
            Card(
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(activeRhyme.bgGradient[0]),
                                    Color(activeRhyme.bgGradient[1])
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = activeRhyme.icon, fontSize = 60.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (language) {
                                "hi" -> activeRhyme.titleHi
                                "gu" -> activeRhyme.titleGu
                                else -> activeRhyme.titleEn
                            },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Lyrics Karaoke List
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            itemsIndexed(activeRhyme.lyrics) { lIdx, line ->
                                val isCurrentLine = lIdx == activeLineIndex
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isCurrentLine) Color(0xFFFFD600) else Color.White.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            activeLineIndex = lIdx
                                            onSpeak(line)
                                            onCompleted("rhymes", 15)
                                        }
                                ) {
                                    Text(
                                        text = line,
                                        modifier = Modifier.padding(12.dp),
                                        color = if (isCurrentLine) Color.Black else Color.White,
                                        fontWeight = if (isCurrentLine) FontWeight.ExtraBold else FontWeight.Bold,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Karaoke Controls Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (activeLineIndex < activeRhyme.lyrics.size - 1) {
                                        activeLineIndex++
                                    } else {
                                        activeLineIndex = 0
                                    }
                                    val currentText = activeRhyme.lyrics[activeLineIndex]
                                    onSpeak(currentText)
                                },
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            ) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Next Line", tint = Color.White)
                            }

                            // Play Entire Rhyme Button
                            IconButton(
                                onClick = {
                                    isPlaying = !isPlaying
                                    if (isPlaying) {
                                        activeLineIndex = 0
                                        val fullText = activeRhyme.lyrics.joinToString(". ")
                                        onSpeak(fullText)
                                        onCompleted("rhymes", 20)
                                    }
                                },
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color(0xFFFFD600), RoundedCornerShape(20.dp))
                            ) {
                                Icon(
                                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.Black,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val textToRead = if (activeLineIndex in activeRhyme.lyrics.indices) activeRhyme.lyrics[activeLineIndex] else activeRhyme.lyrics.first()
                                    onSpeak(textToRead)
                                },
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Repeat Line", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
