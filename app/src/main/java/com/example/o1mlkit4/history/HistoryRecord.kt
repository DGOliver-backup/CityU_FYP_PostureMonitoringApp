package com.example.o1mlkit4.history

data class HistoryRecord(
    val exerciseName: String,
    val correct: Int,
    val wrong: Int,
    val systemTime: String,
    val accuracy: Double,
    val videoPath: String?,
    val motionCount: Int
)