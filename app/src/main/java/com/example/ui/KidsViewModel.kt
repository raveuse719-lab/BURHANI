package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.KidsRepository
import com.example.data.entity.AchievementEntity
import com.example.data.entity.ChildProfileEntity
import com.example.data.entity.CustomQuizEntity
import com.example.data.entity.ParentSettingsEntity
import com.example.data.entity.SavedDrawingEntity
import com.example.data.entity.UserProgressEntity
import com.example.ui.util.TTSHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KidsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KidsRepository
    private val ttsHelper: TTSHelper = TTSHelper(application)

    val currentProfile: StateFlow<ChildProfileEntity?>
    val allProfiles: StateFlow<List<ChildProfileEntity>>
    val parentSettings: StateFlow<ParentSettingsEntity?>
    val customQuizzes: StateFlow<List<CustomQuizEntity>>

    val achievements: StateFlow<List<AchievementEntity>>
    val savedDrawings: StateFlow<List<SavedDrawingEntity>>
    val progressList: StateFlow<List<UserProgressEntity>>

    private val _selectedLanguage = MutableStateFlow("en")
    val selectedLanguage = _selectedLanguage.asStateFlow()

    private val _screenTimeBreakActive = MutableStateFlow(false)
    val screenTimeBreakActive = _screenTimeBreakActive.asStateFlow()

    private val _dailyChallengeCompleted = MutableStateFlow(false)
    val dailyChallengeCompleted = _dailyChallengeCompleted.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = KidsRepository(
            db.childProfileDao(),
            db.achievementDao(),
            db.progressDao(),
            db.drawingDao(),
            db.parentSettingsDao(),
            db.customQuizDao()
        )

        viewModelScope.launch {
            repository.ensureDefaultParentSettings()
        }

        allProfiles = repository.allProfiles.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        currentProfile = repository.currentProfile.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), null
        )

        parentSettings = repository.parentSettings.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), null
        )

        customQuizzes = repository.customQuizzes.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        achievements = currentProfile.flatMapLatest { profile ->
            if (profile != null) repository.getAchievements(profile.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        savedDrawings = currentProfile.flatMapLatest { profile ->
            if (profile != null) repository.getSavedDrawings(profile.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        progressList = currentProfile.flatMapLatest { profile ->
            if (profile != null) repository.getProgress(profile.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun speak(text: String, lang: String = _selectedLanguage.value) {
        val settings = parentSettings.value
        if (settings == null || settings.voiceEnabled) {
            ttsHelper.speak(text, lang)
        }
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
        viewModelScope.launch {
            val settings = parentSettings.value ?: return@launch
            repository.updateParentSettings(settings.copy(language = lang))
        }
    }

    fun createChildProfile(name: String, age: Int, avatar: String) {
        viewModelScope.launch {
            repository.createChildProfile(name, age, avatar)
        }
    }

    fun switchProfile(id: Int) {
        viewModelScope.launch {
            repository.selectProfile(id)
        }
    }

    fun completeActivity(category: String, activityName: String, score: Int, timeSpentSec: Long) {
        val profile = currentProfile.value ?: return
        viewModelScope.launch {
            repository.recordActivityCompleted(profile.id, category, activityName, score, timeSpentSec)
        }
    }

    fun claimDailyReward() {
        val profile = currentProfile.value ?: return
        viewModelScope.launch {
            repository.addCoinsAndStars(profile.id, 50, 5)
            speak("Daily reward claimed! You earned 50 coins!", _selectedLanguage.value)
        }
    }

    fun completeDailyChallenge() {
        if (_dailyChallengeCompleted.value) return
        val profile = currentProfile.value ?: return
        viewModelScope.launch {
            _dailyChallengeCompleted.value = true
            repository.addCoinsAndStars(profile.id, 100, 10)
            speak("Awesome! Daily challenge completed! You earned 100 coins!", _selectedLanguage.value)
        }
    }

    fun saveArtwork(title: String, colorDataJson: String) {
        val profile = currentProfile.value ?: return
        viewModelScope.launch {
            repository.saveDrawing(profile.id, title, colorDataJson)
            speak("Artwork saved! Great job little artist!", _selectedLanguage.value)
        }
    }

    fun deleteArtwork(id: Int) {
        viewModelScope.launch {
            repository.deleteDrawing(id)
        }
    }

    fun updateParentSettings(settings: ParentSettingsEntity) {
        viewModelScope.launch {
            repository.updateParentSettings(settings)
        }
    }

    fun triggerScreenTimeBreak(active: Boolean) {
        _screenTimeBreakActive.value = active
    }

    fun addCustomQuiz(category: String, question: String, o1: String, o2: String, o3: String, o4: String, correct: Int, explanation: String) {
        viewModelScope.launch {
            repository.addCustomQuiz(
                CustomQuizEntity(
                    category = category,
                    question = question,
                    option1 = o1,
                    option2 = o2,
                    option3 = o3,
                    option4 = o4,
                    correctIndex = correct,
                    explanation = explanation
                )
            )
        }
    }

    fun deleteCustomQuiz(id: Int) {
        viewModelScope.launch {
            repository.deleteCustomQuiz(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}
