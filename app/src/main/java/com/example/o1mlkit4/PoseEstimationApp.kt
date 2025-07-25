package com.example.o1mlkit4.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.o1mlkit4.HistoryManager
import com.example.o1mlkit4.MainActivity
import com.example.o1mlkit4.VideoFilePathPassing
import com.example.o1mlkit4.classifiers.ClassifierRegistry
import com.example.o1mlkit4.layout.CameraPreviewWithPoseEstimation
import com.example.o1mlkit4.ui.theme.O1mlkit4Theme

@Composable
fun PoseEstimationApp(
    cameraPermissionGranted: Boolean,
    onRequestPermission: () -> Unit,
    onStartScreenRecording: () -> Unit,
    onStopScreenRecording: () -> Unit
) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf<Screen>(Screen.SelectionScreen) }
    val historyManager = HistoryManager()

    O1mlkit4Theme {
        Scaffold(
            topBar = {
                TopAppBarBasedOnScreen(screen = currentScreen) {
                    currentScreen = it
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            when (val screen = currentScreen) {
                is Screen.SelectionScreen -> {
                    if (cameraPermissionGranted) {
                        SelectionScreen(
                            classifiers = ClassifierRegistry.classifiers,
                            onSelectClassifier = { classifier ->
                                ClassifierRegistry.setCurrentClassifier(classifier)
                                currentScreen = Screen.PrePoseStartScreen(
                                    exerciseName = classifier.name,
                                    code = classifier.code
                                )
                            },
                            onGoToHistory = {
                                currentScreen = Screen.HistoryScreen
                            },
                            onInfo = { currentScreen = Screen.InfoScreen },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            onRequestPermission()
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Text(
                                text = "Camera permission is required to use this feature",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                is Screen.CameraScreen -> {

                    CameraPreviewWithPoseEstimation(
                        exerciseName = screen.exerciseName,
                        onResults = { correct, wrong, motionCount  ->
                            val exerciseName = screen.exerciseName

                            onStopScreenRecording()
                            val videoPath = VideoFilePathPassing.getString()
                            Log.d("ScreenRecorder_PoseEstimationApp", "videoPath returned from onStopScreenRecording(): $videoPath")
                            currentScreen = Screen.ResultsScreen(
                                exerciseName = exerciseName,
                                correctFrames = correct,
                                wrongFrames = wrong,
                                recordedVideoPath = videoPath,
                                motionCount = motionCount
                            )
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
                is Screen.ResultsScreen -> {
                    ResultsScreen(
                        exerciseName = screen.exerciseName,
                        correctFrames = screen.correctFrames,
                        wrongFrames = screen.wrongFrames,
                        recordedVideoPath = screen.recordedVideoPath,
                        onSave = {
                            historyManager.saveResultsToJson(
                                context = context,
                                exerciseName = screen.exerciseName,
                                correct = screen.correctFrames,
                                wrong = screen.wrongFrames,
                                videoPath = screen.recordedVideoPath,
                                motionCount = screen.motionCount
                            )
                            currentScreen = Screen.SelectionScreen
                        },
                        onBack = {
                            currentScreen = Screen.SelectionScreen
                        },
                        onPlayVideo = {
                            if (screen.recordedVideoPath != null) {
                                currentScreen = Screen.ResultVideoPlaybackScreen(
                                    videoPath = screen.recordedVideoPath,
                                    previousScreen = screen
                                )
                            }
                        },
                        motionCount = screen.motionCount,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
                is Screen.ResultVideoPlaybackScreen -> {
                    ResultVideoPlaybackScreen(
                        videoPath = screen.videoPath,
                        onBack = { currentScreen = screen.previousScreen }
                    )
                }
                is Screen.HistoryScreen -> {
                    HistoryScreen(
                        onBack = { currentScreen = Screen.SelectionScreen },
                        onClickRecord = { path ->
                            if (!path.isNullOrBlank()) {
                                currentScreen = Screen.VideoPlaybackScreen(path)
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
                is Screen.PrePoseStartScreen -> {
                    PrePoseStartScreen(
                        exerciseName = screen.exerciseName,
                        code = screen.code,
                        onStart = {
                            currentScreen = Screen.CountDownScreen(
                                exerciseName = screen.exerciseName
                            )
                        },
                        onBack = {
                            currentScreen = Screen.SelectionScreen
                        }
                    )
                }
                is Screen.CountDownScreen -> {
                    //Start recording
                    LaunchedEffect(Unit) {
                        onStartScreenRecording()
                    }
                    CountDownScreen(
                        exerciseName = screen.exerciseName,
                        onCountdownFinished = {
                            currentScreen = Screen.CameraScreen(
                                exerciseName = screen.exerciseName
                            )
                        },
                        onBack = {
                            currentScreen = Screen.PrePoseStartScreen(
                                exerciseName = ClassifierRegistry.getCurrentClassifier().name,
                                code = ClassifierRegistry.getCurrentClassifier().code
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is Screen.VideoPlaybackScreen -> {
                    VideoPlaybackScreen(
                        videoPath = screen.videoPath,
                        onBack = { currentScreen = Screen.HistoryScreen }
                    )
                }
                is Screen.InfoScreen ->{
                    InfoScreen(
                        onBack ={currentScreen = Screen.SelectionScreen}
                    )

                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarBasedOnScreen(screen: Screen, onNavigate: (Screen) -> Unit) {
    when (screen) {
        is Screen.SelectionScreen -> {}
        is Screen.CameraScreen -> {}
        is Screen.ResultsScreen -> {
            TopAppBar(
                title = { Text("Results") },
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigate(Screen.SelectionScreen)
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
        is Screen.HistoryScreen -> {}
        is Screen.PrePoseStartScreen -> {}
        is Screen.CountDownScreen -> {}
        is Screen.VideoPlaybackScreen -> {}
        else -> {}
    }
}
