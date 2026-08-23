package com.drcmind.kelasisuite.ui.schooladmin.academics.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportCardsScreen(
    viewModel: ReportCardsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var classExpanded by remember { mutableStateOf(false) }
    var studentExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState.reportCardSummary != null) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.exportPdf() },
                    icon = { Icon(Icons.Rounded.PictureAsPdf, contentDescription = null) },
                    text = { Text("Télécharger / Imprimer PDF") },
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Print,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bulletins Officiels & Export PDF",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Génération, prévisualisation et impression des bulletins de notes certifiés",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selectors Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Class Selector
                    ExposedDropdownMenuBox(
                        expanded = classExpanded,
                        onExpandedChange = { classExpanded = !classExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedClass?.name ?: "Sélectionner Classe",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Classe") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = classExpanded,
                            onDismissRequest = { classExpanded = false }
                        ) {
                            uiState.classes.forEach { cls ->
                                DropdownMenuItem(
                                    text = { Text(cls.name) },
                                    onClick = {
                                        viewModel.selectClass(cls)
                                        classExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Student Selector
                    ExposedDropdownMenuBox(
                        expanded = studentExpanded,
                        onExpandedChange = { studentExpanded = !studentExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        val selectedStudentName = uiState.selectedStudent?.let {
                            it.fullName.ifEmpty { "${it.lastName} ${it.firstName}" }
                        } ?: "Sélectionner Élève"

                        OutlinedTextField(
                            value = selectedStudentName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Élève") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = studentExpanded,
                            onDismissRequest = { studentExpanded = false }
                        ) {
                            uiState.students.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st.fullName.ifEmpty { "${st.lastName} ${st.firstName}" }) },
                                    onClick = {
                                        viewModel.selectStudent(st)
                                        studentExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Report Card Preview
            val summary = uiState.reportCardSummary

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (summary == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Veuillez sélectionner un élève pour afficher le bulletin.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    item {
                        // Header Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "BULLETIN DE NOTES OFFICIEL",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Élève : ${summary.student.fullName.ifEmpty { "${summary.student.lastName} ${summary.student.firstName}" }}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Classe : ${summary.className}  |  Année Scolaire : ${summary.schoolYear}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Pourcentage: ${formatDecimal(summary.percentage)}%", fontWeight = FontWeight.Bold)
                                    Text("Rang: ${summary.rank}e / ${summary.totalStudentsInClass}", fontWeight = FontWeight.Bold)
                                    Text("Décision: ${summary.decision}", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Détail des Matières & Évaluations",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(summary.subjects) { subject ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = subject.subjectName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = subject.teacherName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "${subject.obtainedPoints} / ${subject.maxPoints}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
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

private fun formatDecimal(value: Double): String {
    val rounded = (value * 10.0).toInt() / 10.0
    return rounded.toString()
}
