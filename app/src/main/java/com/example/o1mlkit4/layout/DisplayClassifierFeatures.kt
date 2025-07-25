package com.example.o1mlkit4.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.o1mlkit4.classifiers.BaseClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.BBRW.BarbellRowClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.SAPD.StraightArmPullDownClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.CTPD.TricepsPulldownClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.DBSP.DumbbellShoulderPressClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.FRSQ.FrontSquadClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.HICC.HighCableCurlsClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.INCP.InclineChestPressClassaifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.PSUP.PushUpClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.SAPD.SitupClassifier

@Composable
fun DisplayClassifierFeatures(
    classifier: BaseClassifier,
    leftAngle: Double,
    rightAngle: Double,
    averageAngle: Double,
    leftIsValid: Boolean,
    rightIsValid: Boolean,
    averageIsValid: Boolean,
    motionCount: Int,
    modifier: Modifier = Modifier
) {
    when (classifier) {
        is StraightArmPullDownClassifier -> {
            StrightArmPullDownDisplay(
                leftAngle = leftAngle,
                rightAngle = rightAngle,
                averageAngle = averageAngle,
                leftIsValid = leftIsValid,
                rightIsValid = rightIsValid,
                averageIsValid = averageIsValid,
                motionCount = motionCount,
                modifier = modifier
            )
        }
        is TricepsPulldownClassifier -> {
            TricepsPulldownDisplay(
                leftAngle = leftAngle,
                rightAngle = rightAngle,
                averageAngle = averageAngle,
                leftIsValid = leftIsValid,
                rightIsValid = rightIsValid,
                averageIsValid = averageIsValid,
                motionCount = motionCount,
                modifier = modifier
            )
        }
        is BarbellRowClassifier -> {
              BarbellRowDisplay(
                leftAngle = leftAngle,
                rightAngle = rightAngle,
                averageAngle = averageAngle,
                leftIsValid = leftIsValid,
                rightIsValid = rightIsValid,
                averageIsValid = averageIsValid,
                motionCount = motionCount,
                modifier = modifier
            )
        }
        is DumbbellShoulderPressClassifier -> {
              DumbbellShoulderPressDisplay(
                leftAngle = leftAngle,
                rightAngle = rightAngle,
                averageAngle = averageAngle,
                leftIsValid = leftIsValid,
                rightIsValid = rightIsValid,
                averageIsValid = averageIsValid,
                motionCount = motionCount,
                modifier = modifier
            )
        }
        is FrontSquadClassifier -> {
              FrontSquatDisplay(
                leftAngle = leftAngle,
                rightAngle = rightAngle,
                averageAngle = averageAngle,
                leftIsValid = leftIsValid,
                rightIsValid = rightIsValid,
                averageIsValid = averageIsValid,
                motionCount = motionCount,
                modifier = modifier
            )
        }
        is InclineChestPressClassaifier -> {
              InclineChestPressDisplay(
                leftAngle = leftAngle,
                rightAngle = rightAngle,
                averageAngle = averageAngle,
                leftIsValid = leftIsValid,
                rightIsValid = rightIsValid,
                averageIsValid = averageIsValid,
                motionCount = motionCount,
                modifier = modifier
            )
        }
        is PushUpClassifier -> {
              PushUpDisplay(
                leftAngle = leftAngle,
                rightAngle = rightAngle,
                averageAngle = averageAngle,
                leftIsValid = leftIsValid,
                rightIsValid = rightIsValid,
                averageIsValid = averageIsValid,
                motionCount = motionCount,
                modifier = modifier
            )
        }
        is SitupClassifier -> {
              SitupDisplay(
                leftAngle = leftAngle,
                rightAngle = rightAngle,
                averageAngle = averageAngle,
                leftIsValid = leftIsValid,
                rightIsValid = rightIsValid,
                averageIsValid = averageIsValid,
                motionCount = motionCount,
                modifier = modifier
            )
        }

        is HighCableCurlsClassifier -> {
              HighCableCurlsDisplay(
                leftAngle = leftAngle,
                rightAngle = rightAngle,
                averageAngle = averageAngle,
                leftIsValid = leftIsValid,
                rightIsValid = rightIsValid,
                averageIsValid = averageIsValid,
                motionCount = motionCount,
                modifier = modifier
            )
        }
        // Add more classifier types and their corresponding displays here
        else -> {
            // Default case or do nothing
        }
    }
}
