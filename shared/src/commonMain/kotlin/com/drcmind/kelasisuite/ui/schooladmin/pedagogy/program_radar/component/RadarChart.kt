package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.program_radar.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.program_radar.SubjectCoverageUi
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun RadarChart(
    subjects: List<SubjectCoverageUi>,
    modifier: Modifier = Modifier
) {
    if (subjects.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    val nationalColor = MaterialTheme.colorScheme.primary
    val realizedColor = MaterialTheme.colorScheme.tertiary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val labelStyle = TextStyle(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
    )

    val count = subjects.size
    val angleStep = 360f / count

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) / 2f - 52.dp.toPx()
        val startAngle = -90f

        fun pointFor(angleDeg: Float, radial: Float): Offset {
            val rad = (angleDeg * kotlin.math.PI / 180.0)
            return Offset(
                x = center.x + (radial * cos(rad.toFloat())),
                y = center.y + (radial * sin(rad.toFloat()))
            )
        }

        val ringCount = 5
        for (ring in 0..ringCount) {
            val ringRadius = radius * ring / ringCount
            val ringPath = Path()
            for (i in 0 until count) {
                val point = pointFor(startAngle + i * angleStep, ringRadius)
                if (i == 0) ringPath.moveTo(point.x, point.y) else ringPath.lineTo(point.x, point.y)
            }
            ringPath.close()
            drawPath(
                path = ringPath,
                color = gridColor,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        for (i in 0 until count) {
            val edge = pointFor(startAngle + i * angleStep, radius)
            drawLine(
                color = gridColor,
                start = center,
                end = edge,
                strokeWidth = 1.dp.toPx()
            )
        }

        fun polygonPath(values: List<Float>): Path {
            val path = Path()
            for (i in 0 until count) {
                val value = values[i].coerceIn(0f, 1f)
                val point = pointFor(startAngle + i * angleStep, radius * value)
                if (i == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
            }
            path.close()
            return path
        }

        val nationalPath = polygonPath(subjects.map { it.nationalTarget })
        drawPath(
            path = nationalPath,
            color = nationalColor.copy(alpha = 0.12f),
            style = Fill
        )
        drawPath(
            path = nationalPath,
            color = nationalColor,
            style = Stroke(width = 2.dp.toPx())
        )

        val realizedPath = polygonPath(subjects.map { it.realized })
        drawPath(
            path = realizedPath,
            color = realizedColor.copy(alpha = 0.18f),
            style = Fill
        )
        drawPath(
            path = realizedPath,
            color = realizedColor,
            style = Stroke(width = 2.5.dp.toPx())
        )

        for (i in 0 until count) {
            val realizedValue = subjects[i].realized.coerceIn(0f, 1f)
            val vertex = pointFor(startAngle + i * angleStep, radius * realizedValue)
            drawCircle(
                color = realizedColor,
                radius = 3.5.dp.toPx(),
                center = vertex
            )
        }

        for (i in 0 until count) {
            val label = subjects[i].subjectName
            val layout = textMeasurer.measure(AnnotatedString(label), labelStyle)
            val labelPosition = pointFor(startAngle + i * angleStep, radius + 20.dp.toPx())
            drawText(
                textLayoutResult = layout,
                topLeft = Offset(
                    x = labelPosition.x - layout.size.width / 2f,
                    y = labelPosition.y - layout.size.height / 2f
                )
            )
        }
    }
}

@Composable
fun RadarChartLegend(
    nationalLabel: String = "Programme national",
    realizedLabel: String = "Réalisé",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(
            color = MaterialTheme.colorScheme.primary,
            label = nationalLabel
        )
        Spacer(modifier = Modifier.width(24.dp))
        LegendItem(
            color = MaterialTheme.colorScheme.tertiary,
            label = realizedLabel
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        ) {}
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun RadarChartPlaceholder(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
