package com.example.o1mlkit4.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.o1mlkit4.HistoryManager
import com.example.o1mlkit4.history.HistoryRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onClickRecord: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val historyManager = HistoryManager()

    var historyList by remember { mutableStateOf<List<HistoryRecord>>(emptyList()) }
    var showClearConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        historyList = historyManager.loadHistoryRecords(context).reversed()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History Records", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showClearConfirmation = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear"
                        )
                    }
                },
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (showClearConfirmation) {
            AlertDialog(
                onDismissRequest = { showClearConfirmation = false },
                title = { Text("Clear All Records") },
                text = { Text("Remove all history records") },
                confirmButton = {
                    TextButton(onClick = {
                        historyManager.clearHistory(context)
                        historyList = emptyList()
                        showClearConfirmation = false
                    }) {
                        Text("Yes, Clear")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (historyList.isEmpty()) {
                Text(
                    "No history records found.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(historyList) { record ->
                        HistoryItem(record = record) {
                            Log.d("ScreenRecorder","saved to ${record.videoPath}")
                            onClickRecord(record.videoPath)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(record: HistoryRecord, onClick: () -> Unit) {
    val accuracyColor = when {
        record.accuracy >= 85 -> Color.Green
        record.accuracy >= 60 -> Color(0xFFFFD300)
        else -> Color.Red
    }

    // Format the systemTime format
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = sdf.format(Date(record.systemTime.toLong()))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        border = BorderStroke(1.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Exercise: ${record.exerciseName}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Correct: ${record.correct}, Wrong: ${record.wrong}",
                    fontSize = 14.sp
                )
                Text(
                    text = "Effective Repetition Count: ${record.motionCount}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = formattedDate,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "${record.accuracy.roundToInt()}%",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = accuracyColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}