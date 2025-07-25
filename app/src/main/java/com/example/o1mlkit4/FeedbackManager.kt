package com.example.o1mlkit4

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object FeedbackManager {
    private var vibrator: Vibrator? = null
    private var toneGenerator: ToneGenerator? = null
    private var soundJob: Job? = null

    private var _isVibrationFeedbackEnabled: Boolean = false
    val isVibrationFeedbackEnabled: Boolean
        get() = _isVibrationFeedbackEnabled // Public getter

    private var _isSoundFeedbackEnabled: Boolean = false
    val isSoundFeedbackEnabled: Boolean
        get() = _isSoundFeedbackEnabled

    fun initialize(context: Context) {
        if (vibrator == null) {
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (toneGenerator == null) {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
        }
    }

    fun enableVibrationFeedback() {
        _isVibrationFeedbackEnabled = true
    }

    fun disableVibrationFeedback() {
        _isVibrationFeedbackEnabled = false
        cancelVibration()
    }

    fun enableSoundFeedback() {
        _isSoundFeedbackEnabled = true
    }

    fun disableSoundFeedback() {
        _isSoundFeedbackEnabled = false
        cancelSound()
    }

    fun triggerOneShotVibration(pattern: LongArray) {
        if (_isVibrationFeedbackEnabled && vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator!!.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                vibrator!!.vibrate(pattern, -1)
            }
        }
    }

    fun triggerSoundFeedback() {
        if (_isSoundFeedbackEnabled && toneGenerator != null) {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
        }
    }

    fun cancelVibration() {
        vibrator?.cancel()
    }

    fun cancelSound() {
        toneGenerator?.stopTone()
        soundJob?.cancel()
    }

    fun startCustomRepeatingBeep() {
        soundJob?.cancel()

        soundJob = GlobalScope.launch(Dispatchers.Main) {
            while (_isSoundFeedbackEnabled) {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100)
                delay(100)
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100)
                delay(1000)
            }
        }
    }
}