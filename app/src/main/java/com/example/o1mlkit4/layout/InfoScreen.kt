package com.example.o1mlkit4.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.o1mlkit4.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tutorial",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())  // Makes the Column scrollable

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Section A
                Section("Step 1", "Select the desired workout.", "file:///android_asset/info/info1.jpg")

                // Section B
                Section("Step 2", "Read and understand the general posture and body motion of the workout.", "file:///android_asset/info/info2.jpg")

                // Section C
                Section("Step 3", "Find a place where you can place your phone on the ground with front camera being able to capture your face and other body parts.", "file:///android_asset/info/info3.jpg")

                // Section D
                Section("Step 4", "Click the 'Start' button and posture monitoring session begins, start to perform workout repetitions (required screen recording & camera permission).", "file:///android_asset/info/info4.jpg")

                // Section E
                Section("Step 5", "Double tab screen to end workout session and view workout summary.", "file:///android_asset/info/info5.jpg")
                Section("Step 6", "For saved records, click the right top corner of main menu to view and replay recordings.", "file:///android_asset/info/info6.jpg")

            }
        }
    }
}

@Composable
fun Section(title: String, content: String, imagePath: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Center
        )

        Image(
            painter = rememberImagePainter(imagePath),
            contentDescription = "Section Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Fit
        )
    }
}