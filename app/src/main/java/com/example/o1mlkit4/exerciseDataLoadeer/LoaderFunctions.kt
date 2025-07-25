// Utils.kt
package com.example.o1mlkit4.utils

import android.content.Context
import com.example.o1mlkit4.data.ExerciseData
import com.google.gson.Gson
import java.io.IOException

fun loadExerciseData(context: Context, code: String): ExerciseData? {
    val gson = Gson()
    val fileName = "exerciseDataBase/$code/$code.json"
    return try {
        val jsonString = context.assets.open(fileName)
            .bufferedReader()
            .use { it.readText() }
        gson.fromJson(jsonString, ExerciseData::class.java)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
