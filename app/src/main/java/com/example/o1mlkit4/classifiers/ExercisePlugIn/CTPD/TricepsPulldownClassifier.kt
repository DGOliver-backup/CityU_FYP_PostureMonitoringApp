package com.example.o1mlkit4.classifiers.ExercisePlugIn.CTPD

import android.util.Log
import com.example.o1mlkit4.classifiers.BaseClassifier
import com.example.o1mlkit4.classifiers.ClassificationResult
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.acos
import kotlin.math.sqrt

class TricepsPulldownClassifier : BaseClassifier {

    override val name: String = "Cable Triceps Pull Down (Machine Weights)"
    override val code: String = "CTPD"
    override val type:String = "arms"

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
            if (averageAngle in 90.0..120.0) {
                initialized = true
                return false
            }
        }
        if (initialized) {
            if (!down && averageAngle > 135) {

                down = true
                up = false
            } else if (down && averageAngle < 90) {
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
                PoseLandmark.LEFT_ELBOW,
                PoseLandmark.LEFT_SHOULDER,
                PoseLandmark.LEFT_HIP
            )

            val rightElbowAngle = calculateAngle(
                pose,
                PoseLandmark.RIGHT_ELBOW,
                PoseLandmark.RIGHT_SHOULDER,
                PoseLandmark.RIGHT_HIP
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


    private fun classifyElbowAngles(
        leftElbowAngle: Double,
        rightElbowAngle: Double,
        lowerBound: Double = 0.0,
        upperBound: Double = 30.0
    ): Pair<Boolean, Boolean> {
        val leftIsValid = leftElbowAngle in lowerBound..upperBound
        val rightIsValid = rightElbowAngle in lowerBound..upperBound
        return Pair(leftIsValid, rightIsValid)
    }


    private fun calculateAndClassifyAverageAngle(
        leftElbowAngle: Double,
        rightElbowAngle: Double,
        lowerBound: Double = 0.0,
        upperBound: Double = 30.0
    ): Pair<Double, Boolean> {
        return if (leftElbowAngle > 0 && rightElbowAngle > 0) {
            val averageAngle = (leftElbowAngle + rightElbowAngle) / 2.0
            val isValid = averageAngle in lowerBound..upperBound
            Pair(averageAngle, isValid)
        } else {
            Pair(-1.0, false)
        }
    }


    private fun calculateAngle(
        pose: Pose,
        shoulderId: Int,
        elbowId: Int,
        hipId: Int
    ): Double {
        val shoulderLandmark = pose.getPoseLandmark(shoulderId)
        val elbowLandmark = pose.getPoseLandmark(elbowId)
        val hipLandmark = pose.getPoseLandmark(hipId)

        if (shoulderLandmark == null || elbowLandmark == null || hipLandmark == null) {
            return -1.0
        }
        val elbow = elbowLandmark.position
        val shoulder = shoulderLandmark.position
        val hip = hipLandmark.position

        val vectorSE = Pair(shoulder.x - elbow.x, shoulder.y - elbow.y)
        val vectorHE = Pair(hip.x - elbow.x, hip.y - elbow.y)

        val dotProduct = vectorSE.first * vectorHE.first + vectorSE.second * vectorHE.second
        val magnitudeSE = sqrt(vectorSE.first.toDouble().pow(2.0) + vectorSE.second.toDouble().pow(2.0))
        val magnitudeHE = sqrt(vectorHE.first.toDouble().pow(2.0) + vectorHE.second.toDouble().pow(2.0))

        if (magnitudeSE == 0.0 || magnitudeHE == 0.0) {
            return -1.0
        }

        val cosTheta = dotProduct / (magnitudeSE * magnitudeHE)
        val clampedCosTheta = cosTheta.coerceIn(-1.0, 1.0)
        val angleRadians = acos(clampedCosTheta)
        val angleDegrees = Math.toDegrees(angleRadians)

        return angleDegrees
    }

    private fun Double.pow(power: Double): Double {
        return Math.pow(this, power)
    }
    override fun initializeCounter(){
        up=false
        down=false
        initialized = false

        Log.d("count1232131","initialized, up $up， down:$down")

    }
}
