// File: layout/OverlayView.kt
package com.example.o1mlkit4.layout

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

class OverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var pose: Pose? = null
    private var rotationDegrees: Int = 0
    private var isFrontCamera: Boolean = false

    private var imageWidth: Float = 0f
    private var imageHeight: Float = 0f

    private val paintCircle = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paintLine = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }


    fun setPose(pose: Pose, rotationDegrees: Int) {
        this.pose = pose
        this.rotationDegrees = rotationDegrees
        invalidate()
    }


    fun setImageInfo(width: Int, height: Int, isFrontCamera: Boolean) {
        this.imageWidth = height.toFloat()
        this.imageHeight = width.toFloat()
        this.isFrontCamera = isFrontCamera
        invalidate()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pose?.let { pose ->
            val landmarks = pose.allPoseLandmarks

            if (landmarks.isEmpty()) {
                return
            }

            canvas.save()

            val scaleX = width / imageWidth
            val scaleY = height / imageHeight

            canvas.scale(scaleX, scaleY)

            canvas.translate((width / scaleX - imageWidth) / 2f, (height / scaleY - imageHeight) / 2f)

            if (isFrontCamera) {
                canvas.scale(-1f, 1f, imageWidth / 2f, imageHeight / 2f)
            }

            for (landmark in landmarks) {
                val x = landmark.position.x
                val y = landmark.position.y
                canvas.drawCircle(x, y, 8f / scaleX, paintCircle)
            }
            drawSkeleton(canvas, pose)
            canvas.restore()
        }
    }

    private fun drawSkeleton(canvas: Canvas, pose: Pose) {
        val connections = listOf(
            // Head
            Pair(PoseLandmark.NOSE, PoseLandmark.LEFT_EYE_INNER),
            Pair(PoseLandmark.LEFT_EYE_INNER, PoseLandmark.LEFT_EYE),
            Pair(PoseLandmark.LEFT_EYE, PoseLandmark.LEFT_EYE_OUTER),
            Pair(PoseLandmark.LEFT_EYE_OUTER, PoseLandmark.LEFT_EAR),
            Pair(PoseLandmark.NOSE, PoseLandmark.RIGHT_EYE_INNER),
            Pair(PoseLandmark.RIGHT_EYE_INNER, PoseLandmark.RIGHT_EYE),
            Pair(PoseLandmark.RIGHT_EYE, PoseLandmark.RIGHT_EYE_OUTER),
            Pair(PoseLandmark.RIGHT_EYE_OUTER, PoseLandmark.RIGHT_EAR),

            // Upper body
            Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER),
            Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW),
            Pair(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST),
            Pair(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW),
            Pair(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST),

            // Hands
            Pair(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_THUMB),
            Pair(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_INDEX),
            Pair(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_PINKY),
            Pair(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_THUMB),
            Pair(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_INDEX),
            Pair(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_PINKY),

            // Lower body
            Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP),
            Pair(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP),
            Pair(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP),
            Pair(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE),
            Pair(PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE),
            Pair(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE),
            Pair(PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE),

            // Feet
            Pair(PoseLandmark.LEFT_ANKLE, PoseLandmark.LEFT_HEEL),
            Pair(PoseLandmark.LEFT_HEEL, PoseLandmark.LEFT_FOOT_INDEX),
            Pair(PoseLandmark.RIGHT_ANKLE, PoseLandmark.RIGHT_HEEL),
            Pair(PoseLandmark.RIGHT_HEEL, PoseLandmark.RIGHT_FOOT_INDEX)
        )

        for (connection in connections) {
            val startLandmark = pose.getPoseLandmark(connection.first)
            val endLandmark = pose.getPoseLandmark(connection.second)

            if (startLandmark != null && endLandmark != null) {
                val startX = startLandmark.position.x
                val startY = startLandmark.position.y
                val endX = endLandmark.position.x
                val endY = endLandmark.position.y
                canvas.drawLine(startX, startY, endX, endY, paintLine)
            }
        }
    }

}
