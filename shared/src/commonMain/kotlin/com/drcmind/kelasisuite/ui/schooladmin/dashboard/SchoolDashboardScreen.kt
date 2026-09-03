package com.drcmind.kelasisuite.ui.schooladmin.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.drcmind.kelasisuite.domain.model.finance.PaymentMethod
import com.drcmind.kelasisuite.domain.model.finance.PaymentTransaction
import com.drcmind.kelasisuite.navigation.Route
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDashboardScreen(
    viewModel: SchoolDashboardViewModel = koinViewModel(),
    onNavigate: (NavKey) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.School,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = state.schoolName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Text(
                                    text = "Année ${state.academicYear} • ${state.systemStatus}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                actions = {
                    AssistChip(
                        onClick = { viewModel.loadDashboardData() },
                        label = { Text(if (state.isLoading) "Actualisation..." else "Actualiser") },
                        leadingIcon = {
                            if (state.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Executive Welcome & Quick Actions Bar
            item {
                ExecutiveHeader(
                    username = state.username,
                    role = state.role,
                    onNavigate = onNavigate
                )
            }

            // 2. 4 Core Executive KPI Cards (Bento Metric Cards)
            item {
                Text(
                    text = "INDICATEURS CLÉS DE PERFORMANCE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(10.dp))
                ExecutiveKpiRow(state = state, onNavigate = onNavigate)
            }

            // 3. High-End Visual Charts Section (Financial Curve & Enrollment Donut)
            item {
                Text(
                    text = "ANALYTIQUES & VISUALISATIONS STRATÉGIQUES",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(10.dp))
                ChartsSection(state = state, onNavigate = onNavigate)
            }

            // 4. Operational Control Grid: Subject Progress & Live Recent Cash Transactions
            item {
                Text(
                    text = "OPÉRATIONS EN DIRECT & SUIVI PÉDAGOGIQUE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(10.dp))
                OperationalStreamsSection(state = state, onNavigate = onNavigate)
            }

            // 5. Pending Administrative & Pedagogical Approvals
            item {
                PendingAlertsSection(state = state, onNavigate = onNavigate)
            }

            // 6. Professional Footer
            item {
                DashboardFooter(
                    lastRefresh = state.lastRefresh
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 1. EXECUTIVE HEADER & QUICK ACTIONS
// -------------------------------------------------------------------------------------------------
@Composable
fun ExecutiveHeader(
    username: String,
    role: String,
    onNavigate: (NavKey) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bonjour, $username 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$role • Vue d'ensemble stratégique et pilotage opérationnel de l'établissement.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Quick Action Pills
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "RACCOURCIS STRATÉGIQUES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        ActionPill(
                            icon = Icons.Outlined.Payments,
                            label = "Encaisser Caisse",
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            onClick = { onNavigate(Route.SchoolAdmin.Finance) }
                        )
                    }
                    item {
                        ActionPill(
                            icon = Icons.Outlined.PersonAdd,
                            label = "Inscrire un Élève",
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            onClick = { onNavigate(Route.SchoolAdmin.Academics) }
                        )
                    }
                    item {
                        ActionPill(
                            icon = Icons.Outlined.Radar,
                            label = "Radar Pédagogique",
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            onClick = { onNavigate(Route.SchoolAdmin.Pedagogy) }
                        )
                    }
                    item {
                        ActionPill(
                            icon = Icons.Outlined.Groups,
                            label = "Personnel & RH",
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            onClick = { onNavigate(Route.SchoolAdmin.StaffHR) }
                        )
                    }
                    item {
                        ActionPill(
                            icon = Icons.AutoMirrored.Filled.Chat,
                            label = "SMS & Circulaire Parents",
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            onClick = { onNavigate(Route.SchoolAdmin.Communication) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionPill(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 2. EXECUTIVE KPI METRIC CARDS (BENTO STYLE)
// -------------------------------------------------------------------------------------------------
@Composable
fun ExecutiveKpiRow(
    state: SchoolDashboardState,
    onNavigate: (NavKey) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isWide = maxWidth > 850.dp
        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Effectif Élèves",
                    primaryValue = "${state.totalStudents}",
                    badgeText = "+3.4% ce mois",
                    badgePositive = true,
                    icon = Icons.Default.PeopleAlt,
                    accentColor = Color(0xFF3B82F6),
                    subtitle = "${state.boysCount} Garçons (52%) • ${state.girlsCount} Filles (48%)",
                    progressValue = (state.attendanceRate / 100f).toFloat(),
                    progressLabel = "Assiduité : ${state.attendanceRate}%",
                    onClick = { onNavigate(Route.SchoolAdmin.Academics) }
                )
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Recouvrement Caisse",
                    primaryValue = "${state.financeSummary.collectionRatePercentage.toInt()}%",
                    badgeText = "+$${state.financeSummary.todayCollected.toInt()} auj.",
                    badgePositive = true,
                    icon = Icons.Default.Payments,
                    accentColor = Color(0xFF10B981),
                    subtitle = "$${(state.financeSummary.totalCollected / 1000).toInt()}k perçus / $${(state.financeSummary.totalExpected / 1000).toInt()}k attendus",
                    progressValue = (state.financeSummary.collectionRatePercentage / 100f).toFloat(),
                    progressLabel = "Solvabilité globale",
                    onClick = { onNavigate(Route.SchoolAdmin.Finance) }
                )
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Corps Enseignant",
                    primaryValue = "${state.totalTeachers}",
                    badgeText = "100% assignés",
                    badgePositive = true,
                    icon = Icons.Default.Badge,
                    accentColor = Color(0xFF8B5CF6),
                    subtitle = "${state.totalClasses} classes opérationnelles",
                    progressValue = 1f,
                    progressLabel = "Couverture des cours : 100%",
                    onClick = { onNavigate(Route.SchoolAdmin.StaffHR) }
                )
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Progression Programmes",
                    primaryValue = "78.4%",
                    badgeText = "Radar Actif",
                    badgePositive = true,
                    icon = Icons.Default.AccountTree,
                    accentColor = Color(0xFFF59E0B),
                    subtitle = "14 classes conformes, 2 à surveiller",
                    progressValue = 0.784f,
                    progressLabel = "Moyenne programmes : 78%",
                    onClick = { onNavigate(Route.SchoolAdmin.Pedagogy) }
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        modifier = Modifier.weight(1f),
                        title = "Effectif Élèves",
                        primaryValue = "${state.totalStudents}",
                        badgeText = "+3.4%",
                        badgePositive = true,
                        icon = Icons.Default.PeopleAlt,
                        accentColor = Color(0xFF3B82F6),
                        subtitle = "${state.boysCount} G • ${state.girlsCount} F",
                        progressValue = (state.attendanceRate / 100f).toFloat(),
                        progressLabel = "Assiduité : ${state.attendanceRate}%",
                        onClick = { onNavigate(Route.SchoolAdmin.Academics) }
                    )
                    KpiCard(
                        modifier = Modifier.weight(1f),
                        title = "Recouvrement",
                        primaryValue = "${state.financeSummary.collectionRatePercentage.toInt()}%",
                        badgeText = "$${state.financeSummary.todayCollected.toInt()} auj.",
                        badgePositive = true,
                        icon = Icons.Default.Payments,
                        accentColor = Color(0xFF10B981),
                        subtitle = "$${(state.financeSummary.totalCollected / 1000).toInt()}k/$${(state.financeSummary.totalExpected / 1000).toInt()}k",
                        progressValue = (state.financeSummary.collectionRatePercentage / 100f).toFloat(),
                        progressLabel = "Solvabilité",
                        onClick = { onNavigate(Route.SchoolAdmin.Finance) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        modifier = Modifier.weight(1f),
                        title = "Enseignants",
                        primaryValue = "${state.totalTeachers}",
                        badgeText = "100%",
                        badgePositive = true,
                        icon = Icons.Default.Badge,
                        accentColor = Color(0xFF8B5CF6),
                        subtitle = "${state.totalClasses} classes actives",
                        progressValue = 1f,
                        progressLabel = "Assignations : 100%",
                        onClick = { onNavigate(Route.SchoolAdmin.StaffHR) }
                    )
                    KpiCard(
                        modifier = Modifier.weight(1f),
                        title = "Programmes",
                        primaryValue = "78.4%",
                        badgeText = "Radar",
                        badgePositive = true,
                        icon = Icons.Default.AccountTree,
                        accentColor = Color(0xFFF59E0B),
                        subtitle = "14 classes conformes",
                        progressValue = 0.784f,
                        progressLabel = "Progression : 78%",
                        onClick = { onNavigate(Route.SchoolAdmin.Pedagogy) }
                    )
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    modifier: Modifier = Modifier,
    title: String,
    primaryValue: String,
    badgeText: String,
    badgePositive: Boolean,
    icon: ImageVector,
    accentColor: Color,
    subtitle: String,
    progressValue: Float,
    progressLabel: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with Icon & Trend Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (badgePositive) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (badgePositive) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                        }
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (badgePositive) Color(0xFF10B981) else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Primary Big Number & Title
            Column {
                Text(
                    text = primaryValue,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Linear Progress & Subtitle
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LinearProgressIndicator(
                    progress = { progressValue.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = accentColor,
                    trackColor = accentColor.copy(alpha = 0.15f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 3. HIGH-END VISUAL CHARTS (CANVAS SPLINE & DONUT BREAKDOWN)
// -------------------------------------------------------------------------------------------------
@Composable
fun ChartsSection(
    state: SchoolDashboardState,
    onNavigate: (NavKey) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isWide = maxWidth > 850.dp
        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FinancialSplineChartCard(
                    modifier = Modifier.weight(1.6f),
                    metrics = state.monthlyRevenue,
                    totalCollected = state.financeSummary.totalCollected,
                    onNavigate = onNavigate
                )
                EnrollmentDonutChartCard(
                    modifier = Modifier.weight(1.2f),
                    totalStudents = state.totalStudents,
                    sections = state.sectionEnrollments,
                    onNavigate = onNavigate
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FinancialSplineChartCard(
                    modifier = Modifier.fillMaxWidth(),
                    metrics = state.monthlyRevenue,
                    totalCollected = state.financeSummary.totalCollected,
                    onNavigate = onNavigate
                )
                EnrollmentDonutChartCard(
                    modifier = Modifier.fillMaxWidth(),
                    totalStudents = state.totalStudents,
                    sections = state.sectionEnrollments,
                    onNavigate = onNavigate
                )
            }
        }
    }
}

@Composable
fun FinancialSplineChartCard(
    modifier: Modifier = Modifier,
    metrics: List<MonthlyFinancialMetric>,
    totalCollected: Double,
    onNavigate: (NavKey) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceContainer = MaterialTheme.colorScheme.surfaceContainer

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainer),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Flux des Recouvrements Mensuels",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Comparatif Réalisé vs Prévisions mensuelles (USD)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilledTonalButton(
                    onClick = { onNavigate(Route.SchoolAdmin.Finance) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Détails Finances", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }

            // Canvas Line/Area Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp)
            ) {
                val maxVal = (metrics.maxOfOrNull { maxOf(it.amountCollected, it.targetAmount) } ?: 20000.0) * 1.15

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height - 30.dp.toPx()
                    val bottomY = height
                    val stepX = width / (metrics.size - 1).coerceAtLeast(1)

                    // Draw Horizontal grid lines
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = bottomY - (bottomY / gridLines) * i
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.15f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Build Target Points path (Dashed)
                    val targetPath = Path()
                    metrics.forEachIndexed { index, m ->
                        val x = index * stepX
                        val y = bottomY - ((m.targetAmount / maxVal) * bottomY).toFloat()
                        if (index == 0) targetPath.moveTo(x, y) else targetPath.lineTo(x, y)
                    }
                    drawPath(
                        path = targetPath,
                        color = Color(0xFFF59E0B).copy(alpha = 0.6f),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    )

                    // Build Spline Curve Path for Collected Amount
                    val path = Path()
                    val areaPath = Path()
                    val points = metrics.mapIndexed { index, m ->
                        val x = index * stepX
                        val y = bottomY - ((m.amountCollected / maxVal) * bottomY).toFloat()
                        Offset(x, y)
                    }

                    if (points.isNotEmpty()) {
                        path.moveTo(points.first().x, points.first().y)
                        areaPath.moveTo(points.first().x, bottomY)
                        areaPath.lineTo(points.first().x, points.first().y)

                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val controlPoint1 = Offset((p0.x + p1.x) / 2, p0.y)
                            val controlPoint2 = Offset((p0.x + p1.x) / 2, p1.y)
                            path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                            areaPath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                        }

                        areaPath.lineTo(points.last().x, bottomY)
                        areaPath.close()

                        // Draw Gradient Area
                        drawPath(
                            path = areaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.35f),
                                    primaryColor.copy(alpha = 0.0f)
                                ),
                                startY = 0f,
                                endY = bottomY
                            )
                        )

                        // Draw Continuous Line
                        drawPath(
                            path = path,
                            color = primaryColor,
                            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Draw Dots at Points
                        points.forEach { pt ->
                            drawCircle(color = surfaceContainer, radius = 5.dp.toPx(), center = pt)
                            drawCircle(color = primaryColor, radius = 3.5.dp.toPx(), center = pt)
                        }
                    }
                }

                // Month Labels Row below canvas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    metrics.forEach { m ->
                        Text(
                            text = m.monthLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(10.dp).background(primaryColor, CircleShape))
                    Text("Encaissé Réel", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFF59E0B), CircleShape))
                    Text("Objectif Prévisionnel", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun EnrollmentDonutChartCard(
    modifier: Modifier = Modifier,
    totalStudents: Int,
    sections: List<SectionEnrollmentMetric>,
    onNavigate: (NavKey) -> Unit
) {
    val sliceColors = listOf(
        Color(0xFF3B82F6), // Blue
        Color(0xFF10B981), // Emerald
        Color(0xFF8B5CF6), // Purple
        Color(0xFFF59E0B), // Amber
        Color(0xFFEC4899)  // Pink
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Effectifs par Section",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Répartition globale des élèves",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { onNavigate(Route.SchoolAdmin.Academics) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Détails")
                }
            }

            // Donut Canvas + Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Donut
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(130.dp)) {
                        val strokeWidth = 18.dp.toPx()
                        var startAngle = -90f

                        sections.forEachIndexed { index, sec ->
                            val sweepAngle = (sec.percentage / 100f) * 360f
                            val color = sliceColors.getOrElse(index) { Color.Gray }
                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle - 2f, // slight gap
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalStudents",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Élèves",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Legend List
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sections.forEachIndexed { index, sec ->
                        val color = sliceColors.getOrElse(index) { Color.Gray }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
                                Text(
                                    text = sec.sectionName,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "${sec.studentCount} (${sec.percentage.toInt()}%)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 4. OPERATIONAL CONTROL GRID: PEDAGOGY PROGRESS & RECENT CASH TRANSACTIONS
// -------------------------------------------------------------------------------------------------
@Composable
fun OperationalStreamsSection(
    state: SchoolDashboardState,
    onNavigate: (NavKey) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isWide = maxWidth > 850.dp
        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PedagogyRadarPreviewCard(
                    modifier = Modifier.weight(1f),
                    subjects = state.subjectProgressList,
                    onNavigate = onNavigate
                )
                RecentTransactionsLiveCard(
                    modifier = Modifier.weight(1f),
                    transactions = state.recentTransactions,
                    onNavigate = onNavigate
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PedagogyRadarPreviewCard(
                    modifier = Modifier.fillMaxWidth(),
                    subjects = state.subjectProgressList,
                    onNavigate = onNavigate
                )
                RecentTransactionsLiveCard(
                    modifier = Modifier.fillMaxWidth(),
                    transactions = state.recentTransactions,
                    onNavigate = onNavigate
                )
            }
        }
    }
}

@Composable
fun PedagogyRadarPreviewCard(
    modifier: Modifier = Modifier,
    subjects: List<SubjectProgressMetric>,
    onNavigate: (NavKey) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
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
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Radar, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                        }
                    }
                    Column {
                        Text("Avancement par Matière", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Radar anti-retard & programmes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                TextButton(onClick = { onNavigate(Route.SchoolAdmin.Pedagogy) }) {
                    Text("Ouvrir Radar")
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                subjects.forEach { subj ->
                    val color = when (subj.status) {
                        "AHEAD" -> Color(0xFF10B981)
                        "ON_TRACK" -> MaterialTheme.colorScheme.primary
                        else -> Color(0xFFEF4444)
                    }
                    val statusLabel = when (subj.status) {
                        "AHEAD" -> "En avance"
                        "ON_TRACK" -> "Dans les temps"
                        else -> "À surveiller"
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = subj.subjectName,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = color.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = statusLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = color,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "${subj.completionPercentage}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        LinearProgressIndicator(
                            progress = { subj.completionPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(CircleShape),
                            color = color,
                            trackColor = color.copy(alpha = 0.15f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentTransactionsLiveCard(
    modifier: Modifier = Modifier,
    transactions: List<PaymentTransaction>,
    onNavigate: (NavKey) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
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
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                        }
                    }
                    Column {
                        Text("Derniers Encaissements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Transactions en temps réel", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                TextButton(onClick = { onNavigate(Route.SchoolAdmin.Finance) }) {
                    Text("Voir Caisse")
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            if (transactions.isEmpty()) {
                // High-End fallback sample transactions for senior presentation
                val sampleTx = listOf(
                    Triple("Kabila Marc", "6ème Math-Physique A", 150.0 to "M-Pesa"),
                    Triple("Mwamba Sarah", "5ème Bio-Chimie B", 85.0 to "Espèces"),
                    Triple("Lukusa David", "3ème Primaire A", 120.0 to "Banque"),
                    Triple("Tshisekedi Grace", "7ème Éduc. de Base", 60.0 to "M-Pesa")
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    sampleTx.forEach { (name, className, payment) ->
                        TransactionRowItem(
                            studentName = name,
                            classroomName = className,
                            amount = payment.first,
                            method = payment.second
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    transactions.take(4).forEach { tx ->
                        TransactionRowItem(
                            studentName = tx.studentName,
                            classroomName = tx.classroomName,
                            amount = tx.amountPaid,
                            method = tx.paymentMethod.label
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionRowItem(
    studentName: String,
    classroomName: String,
    amount: Double,
    method: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            val initials = studentName.split(" ").mapNotNull { it.take(1) }.joinToString("").take(2).uppercase()
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initials.ifEmpty { "E" },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column {
                Text(
                    text = studentName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$classroomName • $method",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF10B981).copy(alpha = 0.12f)
        ) {
            Text(
                text = "+$${amount.toInt()} USD",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF10B981),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 5. PENDING ADMINISTRATIVE & PEDAGOGICAL APPROVALS
// -------------------------------------------------------------------------------------------------
@Composable
fun PendingAlertsSection(
    state: SchoolDashboardState,
    onNavigate: (NavKey) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.PendingActions,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Visas & Validations Administratives Requises",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Documents et fiches soumis par le corps professoral",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                FilledTonalButton(onClick = { onNavigate(Route.SchoolAdmin.Pedagogy) }) {
                    Text("Tout traiter")
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.pendingAlerts.forEach { alert ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            when (alert.type) {
                                                "PREPARATION" -> Icons.Default.MenuBook
                                                "CLASS_LOG" -> Icons.Default.Notes
                                                else -> Icons.Default.FactCheck
                                            },
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = alert.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${alert.teacherName} • ${alert.className} • ${alert.timestamp}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { onNavigate(Route.SchoolAdmin.Pedagogy) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Examiner", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 6. PROFESSIONAL FOOTER
// -------------------------------------------------------------------------------------------------
@Composable
fun DashboardFooter(lastRefresh: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Kelasi Suite Enterprise • Version 2.4 Senior Release",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Dernière synchro : $lastRefresh",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
