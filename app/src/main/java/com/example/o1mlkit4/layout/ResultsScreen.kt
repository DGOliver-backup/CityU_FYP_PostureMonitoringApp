package com.example.o1mlkit4.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.o1mlkit4.FeedbackManager
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    exerciseName: String,
    correctFrames: Int,
    wrongFrames: Int,
    recordedVideoPath: String?,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onPlayVideo: () -> Unit,
    motionCount: Int,
    modifier: Modifier = Modifier
) {
    FeedbackManager.cancelVibration()
    FeedbackManager.cancelSound()

    val totalFrames = correctFrames + wrongFrames
    val accuracy = if (totalFrames > 0) {
        (correctFrames.toDouble() / totalFrames) * 100
    } else 0.0

    val accuracyColor = when {
        accuracy >= 85 -> Color.Green
        accuracy >= 60 -> Color(0xFFFFD300)
        else -> Color.Red
    }

    var isSaved by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!isSaved && recordedVideoPath != null) {
                            val file = File(context.filesDir, recordedVideoPath)
                            if (file.exists()) {
                                Log.d("video123123", "Video found, deleting video")
                                file.delete()
                                Log.d("video123123", "Video deleted")
                            }
                        }
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = exerciseName,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your posture accuracy is:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${accuracy.roundToInt()}%",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = accuracyColor
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Effective repetition Count: $motionCount",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Save Button
            Button(
                onClick = {
                    isSaved = true
                    onSave()
                },
                modifier = Modifier.width(200.dp)
            ) {
                Text(text = "Save")
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Play Video Button
            if (recordedVideoPath != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Play Recording",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FloatingActionButton(onClick = onPlayVideo) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Video Recording"
                        )
                    }
                }
            }
        }
    }
}