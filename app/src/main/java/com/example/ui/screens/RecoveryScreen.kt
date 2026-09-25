package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryScreen(viewModel: FitnessViewModel) {
    val allRecoveryLogs by viewModel.allRecoveryLogs.collectAsState()

    var sleepInput by remember { mutableStateOf(8.0) }
    var stretchingInput by remember { mutableStateOf(false) }

    // Custom Stretching Timer States
    var activeStretchRoutineName by remember { mutableStateOf<String?>(null) }
    var stretchTimeRemaining by remember { mutableStateOf(30) }
    var isStretchTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isStretchTimerRunning, stretchTimeRemaining) {
        if (isStretchTimerRunning && stretchTimeRemaining > 0) {
            delay(1000L)
            stretchTimeRemaining -= 1
        } else if (stretchTimeRemaining == 0) {
            isStretchTimerRunning = false
        }
    }

    val injuryTips = listOf(
        InjuryTip("Shoulder Impingement Prevention", "Perform face pulls and rotator cuff warm-ups before bench pressing. Never flare elbows at 90 degrees.", "Rotator Cuff & Delts"),
        InjuryTip("Lower Back Decompression", "Do bird-dogs and child's pose after heavy squatting. Avoid passive flexion when spine is loaded.", "Spinal Health"),
        InjuryTip("Patellar Knee Tracking", "Ensure knees track outward in line with toes during squats. Mobilize ankles to take stress off knees.", "Knee Alignment")
    )

    val stretchRoutines = listOf(
        StretchRoutine("Lower Body Opening", "30s Deep Squat Hold, 30s Hip Flexor Stretch, 30s Pigeon Pose", 90),
        StretchRoutine("Upper Body Mobility", "30s Doorway Chest Stretch, 30s Shoulder Dislocates, 30s Wall Slides", 90),
        StretchRoutine("Spinal Decompression", "30s Cat-Cow Flow, 30s Child's Pose, 30s Cobra Stretch", 90)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp) // Space for bottom bar
    ) {
        // --- Header ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = "RECOVERY SENSEI",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "REBUILD MUSCLE • RECHARGE MIND • PREVENT INJURY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = VoltLime
                )
            }
        }

        // --- 1. Log Recovery Stats Form ---
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "LOG DAILY RECOVERY",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Sleep hours log
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sleep Duration",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Text(
                        text = "${String.format("%.1f", sleepInput)} Hours",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                        color = VoltLime
                    )
                }
                
                Slider(
                    value = sleepInput.toFloat(),
                    onValueChange = { sleepInput = it.toDouble() },
                    valueRange = 4f..12f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        thumbColor = VoltLime,
                        activeTrackColor = VoltLime,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.testTag("sleep_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stretching completed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completed Stretching & Mobility?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Switch(
                        checked = stretchingInput,
                        onCheckedChange = { stretchingInput = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = VoltLime,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.testTag("stretching_switch")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.logRecovery(sleepInput, stretchingInput)
                        // Show quick confirmation toast style or just reset stretching checkbox
                        stretchingInput = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_recovery_btn")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Log")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Daily Recovery Stats", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 2. Active Mobility Timed Routines ---
        Text(
            text = "ACTIVE MOBILITY TIMER",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (activeStretchRoutineName != null) {
            // Stretching countdown card active
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                border = BorderStroke(2.dp, VoltLime),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "STRETCHING: $activeStretchRoutineName",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = VoltLime
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (stretchTimeRemaining == 0) "ROUTINE COMPLETE!" else "$stretchTimeRemaining s",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = { isStretchTimerRunning = !isStretchTimerRunning },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isStretchTimerRunning) Color.DarkGray else VoltLime
                            )
                        ) {
                            Text(
                                text = if (isStretchTimerRunning) "Pause" else "Start Timer",
                                color = if (isStretchTimerRunning) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                isStretchTimerRunning = false
                                activeStretchRoutineName = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text("Quit Routine", color = Color.White)
                        }
                    }
                }
            }
        } else {
            // List of stretching routines
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stretchRoutines.forEach { routine ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                activeStretchRoutineName = routine.name
                                stretchTimeRemaining = routine.totalSeconds
                                isStretchTimerRunning = true
                            }
                            .testTag("stretch_routine_${routine.name.replace(" ", "_")}")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = routine.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = routine.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryDark
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .background(VoltLime.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${routine.totalSeconds}s",
                                    fontWeight = FontWeight.Bold,
                                    color = VoltLime,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 3. Injury Prevention tips by Sensei ---
        Text(
            text = "SENSEI INJURY PREVENTION PROTOCOL",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            injuryTips.forEach { tip ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tip.tag.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = VoltLime
                            )
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Shield",
                                tint = VoltLime,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = tip.title,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = tip.details,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 4. History Logs ---
        if (allRecoveryLogs.isNotEmpty()) {
            Text(
                text = "RECOVERY HISTORY",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.White
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    allRecoveryLogs.take(5).forEachIndexed { index, log ->
                        val dateStr = remember(log.timestamp) {
                            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(log.timestamp))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = dateStr, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Text(
                                    text = if (log.stretchingCompleted) "Stretching: Completed" else "Stretching: Skipped",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (log.stretchingCompleted) VoltLime else MaterialTheme.colorScheme.error
                                )
                            }
                            Text(
                                text = "${log.sleepHours}h Sleep",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                                color = VoltLime
                            )
                        }

                        if (index < allRecoveryLogs.take(5).size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}

data class InjuryTip(
    val title: String,
    val details: String,
    val tag: String
)

data class StretchRoutine(
    val name: String,
    val details: String,
    val totalSeconds: Int
)
