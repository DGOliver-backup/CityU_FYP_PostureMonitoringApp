// File: classifiers/BaseClassifier.kt
package com.example.o1mlkit4.classifiers

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose

interface BaseClassifier {
    val name: String
    val code: String
    val type:String
    fun classify(image: InputImage, pose: Pose): ClassificationResult
    fun initializeCounter()
}
