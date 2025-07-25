package com.example.o1mlkit4.classifiers.ExercisePlugIn.PSUP

import android.util.Log
import com.example.o1mlkit4.classifiers.BaseClassifier
import com.example.o1mlkit4.classifiers.ClassificationResult
import com.example.o1mlkit4.classifiers.calculateAngle
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.acos
import kotlin.math.sqrt

class PushUpClassifier : BaseClassifier {

    override val name: String = "Push-Ups (Body Weights)"
    override val code: String = "PSUP"
    override val type:String = "chest"

    private var up: Boolean = false
    private var down: Boolean = false
    private var initialized: Boolean = false
    override fun classify(image: InputImage, pose: Pose): ClassificationResult {
        val (leftElbowAngle, rightElbowAngle) = calculateElbowAngles(pose,"valid")
        val (leftIsValid, rightIsValid) = classifyAngles_Hip(leftElbowAngle, rightElbowAngle)
        val (averageAngle, averageIsValid) = calculateAndClassifyAverageAngle_Hip(leftElbowAngle, rightElbowAngle)

        val (leftAngleCount, rightAngleCount) = calculateElbowAngles(pose,"count")
        val averageCountAngle = (leftAngleCount + rightAngleCount) / 2.0

        val countUpdated = updateFlagsAndCount(averageCountAngle)
        return ClassificationResult(
            leftIsValid = leftIsValid,
            rightIsValid = rightIsValid,
            leftAngle = leftElbowAngle,
            rightAngle = averageCountAngle,
            averageIsValid = averageIsValid,
            averageAngle = averageAngle,
            count_check = countUpdated
        )
    }

    private fun updateFlagsAndCount(averageAngle: Double): Boolean {
        if (!initialized) {
            // Only initialize when the first valid angle is detected
            if (averageAngle in 135.0..180.0) {
                initialized = true
                return false // Don't count motions during initialization
            }
        }
        if (initialized) {
            if (!down && averageAngle <105) {
                up = false
                down = true
            } else if (down && averageAngle > 135) {
                up = true
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
                PoseLandmark.LEFT_SHOULDER,
                PoseLandmark.LEFT_HIP,
                PoseLandmark.LEFT_KNEE
            )

            val rightElbowAngle = calculateAngle(
                pose,
                PoseLandmark.RIGHT_SHOULDER,
                PoseLandmark.RIGHT_HIP,
                PoseLandmark.RIGHT_KNEE
            )
            return Pair(leftElbowAngle, rightElbowAngle)

        }
        else{
            val leftElbowAngle = calculateAngle(
                pose,
                PoseLandmark.LEFT_SHOULDER,
                PoseLandmark.LEFT_ELBOW,
                PoseLandmark.LEFT_WRIST
            )

            val rightElbowAngle = calculateAngle(
                pose,
                PoseLandmark.RIGHT_SHOULDER,
                PoseLandmark.RIGHT_ELBOW,
                PoseLandmark.RIGHT_WRIST
            )
            return Pair(leftElbowAngle, rightElbowAngle)


        }

    }

    private fun classifyAngles_Hip(
        leftElbowAngle: Double,
        rightElbowAngle: Double,
        lowerBound: Double = 165.0,
        upperBound: Double = 180.0
    ): Pair<Boolean, Boolean> {
        val leftIsValid = leftElbowAngle in lowerBound..upperBound
        val rightIsValid = rightElbowAngle in lowerBound..upperBound
        return Pair(leftIsValid, rightIsValid)
    }

    private fun calculateAndClassifyAverageAngle_Hip(
        leftElbowAngle: Double,
        rightElbowAngle: Double,
        lowerBound: Double = 165.0,
        upperBound: Double = 180.0
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
