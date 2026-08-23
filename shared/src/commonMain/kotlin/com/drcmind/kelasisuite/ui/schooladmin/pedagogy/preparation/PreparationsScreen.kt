@file:OptIn(ExperimentalMaterial3Api::class)

package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.preparation

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
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
import com.drcmind.kelasisuite.data.datasource.remote.dto.PreparationReviewDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.PreparationReviewStatusDto
import com.drcmind.kelasisuite.ui.components.EmptyStateCard
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import com.drcmind.kelasisuite.ui.components.friendlyErrorMessage
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PreparationsScreen(
    viewModel: PreparationsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val visiblePreparations = when (state.filter) {
        PreparationFilter.ALL -> state.preparations
        PreparationFilter.PENDING -> state.preparations.filter {
            it.status == PreparationReviewStatusDto.PENDING
        }
        PreparationFilter.VALIDATED -> state.preparations.filter {
            it.status == PreparationReviewStatusDto.VALIDATED
        }
        PreparationFilter.REJECTED -> state.preparations.filter {
            it.status == PreparationReviewStatusDto.REJECTED
        }
    }

    LaunchedEffect(state.error) {
        if (state.error != null && state.preparations.isNotEmpty()) {
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
                            text = "Préparations",
                            style = MaterialTheme.typography.displaySmall,
                        )
                        Text(
                            text = "Boîte de réception des fiches de préparation pour validation par le Préfet.",
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
                pendingCount = state.preparations.count {
                    it.status == PreparationReviewStatusDto.PENDING
                },
                validatedCount = state.preparations.count {
                    it.status == PreparationReviewStatusDto.VALIDATED
                },
                rejectedCount = state.preparations.count {
                    it.status == PreparationReviewStatusDto.REJECTED
                }
            )

            when {
                state.isLoading && state.preparations.isEmpty() -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.weight(1f))
                }

                state.error != null && state.preparations.isEmpty() -> {
                    ErrorStateCard(
                        message = state.error,
                        onRetry = viewModel::loadPreparations
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                state.preparations.isEmpty() -> {
                    EmptyStateCard(
                        title = "Aucune fiche de préparation à valider.",
                        subtitle = "Les fiches soumises par les enseignants apparaîtront ici."
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        if (visiblePreparations.isEmpty()) {
                            item {
                                Text(
                                    text = "Aucune fiche dans cette catégorie.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        }
                        items(visiblePreparations, key = { it.id }) { preparation ->
                            PreparationCard(
                                preparation = preparation,
                                onClick = { viewModel.openDetail(preparation) }
                            )
                        }
                    }
                }
            }
        }
    }

    state.selectedPreparation?.let { selected ->
        PreparationDetailDialog(
            preparation = selected,
            isActionInProgress = state.isActionInProgress,
            onDismiss = viewModel::dismissDetail,
            onValidate = viewModel::validatePreparation,
            onReject = viewModel::showRejectDialog
        )
    }

    if (state.showRejectDialog) {
        RejectDialog(
            comment = state.rejectComment,
            isActionInProgress = state.isActionInProgress,
            onCommentChange = viewModel::updateRejectComment,
            onConfirm = viewModel::rejectPreparation,
            onDismiss = viewModel::dismissRejectDialog
        )
    }
}

@Composable
private fun FilterSection(
    filter: PreparationFilter,
    onSelect: (PreparationFilter) -> Unit,
    pendingCount: Int,
    validatedCount: Int,
    rejectedCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilterChip(
            selected = filter == PreparationFilter.ALL,
            onClick = { onSelect(PreparationFilter.ALL) },
            label = { Text("Toutes (${pendingCount + validatedCount + rejectedCount})") }
        )
        FilterChip(
            selected = filter == PreparationFilter.PENDING,
            onClick = { onSelect(PreparationFilter.PENDING) },
            label = { Text("En attente ($pendingCount)") }
        )
        FilterChip(
            selected = filter == PreparationFilter.VALIDATED,
            onClick = { onSelect(PreparationFilter.VALIDATED) },
            label = { Text("Validées ($validatedCount)") }
        )
        FilterChip(
            selected = filter == PreparationFilter.REJECTED,
            onClick = { onSelect(PreparationFilter.REJECTED) },
            label = { Text("Rejetées ($rejectedCount)") }
        )
    }
}

@Composable
private fun PreparationCard(
    preparation: PreparationReviewDto,
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
                    text = preparation.subject,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${preparation.className} • ${preparation.teacherName}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = preparation.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            StatusChip(status = preparation.status)
        }
    }
}

@Composable
private fun PreparationDetailDialog(
    preparation: PreparationReviewDto,
    isActionInProgress: Boolean,
    onDismiss: () -> Unit,
    onValidate: () -> Unit,
    onReject: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isActionInProgress) onDismiss() },
        title = {
            Column {
                Text(
                    text = preparation.subject,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${preparation.className} • ${preparation.teacherName} • ${preparation.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow(label = "Classe", value = preparation.className)
                DetailRow(label = "Enseignant", value = preparation.teacherName)
                DetailRow(label = "Matière", value = preparation.subject)
                DetailRow(label = "Date", value = preparation.date)
                DetailRow(label = "Objectif opérationnel", value = preparation.operationalObjective)
                DetailRow(label = "Référence", value = preparation.reference)

                PhaseSection(title = "Phase d'introduction", content = preparation.introPhase)
                PhaseSection(title = "Phase de développement", content = preparation.developmentPhase)
                PhaseSection(title = "Phase de synthèse", content = preparation.synthesisPhase)
                PhaseSection(title = "Phase d'application", content = preparation.applicationPhase)

                preparation.comment?.let { comment ->
                    DetailRow(label = "Commentaire du Préfet", value = comment)
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onReject,
                    enabled = !isActionInProgress &&
                        preparation.status != PreparationReviewStatusDto.REJECTED
                ) {
                    Text("Rejeter")
                }
                Button(
                    onClick = onValidate,
                    enabled = !isActionInProgress &&
                        preparation.status != PreparationReviewStatusDto.VALIDATED
                ) {
                    if (isActionInProgress) {
                        Text("En cours...")
                    } else {
                        Text("Valider")
                    }
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
private fun RejectDialog(
    comment: String,
    isActionInProgress: Boolean,
    onCommentChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isActionInProgress) onDismiss() },
        title = {
            Text("Rejeter la préparation", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Un commentaire peut être ajouté pour indiquer à l'enseignant les corrections à apporter.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Commentaire (facultatif)") },
                    enabled = !isActionInProgress
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isActionInProgress,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (isActionInProgress) {
                    Text("En cours...")
                } else {
                    Text("Rejeter")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isActionInProgress
            ) {
                Text("Annuler")
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
private fun PhaseSection(
    title: String,
    content: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StatusChip(status: PreparationReviewStatusDto) {
    val color = statusColor(status)
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (status == PreparationReviewStatusDto.VALIDATED) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = color
                )
            }
            Text(
                text = statusLabel(status),
                color = color,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun statusColor(status: PreparationReviewStatusDto): Color = when (status) {
    PreparationReviewStatusDto.VALIDATED -> MaterialTheme.colorScheme.primary
    PreparationReviewStatusDto.REJECTED -> MaterialTheme.colorScheme.error
    PreparationReviewStatusDto.PENDING -> MaterialTheme.colorScheme.tertiary
}

@Composable
private fun statusLabel(status: PreparationReviewStatusDto): String = when (status) {
    PreparationReviewStatusDto.VALIDATED -> "Validée"
    PreparationReviewStatusDto.REJECTED -> "Rejetée"
    PreparationReviewStatusDto.PENDING -> "En attente"
}
