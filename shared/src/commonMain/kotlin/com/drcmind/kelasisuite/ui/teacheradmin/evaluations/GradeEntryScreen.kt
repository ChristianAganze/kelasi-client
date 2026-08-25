package com.drcmind.kelasisuite.ui.teacheradmin.evaluations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.domain.model.teacher.StudentEval
import com.drcmind.kelasisuite.ui.components.EmptyStateCard
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import com.drcmind.kelasisuite.ui.components.LoadingState
import com.drcmind.kelasisuite.ui.teacheradmin.classes.ClassesViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeEntryScreen(
    modifier: Modifier = Modifier,
    viewModel: ClassesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSubmitConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(state.saveSuccess, state.saveError) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar("Cotes enregistrées et synchronisées avec succès.")
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Encodage & Grille des Cotes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sélectionnez votre classe, cours et période d'évaluation pour saisir les notes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "1. Sélectionner la Classe & le Cours",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (state.isLoadingClasses) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else if (state.availableClasses.isEmpty()) {
                        Text(
                            text = "Aucune affectation trouvée.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.availableClasses) { assignment ->
                                FilterChip(
                                    selected = state.selectedClass?.id == assignment.id,
                                    onClick = { viewModel.selectClass(assignment) },
                                    label = {
                                        Text(
                                            text = "${assignment.subjectName} (${assignment.className})",
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    leadingIcon = if (state.selectedClass?.id == assignment.id) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "2. Période d'évaluation",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (state.evaluationPeriods.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.evaluationPeriods) { period ->
                                FilterChip(
                                    selected = state.selectedPeriod?.id == period.id,
                                    onClick = { viewModel.selectPeriod(period) },
                                    label = { Text(period.label.ifEmpty { "Période ${period.id}" }, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                    leadingIcon = if (state.selectedPeriod?.id == period.id) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "1ère Période (Active par défaut)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoadingStudents -> {
                    LoadingState(modifier = Modifier.fillMaxSize())
                }
                state.studentErrorMessage != null -> {
                    ErrorStateCard(
                        message = state.studentErrorMessage ?: "Erreur de chargement",
                        onRetry = viewModel::retryStudents
                    )
                }
                state.selectedClass == null -> {
                    EmptyStateCard(
                        title = "Sélectionnez un cours",
                        subtitle = "Choisissez l'un de vos cours ci-dessus pour afficher la liste des élèves et saisir les cotes."
                    )
                }
                state.students.isEmpty() -> {
                    EmptyStateCard(
                        title = "Aucun élève trouvé",
                        subtitle = "Cette classe ne contient actuellement aucun élève inscrit."
                    )
                }
                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Liste des Élèves (${state.students.size}) — Maxima : /20",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.saveEvaluations() },
                                enabled = !state.isSaving
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Enregistrer brouillon")
                            }

                            Button(
                                onClick = { showSubmitConfirmation = true },
                                enabled = !state.isSaving
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Soumettre cotes")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.students) { student ->
                            GradeStudentCard(
                                student = student,
                                onGradeChanged = { newScore -> viewModel.updateGrade(student.id, newScore) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSubmitConfirmation) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirmation = false },
            title = { Text("Soumission officielle des Cotes") },
            text = {
                Text(
                    "Êtes-vous certain de vouloir soumettre définitivement les cotes de cette période à la Direction ? Vous pourrez les modifier ultérieurement uniquement avec dérogation."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmation = false
                        viewModel.saveEvaluations()
                    }
                ) {
                    Text("Confirmer la soumission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmation = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun GradeStudentCard(
    student: StudentEval,
    onGradeChanged: (String) -> Unit
) {
    val studentFullName = "${student.lastName} ${student.firstName}".trim().ifEmpty { "Élève #${student.id}" }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = studentFullName.take(2).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = studentFullName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Matricule: STU-${student.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = student.grade ?: "",
                    onValueChange = onGradeChanged,
                    label = { Text("Note /20") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(110.dp),
                    singleLine = true
                )

                val scoreFloat = student.grade?.toFloatOrNull()
                if (scoreFloat != null) {
                    val isPass = scoreFloat >= 10f
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (isPass) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = "${(scoreFloat / 20f * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isPass) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
