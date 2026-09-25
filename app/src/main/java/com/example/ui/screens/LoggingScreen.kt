package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FitnessViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoggingScreen(
    viewModel: FitnessViewModel,
    onNavigateBack: () -> Unit
) {
    val activeExercise by viewModel.activeExercise.collectAsState()
    val loggedSets by viewModel.loggedSetsInSession.collectAsState()

    if (activeExercise == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text("No active workout selected. Return to Dashboard.", color = Color.White)
        }
        return
    }

    val exercise = activeExercise!!

    var weightInput by remember { mutableStateOf(80.0) }
    var repsInput by remember { mutableStateOf(10) }

    // Timer states
    var restTimeRemaining by remember { mutableStateOf(exercise.defaultRestSeconds) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var timerTotalTime by remember { mutableStateOf(exercise.defaultRestSeconds) }

    var showCelebration by remember { mutableStateOf(false) }

    // Coroutine Timer Countdown
    LaunchedEffect(isTimerRunning, restTimeRemaining) {
        if (isTimerRunning && restTimeRemaining > 0) {
            delay(1000L)
            restTimeRemaining -= 1
        } else if (restTimeRemaining == 0) {
            isTimerRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp) // Cushion
    ) {
        // --- Header / Back Navigation ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .testTag("back_button")
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "ACTIVE GYM LOGGER",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
            }
        }

        // --- 1. Movement Guide Visual ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            ExerciseSimulatorCanvas(simulationType = exercise.videoSimulationType)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. Logging Inputs & Adjusters (One Hand Friendly) ---
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ENTER WEIGHT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextSecondaryDark
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { weightInput = (weightInput - 5.0).coerceAtLeast(0.0) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(44.dp).testTag("weight_minus_5")
                        ) {
                            Text("-5", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { weightInput = (weightInput - 2.5).coerceAtLeast(0.0) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(44.dp).testTag("weight_minus_2_5")
                        ) {
                            Text("-2.5", fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "$weightInput kg",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { weightInput += 2.5 },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(44.dp).testTag("weight_plus_2_5")
                        ) {
                            Text("+2.5", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { weightInput += 5.0 },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(44.dp).testTag("weight_plus_5")
                        ) {
                            Text("+5", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ENTER REPS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextSecondaryDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { repsInput = (repsInput - 1).coerceAtLeast(1) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.size(44.dp).testTag("reps_minus_1")
                    ) {
                        Text("-1", fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "$repsInput Reps",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                        color = VoltLime
                    )

                    Button(
                        onClick = { repsInput += 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.size(44.dp).testTag("reps_plus_1")
                    ) {
                        Text("+1", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Log Set Button
                Button(
                    onClick = {
                        viewModel.logSet(weightInput, repsInput)
                        // Trigger rest timer automatically
                        restTimeRemaining = exercise.defaultRestSeconds
                        timerTotalTime = exercise.defaultRestSeconds
                        isTimerRunning = true
                        
                        // Check if they completed required sets (usually 3 or 4)
                        if (loggedSets.size + 1 >= exercise.defaultSets) {
                            showCelebration = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("log_set_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LOG SET ${loggedSets.size + 1}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. Rest Timer (Floating/Nested style) ---
        if (isTimerRunning || restTimeRemaining < exercise.defaultRestSeconds) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (restTimeRemaining == 0) GymSuccess.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(
                    1.dp,
                    if (restTimeRemaining == 0) GymSuccess else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (restTimeRemaining == 0) Icons.Default.NotificationsActive else Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = if (restTimeRemaining == 0) GymSuccess else VoltLime,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (restTimeRemaining == 0) "REST PERIOD OVER!" else "REST TIME REMAINING",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (restTimeRemaining == 0) GymSuccess else Color.White
                            )
                            Text(
                                text = if (restTimeRemaining == 0) "Get back on the barbell!" else "$restTimeRemaining seconds",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = {
                                restTimeRemaining += 30
                                timerTotalTime += 30
                                isTimerRunning = true
                            },
                            modifier = Modifier.testTag("timer_add_30")
                        ) {
                            Text("+30s", fontWeight = FontWeight.Bold, color = VoltLime)
                        }

                        Button(
                            onClick = {
                                isTimerRunning = false
                                restTimeRemaining = exercise.defaultRestSeconds
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.testTag("timer_skip")
                        ) {
                            Text("Skip", color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 4. Logs in Today's Session ---
        Text(
            text = "SETS LOGGED IN THIS SESSION",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.White.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (loggedSets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No sets logged yet. Lift heavy!", style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
                    }
                } else {
                    loggedSets.forEachIndexed { idx, log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(VoltLime),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${idx + 1}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                                        color = Color.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Set ${idx + 1}",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${log.weight} kg x ${log.reps}",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                                    color = Color.White
                                )
                                if (log.isPR) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "New PR",
                                        tint = VoltLime,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        if (idx < loggedSets.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }

    // --- Celebration Alert Dialog ---
    if (showCelebration) {
        AlertDialog(
            onDismissRequest = { showCelebration = false },
            confirmButton = {
                Button(
                    onClick = {
                        showCelebration = false
                        viewModel.finishExerciseLogging()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VoltLime),
                    modifier = Modifier.testTag("dismiss_celebration")
                ) {
                    Text("CONTINUE WORKOUT", color = Color.Black, fontWeight = FontWeight.Black)
                }
            },
            title = {
                Text(
                    text = "SENSEI APPRECIATION",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = VoltLime,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MilitaryTech,
                        contentDescription = "Trophy",
                        tint = VoltLime,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "EXERCISE GOAL MET!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You completed all target sets for ${exercise.name}. Consistently pushing limits is how giants are forged. Keep driving forward.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark,
                        textAlign = TextAlign.Center
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
