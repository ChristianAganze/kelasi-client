package com.drcmind.kelasisuite.ui.teacheradmin.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.data.datasource.remote.dto.ReportCardDTO
import com.drcmind.kelasisuite.ui.components.EmptyStateCard
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import com.drcmind.kelasisuite.ui.components.LoadingState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(message = it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Long)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.availableClasses.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Classe",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.availableClasses) { assignment ->
                            FilterChip(
                                selected = state.selectedClass?.id == assignment.id,
                                onClick = { viewModel.selectClass(assignment) },
                                label = { Text("${assignment.className} - ${assignment.subjectName}") }
                            )
                        }
                    }
                }
            }

            if (state.evaluationPeriods.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Période",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.evaluationPeriods) { period ->
                            FilterChip(
                                selected = state.selectedPeriod?.id == period.id,
                                onClick = { viewModel.selectPeriod(period) },
                                label = { Text(period.label.ifEmpty { "Période ${period.id}" }) }
                            )
                        }
                    }
                }
            }

            val selectedClass = state.selectedClass
            val selectedPeriod = state.selectedPeriod
            if (selectedClass != null && selectedPeriod != null) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Informations de la classe",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = selectedClass.className,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${selectedClass.subjectName} • ${selectedPeriod.label.ifEmpty { "Période ${selectedPeriod.id}" }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    state.isLoading && state.reportCards.isEmpty() -> {
                        LoadingState(modifier = Modifier.align(Alignment.Center))
                    }
                    state.loadError != null -> {
                        ErrorStateCard(
                            message = state.loadError,
                            onRetry = viewModel::retry,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp)
                        )
                    }
                    state.selectedClass == null || state.selectedPeriod == null -> {
                        EmptyStateCard(
                            title = "Sélectionnez une classe et une période",
                            subtitle = "Choisissez ci-dessus pour afficher les bulletins.",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp)
                        )
                    }
                    state.reportCards.isEmpty() -> {
                        EmptyStateCard(
                            title = "Aucun bulletin",
                            subtitle = "Aucun bulletin à afficher pour cette classe et cette période.",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.reportCards) { report ->
                                ReportCardItem(
                                    reportCard = report,
                                    onSave = { remarks, conduct ->
                                        viewModel.saveReportCard(report, remarks, conduct)
                                    },
                                    onExportPdf = { viewModel.exportReportCard(report) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCardItem(
    reportCard: ReportCardDTO,
    onSave: (String, String) -> Unit,
    onExportPdf: () -> Unit
) {
    var remarks by remember { mutableStateOf(reportCard.teacherRemarks ?: "") }
    var conduct by remember { mutableStateOf(reportCard.studentConduct ?: "") }
    val readOnly = reportCard.isPublished

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reportCard.studentName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (readOnly) {
                    Badge {
                        Text("Publié")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total: ${reportCard.totalScore} / ${reportCard.maxScore}")
                Text(
                    text = "Moyenne: ${reportCard.average}%",
                    fontWeight = FontWeight.SemiBold,
                    color = if (reportCard.average >= 50) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = conduct,
                onValueChange = { conduct = it },
                label = { Text("Conduite de l'élève") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                enabled = !readOnly
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text("Commentaire général (Titulaire)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                enabled = !readOnly
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onExportPdf) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export PDF")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onSave(remarks, conduct) }, enabled = !readOnly) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enregistrer")
                }
            }
        }
    }
}
