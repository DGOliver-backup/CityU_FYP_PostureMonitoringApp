package com.example.o1mlkit4.classifiers.ExercisePlugIn.BBRW


import android.util.Log
import com.example.o1mlkit4.classifiers.BaseClassifier
import com.example.o1mlkit4.classifiers.ClassificationResult
import com.example.o1mlkit4.classifiers.calculateAngle
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.acos
import kotlin.math.sqrt

class BarbellRowClassifier : BaseClassifier {

    override val name: String = "Barbell Row (Free Weights)"
    override val code: String = "BBRW"
    override val type:String = "back"

    private var up: Boolean = false
    private var down: Boolean = false
    private var initialized: Boolean = false
    override fun classify(image: InputImage, pose: Pose): ClassificationResult {

        val (leftKneeAngle, rightKneeAngle) = calculateElbowAngles(pose,"valid")
        val (averageKneeAngle, averageKneeIsValid) = calculateAndClassifyAverageAngleKnee(leftKneeAngle, rightKneeAngle)

        // Counting Elbow Movement
        val (leftAngleCount, rightAngleCount) = calculateElbowAngles(pose,"count")
        val averageCountAngle = (leftAngleCount + rightAngleCount) / 2.0
        val countUpdated = updateFlagsAndCount(averageCountAngle)

        //Hip
        val (leftHipAngle, rightHipAngle) = calculateElbowAngles(pose,"valid2")
        val  (averageHipAngle, averageHipAngleValid) = calculateAndClassifyAverageAngleHip(leftHipAngle, rightHipAngle)
        val averageIsValid = averageKneeIsValid && averageHipAngleValid
        return ClassificationResult(
            leftIsValid = averageHipAngleValid,
            rightIsValid = averageKneeIsValid,
            leftAngle = averageCountAngle,
            rightAngle = averageKneeAngle,
            averageIsValid = averageIsValid,
            averageAngle = averageHipAngle,
            count_check = countUpdated // Pass the updated count flag
        )
    }

    private fun updateFlagsAndCount(averageAngle: Double): Boolean {
        Log.d("count1232131","enterd: up $up, down $down")

        if (!initialized) {
            Log.d("count1232131","enterd 1")
            if (averageAngle in 135.0..180.0) {
                initialized = true
                return false
            }
        }

        if (initialized) {
            if (!up && averageAngle < 120) {
                Log.d("count1232131","enterd 2")

                down = false
                up = true
            } else if (up && averageAngle > 135) {
                Log.d("count1232131","enterd 3")

                down = true
            }

            if (up && down) {
                Log.d("count1232131","enterd 4")

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
                PoseLandmark.LEFT_HIP,
                PoseLandmark.LEFT_KNEE,
                PoseLandmark.LEFT_HEEL
            )

            val rightElbowAngle = calculateAngle(
                pose,
                PoseLandmark.RIGHT_HIP,
                PoseLandmark.RIGHT_KNEE,
                PoseLandmark.RIGHT_HEEL
            )
            return Pair(leftElbowAngle, rightElbowAngle)

        }
        if(s=="valid2"){
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
        // to be modified
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

    private fun classifyElbowAngles(
        leftElbowAngle: Double,
        rightElbowAngle: Double,
        lowerBound: Double = 135.0,
        upperBound: Double = 160.0
    ): Pair<Boolean, Boolean> {
        val leftIsValid = leftElbowAngle in lowerBound..upperBound
        val rightIsValid = rightElbowAngle in lowerBound..upperBound
        return Pair(leftIsValid, rightIsValid)
    }


    private fun calculateAndClassifyAverageAngleHip(
        leftElbowAngle: Double,
        rightElbowAngle: Double,

        lowerBound: Double = 90.0,
        upperBound: Double = 160.0
    ): Pair<Double, Boolean> {
        return if (leftElbowAngle > 0 && rightElbowAngle > 0) {
            val averageAngle = (leftElbowAngle + rightElbowAngle) / 2.0
            val isValid = averageAngle in lowerBound..upperBound
            Pair(averageAngle, isValid)
        } else {
            Pair(-1.0, false)
        }
    }
    private fun calculateAndClassifyAverageAngleKnee(
        leftElbowAngle: Double,
        rightElbowAngle: Double,

        lowerBound: Double = 135.0,
        upperBound: Double = 160.0
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

        Log.d("count1232131","initialized, up $up， down:$down")

    }
}
