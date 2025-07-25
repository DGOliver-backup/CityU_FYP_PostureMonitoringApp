package com.example.o1mlkit4.classifiers.ExercisePlugIn.SAPD

import android.util.Log
import com.example.o1mlkit4.classifiers.BaseClassifier
import com.example.o1mlkit4.classifiers.ClassificationResult
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.acos
import kotlin.math.sqrt

class StraightArmPullDownClassifier : BaseClassifier {

    override val name: String = "Straight Arm Pull Down (Machine Weights)"
    override val code: String = "SAPD"
    override val type:String = "back"

    private var up: Boolean = false
    private var down: Boolean = false
    private var initialized: Boolean = false

    override fun classify(image: com.google.mlkit.vision.common.InputImage, pose: Pose): ClassificationResult {

        val (leftAngle, rightAngle) = calculteArmpitAngles(pose, "valid")
        val averageAngle = (leftAngle + rightAngle) / 2.0

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
            averageAngle = averageAngle,
            count_check = countUpdated
        )
    }


    private fun updateFlagsAndCount(averageAngle: Double): Boolean {
        Log.d("count1232131","enterd: up $up, down $down")

        if (!initialized) {
            Log.d("count1232131","enterd 1")
            if (averageAngle in 45.0..90.0) {
                initialized = true
                return false
            }
        }

        if (initialized) {
            if (!down && averageAngle < 45.0) {
                down = true
                up = false
            } else if (down && averageAngle > 90) {
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

    fun calculteArmpitAngles(pose: Pose, type: String): Pair<Double, Double> {
        if(type=="valid"){
            val leftAngle = calculateAngle(
                pose,
                PoseLandmark.LEFT_SHOULDER,
                PoseLandmark.LEFT_ELBOW,
                PoseLandmark.LEFT_WRIST
            )

            val rightAngle = calculateAngle(
                pose,
                PoseLandmark.RIGHT_SHOULDER,
                PoseLandmark.RIGHT_ELBOW,
                PoseLandmark.RIGHT_WRIST
            )
            return Pair(leftAngle, rightAngle)
        }
        else{
            val leftAngle = calculateAngle(
                pose,
                PoseLandmark.LEFT_ELBOW,
                PoseLandmark.LEFT_SHOULDER,
                PoseLandmark.LEFT_HIP
            )

            val rightAngle = calculateAngle(
                pose,
                PoseLandmark.RIGHT_ELBOW,
                PoseLandmark.RIGHT_SHOULDER,
                PoseLandmark.RIGHT_HIP
            )
            return Pair(leftAngle, rightAngle)

        }

    }

    private fun classifyElbowAngles(
        leftElbowAngle: Double,
        rightElbowAngle: Double,
        lowerBound: Double = 150.0,
        upperBound: Double = 180.0
    ): Pair<Boolean, Boolean> {
        val leftIsValid = leftElbowAngle in lowerBound..upperBound
        val rightIsValid = rightElbowAngle in lowerBound..upperBound
        return Pair(leftIsValid, rightIsValid)
    }

    private fun calculateAndClassifyAverageAngle(
        leftElbowAngle: Double,
        rightElbowAngle: Double,
        lowerBound: Double = 150.0,
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

    private fun calculateAngle(
        pose: Pose,
        shoulderId: Int,
        elbowId: Int,
        wristId: Int
    ): Double {
        val shoulderLandmark = pose.getPoseLandmark(shoulderId)
        val elbowLandmark = pose.getPoseLandmark(elbowId)
        val wristLandmark = pose.getPoseLandmark(wristId)

        if (shoulderLandmark == null || elbowLandmark == null || wristLandmark == null) {
            return -1.0
        }

        val shoulder = shoulderLandmark.position
        val elbow = elbowLandmark.position
        val wrist = wristLandmark.position

        val vectorSE = Pair(shoulder.x - elbow.x, shoulder.y - elbow.y)
        val vectorWE = Pair(elbow.x - wrist.x, elbow.y - wrist.y)

        val dotProduct = vectorSE.first * vectorWE.first + vectorSE.second * vectorWE.second
        val magnitudeSE = sqrt(vectorSE.first.toDouble().pow(2.0) + vectorSE.second.toDouble().pow(2.0))
        val magnitudeWE = sqrt(vectorWE.first.toDouble().pow(2.0) + vectorWE.second.toDouble().pow(2.0))

        if (magnitudeSE == 0.0 || magnitudeWE == 0.0) {
            return -1.0
        }

        val cosTheta = dotProduct / (magnitudeSE * magnitudeWE)
        val clampedCosTheta = cosTheta.coerceIn(-1.0, 1.0)
        val angleRadians = acos(clampedCosTheta)
        val angleDegrees = Math.toDegrees(angleRadians)

        return 180.0 -angleDegrees
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
