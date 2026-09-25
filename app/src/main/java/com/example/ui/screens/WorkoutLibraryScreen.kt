package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FitnessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutLibraryScreen(
    viewModel: FitnessViewModel,
    onNavigateToLog: (Exercise) -> Unit
) {
    val filteredExercises by viewModel.filteredExercises.collectAsState()
    val activeMuscleGroup by viewModel.selectedMuscleGroup.collectAsState()
    val activeEquipment by viewModel.selectedEquipment.collectAsState()

    var selectedExerciseForDetails by remember { mutableStateOf<Exercise?>(null) }

    val muscleGroups = listOf("Chest", "Back", "Legs", "Shoulders", "Arms", "Core")
    val equipments = listOf("Barbell", "Dumbbell", "Machine", "Bodyweight")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 80.dp) // Space for bottom bar
    ) {
        // --- Header ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = "MOVEMENT LIBRARY",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "MASTER TECHNIQUE • SECURE PROGRESS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = VoltLime
                )
            }
        }

        // --- Muscle Group Filters (Horizontal Scroll) ---
        Text(
            text = "TARGET MUSCLE GROUP",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = Color.White.copy(alpha = 0.6f)
        )
        
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                FilterChip(
                    selected = activeMuscleGroup == null,
                    onClick = { viewModel.setMuscleGroupFilter(null) },
                    label = { Text("All Muscles") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("muscle_all")
                )
            }

            items(muscleGroups) { group ->
                val isSelected = activeMuscleGroup == group
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setMuscleGroupFilter(if (isSelected) null else group) },
                    label = { Text(group) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("muscle_$group")
                )
            }
        }

        // --- Equipment Filters (Horizontal Scroll) ---
        Text(
            text = "EQUIPMENT REQUIRED",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = Color.White.copy(alpha = 0.6f)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                FilterChip(
                    selected = activeEquipment == null,
                    onClick = { viewModel.setEquipmentFilter(null) },
                    label = { Text("Any Equipment") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VoltLime,
                        selectedLabelColor = Color.Black,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("equip_all")
                )
            }

            items(equipments) { equip ->
                val isSelected = activeEquipment == equip
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setEquipmentFilter(if (isSelected) null else equip) },
                    label = { Text(equip) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VoltLime,
                        selectedLabelColor = Color.Black,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("equip_$equip")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Exercises Grid/List ---
        if (filteredExercises.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterListOff,
                        contentDescription = "No movements",
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No movements found matching filters",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                filteredExercises.forEach { exercise ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { selectedExerciseForDetails = exercise }
                            .testTag("exercise_card_${exercise.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = exercise.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                                        color = Color.White
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = exercise.muscleGroup,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                VoltLime.copy(alpha = 0.15f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = exercise.equipment,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = VoltLime
                                        )
                                    }
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Details",
                                tint = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }

    // --- Exercise Details Dialog & Simulated Video Player ---
    if (selectedExerciseForDetails != null) {
        val exercise = selectedExerciseForDetails!!
        Dialog(onDismissRequest = { selectedExerciseForDetails = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Title and Muscle Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = exercise.name.uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { selectedExerciseForDetails = null },
                            modifier = Modifier.testTag("close_details_dialog")
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated Video Guide / Motion Canvas Player
                    Text(
                        text = "SENSEI MOTION GUIDE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = VoltLime
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        ExerciseSimulatorCanvas(simulationType = exercise.videoSimulationType)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Info parameters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Target Muscle", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
                            Text(text = exercise.muscleGroup, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Equipment", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
                            Text(text = exercise.equipment, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = VoltLime)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Default Rest", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
                            Text(text = "${exercise.defaultRestSeconds}s", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Step by step guide
                    Text(
                        text = "INSTRUCTIONS",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = exercise.instructions,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
                    Button(
                        onClick = {
                            viewModel.startExerciseLogging(exercise)
                            selectedExerciseForDetails = null
                            onNavigateToLog(exercise)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("log_this_movement_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Log Set")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Dynamic Set Now", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * Animated Canvas that simulates fitness exercise movements in real-time.
 * Gives gym-goers a clear, lightweight, and super helpful visual guide.
 */
@Composable
fun ExerciseSimulatorCanvas(simulationType: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "sim_transition")
    
    // Smooth upward/downward looping modifier
    val animationOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "height_oscillator"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f

        when (simulationType) {
            "bench_press" -> {
                // Draw Bench
                drawLine(
                    color = Color.DarkGray,
                    start = Offset(centerX - 100f, centerY + 30f),
                    end = Offset(centerX + 100f, centerY + 30f),
                    strokeWidth = 8f
                )
                // Draw barbell oscillating vertically
                val barY = centerY - 40f + (animationOffset * 50f)
                drawLine(
                    color = Color.White,
                    start = Offset(centerX - 120f, barY),
                    end = Offset(centerX + 120f, barY),
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
                // Barbell weight plates
                drawRect(color = VoltLime, topLeft = Offset(centerX - 135f, barY - 20f), size = Size(15f, 40f))
                drawRect(color = VoltLime, topLeft = Offset(centerX + 120f, barY - 20f), size = Size(15f, 40f))

                // Simulated arms pulling
                drawLine(color = Color.LightGray, start = Offset(centerX - 60f, centerY + 30f), end = Offset(centerX - 60f, barY), strokeWidth = 6f)
                drawLine(color = Color.LightGray, start = Offset(centerX + 60f, centerY + 30f), end = Offset(centerX + 60f, barY), strokeWidth = 6f)
            }
            "barbell_squat" -> {
                // Draw squat stand rack posts
                drawLine(color = Color.DarkGray, start = Offset(centerX - 80f, centerY + 60f), end = Offset(centerX - 80f, centerY - 60f), strokeWidth = 6f)
                drawLine(color = Color.DarkGray, start = Offset(centerX + 80f, centerY + 60f), end = Offset(centerX + 80f, centerY - 60f), strokeWidth = 6f)

                // Barbell moving up/down (squatting motion)
                val barY = centerY - 30f + (animationOffset * 65f)
                drawLine(
                    color = Color.White,
                    start = Offset(centerX - 110f, barY),
                    end = Offset(centerX + 110f, barY),
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
                // Dumbbell/plates
                drawCircle(color = VoltLime, radius = 22f, center = Offset(centerX - 110f, barY))
                drawCircle(color = VoltLime, radius = 22f, center = Offset(centerX + 110f, barY))
            }
            "dumbbell_fly" -> {
                // Bench
                drawLine(color = Color.DarkGray, start = Offset(centerX, centerY - 60f), end = Offset(centerX, centerY + 60f), strokeWidth = 12f)
                // Arms sweeping in arc
                val angleRad = Math.toRadians((animationOffset * 70.0) - 35.0)
                val armLen = 80f
                val leftHandX = centerX - (Math.cos(angleRad) * armLen).toFloat()
                val leftHandY = centerY - (Math.sin(angleRad) * armLen).toFloat()
                val rightHandX = centerX + (Math.cos(angleRad) * armLen).toFloat()
                val rightHandY = centerY - (Math.sin(angleRad) * armLen).toFloat()

                // Draw arm extensions
                drawLine(color = Color.LightGray, start = Offset(centerX, centerY), end = Offset(leftHandX, leftHandY), strokeWidth = 6f)
                drawLine(color = Color.LightGray, start = Offset(centerX, centerY), end = Offset(rightHandX, rightHandY), strokeWidth = 6f)

                // Draw dumbbells
                drawCircle(color = Color.White, radius = 10f, center = Offset(leftHandX, leftHandY))
                drawCircle(color = Color.White, radius = 10f, center = Offset(rightHandX, rightHandY))
            }
            else -> {
                // Default generic lifting simulation (bicep curl / lift loop)
                val lifterY = centerY + 30f - (animationOffset * 50f)
                drawCircle(
                    color = VoltLime,
                    radius = 20f,
                    center = Offset(centerX, lifterY)
                )
                drawLine(
                    color = Color.White,
                    start = Offset(centerX - 60f, lifterY),
                    end = Offset(centerX + 60f, lifterY),
                    strokeWidth = 8f
                )
            }
        }
    }
}

// Helper utility for Rect coordinates in drawRect
fun gapRect(left: Float, top: Float, width: Float, height: Float): androidx.compose.ui.geometry.Rect {
    return androidx.compose.ui.geometry.Rect(left, top, left + width, top + height)
}
