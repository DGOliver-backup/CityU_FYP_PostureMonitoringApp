package com.example.o1mlkit4.classifiers

import com.google.mlkit.vision.pose.Pose
import kotlin.math.acos
import kotlin.math.sqrt


fun calculateAngle(
    pose: Pose,
    shoulderId: Int,
    elbowId: Int,
    hipId: Int
): Double {
    val a = pose.getPoseLandmark(shoulderId)
    val b = pose.getPoseLandmark(elbowId)
    val c = pose.getPoseLandmark(hipId)

    if (a == null || b == null || c == null) {
        return -1.0
    }

    val aa = b.position
    val bb = a.position
    val cc = c.position

    val vectorSE = Pair(bb.x - aa.x, bb.y - aa.y)
    val vectorHE = Pair(cc.x - aa.x, cc.y - aa.y)

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
