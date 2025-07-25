package com.example.o1mlkit4.classifiers.ExercisePlugIn.HICC

import android.util.Log
import com.example.o1mlkit4.classifiers.BaseClassifier
import com.example.o1mlkit4.classifiers.ClassificationResult
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.acos
import kotlin.math.sqrt

class HighCableCurlsClassifier : BaseClassifier {

    override val name: String = "High Cable Curls (Machine Weights)"
    override val code: String = "HICC"
    override val type:String = "arms"

    private var up: Boolean = false
    private var down: Boolean = false
    private var initialized: Boolean = false

    override fun classify(image: com.google.mlkit.vision.common.InputImage, pose: Pose): ClassificationResult {

        val (leftAngleCount, rightAngleCount) = calculteAngles(pose, "count")
        val averageCountAngle = (leftAngleCount + rightAngleCount) / 2.0
        val countUpdated = updateFlagsAndCount(averageCountAngle)

        val (leftArmpitAngle, righArmpittAngle) = calculteAngles(pose, "valid")
        val (AverageArmpitAngle,ArmpitVal) = calculateAndClassifyAverageAngle(leftArmpitAngle, righArmpittAngle)


        val (leftElbowAngle, rightElbowAngle) = calculteAngles(pose, "count")
        val (AverageElbowAngle,ElbowtVal) = calculateAndClassifyAverageAngle2(leftElbowAngle, rightElbowAngle)

        val acerageVal = ElbowtVal && ArmpitVal


        return ClassificationResult(
            leftIsValid = ElbowtVal,
            rightIsValid = ArmpitVal,
            leftAngle = averageCountAngle,
            rightAngle = AverageArmpitAngle,
            averageIsValid = acerageVal,
            averageAngle = AverageElbowAngle,
            count_check = countUpdated
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
            if (!down && averageAngle <60) {

                down = true
                up = false
            } else if (down && averageAngle >135 ) {
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

    fun calculteAngles(pose: Pose, type: String): Pair<Double, Double> {
        if(type=="valid"){
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
        else{
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

    }

    private fun calculateAndClassifyAverageAngle(
        leftElbowAngle: Double,
        rightElbowAngle: Double,
        lowerBound: Double = 90.0,
        upperBound: Double = 135.0
    ): Pair<Double, Boolean> {
        return if (leftElbowAngle > 0 && rightElbowAngle > 0) {
            val averageAngle = (leftElbowAngle + rightElbowAngle) / 2.0
            val isValid = averageAngle in lowerBound..upperBound
            Pair(averageAngle, isValid)
        } else {
            Pair(-1.0, false)
        }
    }
    private fun calculateAndClassifyAverageAngle2(
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
