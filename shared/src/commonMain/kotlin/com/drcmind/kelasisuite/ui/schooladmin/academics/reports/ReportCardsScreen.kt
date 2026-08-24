package com.drcmind.kelasisuite.ui.schooladmin.academics.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.domain.model.academic.AcademicDecision
import com.drcmind.kelasisuite.domain.model.academic.ClassPalmaresSummary
import com.drcmind.kelasisuite.domain.model.academic.StudentPalmaresItem
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportCardsScreen(
    viewModel: ReportCardsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var classExpanded by remember { mutableStateOf(false) }
    var periodExpanded by remember { mutableStateOf(false) }
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
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Bulletins & Palmarès Officiels", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Synthèse académique, délibérations et exports certifiés MINEPST",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (!uiState.isPeriodLocked) {
                        OutlinedButton(
                            onClick = { viewModel.lockPeriodAndOfficialize() },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Clôturer la Période")
                        }
                    } else {
                        Surface(
                            color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Période Clôturée & Verrouillée", style = MaterialTheme.typography.labelMedium, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.exportPdf() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Exporter PDF")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            // Barre de Filtres & Commutateur de Mode (Palmarès vs Bulletin Individuel)
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Segmented Button Mode
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = uiState.viewMode == ReportViewMode.CLASS_PALMARES,
                            onClick = { viewModel.setViewMode(ReportViewMode.CLASS_PALMARES) },
                            shape = SegmentedButtonDefaults.itemShape(0, 2)
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Palmarès Général de la Classe")
                        }
                        SegmentedButton(
                            selected = uiState.viewMode == ReportViewMode.INDIVIDUAL_BULLETIN,
                            onClick = { viewModel.setViewMode(ReportViewMode.INDIVIDUAL_BULLETIN) },
                            shape = SegmentedButtonDefaults.itemShape(1, 2)
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bulletin Individuel de l'Élève")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filtres Dropdowns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Classe
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
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(expanded = classExpanded, onDismissRequest = { classExpanded = false }) {
                                uiState.classes.forEach { cls ->
                                    DropdownMenuItem(
                                        text = { Text(cls.name) },
                                        onClick = { viewModel.selectClass(cls); classExpanded = false }
                                    )
                                }
                            }
                        }

                        // Période
                        ExposedDropdownMenuBox(
                            expanded = periodExpanded,
                            onExpandedChange = { periodExpanded = !periodExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = uiState.selectedPeriod?.label ?: "Période",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Période d'évaluation") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodExpanded) },
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(expanded = periodExpanded, onDismissRequest = { periodExpanded = false }) {
                                uiState.evaluationPeriods.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p.label) },
                                        onClick = { viewModel.selectPeriod(p); periodExpanded = false }
                                    )
                                }
                            }
                        }

                        // Élève (Si mode individuel)
                        if (uiState.viewMode == ReportViewMode.INDIVIDUAL_BULLETIN) {
                            ExposedDropdownMenuBox(
                                expanded = studentExpanded,
                                onExpandedChange = { studentExpanded = !studentExpanded },
                                modifier = Modifier.weight(1.2f)
                            ) {
                                val name = uiState.selectedStudent?.let {
                                    it.fullName.ifEmpty { "${it.lastName} ${it.firstName}" }
                                } ?: "Sélectionner Élève"
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Élève") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentExpanded) },
                                    modifier = Modifier.menuAnchor(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                ExposedDropdownMenu(expanded = studentExpanded, onDismissRequest = { studentExpanded = false }) {
                                    uiState.students.forEach { st ->
                                        DropdownMenuItem(
                                            text = { Text(st.fullName.ifEmpty { "${st.lastName} ${st.firstName}" }) },
                                            onClick = { viewModel.selectStudent(st); studentExpanded = false }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Corps principal selon le mode sélectionné
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.viewMode == ReportViewMode.CLASS_PALMARES) {
                    val palmares = uiState.classPalmares
                    if (palmares != null) {
                        ClassPalmaresView(palmares = palmares)
                    } else {
                        EmptyPlaceholder(message = "Sélectionnez une classe et une période pour charger le palmarès.")
                    }
                } else {
                    val summary = uiState.reportCardSummary
                    if (summary != null) {
                        IndividualReportCardView(summary = summary)
                    } else {
                        EmptyPlaceholder(message = "Sélectionnez un élève pour afficher son bulletin officiel.")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. Vue Palmarès Général de la Classe
// -------------------------------------------------------------

@Composable
fun ClassPalmaresView(palmares: ClassPalmaresSummary) {
    Column(modifier = Modifier.fillMaxSize()) {
        // En-tête des Statistiques de la Classe
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiStatCard(title = "Effectif Total", value = "${palmares.totalStudents} élèves", color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.weight(1f))
            KpiStatCard(title = "Moyenne Générale", value = "${formatDecimal(palmares.classAveragePercentage)}%", color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.weight(1f))
            KpiStatCard(title = "Taux de Réussite", value = "${formatDecimal(palmares.passRatePercentage)}%", color = Color(0xFF2E7D32).copy(alpha = 0.15f), modifier = Modifier.weight(1f))
            KpiStatCard(title = "Note Max / Min", value = "${formatDecimal(palmares.highestPercentage)}% / ${formatDecimal(palmares.lowestPercentage)}%", color = MaterialTheme.colorScheme.tertiaryContainer, modifier = Modifier.weight(1.2f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Table du Palmarès
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Table
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rang", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(55.dp))
                        Text("Nom & Postnom de l'élève", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Total Points", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp), textAlign = TextAlign.Center)
                        Text("Pourcentage", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp), textAlign = TextAlign.Center)
                        Text("Conduite", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp), textAlign = TextAlign.Center)
                        Text("Décision du Jury", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp), textAlign = TextAlign.End)
                    }
                }

                HorizontalDivider()

                // Items
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(palmares.students, key = { it.studentId }) { student ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (student.rank <= 3) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${student.rank}e",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (student.rank <= 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(23.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(student.studentName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(student.rollNumber, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Text(
                                text = "${formatDecimal(student.totalObtained)} / ${student.totalMax.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(100.dp),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "${formatDecimal(student.percentage)}%",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (student.percentage >= 50.0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                modifier = Modifier.width(100.dp),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = student.conductLabel,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.width(100.dp),
                                textAlign = TextAlign.Center
                            )

                            Box(modifier = Modifier.width(120.dp), contentAlignment = Alignment.CenterEnd) {
                                DecisionBadge(decision = student.decision)
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. Vue Bulletin Officiel Individuel
// -------------------------------------------------------------

@Composable
fun IndividualReportCardView(summary: ReportCardSummary) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Cartouche Officiel MINEPST
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BULLETIN DE NOTES OFFICIEL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "Certifié MINEPST",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = summary.student.fullName.ifEmpty { "${summary.student.lastName} ${summary.student.firstName}" },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Classe : ${summary.className}  •  Année Scolaire : ${summary.schoolYear}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total : ${formatDecimal(summary.totalObtained)} / ${summary.totalMax.toInt()} pts", fontWeight = FontWeight.Bold)
                        Text("Pourcentage : ${formatDecimal(summary.percentage)}%", fontWeight = FontWeight.ExtraBold, color = if (summary.percentage >= 50.0) Color(0xFF1B5E20) else MaterialTheme.colorScheme.error)
                        Text("Rang : ${summary.rank}e sur ${summary.totalStudentsInClass}", fontWeight = FontWeight.Bold)
                        Text("Conduite : ${summary.conductLabel}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Détail par Discipline d'Enseignement", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(summary.subjects) { subject ->
            OutlinedCard(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(subject.subjectName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(subject.teacherName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Text(
                            text = "${subject.obtainedPoints} / ${subject.maxPoints.toInt()} pts",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Composants Visuels Utilitaires
// -------------------------------------------------------------

@Composable
fun DecisionBadge(decision: AcademicDecision) {
    val (color, bgColor) = when (decision) {
        AcademicDecision.ADMITTED -> Color(0xFF2E7D32) to Color(0xFFE8F5E9)
        AcademicDecision.ADMITTED_WITH_WARNING -> Color(0xFFF57C00) to Color(0xFFFFF3E0)
        AcademicDecision.CONDITIONAL -> Color(0xFF1976D2) to Color(0xFFE3F2FD)
        AcademicDecision.RETAKE, AcademicDecision.EXPELLED -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.errorContainer
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = decision.shortCode,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun KpiStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EmptyPlaceholder(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDecimal(value: Double): String {
    val rounded = (value * 10.0).toInt() / 10.0
    return rounded.toString()
}
