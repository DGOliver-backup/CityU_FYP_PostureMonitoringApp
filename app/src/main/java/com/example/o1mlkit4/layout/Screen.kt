package com.example.o1mlkit4.ui

sealed class Screen {
    object SelectionScreen : Screen()
    data class PrePoseStartScreen(val exerciseName: String, val code: String) : Screen()
    data class CountDownScreen(val exerciseName: String) : Screen()
    data class CameraScreen(val exerciseName: String) : Screen()
    data class ResultsScreen(
        val exerciseName: String,
        val correctFrames: Int,
        val wrongFrames: Int,
        val recordedVideoPath: String? = null,
        val motionCount:Int,
    ) : Screen()
    data class ResultVideoPlaybackScreen(
        val videoPath: String,
        val previousScreen: ResultsScreen
    ) : Screen()
    object HistoryScreen : Screen()
    // 新增
    data class VideoPlaybackScreen(val videoPath: String) : Screen()

    data object InfoScreen:Screen()

}
