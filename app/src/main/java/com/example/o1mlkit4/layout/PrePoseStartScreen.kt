package com.example.o1mlkit4.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.o1mlkit4.data.ExerciseData
import com.example.o1mlkit4.utils.loadExerciseData

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PrePoseStartScreen(
    exerciseName: String,
    code: String,
    onBack: () -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var exerciseData by remember { mutableStateOf<ExerciseData?>(null) }

    LaunchedEffect(code) {
        exerciseData = loadExerciseData(context, code)
    }

    val pagerState = rememberPagerState(pageCount = { 2 })

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(exerciseName) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = if (pagerState.currentPage == 0) Color.Black else Color.Gray,
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = if (pagerState.currentPage == 1) Color.Black else Color.Gray,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                text = "Exercise Illustration",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val image1Path = "file:///android_asset/exerciseDataBase/$code/${code}_1.jpg"
                            Image(
                                painter = rememberImagePainter(
                                    data = image1Path,
                                    builder = {
                                        crossfade(true)
                                    }
                                ),
                                contentDescription = "Illustration of $exerciseName exercise position",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Primary Muscles",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val image2Path = "file:///android_asset/exerciseDataBase/$code/${code}_2.jpg"
                            Image(
                                painter = rememberImagePainter(
                                    data = image2Path,
                                    builder = {
                                        crossfade(true)
                                    }
                                ),
                                contentDescription = "Detailed illustration of $exerciseName exercise",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            exerciseData?.let { data ->
                                data.mainMuscles.forEach { muscle ->
                                    Text(text = muscle, fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Instructions",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                )
                                data.stepByStepInstructions.forEachIndexed { index, step ->
                                    Text(text = "${index + 1}. $step", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Common Mistakes",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                )
                                data.commonMistake.forEachIndexed { index, mistake ->
                                    Text(text = "${index + 1}. $mistake", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                //Video Tutorial
                                Text(
                                    text = "Video Tutorial",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Text(
                                    text = "Watch Video Tutorial",
                                    fontSize = 16.sp,
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier
                                        .padding(bottom = 12.dp)
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.videoTutorial))
                                            context.startActivity(intent)
                                        }
                                )
                                //Information Source
                                Text(
                                    text = "Information Source",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Text(
                                    text = "View Information Source",
                                    fontSize = 16.sp,
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.infoObtainedFrom))
                                            context.startActivity(intent)
                                        }
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text=data.citation,
                                    fontWeight=FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                )


                                Spacer(modifier = Modifier.height(80.dp))
                            } ?: run {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(text = "Loading...", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                    1 -> {
                        // Second page content (Posture Monitoring)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            // Title for the second page with Underline
                            Text(
                                text = "Posture Monitoring",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val image3Path = "file:///android_asset/exerciseDataBase/$code/${code}_3.jpg"
                            Image(
                                painter = rememberImagePainter(
                                    data = image3Path,
                                    builder = {
                                        crossfade(true)
                                    }
                                ),
                                contentDescription = "Posture Monitor",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Repetition Counting",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                            )
                            Spacer(modifier = Modifier.height(16.dp))


                            val image4Path = "file:///android_asset/exerciseDataBase/$code/${code}_4.jpg"
                            Image(
                                painter = rememberImagePainter(
                                    data = image4Path,
                                    builder = {
                                        crossfade(true)
                                    }
                                ),
                                contentDescription = "Detailed posture illustration of $exerciseName exercise",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }

            // start Button
            Button(
                onClick = onStart,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Start")
            }
        }
    }
}
