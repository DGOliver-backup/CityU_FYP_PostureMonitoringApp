package com.example.o1mlkit4

import android.content.Context
import android.util.Log
import com.example.o1mlkit4.history.HistoryRecord
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class HistoryManager {

    private val HISTORY_FILE_NAME = "history.json"

    fun loadHistoryRecords(context: Context): List<HistoryRecord> {
        val historyFile = File(context.filesDir, HISTORY_FILE_NAME)
        if (!historyFile.exists()) {
            return emptyList()
        }
        val content = historyFile.readText()
        if (content.isEmpty()) {
            return emptyList()
        }

        val jsonArray = JSONArray(content)
        val records = mutableListOf<HistoryRecord>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val exerciseName = obj.optString("exerciseName", "")
            val correct = obj.optInt("correct", 0)
            val wrong = obj.optInt("wrong", 0)
            val systemTime = obj.optString("systemTime", "")
            val accuracy = obj.optDouble("accuracy", 0.0)
            val videoPath = obj.optString("videoPath", "")
            val motionCount = obj.optInt("motionCount", 0)
            records.add(
                HistoryRecord(
                    exerciseName = exerciseName,
                    correct = correct,
                    wrong = wrong,
                    systemTime = systemTime,
                    accuracy = accuracy,
                    videoPath = if (videoPath.isBlank()) null else videoPath,
                    motionCount = motionCount
                )
            )
        }
        logAllVideoFiles(context)

        return records
    }

    fun saveResultsToJson(
        context: Context,
        exerciseName: String,
        correct: Int,
        wrong: Int,
        motionCount: Int,
        videoPath: String? = null
    ) {
        val historyFile = File(context.filesDir, HISTORY_FILE_NAME)
        val jsonArray = if (historyFile.exists() && historyFile.readText().isNotEmpty()) {
            JSONArray(historyFile.readText())
        } else {
            JSONArray()
        }
        val accuracy = computeAccuracy(correct, wrong)
        val record = JSONObject().apply {
            put("exerciseName", exerciseName)
            put("correct", correct)
            put("wrong", wrong)
            put("systemTime", System.currentTimeMillis().toString())
            put("accuracy", accuracy)
            put("videoPath", videoPath ?: "")
            put("motionCount", motionCount)
        }
        jsonArray.put(record)

        historyFile.writeText(jsonArray.toString())
        Log.d("ScreenRecorder", "Saved record: ${videoPath}")
    }

    fun clearHistory(context: Context) {
        val records = loadHistoryRecords(context)
        records.forEach { record ->
            record.videoPath?.let { path ->
                val file = if (path.startsWith("/")) {

                    File(path)

                } else {
                    File(context.filesDir, path)
                }
                if (file.exists()) {
                    val deleted = file.delete()
                    Log.d("ScreenRecorder", "Deleting video: ${file.absolutePath}, success=$deleted")
                } else {
                    Log.d("ScreenRecorder", "File not found: ${file.absolutePath}")
                }
            }
        }

        val historyFile = File(context.filesDir, HISTORY_FILE_NAME)
        if (historyFile.exists()) {
            historyFile.writeText("")
            Log.d("ScreenRecorder", "History file cleared")
        }
    }

    fun logAllVideoFiles(context: Context) {
        val directory = context.filesDir
        if (directory.exists() && directory.isDirectory) {
            val videoFiles = directory.listFiles { file ->
                file.isFile && (file.extension == "mp4" || file.extension == "avi" || file.extension == "mkv")
            }
            val videoCount = videoFiles?.size ?: 0
            Log.d("ScreenRecorder", "Total video files found: $videoCount")

            videoFiles?.forEach { file ->
            }
        } else {
            Log.d("HistoryManager", "No directory found or not a valid directory.")
        }
    }
    private fun computeAccuracy(correct: Int, wrong: Int): Double {
        val total = correct + wrong
        if (total == 0) return 0.0
        return (correct.toDouble() / total.toDouble()) * 100.0
    }
}
