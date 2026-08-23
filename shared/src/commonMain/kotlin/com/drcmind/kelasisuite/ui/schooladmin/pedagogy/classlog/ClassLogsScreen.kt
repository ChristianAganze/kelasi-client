@file:OptIn(ExperimentalMaterial3Api::class)

package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.classlog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.data.datasource.remote.dto.ClassLogReviewDto
import com.drcmind.kelasisuite.ui.components.EmptyStateCard
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import com.drcmind.kelasisuite.ui.components.friendlyErrorMessage
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClassLogsScreen(
    viewModel: ClassLogsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val visibleLogs = when (state.filter) {
        ClassLogFilter.ALL -> state.logs
        ClassLogFilter.UNSIGNED -> state.logs.filter { !it.adminSignature }
        ClassLogFilter.SIGNED -> state.logs.filter { it.adminSignature }
    }

    LaunchedEffect(state.error) {
        if (state.error != null && state.logs.isNotEmpty()) {
            snackbarHostState.showSnackbar(friendlyErrorMessage(state.error))
            viewModel.dismissError()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Journaux de classe",
                            style = MaterialTheme.typography.displaySmall,
                        )
                        Text(
                            text = "Audit des journaux soumis par les enseignants — signez les journaux vérifiés.",
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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 32.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterSection(
                filter = state.filter,
                onSelect = viewModel::setFilter,
                totalCount = state.logs.size,
                unsignedCount = state.logs.count { !it.adminSignature },
                signedCount = state.logs.count { it.adminSignature }
            )

            when {
                state.isLoading && state.logs.isEmpty() -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.weight(1f))
                }

                state.error != null && state.logs.isEmpty() -> {
                    ErrorStateCard(
                        message = state.error,
                        onRetry = viewModel::loadClassLogs
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                state.logs.isEmpty() -> {
                    EmptyStateCard(
                        title = "Aucun journal de classe à auditer.",
                        subtitle = "Les journaux soumis par les enseignants apparaîtront ici."
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        if (visibleLogs.isEmpty()) {
                            item {
                                Text(
                                    text = "Aucun journal dans cette catégorie.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        }
                        items(visibleLogs, key = { it.id }) { log ->
                            ClassLogCard(
                                log = log,
                                onClick = { viewModel.openDetail(log) }
                            )
                        }
                    }
                }
            }
        }
    }

    state.selectedLog?.let { selected ->
        ClassLogDetailDialog(
            log = selected,
            isActionInProgress = state.isActionInProgress,
            onDismiss = viewModel::dismissDetail,
            onSign = viewModel::signClassLog
        )
    }
}

@Composable
private fun FilterSection(
    filter: ClassLogFilter,
    onSelect: (ClassLogFilter) -> Unit,
    totalCount: Int,
    unsignedCount: Int,
    signedCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilterChip(
            selected = filter == ClassLogFilter.ALL,
            onClick = { onSelect(ClassLogFilter.ALL) },
            label = { Text("Toutes ($totalCount)") }
        )
        FilterChip(
            selected = filter == ClassLogFilter.UNSIGNED,
            onClick = { onSelect(ClassLogFilter.UNSIGNED) },
            label = { Text("En attente ($unsignedCount)") }
        )
        FilterChip(
            selected = filter == ClassLogFilter.SIGNED,
            onClick = { onSelect(ClassLogFilter.SIGNED) },
            label = { Text("Signés ($signedCount)") }
        )
    }
}

@Composable
private fun ClassLogCard(
    log: ClassLogReviewDto,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.taughtSubject.ifBlank { log.subject },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${log.className} • ${log.teacherName}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = listOfNotNull(
                        log.date.takeIf { it.isNotBlank() },
                        log.timeSlot.takeIf { it.isNotBlank() }
                    ).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            StatusChip(signed = log.adminSignature)
        }
    }
}

@Composable
private fun ClassLogDetailDialog(
    log: ClassLogReviewDto,
    isActionInProgress: Boolean,
    onDismiss: () -> Unit,
    onSign: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isActionInProgress) onDismiss() },
        title = {
            Column {
                Text(
                    text = log.taughtSubject.ifBlank { log.subject },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = listOfNotNull(
                        log.className,
                        log.teacherName,
                        log.date.takeIf { it.isNotBlank() }
                    ).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailRow(label = "Classe", value = log.className)
                DetailRow(label = "Enseignant", value = log.teacherName)
                DetailRow(label = "Matière", value = log.subject)
                DetailRow(label = "Contenu enseigné", value = log.taughtSubject)
                DetailRow(label = "Devoirs", value = log.homework)
                if (log.timeSlot.isNotBlank()) {
                    DetailRow(label = "Créneau horaire", value = log.timeSlot)
                }
                DetailRow(label = "Date", value = log.date)
                DetailRow(
                    label = "Signature enseignant",
                    value = if (log.teacherSignature) "Signé" else "Non signé"
                )
                DetailRow(
                    label = "Signature préfet",
                    value = if (log.adminSignature) "Signé" else "En attente"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSign,
                enabled = !isActionInProgress && !log.adminSignature
            ) {
                if (isActionInProgress) {
                    Text("En cours...")
                } else if (log.adminSignature) {
                    Text("Signé")
                } else {
                    Text("Signer")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isActionInProgress
            ) {
                Text("Fermer")
            }
        }
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StatusChip(signed: Boolean) {
    val color = if (signed) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (signed) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = color
                )
            }
            Text(
                text = if (signed) "Signé" else "En attente",
                color = color,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
