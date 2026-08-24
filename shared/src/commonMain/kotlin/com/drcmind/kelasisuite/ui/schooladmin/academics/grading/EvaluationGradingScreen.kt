package com.drcmind.kelasisuite.ui.schooladmin.academics.grading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.EmptyDetailPlaceholder
import com.drcmind.kelasisuite.ui.components.EmptyStateCard
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun EvaluationGradingScreen(
    viewModel: EvaluationGradingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var classExpanded by remember { mutableStateOf(false) }
    var periodExpanded by remember { mutableStateOf(false) }
    var assignmentExpanded by remember { mutableStateOf(false) }

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

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Academics.EvaluationGrading.List::class,
                        Route.SchoolAdmin.Academics.EvaluationGrading.List.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.EvaluationGrading.Detail::class,
                        Route.SchoolAdmin.Academics.EvaluationGrading.Detail.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Academics.EvaluationGrading.List
    )

    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(
                horizontalPartitionSpacerSize = 0.dp,
                verticalPartitionSpacerSize = 0.dp,
                defaultPanePreferredWidth = 800.dp
            )
    }

    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
        directive = directive,
        adaptStrategies = ListDetailPaneScaffoldDefaults.adaptStrategies()
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Option A: Horizontal Filter Bar
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Class Selector
                ExposedDropdownMenuBox(
                    expanded = classExpanded,
                    onExpandedChange = { classExpanded = !classExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = uiState.selectedClass?.name ?: "Classe",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Classe") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(8.dp)
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

                // Period Selector
                ExposedDropdownMenuBox(
                    expanded = periodExpanded,
                    onExpandedChange = { periodExpanded = !periodExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = uiState.selectedPeriod?.label ?: "Période",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Période") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodExpanded) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(8.dp)
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

                // Assignment Selector
                ExposedDropdownMenuBox(
                    expanded = assignmentExpanded,
                    onExpandedChange = { assignmentExpanded = !assignmentExpanded },
                    modifier = Modifier.weight(1.5f)
                ) {
                    OutlinedTextField(
                        value = uiState.selectedAssignment?.subjectName ?: "Discipline",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cours / Discipline") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assignmentExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(expanded = assignmentExpanded, onDismissRequest = { assignmentExpanded = false }) {
                        uiState.assignments.forEach { a ->
                            DropdownMenuItem(
                                text = { Text("${a.subjectName} (${a.teacherName})") },
                                onClick = { viewModel.selectAssignment(a); assignmentExpanded = false }
                            )
                        }
                    }
                }
            }
        }

        NavDisplay(
            modifier = Modifier.fillMaxSize().weight(1f),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            sceneStrategies = listOf(listDetailStrategy),
            entryProvider = entryProvider {
                entry<Route.SchoolAdmin.Academics.EvaluationGrading.List>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = {
                            EmptyDetailPlaceholder(
                                icon = Icons.Default.Numbers,
                                title = "Aucun élève sélectionné",
                                subtitle = "Sélectionnez un élève pour saisir sa note",
                            )
                        }
                    )
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text("Liste des élèves", style = MaterialTheme.typography.titleLarge)
                                },
                                actions = {
                                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null) }
                                }
                            )
                        },
                        floatingActionButton = {
                            if (uiState.grades.isNotEmpty()) {
                                ExtendedFloatingActionButton(
                                    onClick = { viewModel.saveGrades() },
                                    icon = { Icon(Icons.Rounded.Save, null) },
                                    text = { Text("Enregistrer tout") }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                            if (uiState.isLoading) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                            } else if (uiState.error != null && uiState.classes.isEmpty()) {
                                ErrorStateCard(message = uiState.error, onRetry = { viewModel.loadClassesAndPeriods() })
                            } else if (uiState.classes.isEmpty()) {
                                EmptyStateCard(title = "Aucune classe", subtitle = "Veuillez configurer la structure.")
                            } else {
                                OutlinedCard(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                                    shape = MaterialTheme.shapes.medium.copy(bottomEnd = CornerSize(0.dp), bottomStart = CornerSize(0.dp))
                                ) {
                                    Column {
                                        Row(
                                            Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("NOM DE L'ÉLÈVE", Modifier.weight(2f), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                            Text("STATUT", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                        LazyColumn {
                                            items(uiState.students) { student ->
                                                val hasGrade = uiState.grades.containsKey(student.id)
                                                StudentGradeRow(
                                                    student = student,
                                                    hasGrade = hasGrade,
                                                    isSelected = uiState.activeStudent?.id == student.id,
                                                    onClick = {
                                                        viewModel.setActiveStudent(student)
                                                        if (backStack.last() != Route.SchoolAdmin.Academics.EvaluationGrading.List) {
                                                            backStack.removeLastOrNull()
                                                        }
                                                        backStack.add(Route.SchoolAdmin.Academics.EvaluationGrading.Detail(student.id))
                                                    }
                                                )
                                                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                entry<Route.SchoolAdmin.Academics.EvaluationGrading.Detail>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) {
                    GradingDetailPane(
                        student = uiState.activeStudent,
                        currentGrade = uiState.activeStudent?.let { uiState.grades[it.id] } ?: "",
                        onGradeChange = { grade -> uiState.activeStudent?.let { viewModel.updateStudentGrade(it.id, grade) } },
                        onBack = { backStack.removeLastOrNull() },
                        onSave = { viewModel.saveGrades() },
                        isSaving = uiState.isSaving
                    )
                }
            }
        )
    }
}

@Composable
fun StudentGradeRow(
    student: StudentDTO,
    hasGrade: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(40.dp).background(
                    if (hasGrade) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(student.fullName.take(1), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(2f)) {
                Text(student.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Matricule: ${student.studentIdNumber}", style = MaterialTheme.typography.bodySmall)
            }
            if (hasGrade) {
                Badge(containerColor = MaterialTheme.colorScheme.primary) { Text("Noté") }
            } else {
                Badge(containerColor = MaterialTheme.colorScheme.outline) { Text("À noter") }
            }
        }
    }
}

@Composable
fun GradingDetailPane(
    student: StudentDTO?,
    currentGrade: String,
    onGradeChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean
) {
    if (student == null) return

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text(text = "Saisie de note", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        
        Spacer(Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(student.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Matricule: ${student.studentIdNumber}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = currentGrade,
            onValueChange = onGradeChange,
            label = { Text("Note obtenue") },
            placeholder = { Text("Ex: 15.5") },
            suffix = { Text("/ 20") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(16.dp))
        Text("La note sera automatiquement enregistrée dans le brouillon local.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isSaving && currentGrade.isNotBlank()
        ) {
            if (isSaving) {
                CircularProgressIndicator(Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Confirmer et Valider")
            }
        }
    }
}
