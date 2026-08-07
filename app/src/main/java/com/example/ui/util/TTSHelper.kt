package com.example.ui.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TTSHelper(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTSHelper", "Language US is not supported or missing data")
            } else {
                isReady = true
                tts?.setPitch(1.1f) // Slightly higher pitch for playful kid voice
                tts?.setSpeechRate(0.85f) // Slightly slower speech for young children clarity
            }
        } else {
            Log.e("TTSHelper", "Initialization Failed!")
        }
    }

    fun speak(text: String, lang: String = "en") {
        if (!isReady || tts == null) return
        val locale = when (lang) {
            "hi" -> Locale("hi", "IN")
            "gu" -> Locale("gu", "IN")
            else -> Locale.US
        }
        tts?.language = locale
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "KIDS_SPEECH_ID")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
