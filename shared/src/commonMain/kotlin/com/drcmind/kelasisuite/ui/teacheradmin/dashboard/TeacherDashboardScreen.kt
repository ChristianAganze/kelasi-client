package com.drcmind.kelasisuite.ui.teacheradmin.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.drcmind.kelasisuite.data.datasource.remote.dto.ScheduleEntryDto
import com.drcmind.kelasisuite.domain.util.toFrench
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.EmptyStateCard
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import com.drcmind.kelasisuite.ui.components.LoadingState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherDashboardScreen(
    modifier: Modifier = Modifier,
    onNavigate: (NavKey) -> Unit = {},
    viewModel: TeacherDashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpanded = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DashboardHeader(state = state)
        }

        when {
            state.isLoading -> {
                item { LoadingState(modifier = Modifier.fillMaxWidth().height(200.dp)) }
            }

            state.errorMessage != null -> {
                item {
                    ErrorStateCard(
                        message = state.errorMessage,
                        onRetry = viewModel::retry
                    )
                }
            }

            else -> {
                item { NextClassHeroCard(state = state) }

                item {
                    Text(
                        text = "En un coup d'œil",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item { StatsGrid(state = state, isExpanded = isExpanded) }

                // ---------------------------------------------------------------------------------
                // GDE & PEDAGOGICAL ANALYTICS CHARTS SECTION
                // ---------------------------------------------------------------------------------
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QueryStats,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Analytique GDE & Performance Pédagogique",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "Survolez / touchez les graphiques",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                item {
                    GdeChartsInteractiveSection(
                        state = state,
                        onNavigate = onNavigate
                    )
                }

                item {
                    CurriculumAndWorkloadSection(
                        state = state,
                        onNavigate = onNavigate
                    )
                }

                // ---------------------------------------------------------------------------------
                // PROGRAMME DU JOUR & HORAIRES
                // ---------------------------------------------------------------------------------
                item {
                    Text(
                        text = "Programme du jour",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (state.todaySchedule.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "Aucun cours aujourd'hui",
                            subtitle = "Votre journée est libre. Profitez-en pour préparer vos prochaines leçons."
                        )
                    }
                } else {
                    items(state.todaySchedule) { entry ->
                        TodayEntryCard(entry = entry)
                    }
                }

                if (state.upcomingThisWeek.isNotEmpty()) {
                    item {
                        Text(
                            text = "À venir cette semaine",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(state.upcomingThisWeek) { entry ->
                        UpcomingCourseCard(entry = entry)
                    }
                }

                item {
                    Text(
                        text = "Radar & Alertes Enseignant",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item { AlertsCard(alerts = state.alerts) }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// HEADER & HERO CARD
// -------------------------------------------------------------------------------------------------
@Composable
private fun DashboardHeader(state: TeacherDashboardState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Bonjour, ${state.username}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.dateLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Semaine ${state.weekNumber}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun NextClassHeroCard(state: TeacherDashboardState) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Prochain cours",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.nextClass,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (state.nextClassTime.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = state.nextClassTime,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

private data class StatItem(
    val icon: ImageVector,
    val value: Int,
    val label: String
)

@Composable
private fun StatsGrid(state: TeacherDashboardState, isExpanded: Boolean) {
    val stats = listOf(
        StatItem(
            icon = Icons.Default.Schedule,
            value = state.todayClassesCount,
            label = "Cours aujourd'hui"
        ),
        StatItem(
            icon = Icons.AutoMirrored.Filled.Notes,
            value = state.pendingClassLogs,
            label = "Journaux à saisir"
        ),
        StatItem(
            icon = Icons.Default.Numbers,
            value = state.pendingEvaluations,
            label = "Cotes à saisir"
        ),
        StatItem(
            icon = Icons.Default.WorkspacePremium,
            value = state.pendingPreparations,
            label = "Préparations en attente"
        ),
        StatItem(
            icon = Icons.Default.CheckCircle,
            value = state.approvedPreparations,
            label = "Préparations approuvées"
        ),
        StatItem(
            icon = Icons.Default.Mail,
            value = state.unreadMessages,
            label = "Messages non lus"
        )
    )

    val perRow = if (isExpanded) 4 else 2
    stats.chunked(perRow).forEach { rowStats ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rowStats.forEach { item ->
                StatCard(item = item, modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun StatCard(item: StatItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = item.value.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 1. GDE INTERACTIVE CHARTS SECTION (BAR/SPLINE & DONUT BREAKDOWN)
// -------------------------------------------------------------------------------------------------
@Composable
private fun GdeChartsInteractiveSection(
    state: TeacherDashboardState,
    onNavigate: (NavKey) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isWide = maxWidth > 850.dp
        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GdePerformanceChartCard(
                    modifier = Modifier.weight(1.5f),
                    metrics = state.gdePerformanceList,
                    averageSuccessRate = state.averageSuccessRate,
                    onNavigate = onNavigate
                )
                GdeDistributionDonutCard(
                    modifier = Modifier.weight(1.1f),
                    tiers = state.gdeDistributionTiers,
                    totalStudents = state.totalEvaluatedStudents,
                    onNavigate = onNavigate
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                GdePerformanceChartCard(
                    modifier = Modifier.fillMaxWidth(),
                    metrics = state.gdePerformanceList,
                    averageSuccessRate = state.averageSuccessRate,
                    onNavigate = onNavigate
                )
                GdeDistributionDonutCard(
                    modifier = Modifier.fillMaxWidth(),
                    tiers = state.gdeDistributionTiers,
                    totalStudents = state.totalEvaluatedStudents,
                    onNavigate = onNavigate
                )
            }
        }
    }
}

/**
 * Interactive GDE Evaluation Performance Chart (Bar + Benchmark line + Cursor Hover Tooltips)
 */
@Composable
private fun GdePerformanceChartCard(
    modifier: Modifier = Modifier,
    metrics: List<GdeClassPerformanceMetric>,
    averageSuccessRate: Double,
    onNavigate: (NavKey) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceContainer = MaterialTheme.colorScheme.surfaceContainer
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant

    // Pointer hover / selection state
    var hoveredIndex by remember { mutableStateOf<Int?>(0) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainer),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(outlineVariant.copy(alpha = 0.4f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = primaryColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "GDE • Moyennes & Réussite par Classe",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Moyenne générale : ${averageSuccessRate}% • Périodes en cours",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                FilledTonalButton(
                    onClick = { onNavigate(Route.TeacherAdmin.Evaluations) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Grilles GDE", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            HorizontalDivider(color = outlineVariant.copy(alpha = 0.3f))

            // Canvas Bar Chart with Hover / Cursor / Drag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .pointerInput(metrics) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull()
                                if (change != null) {
                                    val x = change.position.x
                                    val width = size.width.toFloat()
                                    val count = metrics.size.coerceAtLeast(1)
                                    val slotWidth = width / count
                                    val index = (x / slotWidth).toInt().coerceIn(0, metrics.size - 1)
                                    hoveredIndex = index
                                }
                            }
                        }
                    }
                    .pointerInput(metrics) {
                        detectTapGestures { offset ->
                            val width = size.width.toFloat()
                            val count = metrics.size.coerceAtLeast(1)
                            val slotWidth = width / count
                            val index = (offset.x / slotWidth).toInt().coerceIn(0, metrics.size - 1)
                            hoveredIndex = index
                        }
                    }
                    .pointerInput(metrics) {
                        detectDragGestures { change, _ ->
                            val width = size.width.toFloat()
                            val count = metrics.size.coerceAtLeast(1)
                            val slotWidth = width / count
                            val index = (change.position.x / slotWidth).toInt().coerceIn(0, metrics.size - 1)
                            hoveredIndex = index
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height - 28.dp.toPx()
                    val bottomY = height
                    val count = metrics.size.coerceAtLeast(1)
                    val slotWidth = width / count
                    val barWidth = (slotWidth * 0.46f).coerceIn(24.dp.toPx(), 44.dp.toPx())
                    val maxScore = 20.0

                    // 1. Draw horizontal grid benchmarks (0, 5, 10, 15, 20/20)
                    val gridSteps = 4
                    for (i in 0..gridSteps) {
                        val y = bottomY - (bottomY / gridSteps) * i
                        val scoreVal = (20 / gridSteps) * i
                        drawLine(
                            color = Color.Gray.copy(alpha = if (scoreVal == 10) 0.35f else 0.12f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = if (scoreVal == 10) 1.5.dp.toPx() else 1.dp.toPx(),
                            pathEffect = if (scoreVal == 10) PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f) else null
                        )
                    }

                    // 2. Draw Bars and Hover effect
                    metrics.forEachIndexed { index, m ->
                        val centerX = index * slotWidth + slotWidth / 2f
                        val isHovered = hoveredIndex == index
                        val barHeight = ((m.averageScore / maxScore) * bottomY).toFloat().coerceAtLeast(8.dp.toPx())
                        val topY = bottomY - barHeight

                        // Background column slot highlight if hovered
                        if (isHovered) {
                            drawRoundRect(
                                color = primaryColor.copy(alpha = 0.08f),
                                topLeft = Offset(index * slotWidth + 4.dp.toPx(), 0f),
                                size = Size(slotWidth - 8.dp.toPx(), bottomY),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                        }

                        // Gradient bar
                        val barGradient = Brush.verticalGradient(
                            colors = if (isHovered) {
                                listOf(
                                    Color(0xFF3B82F6),
                                    Color(0xFF1D4ED8)
                                )
                            } else {
                                listOf(
                                    primaryColor.copy(alpha = 0.9f),
                                    primaryColor.copy(alpha = 0.65f)
                                )
                            },
                            startY = topY,
                            endY = bottomY
                        )

                        drawRoundRect(
                            brush = barGradient,
                            topLeft = Offset(centerX - barWidth / 2f, topY),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )

                        // If hovered, draw glowing top cap dot
                        if (isHovered) {
                            drawCircle(
                                color = Color.White,
                                radius = 4.5.dp.toPx(),
                                center = Offset(centerX, topY)
                            )
                            drawCircle(
                                color = Color(0xFF3B82F6),
                                radius = 3.dp.toPx(),
                                center = Offset(centerX, topY)
                            )
                        }
                    }
                }

                // X-Axis Labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    metrics.forEachIndexed { index, m ->
                        val isHovered = hoveredIndex == index
                        Text(
                            text = m.className,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isHovered) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (isHovered) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Interactive Tooltip / Detail Inspector Card for Hovered Bar
            val selectedMetric = hoveredIndex?.let { metrics.getOrNull(it) } ?: metrics.firstOrNull()
            if (selectedMetric != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(primaryColor.copy(alpha = 0.3f))
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1.4f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = primaryColor,
                                    modifier = Modifier.size(8.dp)
                                ) {}
                                Text(
                                    text = "${selectedMetric.className} • ${selectedMetric.subjectName}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Évalués : ${selectedMetric.totalStudentsEvaluated} élèves • Notes de ${selectedMetric.minScore} à ${selectedMetric.maxScore}/20",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier.height(36.dp).padding(horizontal = 8.dp),
                            color = outlineVariant.copy(alpha = 0.4f)
                        )

                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${selectedMetric.averageScore}/20",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = primaryColor
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (selectedMetric.trendPercentage >= 0) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (selectedMetric.trendPercentage >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                            contentDescription = null,
                                            tint = if (selectedMetric.trendPercentage >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "${if (selectedMetric.trendPercentage >= 0) "+" else ""}${selectedMetric.trendPercentage}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedMetric.trendPercentage >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Réussite : ${selectedMetric.passRatePercentage}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Interactive GDE Student Distribution Donut Chart (Breakdown by score brackets + cursor hover details)
 */
@Composable
private fun GdeDistributionDonutCard(
    modifier: Modifier = Modifier,
    tiers: List<GdeDistributionTier>,
    totalStudents: Int,
    onNavigate: (NavKey) -> Unit
) {
    var hoveredTierIndex by remember { mutableStateOf<Int?>(0) }
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(outlineVariant.copy(alpha = 0.4f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Distribution des Niveaux GDE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Répartition sur l'ensemble de vos élèves",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "$totalStudents élèves",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = outlineVariant.copy(alpha = 0.3f))

            // Donut Canvas + Live Center Stat
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Donut with hover detection
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .pointerInput(tiers) {
                            detectTapGestures { offset ->
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val angle = (kotlin.math.atan2(offset.y - center.y, offset.x - center.x) * 180f / kotlin.math.PI.toFloat() + 360f + 90f) % 360f
                                var currentAngle = 0f
                                tiers.forEachIndexed { index, tier ->
                                    val sweep = (tier.percentage / 100f).toFloat() * 360f
                                    if (angle >= currentAngle && angle <= currentAngle + sweep) {
                                        hoveredTierIndex = index
                                    }
                                    currentAngle += sweep
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(130.dp)) {
                        val strokeWidth = 18.dp.toPx()
                        val hoveredStrokeWidth = 24.dp.toPx()
                        var startAngle = -90f

                        tiers.forEachIndexed { index, tier ->
                            val sweepAngle = ((tier.percentage / 100f).toFloat() * 360f)
                            val isHovered = hoveredTierIndex == index
                            val color = Color(tier.colorHex)

                            drawArc(
                                color = if (isHovered) color else color.copy(alpha = 0.85f),
                                startAngle = startAngle,
                                sweepAngle = sweepAngle - 3f,
                                useCenter = false,
                                style = Stroke(
                                    width = if (isHovered) hoveredStrokeWidth else strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            )
                            startAngle += sweepAngle
                        }
                    }

                    // Dynamic Center Info
                    val currentSelected = hoveredTierIndex?.let { tiers.getOrNull(it) } ?: tiers.firstOrNull()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${currentSelected?.studentCount ?: totalStudents}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${currentSelected?.percentage?.toInt() ?: 100}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = currentSelected?.let { Color(it.colorHex) } ?: MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Interactive Legend Items (clickable / hoverable)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tiers.forEachIndexed { index, tier ->
                        val isHovered = hoveredTierIndex == index
                        val color = Color(tier.colorHex)

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isHovered) color.copy(alpha = 0.15f) else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { hoveredTierIndex = index }
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            if (event.type == PointerEventType.Move || event.type == PointerEventType.Enter) {
                                                hoveredTierIndex = index
                                            }
                                        }
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(if (isHovered) 10.dp else 8.dp)
                                            .background(color, CircleShape)
                                    )
                                    Text(
                                        text = tier.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isHovered) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "${tier.studentCount} (${tier.percentage.toInt()}%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHovered) color else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Pedagogical Action Insight Box for the Selected Tier
            val activeTier = hoveredTierIndex?.let { tiers.getOrNull(it) } ?: tiers.firstOrNull()
            if (activeTier != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(activeTier.colorHex).copy(alpha = 0.1f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(Color(activeTier.colorHex).copy(alpha = 0.3f))
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(activeTier.colorHex),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${activeTier.rangeLabel} : ${activeTier.description}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 2. RADAR D'AVANCEMENT PÉDAGOGIQUE & CHARGE HEBDOMADAIRE SECTION
// -------------------------------------------------------------------------------------------------
@Composable
private fun CurriculumAndWorkloadSection(
    state: TeacherDashboardState,
    onNavigate: (NavKey) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isWide = maxWidth > 850.dp
        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CurriculumProgressionRadarCard(
                    modifier = Modifier.weight(1.4f),
                    curriculum = state.curriculumProgressionList,
                    onNavigate = onNavigate
                )
                WeeklyWorkloadChartCard(
                    modifier = Modifier.weight(1.1f),
                    workload = state.weeklyWorkload
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CurriculumProgressionRadarCard(
                    modifier = Modifier.fillMaxWidth(),
                    curriculum = state.curriculumProgressionList,
                    onNavigate = onNavigate
                )
                WeeklyWorkloadChartCard(
                    modifier = Modifier.fillMaxWidth(),
                    workload = state.weeklyWorkload
                )
            }
        }
    }
}

/**
 * Radar & Progression of National Curriculum per Class
 */
@Composable
private fun CurriculumProgressionRadarCard(
    modifier: Modifier = Modifier,
    curriculum: List<CurriculumProgressionMetric>,
    onNavigate: (NavKey) -> Unit
) {
    var selectedCurriculumIndex by remember { mutableStateOf<Int?>(0) }
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(outlineVariant.copy(alpha = 0.4f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Avancement des Programmes & Fiches",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Progression des leçons vs calendrier officiel",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                FilledTonalButton(
                    onClick = { onNavigate(Route.TeacherAdmin.Pedagogy) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Fiches Préparations", style = MaterialTheme.typography.labelSmall)
                }
            }

            HorizontalDivider(color = outlineVariant.copy(alpha = 0.3f))

            // Progression List with hover / click inspection
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                curriculum.forEachIndexed { index, item ->
                    val isSelected = selectedCurriculumIndex == index
                    val statusColor = when (item.status) {
                        "AHEAD" -> Color(0xFF10B981) // Green
                        "ON_TRACK" -> MaterialTheme.colorScheme.primary // Blue
                        else -> Color(0xFFEF4444) // Red
                    }
                    val statusLabel = when (item.status) {
                        "AHEAD" -> "En avance"
                        "ON_TRACK" -> "Dans les temps"
                        else -> "À rattraper"
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.surfaceContainerHighest else Color.Transparent,
                        border = if (isSelected) {
                            CardDefaults.outlinedCardBorder().copy(brush = SolidColor(statusColor.copy(alpha = 0.5f)))
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedCurriculumIndex = index }
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        if (event.type == PointerEventType.Move || event.type == PointerEventType.Enter) {
                                            selectedCurriculumIndex = index
                                        }
                                    }
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.className} • ${item.subjectName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = statusColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = statusLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Progress Bar
                            LinearProgressIndicator(
                                progress = { (item.completionPercentage / 100f).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = statusColor,
                                trackColor = statusColor.copy(alpha = 0.15f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.completedLessons}/${item.totalLessons} leçons (${item.hoursDispensed}h dispensées)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${item.completionPercentage.toInt()}% (Attendu: ${item.targetPercentage.toInt()}%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.completionPercentage >= item.targetPercentage) Color(0xFF10B981) else Color(0xFFEF4444)
                                )
                            }
                        }
                    }
                }
            }

            // Expanded Lesson Inspector Tooltip Box
            val selectedItem = selectedCurriculumIndex?.let { curriculum.getOrNull(it) } ?: curriculum.firstOrNull()
            if (selectedItem != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Détail Pédagogique Actuel",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "• ${selectedItem.currentChapter}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "• Prochaine leçon : ${selectedItem.nextLesson}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Weekly Teaching Workload Distribution Interactive Chart
 */
@Composable
private fun WeeklyWorkloadChartCard(
    modifier: Modifier = Modifier,
    workload: List<WeeklyTeachingWorkloadMetric>
) {
    var hoveredDayIndex by remember { mutableStateOf<Int?>(0) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(outlineVariant.copy(alpha = 0.4f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Charge Horaire Hebdomadaire",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Volume de cours par jour de la semaine",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ) {
                    val totalHours = workload.sumOf { it.hoursPlanned }
                    Text(
                        text = "${totalHours.toInt()}h / semaine",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = outlineVariant.copy(alpha = 0.3f))

            // Workload Bar Canvas with hover / drag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .pointerInput(workload) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull()
                                if (change != null) {
                                    val count = workload.size.coerceAtLeast(1)
                                    val slotWidth = size.width.toFloat() / count
                                    val index = (change.position.x / slotWidth).toInt().coerceIn(0, workload.size - 1)
                                    hoveredDayIndex = index
                                }
                            }
                        }
                    }
                    .pointerInput(workload) {
                        detectTapGestures { offset ->
                            val count = workload.size.coerceAtLeast(1)
                            val slotWidth = size.width.toFloat() / count
                            val index = (offset.x / slotWidth).toInt().coerceIn(0, workload.size - 1)
                            hoveredDayIndex = index
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height - 24.dp.toPx()
                    val count = workload.size.coerceAtLeast(1)
                    val slotWidth = width / count
                    val barWidth = (slotWidth * 0.5f).coerceIn(16.dp.toPx(), 32.dp.toPx())
                    val maxHours = 6.0

                    workload.forEachIndexed { index, w ->
                        val centerX = index * slotWidth + slotWidth / 2f
                        val isHovered = hoveredDayIndex == index
                        val barHeight = ((w.hoursPlanned / maxHours) * height).toFloat().coerceAtLeast(6.dp.toPx())
                        val topY = height - barHeight

                        if (isHovered) {
                            drawRoundRect(
                                color = primaryColor.copy(alpha = 0.1f),
                                topLeft = Offset(index * slotWidth + 2.dp.toPx(), 0f),
                                size = Size(slotWidth - 4.dp.toPx(), height),
                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                            )
                        }

                        val barBrush = Brush.verticalGradient(
                            colors = if (isHovered) {
                                listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
                            } else {
                                listOf(Color(0xFF8B5CF6).copy(alpha = 0.8f), Color(0xFF8B5CF6).copy(alpha = 0.5f))
                            },
                            startY = topY,
                            endY = height
                        )

                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(centerX - barWidth / 2f, topY),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                }

                // X-Axis Day Labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    workload.forEachIndexed { index, w ->
                        val isHovered = hoveredDayIndex == index
                        Text(
                            text = w.dayLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isHovered) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (isHovered) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Hovered Day Tooltip Detail
            val currentDay = hoveredDayIndex?.let { workload.getOrNull(it) } ?: workload.firstOrNull()
            if (currentDay != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF8B5CF6).copy(alpha = 0.1f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(Color(0xFF8B5CF6).copy(alpha = 0.3f))
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${currentDay.dayLabel} : ${currentDay.classesDescription}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "${currentDay.hoursPlanned.toInt()} heures",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF8B5CF6)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 3. PROGRAMME DU JOUR & ALERTES CARDS
// -------------------------------------------------------------------------------------------------
@Composable
private fun TodayEntryCard(entry: ScheduleEntryDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.width(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = entry.startDayHourTime.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.endDayHourTime.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            VerticalDivider(modifier = Modifier.height(36.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.subjectName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${entry.schoolClassName} • ${entry.teacherName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UpcomingCourseCard(entry: ScheduleEntryDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text(
                    text = entry.dayOfWeek.toFrench(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.subjectName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${entry.schoolClassName} • ${entry.startDayHourTime} - ${entry.endDayHourTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AlertsCard(alerts: List<DashboardAlert>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            alerts.forEach { alert ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = alert.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
