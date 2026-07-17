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
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesScreen(
    modifier: Modifier = Modifier,
    viewModel: ClassesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saveSuccess, state.errorMessage) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar("Évaluations sauvegardées avec succès.")
            viewModel.dismissSnackbar()
        } else if (state.errorMessage != null) {
            snackbarHostState.showSnackbar(state.errorMessage ?: "Erreur.")
            viewModel.dismissSnackbar()
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
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (state.errorMessage != null) {
                Text("Erreur: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
            } else if (state.availableClasses.isEmpty()) {
                Text(
                    "Aucune classe assignée pour le moment.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.studentErrorMessage != null) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Erreur: ${state.studentErrorMessage}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else if (state.students.isEmpty() && state.selectedClass != null) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aucun étudiant inscrit dans cette classe.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
