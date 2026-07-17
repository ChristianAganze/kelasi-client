package com.drcmind.kelasisuite.ui.teacheradmin.classlog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Save
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
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassLogScreen(
    modifier: Modifier = Modifier,
    viewModel: ClassLogViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saveSuccess, state.errorMessage) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar("Journaux de classe sauvegardés avec succès.")
            viewModel.dismissSnackbar()
        } else if (state.errorMessage != null) {
            snackbarHostState.showSnackbar(state.errorMessage ?: "Erreur.")
            viewModel.dismissSnackbar()
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
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.scheduleToday.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Aucun cours n'est prévu pour vous aujourd'hui.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(state.scheduleToday) { entry ->
                    ClassLogCard(
                        entry = entry,
                        onLinkClick = { viewModel.selectEntryForLinking(entry.id) },
                        onStatusClick = { viewModel.selectEntryForStatusUpdate(entry.id) }
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
}

@Composable
fun ClassLogCard(
    entry: ClassLogEntry,
    onLinkClick: () -> Unit,
    onStatusClick: () -> Unit
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
            }
            
            // Action
            Button(onClick = onStatusClick) {
                Text(if (entry.status == LogStatus.NOT_STARTED) "Démarrer" else "Bilan")
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
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lier une préparation") },
        text = {
            LazyColumn {
                items(state.availablePreparations) { prep ->
                    ListItem(
                        headlineContent = { Text(prep.header.lessonSubject) },
                        supportingContent = { Text("${prep.header.branch} - ${prep.header.className}") },
                        modifier = Modifier.clickable { onConfirm(prep.id) }
                    )
                    HorizontalDivider()
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
