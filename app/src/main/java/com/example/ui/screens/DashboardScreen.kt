package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.components.ElegantLineChart
import com.example.ui.theme.*
import com.example.ui.viewmodel.FitnessViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FitnessViewModel,
    onNavigateToExercise: (Exercise) -> Unit,
    onNavigateToRecovery: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val todayPlans by viewModel.todayWorkoutPlans.collectAsState()
    val selectedDay by viewModel.selectedDayOfWeek.collectAsState()
    val allWorkoutLogs by viewModel.allWorkoutLogs.collectAsState()
    val allBodyStats by viewModel.allBodyStats.collectAsState()
    
    val todayCalories by viewModel.todayCalories.collectAsState()
    val todayProtein by viewModel.todayProtein.collectAsState()

    var showGoalSettings by remember { mutableStateOf(false) }
    var showWeightLogger by remember { mutableStateOf(false) }

    val daysOfWeek = listOf(
        Pair(1, "M"), Pair(2, "T"), Pair(3, "W"), Pair(4, "T"), Pair(5, "F"), Pair(6, "S"), Pair(7, "S")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp) // Leave space for bottom bar
    ) {
        // --- 1. Top Header with Quote Banner ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .drawBehind {
                    val brush = Brush.verticalGradient(
                        colors = listOf(
                            VoltLime.copy(alpha = 0.15f),
                            BackgroundDark
                        )
                    )
                    drawRect(brush = brush)
                }
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo_vector),
                            contentDescription = "Fitness Sensei Logo",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, VoltLime, CircleShape)
                                .background(Color.Black)
                                .padding(4.dp)
                                .testTag("app_logo_image")
                        )
                        Column {
                            Text(
                                text = "FITNESS SENSEI",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "鍛錬 • MIND OVER MATTER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = VoltLime
                            )
                        }
                    }
                    
                    IconButton(
                        onClick = { showGoalSettings = true },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Inspirational Quote Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Quote",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (userProfile.goal) {
                                "Muscle Gain" -> "No shortcuts. Eat heavy, lift heavy, sleep heavy. Grow."
                                "Strength" -> "Slam the iron. It doesn't matter how slow you go as long as you do not stop."
                                "Fat Loss" -> "Sweat is just fat crying. Push through the burn, secure the victory."
                                else -> "Keep showing up. Discipline eats motivation for breakfast."
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- 2. Interactive Weekly Day Selector ---
        Text(
            text = "WEEKLY SPLIT",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEach { (dayIndex, dayLabel) ->
                val isSelected = selectedDay == dayIndex
                val todayDay = viewModel.getCurrentDayOfWeek()
                val isToday = dayIndex == todayDay

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else if (isToday) MaterialTheme.colorScheme.surfaceVariant
                            else MaterialTheme.colorScheme.surface
                        )
                        .border(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else if (isToday) VoltLime
                            else MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.selectDay(dayIndex) }
                        .padding(vertical = 12.dp)
                        .testTag("day_button_$dayIndex"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dayLabel,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) Color.White else TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color.White
                                    else if (isToday) VoltLime
                                    else Color.Transparent
                                )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. Today's Plan Section ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TODAY'S WORKOUT PLAN",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                Text(
                    text = "Goal: ${userProfile.goal} (${userProfile.experienceLevel})",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
            }
            Text(
                text = if (selectedDay == viewModel.getCurrentDayOfWeek()) "TODAY" else "SELECTED DAY",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = VoltLime,
                modifier = Modifier
                    .background(VoltLime.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (todayPlans.isEmpty()) {
            // Rest/Recovery Day Layout
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = "Rest",
                        tint = IceBlue,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "RECOVERY & REST DAY",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Muscles grow outside the gym. Today is designed for deep tissue healing, mobility stretching, and loading high-quality protein.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToRecovery,
                        colors = ButtonDefaults.buttonColors(containerColor = IceBlue),
                        modifier = Modifier.testTag("recovery_tips_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Healing, contentDescription = "Recovery")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Stretching & Recovery Guides", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Workout List Layout
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    todayPlans.forEachIndexed { index, plan ->
                        var exerciseDetail by remember { mutableStateOf<Exercise?>(null) }
                        
                        LaunchedEffect(plan.exerciseId) {
                            exerciseDetail = viewModel.getExerciseById(plan.exerciseId)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { exerciseDetail?.let { onNavigateToExercise(it) } }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = exerciseDetail?.name ?: "Loading...",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Row {
                                        Text(
                                            text = "${plan.sets} Sets x ${plan.reps} Reps",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondaryDark
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "•  ${exerciseDetail?.equipment ?: ""}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = VoltLime
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { exerciseDetail?.let { onNavigateToExercise(it) } },
                                modifier = Modifier.testTag("log_exercise_${plan.exerciseId}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start Logging",
                                    tint = VoltLime
                                )
                            }
                        }

                        if (index < todayPlans.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 4. Calories & Protein Progress Rings ---
        Text(
            text = "NUTRITION TARGETS",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calorie progress circle
                NutritionProgressWidget(
                    currentValue = todayCalories,
                    targetValue = userProfile.targetCalories,
                    unit = "kcal",
                    label = "Calories",
                    color = MaterialTheme.colorScheme.primary
                )

                // Protein progress circle
                NutritionProgressWidget(
                    currentValue = todayProtein,
                    targetValue = userProfile.targetProtein,
                    unit = "g",
                    label = "Protein",
                    color = VoltLime
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 5. Weight & PR Tracking Dashboard Widgets ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PROGRESS & LOGS",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = Color.White
            )

            TextButton(
                onClick = { showWeightLogger = true },
                modifier = Modifier.testTag("add_weight_button")
            ) {
                Icon(imageVector = Icons.Default.Scale, contentDescription = "Weight Log")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Weight", fontWeight = FontWeight.Bold, color = VoltLime)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Gorgeous interactive native line chart showing bodyweight and exercise PRs
        ElegantLineChart(
            weightLogs = allBodyStats,
            workoutLogs = allWorkoutLogs,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display current weight and body stat logs
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Current Body Weight",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                        Text(
                            text = "${userProfile.weightKg} kg",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                    }

                    // Simple stats trend text
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Weight Logs",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                        Text(
                            text = "${allBodyStats.size} Recorded",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = VoltLime
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Simple mini chart representation / list of weight changes
                if (allBodyStats.isNotEmpty()) {
                    Text(
                        text = "Weight History (Recent logs first)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Column {
                        allBodyStats.take(3).forEach { stat ->
                            val dateString = remember(stat.timestamp) {
                                SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(stat.timestamp))
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = dateString, style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
                                Row {
                                    Text(text = "${stat.weight} kg", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                    if (stat.bodyFat > 0.0) {
                                        Text(text = " (${stat.bodyFat}% BF)", style = MaterialTheme.typography.bodyMedium, color = IceBlue)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No weight logs recorded yet.", style = MaterialTheme.typography.bodyMedium, color = TextMutedDark)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PR tracker
        val prLogs = remember(allWorkoutLogs) {
            allWorkoutLogs.filter { it.isPR }.take(3)
        }

        if (prLogs.isNotEmpty()) {
            Text(
                text = "PERSONAL RECORDS ACHIEVED",
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
                    prLogs.forEach { log ->
                        val dateString = remember(log.timestamp) {
                            SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(log.timestamp))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "PR Logo",
                                    tint = VoltLime,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = log.exerciseName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                    Text(text = "Logged $dateString", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
                                }
                            }
                            Text(
                                text = "${log.weight} kg x ${log.reps}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                                color = VoltLime
                            )
                        }
                    }
                }
            }
        }
    }

    // --- 6. Goal & Details Settings Dialog ---
    if (showGoalSettings) {
        Dialog(onDismissRequest = { showGoalSettings = false }) {
            var selectedGoal by remember { mutableStateOf(userProfile.goal) }
            var weightString by remember { mutableStateOf(userProfile.weightKg.toString()) }
            var caloriesString by remember { mutableStateOf(userProfile.targetCalories.toString()) }
            var proteinString by remember { mutableStateOf(userProfile.targetProtein.toString()) }
            var selectedExperience by remember { mutableStateOf(userProfile.experienceLevel) }

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
                    Text(
                        text = "SENSEI PROFILE SETUP",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("SELECT FITNESS GOAL", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val goalsList = listOf("Muscle Gain", "Strength", "Fat Loss")
                    goalsList.forEach { goalOption ->
                        val isGoalSelected = selectedGoal == goalOption
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isGoalSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .border(1.dp, if (isGoalSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedGoal = goalOption
                                    // Set automatic smart defaults for macros based on the goal
                                    when (goalOption) {
                                        "Muscle Gain" -> {
                                            caloriesString = "2800"
                                            proteinString = "160"
                                        }
                                        "Strength" -> {
                                            caloriesString = "3000"
                                            proteinString = "180"
                                        }
                                        "Fat Loss" -> {
                                            caloriesString = "2000"
                                            proteinString = "170"
                                        }
                                    }
                                }
                                .padding(12.dp)
                                .testTag("goal_opt_$goalOption")
                        ) {
                            Text(
                                text = goalOption,
                                fontWeight = FontWeight.Bold,
                                color = if (isGoalSelected) Color.White else Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("EXPERIENCE LEVEL", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val expList = listOf("Beginner", "Intermediate", "Advanced")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        expList.forEach { exp ->
                            val isExpSelected = selectedExperience == exp
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isExpSelected) VoltLime else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (isExpSelected) VoltLime else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .clickable { selectedExperience = exp }
                                    .padding(8.dp)
                                    .testTag("exp_opt_$exp"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = exp,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isExpSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = weightString,
                        onValueChange = { weightString = it },
                        label = { Text("Body Weight (kg)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("weight_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = caloriesString,
                        onValueChange = { caloriesString = it },
                        label = { Text("Calorie Target (kcal)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calories_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = proteinString,
                        onValueChange = { proteinString = it },
                        label = { Text("Protein Target (g)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("protein_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showGoalSettings = false },
                            modifier = Modifier.testTag("cancel_settings_button")
                        ) {
                            Text("Cancel", color = TextSecondaryDark)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = {
                                val w = weightString.toDoubleOrNull() ?: userProfile.weightKg
                                val c = caloriesString.toIntOrNull() ?: userProfile.targetCalories
                                val p = proteinString.toIntOrNull() ?: userProfile.targetProtein
                                viewModel.updateProfile(selectedGoal, w, c, p, selectedExperience)
                                showGoalSettings = false
                            },
                            modifier = Modifier.testTag("save_settings_button")
                        ) {
                            Text("Save Profile", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // --- 7. Weight Logger Dialog ---
    if (showWeightLogger) {
        Dialog(onDismissRequest = { showWeightLogger = false }) {
            var inputWeight by remember { mutableStateOf(userProfile.weightKg.toString()) }
            var inputBodyFat by remember { mutableStateOf("") }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "LOG BODY WEIGHT",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = inputWeight,
                        onValueChange = { inputWeight = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("weight_log_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputBodyFat,
                        onValueChange = { inputBodyFat = it },
                        label = { Text("Body Fat Percentage (%) - Optional") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bodyfat_log_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showWeightLogger = false },
                            modifier = Modifier.testTag("cancel_weight_btn")
                        ) {
                            Text("Cancel", color = TextSecondaryDark)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = {
                                val w = inputWeight.toDoubleOrNull()
                                val bf = inputBodyFat.toDoubleOrNull() ?: 0.0
                                if (w != null) {
                                    viewModel.logWeight(w, bf)
                                    showWeightLogger = false
                                }
                            },
                            modifier = Modifier.testTag("submit_weight_btn")
                        ) {
                            Text("Save Entry", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionProgressWidget(
    currentValue: Int,
    targetValue: Int,
    unit: String,
    label: String,
    color: Color
) {
    val progress = if (targetValue > 0) currentValue.toFloat() / targetValue else 0f
    val percentage = (progress * 100).toInt().coerceAtMost(999)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background Circle
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Foreground Active Progress
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$currentValue",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                Text(
                    text = "/ $targetValue $unit",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = Color.White
        )
    }
}
