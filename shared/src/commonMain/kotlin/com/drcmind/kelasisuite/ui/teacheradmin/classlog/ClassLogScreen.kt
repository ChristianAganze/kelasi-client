package com.drcmind.kelasisuite.ui.teacheradmin.classlog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.domain.model.teacher.ClassLogEntry
import com.drcmind.kelasisuite.domain.model.teacher.LogStatus
import com.drcmind.kelasisuite.domain.model.teacher.PreparationStatus
import com.drcmind.kelasisuite.ui.components.EmptyStateCard
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import com.drcmind.kelasisuite.ui.components.LoadingState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassLogScreen(
    modifier: Modifier = Modifier,
    viewModel: ClassLogViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saveSuccess, state.saveError) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar("Journaux de classe sauvegardés avec succès.")
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
            if (state.scheduleToday.isNotEmpty() && !state.isLoading) {
                FloatingActionButton(onClick = { viewModel.saveClassLogs() }) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = "Sauvegarder")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Aujourd'hui",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.isLoading) {
                item {
                    LoadingState(modifier = Modifier.fillMaxWidth().height(200.dp))
                }
            } else if (state.errorMessage != null) {
                item {
                    ErrorStateCard(
                        message = state.errorMessage,
                        onRetry = viewModel::retry
                    )
                }
            } else if (state.scheduleToday.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "Aucun cours aujourd'hui",
                        subtitle = "Vous n'avez aucun cours prévu pour aujourd'hui."
                    )
                }
            } else {
                items(state.scheduleToday) { entry ->
                    ClassLogCard(
                        entry = entry,
                        onLinkClick = { viewModel.selectEntryForLinking(entry.id) },
                        onStatusClick = { viewModel.selectEntryForStatusUpdate(entry.id) },
                        onPresenceClick = { viewModel.selectEntryForPresence(entry.id) },
                        onSubmitClick = { viewModel.submitEntry(entry.id) }
                    )
                }
            }
        }
    }

    if (state.showStatusDialog) {
        StatusUpdateDialog(
            onDismiss = { viewModel.dismissDialogs() },
            onConfirm = { status, note, homework -> viewModel.updateStatus(status, note, homework) }
        )
    }

    if (state.showLinkDialog) {
        LinkPreparationDialog(
            state = state,
            onDismiss = { viewModel.dismissDialogs() },
            onConfirm = { prepId -> viewModel.linkPreparation(prepId) }
        )
    }

    if (state.showPresenceDialog) {
        PresenceDialog(
            state = state,
            onDismiss = { viewModel.dismissDialogs() },
            onToggle = { studentId -> viewModel.togglePresence(studentId) },
            onConfirm = { viewModel.confirmPresence() }
        )
    }
}

@Composable
fun ClassLogCard(
    entry: ClassLogEntry,
    onLinkClick: () -> Unit,
    onStatusClick: () -> Unit,
    onPresenceClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Timeline Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Text(entry.timeSlot.split(" - ")[0], style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                val icon = when(entry.status) {
                    LogStatus.COMPLETED -> Icons.Default.CheckCircle
                    LogStatus.IN_PROGRESS -> Icons.Default.PlayCircle
                    LogStatus.POSTPONED -> Icons.Default.WatchLater
                    LogStatus.NOT_STARTED -> Icons.Default.Circle
                }
                val color = when(entry.status) {
                    LogStatus.COMPLETED -> Color(0xFF4CAF50)
                    LogStatus.IN_PROGRESS -> Color(0xFFFF9800)
                    LogStatus.POSTPONED -> Color(0xFFF44336)
                    LogStatus.NOT_STARTED -> MaterialTheme.colorScheme.outline
                }
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(vertical = 4.dp))
                Text(entry.timeSlot.split(" - ")[1], style = MaterialTheme.typography.labelSmall)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.subject, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(entry.className, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (entry.linkedPreparationTitle != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Leçon: ${entry.linkedPreparationTitle}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            entry.linkedObjective?.takeIf { it.isNotBlank() }?.let {
                                Text(
                                    text = "Objectif: $it",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            entry.linkedReference?.takeIf { it.isNotBlank() }?.let {
                                Text(
                                    text = "Réf: $it",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                } else {
                    TextButton(
                        onClick = onLinkClick,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.AddLink, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lier une préparation")
                    }
                }
                
                if (entry.homework.isNotBlank()) {
                    Text(text = "Tâche: ${entry.homework}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }
                if (entry.teacherNote.isNotBlank()) {
                    Text(text = "Note: ${entry.teacherNote}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                }
                if (entry.presentStudentIds.isNotEmpty()) {
                    Text(
                        text = "${entry.presentStudentIds.size} élève(s) présent(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (entry.submitted) {
                    Badge(modifier = Modifier.align(Alignment.End)) {
                        Text("Soumis")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onStatusClick) {
                    Text(if (entry.status == LogStatus.NOT_STARTED) "Démarrer" else "Bilan")
                }
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(onClick = onPresenceClick) {
                    Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Présences")
                }
                if (!entry.submitted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(onClick = onSubmitClick, enabled = entry.status == LogStatus.COMPLETED) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Soumettre pour signature")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusUpdateDialog(
    onDismiss: () -> Unit,
    onConfirm: (LogStatus, String, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(LogStatus.COMPLETED) }
    var note by remember { mutableStateOf("") }
    var homework by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bilan du cours") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Segmented Buttons equivalent using Row of OutlinedButtons for broader compatibility
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    FilterChip(
                        selected = selectedStatus == LogStatus.COMPLETED,
                        onClick = { selectedStatus = LogStatus.COMPLETED },
                        label = { Text("Donnée") }
                    )
                    FilterChip(
                        selected = selectedStatus == LogStatus.IN_PROGRESS,
                        onClick = { selectedStatus = LogStatus.IN_PROGRESS },
                        label = { Text("En cours") }
                    )
                    FilterChip(
                        selected = selectedStatus == LogStatus.POSTPONED,
                        onClick = { selectedStatus = LogStatus.POSTPONED },
                        label = { Text("Reportée") }
                    )
                }
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (Comportement, suivi...)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = homework,
                    onValueChange = { homework = it },
                    label = { Text("Tâche / Devoir à domicile") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedStatus, note, homework) }) {
                Text("Valider")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkPreparationDialog(
    state: ClassLogState,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val approvedPreparations = state.availablePreparations.filter { it.status == PreparationStatus.APPROVED }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lier une préparation validée") },
        text = {
            if (approvedPreparations.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Aucune préparation approuvée disponible. Créez et soumettez d'abord une préparation.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn {
                    items(approvedPreparations) { prep ->
                        ListItem(
                            headlineContent = { Text(prep.header.lessonSubject) },
                            supportingContent = { Text("${prep.header.branch} - ${prep.header.className}") },
                            modifier = Modifier.clickable { onConfirm(prep.id) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresenceDialog(
    state: ClassLogState,
    onDismiss: () -> Unit,
    onToggle: (Long) -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Élèves présents") },
        text = {
            LazyColumn {
                items(state.presenceStudents) { student ->
                    ListItem(
                        headlineContent = { Text("${student.firstName} ${student.lastName}") },
                        leadingContent = {
                            Checkbox(
                                checked = student.id.toLong() in state.presenceStudentIds,
                                onCheckedChange = { onToggle(student.id.toLong()) }
                            )
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Valider")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
