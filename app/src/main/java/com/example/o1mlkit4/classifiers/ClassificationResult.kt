// File: classifiers/ClassificationResult.kt
package com.example.o1mlkit4.classifiers

data class ClassificationResult(
    val leftAngle: Double = -1.0,
    val rightAngle: Double = -1.0,
    val averageAngle: Double = -1.0,
    val leftIsValid: Boolean = false,
    val rightIsValid: Boolean = false,
    val averageIsValid: Boolean = false,

    val leftTricepsPulldownAngle: Double = -1.0,
    val rightTricepsPulldownAngle: Double = -1.0,
    val averageTricepsPulldownAngle: Double = -1.0,
    val leftTricepsPulldownIsValid: Boolean = false,
    val rightTricepsPulldownIsValid: Boolean = false,
    val averageTricepsPulldownIsValid: Boolean = false,


    val count_check: Boolean=false

)
