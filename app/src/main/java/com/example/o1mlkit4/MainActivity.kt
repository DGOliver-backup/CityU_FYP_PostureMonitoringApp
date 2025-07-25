package com.example.o1mlkit4

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.media3.common.util.NotificationUtil.createNotificationChannel
import androidx.media3.common.util.UnstableApi
import com.example.o1mlkit4.classifiers.ClassifierRegistry
import com.example.o1mlkit4.ui.PoseEstimationApp
import com.example.o1mlkit4.ui.Screen

///TWAN TSZ YIN 57149200 Deep Learning Based Posture Monitoring App
class MainActivity : ComponentActivity() {
    private val REQUEST_MEDIA_PROJECTION = 1001
    private var screenRecordingServiceIntent: Intent? = null
    private val historyManager = HistoryManager()


    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val existingRecords = historyManager.loadHistoryRecords(this)
        Log.d("HistoryManager", "Previous records found: $existingRecords")

        ClassifierRegistry.initializeAll()

        setContent {
            val context = LocalContext.current
            var cameraPermissionGranted by remember { mutableStateOf(false) }

            // Check camera permission
            LaunchedEffect(Unit) {
                cameraPermissionGranted = (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                        )
            }

            // Request camera permission
            val requestPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                cameraPermissionGranted = granted
                if (granted) {
                    Toast.makeText(context, "Camera permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            PoseEstimationApp(
                cameraPermissionGranted = cameraPermissionGranted,
                onRequestPermission = {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onStartScreenRecording = {
                    startScreenRecording()
                },
                onStopScreenRecording = {
                    stopScreenRecording()
                }
            )
        }
    }

    private fun startScreenRecording() {
        val mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
    }

    private fun stopScreenRecording() {
        Log.d("ScreenRecorder_MainActivity", "Stop screen recording called")
        screenRecordingServiceIntent?.let { stopService(it) }

    }



    // Handle the result of the screen capture request
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("ScreenRecorder_MainActivity", "onActivity Called")

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK && data != null) {
            screenRecordingServiceIntent = Intent(this, ScreenRecordingService::class.java).apply {
                putExtra("resultCode", resultCode)
                putExtra("data", data)
            }
            Log.d("ScreenRecorder_MainActivity", "Starting screen recording service")
            startService(screenRecordingServiceIntent)
        }
    }

}