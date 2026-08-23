@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.program_radar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.ui.schooladmin.component.SectionCard
import com.drcmind.kelasisuite.ui.components.EmptyStateCard
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.program_radar.component.RadarChart
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.program_radar.component.RadarChartLegend
import kotlin.math.roundToInt
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProgramRadarScreen(
    viewModel: ProgramRadarViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Radar Anti-Retard",
                            style = MaterialTheme.typography.displaySmall,
                        )
                        Text(
                            text = "Le tableau de bord visuel comparant le Programme National au Réalisé.",
                            style = MaterialTheme.typography.labelLarge,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 32.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (state.isLoading && state.classes.isEmpty()) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            if (state.error != null && state.classes.isEmpty()) {
                item {
                    ErrorStateCard(
                        message = state.error,
                        onRetry = viewModel::loadRadar
                    )
                }
            }

            if (state.classes.isEmpty() && !state.isLoading && state.error == null) {
                item {
                    EmptyStateCard(
                        title = "Aucune donnée de couverture disponible.",
                        subtitle = "Le radar apparaîtra dès que les programmes seront renseignés pour cette année académique."
                    )
                }
            }

            if (state.classes.isNotEmpty()) {
                item {
                    OverviewStats(classes = state.classes)
                }
                item {
                    ClassSelectorSection(
                        classes = state.classes,
                        selectedClassId = state.selectedClass?.classId,
                        weekNumber = state.weekNumber,
                        academicYearLabel = state.academicYearLabel,
                        onSelect = viewModel::selectClass
                    )
                }
                state.selectedClass?.let { selected ->
                    item {
                        RadarCoverageSection(selected)
                    }
                    item {
                        SubjectBreakdownSection(selected)
                    }
                }
                item {
                    ClassesStatusSection(state.classes)
                }
            }
        }
    }
}

@Composable
private fun OverviewStats(classes: List<ClassRadarUi>) {
    val onTrackCount = classes.count { it.status == RadarStatus.ON_TRACK }
    val delayedCount = classes.count { it.status == RadarStatus.DELAYED }
    val aheadCount = classes.count { it.status == RadarStatus.AHEAD }
    val averageRealized = if (classes.isEmpty()) 0f else classes.map { it.realized }.average().toFloat()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            label = "En retard",
            value = delayedCount.toString(),
            icon = Icons.Default.WarningAmber,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Dans les temps",
            value = onTrackCount.toString(),
            icon = Icons.Default.CheckCircle,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "En avance",
            value = aheadCount.toString(),
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Couverture moyenne",
            value = "${(averageRealized * 100).roundToInt()}%",
            icon = Icons.Default.Speed,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ClassSelectorSection(
    classes: List<ClassRadarUi>,
    selectedClassId: Long?,
    weekNumber: Int?,
    academicYearLabel: String?,
    onSelect: (Long) -> Unit
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Choisir une classe",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (weekNumber != null || academicYearLabel != null) {
                    Text(
                        text = listOfNotNull(
                            weekNumber?.let { "Semaine $it" },
                            academicYearLabel
                        ).joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                classes.forEach { schoolClass ->
                    val isSelected = schoolClass.classId == selectedClassId
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelect(schoolClass.classId) },
                        label = { Text(schoolClass.className) },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RadarCoverageSection(selected: ClassRadarUi) {
    SectionCard(
        title = "Couverture du programme — ${selected.className}",
        icon = Icons.Default.Radar
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RadarChart(
                subjects = selected.subjects,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            )
            RadarChartLegend()
        }
    }
}

@Composable
private fun SubjectBreakdownSection(selected: ClassRadarUi) {
    SectionCard(
        title = "Progression par matière",
        icon = Icons.AutoMirrored.Filled.TrendingUp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            selected.subjects.forEach { subject ->
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subject.subjectName,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(subject.realized * 100).roundToInt()}% / ${(subject.nationalTarget * 100).roundToInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { subject.realized },
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Réalisé",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { subject.nationalTarget },
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "National",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassesStatusSection(classes: List<ClassRadarUi>) {
    SectionCard(
        title = "État d'avancement des classes",
        icon = Icons.Default.Speed
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            classes.forEach { schoolClass ->
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = schoolClass.className,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${schoolClass.sectionLabel} • ${schoolClass.studentsCount} élèves",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusChip(status = schoolClass.status)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { schoolClass.realized },
                        color = statusColor(schoolClass.status),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = schoolClass.delayLabel(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: RadarStatus) {
    val color = statusColor(status)
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = statusLabel(status),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun statusColor(status: RadarStatus): Color = when (status) {
    RadarStatus.AHEAD -> MaterialTheme.colorScheme.tertiary
    RadarStatus.ON_TRACK -> MaterialTheme.colorScheme.primary
    RadarStatus.DELAYED -> MaterialTheme.colorScheme.error
}

@Composable
private fun statusLabel(status: RadarStatus): String = when (status) {
    RadarStatus.AHEAD -> "En avance"
    RadarStatus.ON_TRACK -> "Dans les temps"
    RadarStatus.DELAYED -> "En retard"
}

private fun ClassRadarUi.delayLabel(): String {
    val delta = (realized - nationalTarget) * 100
    return when (status) {
        RadarStatus.DELAYED -> "En retard de ${(-delta).roundToInt()}% sur le programme national."
        RadarStatus.AHEAD -> "En avance de ${delta.roundToInt()}% sur le programme national."
        RadarStatus.ON_TRACK -> "Alignée sur le programme national."
    }
}
