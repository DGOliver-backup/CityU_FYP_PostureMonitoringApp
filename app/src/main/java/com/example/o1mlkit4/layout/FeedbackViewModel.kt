package com.example.o1mlkit4.layout

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.o1mlkit4.FeedbackManager

class FeedbackViewModel : ViewModel() {
    val isVibrationFeedbackEnabled = mutableStateOf(true)
    val isSoundFeedbackEnabled = mutableStateOf(true)

    fun toggleVibrationFeedback() {
        isVibrationFeedbackEnabled.value = !isVibrationFeedbackEnabled.value
        if (isVibrationFeedbackEnabled.value) {
            FeedbackManager.enableVibrationFeedback()
        } else {
            FeedbackManager.disableVibrationFeedback()
        }
    }

    fun toggleSoundFeedback() {
        isSoundFeedbackEnabled.value = !isSoundFeedbackEnabled.value
        if (isSoundFeedbackEnabled.value) {
            FeedbackManager.enableSoundFeedback()
        } else {
            FeedbackManager.disableSoundFeedback()
        }
    }
}
