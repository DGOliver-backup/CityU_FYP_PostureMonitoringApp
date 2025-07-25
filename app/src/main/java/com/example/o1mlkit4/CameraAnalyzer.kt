package com.example.o1mlkit4

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.o1mlkit4.classifiers.BaseClassifier
import com.example.o1mlkit4.classifiers.ClassificationResult
import com.example.o1mlkit4.layout.OverlayView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.*
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import java.util.concurrent.Executors

class CameraAnalyzer(
    private val classifier: BaseClassifier,
    private val overlayView: OverlayView,
    private val isFrontCamera: Boolean,
    private val onClassificationResult: (ClassificationResult) -> Unit
) : ImageAnalysis.Analyzer {

    companion object {
        private const val TAG = "CameraAnalyzer"
    }

    private val options = AccuratePoseDetectorOptions.Builder()
        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
        .build()

    private val poseDetector = PoseDetection.getClient(options)
    private val executor = Executors.newSingleThreadExecutor()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        poseDetector.process(image)
            .addOnSuccessListener { pose ->
                overlayView.setImageInfo(image.width, image.height, isFrontCamera)
                overlayView.setPose(pose, rotationDegrees)

                executor.execute {
                    val result = classifier.classify(image, pose)
                    Log.d(TAG, "Classification Result: $result")
                    onClassificationResult(result)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Pose detection failed", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

}