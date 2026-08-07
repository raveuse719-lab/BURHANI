package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.DrawingScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearningScreen
import com.example.ui.screens.MemoryGameScreen
import com.example.ui.screens.ParentModeScreen
import com.example.ui.screens.PuzzleScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.RewardsBadgesScreen
import com.example.ui.screens.RhymesScreen
import kotlinx.coroutines.delay

@Composable
fun MainNavigationContainer(
    viewModel: KidsViewModel
) {
    val navController = rememberNavController()

    val currentProfile by viewModel.currentProfile.collectAsStateWithLifecycle()
    val allProfiles by viewModel.allProfiles.collectAsStateWithLifecycle()
    val parentSettings by viewModel.parentSettings.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val savedDrawings by viewModel.savedDrawings.collectAsStateWithLifecycle()
    val progressList by viewModel.progressList.collectAsStateWithLifecycle()
    val customQuizzes by viewModel.customQuizzes.collectAsStateWithLifecycle()
    val language by viewModel.selectedLanguage.collectAsStateWithLifecycle()

    val screenTimeBreak by viewModel.screenTimeBreakActive.collectAsStateWithLifecycle()

    // Auto create default profile on first launch if empty
    LaunchedEffect(allProfiles) {
        if (allProfiles.isEmpty()) {
            viewModel.createChildProfile("Super Star", 5, "lion")
        }
    }

    // Screen Time Timer Tracker
    var sessionSecondsSpent by remember { mutableIntStateOf(0) }
    LaunchedEffect(parentSettings?.screenTimeLimitMinutes) {
        val limit = parentSettings?.screenTimeLimitMinutes ?: 0
        if (limit > 0) {
            while (true) {
                delay(1000L)
                sessionSecondsSpent++
                if (sessionSecondsSpent >= limit * 60) {
                    viewModel.triggerScreenTimeBreak(true)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("home") {
                HomeScreen(
                    profile = currentProfile,
                    language = language,
                    dailyChallengeDone = false,
                    onCategoryClick = { category ->
                        when (category) {
                            "drawing" -> navController.navigate("drawing")
                            "puzzles" -> navController.navigate("puzzles")
                            "memory" -> navController.navigate("memory")
                            "rhymes" -> navController.navigate("rhymes")
                            "quiz" -> navController.navigate("quiz")
                            "challenge" -> {
                                viewModel.completeDailyChallenge()
                                navController.navigate("rewards")
                            }
                            else -> navController.navigate("learning/$category")
                        }
                    },
                    onRewardsClick = { navController.navigate("rewards") },
                    onParentModeClick = { navController.navigate("parent") },
                    onAdminClick = { navController.navigate("admin") },
                    onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                    onClaimDailyReward = { viewModel.claimDailyReward() },
                    onSpeak = { text -> viewModel.speak(text) }
                )
            }

            composable(
                route = "learning/{category}",
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "abc"
                LearningScreen(
                    category = category,
                    language = language,
                    onBackClick = { navController.popBackStack() },
                    onSpeak = { text -> viewModel.speak(text) },
                    onCompleted = { cat, score -> viewModel.completeActivity(cat, "Learning $cat", score, 30) }
                )
            }

            composable("drawing") {
                DrawingScreen(
                    savedDrawings = savedDrawings,
                    onBackClick = { navController.popBackStack() },
                    onSaveArtwork = { title, data -> viewModel.saveArtwork(title, data) },
                    onDeleteArtwork = { id -> viewModel.deleteArtwork(id) },
                    onSpeak = { text -> viewModel.speak(text) }
                )
            }

            composable("puzzles") {
                PuzzleScreen(
                    language = language,
                    onBackClick = { navController.popBackStack() },
                    onSpeak = { text -> viewModel.speak(text) },
                    onCompleted = { cat, score -> viewModel.completeActivity(cat, "Puzzle Game", score, 60) }
                )
            }

            composable("memory") {
                MemoryGameScreen(
                    language = language,
                    onBackClick = { navController.popBackStack() },
                    onSpeak = { text -> viewModel.speak(text) },
                    onCompleted = { cat, score -> viewModel.completeActivity(cat, "Memory Game", score, 60) }
                )
            }

            composable("rhymes") {
                RhymesScreen(
                    language = language,
                    onBackClick = { navController.popBackStack() },
                    onSpeak = { text -> viewModel.speak(text) },
                    onCompleted = { cat, score -> viewModel.completeActivity(cat, "Rhyme Jukebox", score, 45) }
                )
            }

            composable("quiz") {
                QuizScreen(
                    customQuizzes = customQuizzes,
                    language = language,
                    onBackClick = { navController.popBackStack() },
                    onSpeak = { text -> viewModel.speak(text) },
                    onCompleted = { cat, score -> viewModel.completeActivity(cat, "Kids Quiz", score, 90) }
                )
            }

            composable("rewards") {
                RewardsBadgesScreen(
                    profile = currentProfile,
                    achievements = achievements,
                    onBackClick = { navController.popBackStack() },
                    onClaimReward = { viewModel.claimDailyReward() }
                )
            }

            composable("parent") {
                ParentModeScreen(
                    parentSettings = parentSettings,
                    allProfiles = allProfiles,
                    currentProfile = currentProfile,
                    progressList = progressList,
                    onBackClick = { navController.popBackStack() },
                    onUpdateSettings = { settings -> viewModel.updateParentSettings(settings) },
                    onCreateProfile = { name, age, avatar -> viewModel.createChildProfile(name, age, avatar) },
                    onSwitchProfile = { id -> viewModel.switchProfile(id) }
                )
            }

            composable("admin") {
                AdminPanelScreen(
                    customQuizzes = customQuizzes,
                    allProfiles = allProfiles,
                    onBackClick = { navController.popBackStack() },
                    onAddQuiz = { cat, q, o1, o2, o3, o4, corr, exp ->
                        viewModel.addCustomQuiz(cat, q, o1, o2, o3, o4, corr, exp)
                    },
                    onDeleteQuiz = { id -> viewModel.deleteCustomQuiz(id) },
                    onSpeak = { text -> viewModel.speak(text) }
                )
            }
        }

        // Screen Time Rest Overlay
        if (screenTimeBreak) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Screen Time Rest Break 🌙", fontWeight = FontWeight.Bold) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Bedtime, contentDescription = "Rest", tint = Color(0xFF7C4DFF), modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Great job learning today! Time to rest your eyes and take a fun play break!",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            sessionSecondsSpent = 0
                            viewModel.triggerScreenTimeBreak(false)
                        },
                        modifier = Modifier.testTag("resume_play_button")
                    ) {
                        Text("Parent Un-Pause ▶️")
                    }
                }
            )
        }
    }
}
