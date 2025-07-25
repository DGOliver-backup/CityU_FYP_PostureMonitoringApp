package com.example.o1mlkit4.ui

import android.os.VibrationEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.o1mlkit4.FeedbackManager
import com.example.o1mlkit4.classifiers.BaseClassifier
import com.example.o1mlkit4.layout.FeedbackViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SelectionScreen(
    classifiers: List<BaseClassifier>,
    onSelectClassifier: (BaseClassifier) -> Unit,
    onGoToHistory: () -> Unit,
    onInfo: () -> Unit,

    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    FeedbackManager.initialize(context)

    val isFeedbackEnabled = remember { mutableStateOf(true) }
    FeedbackManager.enableVibrationFeedback()

    val feedbackViewModel: FeedbackViewModel = viewModel()
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    val pageTitles = listOf("CHEST", "SHOULDER", "ARMS", "BACK", "ABS", "LEGS")
    val pageTypes = listOf("chest", "shoulder", "arms", "back", "abs", "legs")
    val extendedPageTitles = listOf("ALL") + pageTitles

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Posture Monitoring App",
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                }
                TopAppBar(
                    title = { Text("Select Exercise") },
                    actions = {
                        IconButton(onClick = onGoToHistory) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "History"
                            )
                        }
                    }
                )
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .fillMaxWidth(0.3f)
                                .height(32.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                ) {
                    extendedPageTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (pagerState.currentPage == index)
                                            FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (pagerState.currentPage == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(48.dp)
                        )
                    }
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
                count = extendedPageTitles.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) { page ->
                val filteredClassifiers = if (page == 0) {
                    classifiers
                } else {
                    classifiers.filter { it.type == pageTypes[page - 1] }
                }
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredClassifiers) { classifier ->
                        Button(
                            onClick = { onSelectClassifier(classifier) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = classifier.name)
                        }
                    }
                }
            }
            // Feedback vibrate
            FloatingActionButton(
                onClick = {
                    feedbackViewModel.toggleVibrationFeedback()
                    if (feedbackViewModel.isVibrationFeedbackEnabled.value) {
                        val vibrationPattern = longArrayOf(0, 500)
                        FeedbackManager.triggerOneShotVibration(vibrationPattern)
                    }
                },
                containerColor = if (feedbackViewModel.isVibrationFeedbackEnabled.value) Color.Green else Color.Red,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Vibration,
                    contentDescription = "Toggle Vibration Feedback",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Feedback sound
            FloatingActionButton(
                onClick = {
                    feedbackViewModel.toggleSoundFeedback()
                    if (feedbackViewModel.isSoundFeedbackEnabled.value) {
                        FeedbackManager.triggerSoundFeedback()
                    }
                },
                containerColor = if (feedbackViewModel.isSoundFeedbackEnabled.value) Color.Green else Color.Red,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 96.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.VolumeUp,
                    contentDescription = "Toggle Sound Feedback",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            FloatingActionButton(
                onClick = onInfo,
                containerColor = Color.LightGray,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Information",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
