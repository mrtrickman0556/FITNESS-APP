package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BodyStatLog
import com.example.data.WorkoutLog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElegantLineChart(
    weightLogs: List<BodyStatLog>,
    workoutLogs: List<WorkoutLog>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Body Weight, 1 = Strength PRs
    
    // Filter PRs
    val prLogsOnly = remember(workoutLogs) {
        workoutLogs.filter { it.isPR }.sortedBy { it.timestamp }
    }
    
    // Unique exercises with PRs
    val prExercises = remember(prLogsOnly) {
        prLogsOnly.map { it.exerciseName }.distinct()
    }
    
    var selectedExercise by remember(prExercises) {
        mutableStateOf(prExercises.firstOrNull() ?: "Barbell Bench Press")
    }
    
    var showExerciseDropdown by remember { mutableStateOf(false) }
    
    val chartData: List<ChartPoint> = remember(selectedTab, weightLogs, prLogsOnly, selectedExercise) {
        if (selectedTab == 0) {
            weightLogs.sortedBy { it.timestamp }.map {
                ChartPoint(
                    value = it.weight,
                    label = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(it.timestamp)),
                    dateFull = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(it.timestamp)),
                    extraInfo = if (it.bodyFat > 0.0) "${it.bodyFat}% BF" else null
                )
            }
        } else {
            prLogsOnly.filter { it.exerciseName == selectedExercise }.map {
                ChartPoint(
                    value = it.weight,
                    label = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(it.timestamp)),
                    dateFull = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(it.timestamp)),
                    extraInfo = "${it.reps} Reps"
                )
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .fillMaxWidth()
            .testTag("elegant_progress_chart_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SENSEI TREND ENGINE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    ),
                    color = VoltLime
                )
                
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Chart",
                    tint = VoltLime,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Custom Segmented Control for Tab Choice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .padding(2.dp)
            ) {
                listOf("Body Weight", "Exercise PRs").forEachIndexed { index, title ->
                    val isTabSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isTabSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { selectedTab = index }
                            .padding(vertical = 8.dp)
                            .testTag("chart_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.Scale else Icons.Default.EmojiEvents,
                                contentDescription = title,
                                tint = if (isTabSelected) Color.Black else Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = title.uppercase(Locale.getDefault()),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                color = if (isTabSelected) Color.Black else Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // PR Exercise Selection Dropdown
            if (selectedTab == 1) {
                if (prExercises.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            onClick = { showExerciseDropdown = !showExerciseDropdown },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("exercise_chart_dropdown_trigger")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedExercise,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = VoltLime
                                )
                            }
                        }
                        
                        DropdownMenu(
                            expanded = showExerciseDropdown,
                            onDismissRequest = { showExerciseDropdown = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            prExercises.forEach { exercise ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = exercise,
                                            fontWeight = FontWeight.Bold,
                                            color = if (exercise == selectedExercise) VoltLime else Color.White
                                        )
                                    },
                                    onClick = {
                                        selectedExercise = exercise
                                        showExerciseDropdown = false
                                    },
                                    modifier = Modifier.testTag("chart_dropdown_item_${exercise.replace(" ", "_")}")
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No PR logs registered yet. Log exercises marked as PR to unlock!",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            // Render Chart
            if (chartData.size >= 2) {
                var selectedPointIndex by remember(chartData) { mutableStateOf<Int?>(null) }
                
                // Highlight the last element by default if nothing is selected
                val activeIndex = selectedPointIndex ?: (chartData.size - 1)
                val activePoint = chartData.getOrNull(activeIndex)

                // Tooltip info section above chart
                if (activePoint != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = activePoint.dateFull,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryDark
                            )
                            Text(
                                text = if (selectedTab == 0) "Body Weight" else selectedExercise,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${activePoint.value} kg",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = VoltLime
                            )
                            if (activePoint.extraInfo != null) {
                                Text(
                                    text = activePoint.extraInfo,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextSecondaryDark
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Interactive Line Chart Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(chartData) {
                                detectTapGestures { offset ->
                                    // Calculate closest node to tap
                                    val width = size.width
                                    val pointsCount = chartData.size
                                    val interval = width / (pointsCount - 1)
                                    
                                    var closestIndex = 0
                                    var minDiff = Float.MAX_VALUE
                                    
                                    for (i in 0 until pointsCount) {
                                        val nodeX = i * interval
                                        val diff = kotlin.math.abs(offset.x - nodeX)
                                        if (diff < minDiff) {
                                            minDiff = diff
                                            closestIndex = i
                                        }
                                    }
                                    
                                    selectedPointIndex = closestIndex
                                }
                            }
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        
                        // Extract min and max values
                        val values = chartData.map { it.value }
                        val minVal = (values.minOrNull() ?: 0.0)
                        val maxVal = (values.maxOrNull() ?: 100.0)
                        
                        // Add some head and tail room to values
                        val rangeDiff = maxVal - minVal
                        val padding = if (rangeDiff == 0.0) 5.0 else rangeDiff * 0.2
                        val lowerBound = (minVal - padding).coerceAtLeast(0.0)
                        val upperBound = maxVal + padding
                        
                        val pointsCount = chartData.size
                        val intervalX = canvasWidth / (pointsCount - 1)
                        
                        // Helper to calculate Y position
                        fun getY(value: Double): Float {
                            val ratio = (value - lowerBound) / (upperBound - lowerBound)
                            return (canvasHeight - (ratio * canvasHeight)).toFloat()
                        }
                        
                        // 1. Draw horizontal grid dashed lines (3 lines)
                        val gridLineCount = 3
                        val gridIntervalY = canvasHeight / (gridLineCount + 1)
                        for (i in 1..gridLineCount) {
                            val y = i * gridIntervalY
                            drawLine(
                                color = BorderDark.copy(alpha = 0.5f),
                                start = Offset(0f, y),
                                end = Offset(canvasWidth, y),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }
                        
                        // 2. Build line path
                        val linePath = Path().apply {
                            val firstX = 0f
                            val firstY = getY(chartData[0].value)
                            moveTo(firstX, firstY)
                            
                            for (i in 1 until pointsCount) {
                                val x = i * intervalX
                                val y = getY(chartData[i].value)
                                
                                // Beautiful smooth cubic Bezier curve
                                val prevX = (i - 1) * intervalX
                                val prevY = getY(chartData[i - 1].value)
                                val controlPointX1 = prevX + (intervalX / 2f)
                                val controlPointY1 = prevY
                                val controlPointX2 = prevX + (intervalX / 2f)
                                val controlPointY2 = y
                                
                                cubicTo(controlPointX1, controlPointY1, controlPointX2, controlPointY2, x, y)
                            }
                        }
                        
                        // 3. Build closed path for vertical gradient fill under the curve
                        val fillPath = Path().apply {
                            addPath(linePath)
                            lineTo(canvasWidth, canvasHeight)
                            lineTo(0f, canvasHeight)
                            close()
                        }
                        
                        // 4. Draw area gradient fill
                        val fillBrush = Brush.verticalGradient(
                            colors = listOf(
                                VoltLime.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                        drawPath(path = fillPath, brush = fillBrush)
                        
                        // 5. Draw smooth trend line
                        drawPath(
                            path = linePath,
                            color = VoltLime,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                        
                        // 6. Draw vertical guide line if clicked
                        if (activeIndex < pointsCount) {
                            val activeX = activeIndex * intervalX
                            val activeY = getY(chartData[activeIndex].value)
                            
                            drawLine(
                                color = VoltLime.copy(alpha = 0.4f),
                                start = Offset(activeX, 0f),
                                end = Offset(activeX, canvasHeight),
                                strokeWidth = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                            )
                            
                            // Glowing point node outer halo
                            drawCircle(
                                color = VoltLime.copy(alpha = 0.3f),
                                radius = 12.dp.toPx(),
                                center = Offset(activeX, activeY)
                            )
                            
                            // Glowing point node inner circle
                            drawCircle(
                                color = VoltLime,
                                radius = 6.dp.toPx(),
                                center = Offset(activeX, activeY)
                            )
                            
                            // Glowing point node center core
                            drawCircle(
                                color = Color.Black,
                                radius = 3.dp.toPx(),
                                center = Offset(activeX, activeY)
                            )
                        }
                        
                        // 7. Draw normal nodes for other points (unselected)
                        for (i in 0 until pointsCount) {
                            if (i != activeIndex) {
                                val x = i * intervalX
                                val y = getY(chartData[i].value)
                                drawCircle(
                                    color = VoltLime.copy(alpha = 0.7f),
                                    radius = 4.dp.toPx(),
                                    center = Offset(x, y)
                                )
                                drawCircle(
                                    color = Color.Black,
                                    radius = 2.dp.toPx(),
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // X-Axis Labels (Timeline ticks)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    chartData.forEachIndexed { index, point ->
                        val align = when (index) {
                            0 -> TextAlign.Start
                            chartData.size - 1 -> TextAlign.End
                            else -> TextAlign.Center
                        }
                        Text(
                            text = point.label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextSecondaryDark,
                            modifier = Modifier.weight(1f),
                            textAlign = align
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "TAP ANY NODE TO TRACK PROGRESS DETAILS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = VoltLime.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
            } else {
                // Not enough data fallback view (2 points required to draw a line)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Scale,
                            contentDescription = "Stats needed",
                            tint = VoltLime.copy(alpha = 0.4f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Awaiting Trend Metrics...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Please log your stats for at least 2 separate days to chart progression curves.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

data class ChartPoint(
    val value: Double,
    val label: String,
    val dateFull: String,
    val extraInfo: String? = null
)
