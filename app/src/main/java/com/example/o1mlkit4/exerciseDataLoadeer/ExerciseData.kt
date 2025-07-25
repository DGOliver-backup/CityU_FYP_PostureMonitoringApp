// ExerciseData.kt
package com.example.o1mlkit4.data

data class ExerciseData(
    val mainMuscles: List<String>,
    val stepByStepInstructions: List<String>,
    val commonMistake: List<String>,
    val videoTutorial: String,
    val infoObtainedFrom: String,
    val citation:String
)
