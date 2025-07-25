package com.example.o1mlkit4.classifiers.ExercisePlugIn.SAPD

import android.util.Log
import com.example.o1mlkit4.classifiers.BaseClassifier
import com.example.o1mlkit4.classifiers.ClassificationResult
import com.example.o1mlkit4.classifiers.calculateAngle
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.acos
import kotlin.math.sqrt

class SitupClassifier : BaseClassifier {

    override val name: String = "Sit-Up (Body weights)"
    override val code: String = "STUP"
    override val type:String = "abs"

    private var up: Boolean = false
    private var down: Boolean = false
    private var initialized: Boolean = false

    override fun classify(image: com.google.mlkit.vision.common.InputImage, pose: Pose): ClassificationResult {

        val (leftAngle, rightAngle) = calculteArmpitAngles(pose, "valid")
        val averageMonitorAngle = (leftAngle + rightAngle) / 2.0

        val (leftAngleCount, rightAngleCount) = calculteArmpitAngles(pose, "count")
        val averageCountAngle = (leftAngleCount + rightAngleCount) / 2.0

        val countUpdated = updateFlagsAndCount(averageCountAngle)

        val (leftIsValid, rightIsValid) = classifyElbowAngles(leftAngle, rightAngle)
        val (_, averageIsValid) = calculateAndClassifyAverageAngle(leftAngle, rightAngle)

        return ClassificationResult(
            leftIsValid = leftIsValid,
            rightIsValid = rightIsValid,
            leftAngle = averageCountAngle,
            rightAngle = averageCountAngle,
            averageIsValid = averageIsValid,
            averageAngle = averageMonitorAngle,
            count_check = countUpdated
        )
    }

    private fun updateFlagsAndCount(averageAngle: Double): Boolean {
        Log.d("count1232131","enterd: up $up, down $down")

        if (!initialized) {
            Log.d("count1232131","enterd 1")
            if (averageAngle in 45.0..180.0) {
                initialized = true
                return false
            }
        }

        if (initialized) {
            if (!up && averageAngle < 60.0) {
                down = false
                up = true
            } else if (up && averageAngle > 90) {

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

    fun calculteArmpitAngles(pose: Pose, type: String): Pair<Double, Double> {
        if(type=="valid"){
            val leftAngle = calculateAngle(
                pose,
                PoseLandmark.LEFT_HIP,
                PoseLandmark.LEFT_KNEE,
                PoseLandmark.LEFT_ANKLE
            )

            val rightAngle = calculateAngle(
                pose,
                PoseLandmark.RIGHT_HIP,
                PoseLandmark.RIGHT_KNEE,
                PoseLandmark.RIGHT_ANKLE
            )
            return Pair(leftAngle, rightAngle)
        }
        else{
            val leftAngle = calculateAngle(
                pose,
                PoseLandmark.LEFT_SHOULDER,
                PoseLandmark.LEFT_HIP,
                PoseLandmark.LEFT_KNEE
            )

            val rightAngle = calculateAngle(
                pose,
                PoseLandmark.RIGHT_SHOULDER,
                PoseLandmark.RIGHT_HIP,
                PoseLandmark.RIGHT_KNEE
            )
            return Pair(leftAngle, rightAngle)

        }

    }

    private fun classifyElbowAngles(
        leftElbowAngle: Double,
        rightElbowAngle: Double,
        lowerBound: Double = 60.0,
        upperBound: Double = 105.0
    ): Pair<Boolean, Boolean> {
        val leftIsValid = leftElbowAngle in lowerBound..upperBound
        val rightIsValid = rightElbowAngle in lowerBound..upperBound
        return Pair(leftIsValid, rightIsValid)
    }

    private fun calculateAndClassifyAverageAngle(
        leftElbowAngle: Double,
        rightElbowAngle: Double,
        lowerBound: Double = 60.0,
        upperBound: Double = 105.0
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
        initialized = false

        Log.d("count1232131","initialized, up $up， down:$down")

    }
}
