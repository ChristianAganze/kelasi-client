package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.inspections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.domain.model.pedagogy.ClassInspectionReport
import com.drcmind.kelasisuite.domain.model.pedagogy.InspectionRating
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionsScreen(
    viewModel: InspectionsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Inspections & Visites de Classe", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Évaluation didactique, coaching des enseignants et rapports officiels",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.openCreateDialog() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AddModerator, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nouvelle Visite")
                    }
                }
            )
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Liste des Visites à Gauche
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Rechercher un professeur, matière...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    val filteredList = uiState.reports.filter {
                        it.teacherName.contains(uiState.searchQuery, ignoreCase = true) ||
                        it.subjectName.contains(uiState.searchQuery, ignoreCase = true) ||
                        it.classroomName.contains(uiState.searchQuery, ignoreCase = true)
                    }

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredList, key = { it.id }) { report ->
                            val isSelected = uiState.selectedReport?.id == report.id
                            Surface(
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectReport(report) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(report.rating.colorHex).copy(alpha = 0.15f),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${report.globalScore}%",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(report.rating.colorHex)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(report.teacherName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            Text(report.inspectionDate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text("${report.subjectName} • ${report.classroomName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        Text(report.lessonTopic, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                    }
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        }
                    }
                }
            }

            // Volet Détail du Rapport à Droite
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.weight(1.3f).fillMaxHeight()
            ) {
                val rep = uiState.selectedReport
                if (rep != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // En-tête Rapport
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(rep.rating.colorHex).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${rep.rating.label} (${rep.globalScore}/100)",
                                    color = Color(rep.rating.colorHex),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            Text(
                                text = "Visite du ${rep.inspectionDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(rep.teacherName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Classe : ${rep.classroomName} • Discipline : ${rep.subjectName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Sujet / Leçon : « ${rep.lessonTopic} »",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                SectionCard(
                                    title = "Points Forts & Aspects Positifs",
                                    content = rep.strengths,
                                    icon = Icons.Default.CheckCircle,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            item {
                                SectionCard(
                                    title = "Points à Améliorer & Faiblesses",
                                    content = rep.areasForImprovement.ifBlank { "Aucune faiblesse majeure signalée." },
                                    icon = Icons.Default.Warning,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            item {
                                SectionCard(
                                    title = "Recommandations & Directives Didactiques",
                                    content = rep.recommendations,
                                    icon = Icons.Default.Lightbulb,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            if (rep.teacherFeedback.isNotBlank()) {
                                item {
                                    SectionCard(
                                        title = "Accusé de réception & Remarques de l'enseignant",
                                        content = rep.teacherFeedback,
                                        icon = Icons.Default.Comment,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Inspecteur : ${rep.inspectorName} (${rep.inspectorRole})",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (rep.isAcknowledgedByTeacher) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "Vu & Notifié à l'enseignant",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Sélectionnez une visite de classe", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (uiState.isCreateDialogOpen) {
        CreateInspectionDialog(
            form = uiState.formState,
            isSubmitting = uiState.isSubmitting,
            onDismiss = { viewModel.closeCreateDialog() },
            onUpdate = { viewModel.updateForm(it) },
            onSubmit = { viewModel.submitReport() }
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(content, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
        }
    }
}

@Composable
fun CreateInspectionDialog(
    form: InspectionFormState,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onUpdate: (InspectionFormState.() -> InspectionFormState) -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvelle Visite d'Inspection Didactique", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = form.teacherName,
                        onValueChange = { onUpdate { copy(teacherName = it) } },
                        label = { Text("Nom du Professeur inspecté") },
                        placeholder = { Text("ex: Prof. Kasongo Ilunga") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = form.classroomName,
                            onValueChange = { onUpdate { copy(classroomName = it) } },
                            label = { Text("Classe") },
                            placeholder = { Text("ex: 6ème Math-Physique A") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = form.subjectName,
                            onValueChange = { onUpdate { copy(subjectName = it) } },
                            label = { Text("Discipline / Matière") },
                            placeholder = { Text("ex: Physique") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = form.lessonTopic,
                        onValueChange = { onUpdate { copy(lessonTopic = it) } },
                        label = { Text("Thème / Sujet de la leçon") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = form.globalScore,
                        onValueChange = { onUpdate { copy(globalScore = it) } },
                        label = { Text("Note d'évaluation globale (sur 100)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = form.strengths,
                        onValueChange = { onUpdate { copy(strengths = it) } },
                        label = { Text("Points Forts constatés") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = form.areasForImprovement,
                        onValueChange = { onUpdate { copy(areasForImprovement = it) } },
                        label = { Text("Points à Améliorer") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = form.recommendations,
                        onValueChange = { onUpdate { copy(recommendations = it) } },
                        label = { Text("Directives & Recommandations didactiques") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onSubmit, enabled = !isSubmitting) {
                if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else Text("Enregistrer le Rapport")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
