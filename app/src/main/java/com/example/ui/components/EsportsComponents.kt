package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = ElectricBlue.copy(alpha = 0.2f),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(EsportsSurface.copy(alpha = 0.85f))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun EsportsButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    testTag: String = ""
) {
    val gradient = if (isPrimary) {
        Brush.horizontalGradient(listOf(ElectricBlue, CyberPurple))
    } else {
        Brush.horizontalGradient(listOf(EsportsSurfaceVariant, EsportsSurfaceVariant))
    }

    Box(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .background(gradient)
            .clickable(onClick = onClick)
            .then(
                if (!isPrimary) Modifier.border(
                    1.dp,
                    ElectricBlue.copy(alpha = 0.5f),
                    RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .padding(vertical = 14.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isPrimary) Color.White else ElectricBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TournamentStatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status) {
        "LIVE" -> Triple(EsportsRed.copy(alpha = 0.15f), EsportsRed, "LIVE")
        "COMPLETED" -> Triple(EsportsGold.copy(alpha = 0.15f), EsportsGold, "FINISHED")
        else -> Triple(ElectricBlue.copy(alpha = 0.15f), ElectricBlue, "UPCOMING")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        if (status == "LIVE") {
            val infiniteTransition = rememberInfiniteTransition(label = "pulsate")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(EsportsRed.copy(alpha = alpha))
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EsportsCountdownTimer(matchTime: Long, onTimeUp: () -> Unit = {}) {
    var timeLeft by remember { mutableLongStateOf(matchTime - System.currentTimeMillis()) }

    LaunchedEffect(matchTime) {
        while (timeLeft > 0) {
            kotlinx.coroutines.delay(1000)
            timeLeft = matchTime - System.currentTimeMillis()
        }
        onTimeUp()
    }

    val displayStr = if (timeLeft <= 0) {
        "MATCH STARTED"
    } else {
        val seconds = (timeLeft / 1000) % 60
        val minutes = (timeLeft / (1000 * 60)) % 60
        val hours = (timeLeft / (1000 * 60 * 60)) % 24
        val days = timeLeft / (1000 * 60 * 60 * 24)

        if (days > 0) {
            "${days}d ${hours}h ${minutes}m"
        } else {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(EsportsSurfaceVariant)
            .border(0.5.dp, EsportsGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "⏰ $displayStr",
            color = EsportsGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PerformanceChart(
    modifier: Modifier = Modifier,
    points: List<Float> = listOf(2f, 5f, 3f, 8f, 6f, 11f, 9f),
    labels: List<String> = listOf("M1", "M2", "M3", "M4", "M5", "M6", "M7")
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val maxPoint = points.maxOrNull() ?: 1f
        val padding = 30f

        val path = Path()
        val stepX = (width - padding * 2) / (points.size - 1)

        points.forEachIndexed { idx, point ->
            val cx = padding + idx * stepX
            val cy = height - padding - (point / maxPoint) * (height - padding * 2)

            if (idx == 0) {
                path.moveTo(cx, cy)
            } else {
                path.lineTo(cx, cy)
            }
        }

        // Draw ambient fill under the line chart
        val fillPath = Path().apply {
            addPath(path)
            lineTo(padding + (points.size - 1) * stepX, height - padding)
            lineTo(padding, height - padding)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(ElectricBlue.copy(alpha = 0.3f), Color.Transparent)
            )
        )

        // Draw line stroke
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(listOf(ElectricBlue, CyberPurple)),
            style = Stroke(width = 6f)
        )

        // Draw joint points
        points.forEachIndexed { idx, point ->
            val cx = padding + idx * stepX
            val cy = height - padding - (point / maxPoint) * (height - padding * 2)

            drawCircle(
                color = EsportsGold,
                radius = 8f,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = EsportsBackground,
                radius = 4f,
                center = Offset(cx, cy)
            )
        }
    }
}

@Composable
fun SkeletonLoader(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(EsportsSurfaceVariant.copy(alpha = alpha))
    )
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
