package com.example.o1mlkit4.layout

import android.annotation.SuppressLint
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.o1mlkit4.CameraAnalyzer
import com.example.o1mlkit4.classifiers.ClassifierRegistry
import com.example.o1mlkit4.ui.DisplayClassifierFeatures
import java.util.concurrent.Executors
import android.content.Context // Import the correct Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.view.TextureView
import androidx.camera.view.PreviewView
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.o1mlkit4.FeedbackManager

object CameraState {
    var isCameraActive by mutableStateOf(true)
}
@SuppressLint("RememberReturnType")
@Composable
fun CameraPreviewWithPoseEstimation(
    exerciseName: String,
    onResults: (correct: Int, wrong: Int, motionCount: Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isFrontCamera = true

    var leftAngle by remember { mutableStateOf(-1.0) }
    var rightAngle by remember { mutableStateOf(-1.0) }
    var averageAngle by remember { mutableStateOf(-1.0) }
    var leftIsValid by remember { mutableStateOf(false) }
    var rightIsValid by remember { mutableStateOf(false) }
    var averageIsValid by remember { mutableStateOf(false) }

    var correctFrameCount by remember { mutableStateOf(0) }
    var wrongFrameCount by remember { mutableStateOf(0) }
    var motionCount by remember { mutableStateOf(0) }

    var isAnalyzing by remember { mutableStateOf(true) }

    var isVibrating by remember { mutableStateOf(false) }

    // Get system vibrator service
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    val cameraSelector = if (isFrontCamera) {
        CameraSelector.DEFAULT_FRONT_CAMERA
    } else {
        CameraSelector.DEFAULT_BACK_CAMERA
    }
    val textureView = remember { TextureView(context) }
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    val overlayView = remember { OverlayView(context, null) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val feedbackViewModel: FeedbackViewModel = viewModel()
    val cameraProviderState = remember { mutableStateOf<ProcessCameraProvider?>(null) }
    // Handle initial feedback
    LaunchedEffect(feedbackViewModel.isVibrationFeedbackEnabled.value, feedbackViewModel.isSoundFeedbackEnabled.value) {
        if (feedbackViewModel.isVibrationFeedbackEnabled.value) {
            FeedbackManager.enableVibrationFeedback()
        } else {
            FeedbackManager.disableVibrationFeedback()
        }

        if (feedbackViewModel.isSoundFeedbackEnabled.value) {
            FeedbackManager.enableSoundFeedback()
        } else {
            FeedbackManager.disableSoundFeedback()
        }
    }
    LaunchedEffect(CameraState.isCameraActive) {
        cameraProvider = cameraProviderFuture.get()

        if (CameraState.isCameraActive) {
            try {
                val preview = Preview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )

                preview.setSurfaceProvider(previewView.surfaceProvider)
            } catch (e: Exception) {
                Log.e("Camera", "Camera initialization failed", e)
            }
        } else {
            cameraProvider?.unbindAll()
            System.gc()
        }
    }

    //Handle app background transition
    val lifecycleObserver = rememberUpdatedState {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onAppResumed() {
                if (feedbackViewModel.isVibrationFeedbackEnabled.value) {
                    FeedbackManager.enableVibrationFeedback()
                }
                if (feedbackViewModel.isSoundFeedbackEnabled.value) {
                    FeedbackManager.enableSoundFeedback()
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onAppPaused() {
                FeedbackManager.cancelVibration()
                FeedbackManager.cancelSound()
            }
        })
    }
    fun startCamera() {
        cameraProvider?.let { provider ->
            try {
                val preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { /* Setup analyzer */ }

                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("CameraPreview", "Use case binding failed", exc)
            }
        }
    }
    fun stopCamera() {
        cameraProvider?.unbindAll()
    }
    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder()
            .build()
            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview
            )
        } catch(exc: Exception) {
            Log.e("CameraPreview", "Use case binding failed", exc)
        }
    }
    LaunchedEffect(Unit) {
        cameraProvider = cameraProviderFuture.get().also {
            bindPreview(it)
        }
    }
    // Cancel vibration and sound feedback when disposed
    DisposableEffect(lifecycleOwner) {
        onDispose {
            FeedbackManager.cancelVibration()
            FeedbackManager.cancelSound()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    //Double tap to stop monitor session
                    onDoubleTap = {
                        //Stop vibration and sound feedback
                        FeedbackManager.cancelVibration()
                        FeedbackManager.cancelSound()
                        // Stop the classifier
                        isAnalyzing = false
                        ClassifierRegistry.getCurrentClassifier().initializeCounter()
                        //Navigate to ResultScreen
                        onResults(correctFrameCount, wrongFrameCount, motionCount)
                    }
                )
            }
    ) {

        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay view
        AndroidView(
            factory = { overlayView },
            modifier = Modifier.fillMaxSize()
        )
        // Display features
        DisplayClassifierFeatures(
            classifier = ClassifierRegistry.getCurrentClassifier(),
            leftAngle = leftAngle,
            rightAngle = rightAngle,
            averageAngle = averageAngle,
            leftIsValid = leftIsValid,
            rightIsValid = rightIsValid,
            averageIsValid = averageIsValid,
            motionCount = motionCount,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        //Main monitor logic
        if (isAnalyzing && CameraState.isCameraActive) {
            LaunchedEffect(cameraProviderFuture, CameraState.isCameraActive) {
                val cameraProvider = cameraProviderFuture.get()
                cameraProviderState.value = cameraProvider

                val preview = Preview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .apply {
                        setAnalyzer(
                            cameraExecutor,
                            CameraAnalyzer(
                                classifier = ClassifierRegistry.getCurrentClassifier(),
                                overlayView = overlayView,
                                isFrontCamera = isFrontCamera
                            ) { result ->
                                // Update information variables
                                leftAngle = result.leftAngle
                                rightAngle = result.rightAngle
                                averageAngle = result.averageAngle
                                leftIsValid = result.leftIsValid
                                rightIsValid = result.rightIsValid
                                averageIsValid = result.averageIsValid
                                val isFrameCorrect = result.averageIsValid
                                // Update counters
                                if (isFrameCorrect) {
                                    correctFrameCount++
                                    if (result.count_check) {
                                        motionCount++
                                    }
                                } else {
                                    wrongFrameCount++
                                }
                                // Handle vibration
                                handleVibration(
                                    isFrameCorrect = isFrameCorrect,
                                    vibrationFeedbackEnabled = feedbackViewModel.isVibrationFeedbackEnabled.value,
                                    soundFeedbackEnabled = feedbackViewModel.isSoundFeedbackEnabled.value,
                                    vibrator = vibrator,
                                    isVibrating = isVibrating,
                                    updateVibrating = { newState -> isVibrating = newState }
                                )
                            }
                        )
                    }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", exc)
                }
            }
        } else {
            LaunchedEffect(isAnalyzing) {
                if (!isAnalyzing) {
                    cameraProviderState.value?.unbindAll()

                    // When analysis ends, initialize counters
                    ClassifierRegistry.getCurrentClassifier().initializeCounter()
                    correctFrameCount = 0
                    wrongFrameCount = 0
                    motionCount = 0
                    val cameraProvider = ProcessCameraProvider.getInstance(context).get()
//                    cameraProvider.unbindAll()
                    //Stop vibrator
                    FeedbackManager.cancelVibration()
                }
            }
        }
    }

}

fun handleVibration(
    isFrameCorrect: Boolean,
    vibrationFeedbackEnabled: Boolean,
    soundFeedbackEnabled: Boolean,
    vibrator: Vibrator,
    isVibrating: Boolean,
    updateVibrating: (Boolean) -> Unit
) {
    if (isFrameCorrect) {
        // If the frame is correct, cancel the vibration and sound feedback.
        if (vibrationFeedbackEnabled && isVibrating) {
            vibrator.cancel()
            updateVibrating(false)
            FeedbackManager.cancelSound()
        }
    } else {
        // If the frame is incorrect and no vibration currently, start vibrating and play sound continuously
        if (vibrationFeedbackEnabled && !isVibrating) {
            updateVibrating(true)
            // Start vibration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 100, 500)
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                vibrator.vibrate(500)
            }
            // Trigger continuous sound feedback if vibration feedback is enabled
            if (soundFeedbackEnabled) {
                FeedbackManager.startCustomRepeatingBeep()
            }
        }
    }
}
