package com.example.o1mlkit4.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun BarbellRowDisplay(
    leftAngle: Double,
    rightAngle: Double,
    averageAngle: Double,
    leftIsValid: Boolean,
    rightIsValid: Boolean,
    averageIsValid: Boolean,
    motionCount: Int,
    modifier: Modifier = Modifier
) {
    val borderColor = if (averageIsValid) Color.Green else Color.Red

    Box(
        modifier = modifier
            .fillMaxSize()
            .border(16.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier) {
                if (leftAngle > 0) {
                    Text(
                        text = "Average Elbow Angle (counting): ${leftAngle.roundToInt()}°",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Yellow
                    )
                }
                if (rightAngle > 0) {
                    Text(
                        text = "Average Knee Angle (monitoring): ${rightAngle.roundToInt()}° (${if (rightIsValid) "OK" else "Out"})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (rightIsValid) Color.Green else Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (averageAngle > 0) {
                    Text(
                        text = "Average Hip Angle (monitoring): ${averageAngle.roundToInt()}° (${if (leftIsValid) "OK" else "Out"})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (leftIsValid) Color.Green else Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Text(
                text = "Motion Count: $motionCount",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}