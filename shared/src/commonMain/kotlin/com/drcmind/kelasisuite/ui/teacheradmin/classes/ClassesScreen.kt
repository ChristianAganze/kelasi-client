package com.drcmind.kelasisuite.ui.teacheradmin.classes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.domain.model.teacher.AttendanceStatus
import com.drcmind.kelasisuite.ui.components.EmptyStateCard
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import com.drcmind.kelasisuite.ui.components.LoadingState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesScreen(
    modifier: Modifier = Modifier,
    viewModel: ClassesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saveSuccess, state.saveError) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar("Évaluations sauvegardées avec succès.")
            viewModel.dismissSnackbar()
        } else {
            val saveError = state.saveError
            if (saveError != null) {
                snackbarHostState.showSnackbar(saveError)
                viewModel.dismissSnackbar()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (state.students.isNotEmpty() && !state.isLoadingStudents) {
                FloatingActionButton(onClick = { viewModel.saveEvaluations() }) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = "Sauvegarder")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Gestion de Classe",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Class selection
            if (state.isLoadingClasses) {
                LoadingState(modifier = Modifier.fillMaxWidth())
            } else if (state.errorMessage != null) {
                ErrorStateCard(
                    message = state.errorMessage,
                    onRetry = viewModel::retryClasses
                )
            } else if (state.availableClasses.isEmpty()) {
                EmptyStateCard(
                    title = "Aucune classe assignée",
                    subtitle = "Aucune classe ne vous est attribuée pour le moment."
                )
            } else {
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

            // Period selection (only meaningful for grades)
            if (state.evaluationPeriods.isNotEmpty() && state.evaluationType == EvaluationType.GRADES) {
                Text(
                    text = "Période d'évaluation",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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

            // Mode selection (Attendance vs Grades)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = state.evaluationType == EvaluationType.ATTENDANCE,
                        onClick = { viewModel.setEvaluationType(EvaluationType.ATTENDANCE) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("Présences")
                    }
                    SegmentedButton(
                        selected = state.evaluationType == EvaluationType.GRADES,
                        onClick = { viewModel.setEvaluationType(EvaluationType.GRADES) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("Évaluations/Cotes")
                    }
                }
            }

            // Student List
            if (state.isLoadingStudents) {
                LoadingState(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else if (state.studentErrorMessage != null) {
                ErrorStateCard(
                    message = state.studentErrorMessage,
                    onRetry = viewModel::retryStudents,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else if (state.students.isEmpty() && state.selectedClass != null) {
                EmptyStateCard(
                    title = "Aucun étudiant",
                    subtitle = "Aucun étudiant n'est inscrit dans cette classe.",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.students) { student ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${student.lastName} ${student.firstName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )

                                if (state.evaluationType == EvaluationType.ATTENDANCE) {
                                    // Simple Attendance Dropdown/Row
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        val statusColor = when (student.attendance) {
                                            AttendanceStatus.PRESENT -> MaterialTheme.colorScheme.primary
                                            AttendanceStatus.ABSENT -> MaterialTheme.colorScheme.error
                                            AttendanceStatus.LATE -> MaterialTheme.colorScheme.tertiary
                                            AttendanceStatus.EXCUSED -> MaterialTheme.colorScheme.secondary
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                // Toggle logic simplified for MVP. E.g. Present -> Absent
                                                val next =
                                                    if (student.attendance == AttendanceStatus.PRESENT) AttendanceStatus.ABSENT else AttendanceStatus.PRESENT
                                                viewModel.updateAttendance(student.id, next)
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = statusColor
                                            )
                                        ) {
                                            Text(student.attendance.name)
                                        }
                                    }
                                } else {
                                    // Grade input
                                    OutlinedTextField(
                                        value = student.grade,
                                        onValueChange = { viewModel.updateGrade(student.id, it) },
                                        label = { Text("Cote") },
                                        modifier = Modifier.width(100.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
