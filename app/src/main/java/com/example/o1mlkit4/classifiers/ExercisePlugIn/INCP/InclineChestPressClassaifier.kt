package com.example.o1mlkit4.classifiers.ExercisePlugIn.INCP

import android.util.Log
import com.example.o1mlkit4.classifiers.BaseClassifier
import com.example.o1mlkit4.classifiers.ClassificationResult
import com.example.o1mlkit4.classifiers.calculateAngle
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.acos
import kotlin.math.sqrt

class InclineChestPressClassaifier : BaseClassifier {

    override val name: String = "Incline Chest Press (Machine Weights)"
    override val code: String = "INCP"
    override val type:String = "chest"

    private var up: Boolean = false
    private var down: Boolean = false
    private var initialized: Boolean = false
    override fun classify(image: InputImage, pose: Pose): ClassificationResult {
        val (leftElbowAngle, rightElbowAngle) = calculateElbowAngles(pose,"valid")
        val (leftIsValid, rightIsValid) = classifyElbowAngles(leftElbowAngle, rightElbowAngle)
        val (averageAngle, averageIsValid) = calculateAndClassifyAverageAngle(leftElbowAngle, rightElbowAngle)
        val (leftAngleCount, rightAngleCount) = calculateElbowAngles(pose,"count")
        val averageCountAngle = (leftAngleCount + rightAngleCount) / 2.0
        val countUpdated = updateFlagsAndCount(averageCountAngle)
        Log.d("trricept","angle: $averageCountAngle")
        return ClassificationResult(
            leftIsValid = leftIsValid,
            rightIsValid = rightIsValid,
            leftAngle = leftElbowAngle,
            rightAngle = rightElbowAngle,
            averageIsValid = averageIsValid,
            averageAngle = averageAngle,
            count_check = countUpdated
        )
    }

    private fun updateFlagsAndCount(averageAngle: Double): Boolean {
        if (!initialized) {
            if (averageAngle in 45.0..120.0) {
                initialized = true
                return false
            }
        }
        if (initialized) {
            if (!up && averageAngle > 135) {
                up = true
                down = false
            } else if (up && averageAngle < 90) {
                down = true
            }
            if (up && down) {
                down = false
                up = false
                return true
            }
        }

        return false
    }

    fun calculateElbowAngles(pose: Pose,s:String): Pair<Double, Double> {
        if(s=="valid"){
            val leftElbowAngle = calculateAngle(
                pose,
                PoseLandmark.LEFT_WRIST,
                PoseLandmark.LEFT_ELBOW,
                PoseLandmark.LEFT_SHOULDER
            )

            val rightElbowAngle = calculateAngle(
                pose,
                PoseLandmark.RIGHT_WRIST,
                PoseLandmark.RIGHT_ELBOW,
                PoseLandmark.RIGHT_SHOULDER
            )
            return Pair(leftElbowAngle, rightElbowAngle)

        }
        else{
            val leftElbowAngle = calculateAngle(
                pose,
                PoseLandmark.LEFT_WRIST,
                PoseLandmark.LEFT_ELBOW,
                PoseLandmark.LEFT_SHOULDER
            )

            val rightElbowAngle = calculateAngle(
                pose,
                PoseLandmark.RIGHT_WRIST,
                PoseLandmark.RIGHT_ELBOW,
                PoseLandmark.RIGHT_SHOULDER
            )
            return Pair(leftElbowAngle, rightElbowAngle)


        }

    }

    private fun classifyElbowAngles(
        leftElbowAngle: Double,
        rightElbowAngle: Double,
        lowerBound: Double = 0.0,
        upperBound: Double = 165.0
    ): Pair<Boolean, Boolean> {
        val leftIsValid = leftElbowAngle in lowerBound..upperBound
        val rightIsValid = rightElbowAngle in lowerBound..upperBound
        return Pair(leftIsValid, rightIsValid)
    }

    private fun calculateAndClassifyAverageAngle(
        leftElbowAngle: Double,
        rightElbowAngle: Double,
        lowerBound: Double = 0.0,
        upperBound: Double = 165.0
    ): Pair<Double, Boolean> {
        return if (leftElbowAngle > 0 && rightElbowAngle > 0) {
            val averageAngle = (leftElbowAngle + rightElbowAngle) / 2.0
            val isValid = averageAngle in lowerBound..upperBound
            Pair(averageAngle, isValid)
        } else {
            Pair(-1.0, false)
        }
    }

    override fun initializeCounter(){
        up=false
        down=false
        initialized = false // Reset the 'initialized' state

    }
}
