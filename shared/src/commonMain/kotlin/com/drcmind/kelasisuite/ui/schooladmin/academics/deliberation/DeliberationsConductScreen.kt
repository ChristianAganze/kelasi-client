package com.drcmind.kelasisuite.ui.schooladmin.academics.deliberation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MoreVert
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
fun DeliberationsConductScreen(
    viewModel: DeliberationsConductViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var classExpanded by remember { mutableStateOf(false) }
    var periodExpanded by remember { mutableStateOf(false) }

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
                        Route.SchoolAdmin.Academics.DeliberationsConduct.List::class,
                        Route.SchoolAdmin.Academics.DeliberationsConduct.List.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.DeliberationsConduct.Detail::class,
                        Route.SchoolAdmin.Academics.DeliberationsConduct.Detail.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Academics.DeliberationsConduct.List
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
        // Horizontal Filter Bar
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            //colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
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
                        value = uiState.selectedClass?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Sélectionner une classe") },
                        label = { Text("Classe") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(expanded = classExpanded, onDismissRequest = { classExpanded = false }) {
                        if (uiState.classes.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Aucune classe disponible", style = MaterialTheme.typography.bodyMedium) },
                                onClick = { classExpanded = false }
                            )
                        } else {
                            uiState.classes.forEach { cls ->
                                DropdownMenuItem(
                                    text = { Text(cls.name) },
                                    onClick = { viewModel.selectClass(cls); classExpanded = false }
                                )
                            }
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
                        trailingIcon = { ExposedDropdownMenuDefaults
                            .TrailingIcon(expanded = periodExpanded)
                        },
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
            }
        }

        NavDisplay(
            modifier = Modifier.fillMaxSize().weight(1f),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            sceneStrategies = listOf(listDetailStrategy),
            entryProvider = entryProvider {
                entry<Route.SchoolAdmin.Academics.DeliberationsConduct.List>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = {
                            EmptyDetailPlaceholder(
                                icon = Icons.Default.Gavel,
                                title = "Aucun élève sélectionné",
                                subtitle = "Sélectionnez un élève pour sa délibération",
                            )
                        }
                    )
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text("Fiches de Conduite",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                },
                                actions = {
                                    IconButton(onClick = {}
                                    ) { Icon(Icons.Default.MoreVert,
                                        null) }
                                }
                            )
                        },
                        floatingActionButton = {
                            if (uiState.studentConducts.isNotEmpty()) {
                                ExtendedFloatingActionButton(
                                    onClick = { viewModel.saveConducts() },
                                    icon = { Icon(Icons.Rounded.Save, null) },
                                    text = { Text("Enregistrer tout") }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                            if (uiState.isLoading) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                            } else if (uiState.studentConducts.isEmpty()) {
                                EmptyStateCard(title = "Aucun élève", subtitle = "Veuillez sélectionner une classe.")
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
                                            Text("APPLICATION", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                        LazyColumn {
                                            items(uiState.studentConducts) { conduct ->
                                                StudentConductRow(
                                                    conductData = conduct,
                                                    isSelected = uiState.activeStudentConduct?.student?.id == conduct.student.id,
                                                    onClick = {
                                                        viewModel.setActiveStudent(conduct.student.id)
                                                        if (backStack.last() != Route.SchoolAdmin.Academics.DeliberationsConduct.List) {
                                                            backStack.removeLastOrNull()
                                                        }
                                                        backStack.add(Route.SchoolAdmin.Academics.DeliberationsConduct.Detail(conduct.student.id))
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
                entry<Route.SchoolAdmin.Academics.DeliberationsConduct.Detail>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) {
                    DeliberationDetailPane(
                        conductData = uiState.activeStudentConduct,
                        onConductChange = { code -> uiState.activeStudentConduct?.let { viewModel.updateConduct(it.student.id, code) } },
                        onCommentChange = { comment -> uiState.activeStudentConduct?.let { viewModel.updateComments(it.student.id, comment) } },
                        onBack = { backStack.removeLastOrNull() },
                        onSave = { viewModel.saveConducts() },
                        isSaving = uiState.isSaving
                    )
                }
            }
        )
    }
}


@Composable
fun StudentConductRow(conductData: StudentConductData, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                Text(conductData.student.fullName.take(1), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(2f)) {
                Text(conductData.student.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(conductData.conduct.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            Text("${conductData.applicationPercentage}%", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliberationDetailPane(
    conductData: StudentConductData?,
    onConductChange: (ConductCode) -> Unit,
    onCommentChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean
) {
    if (conductData == null) return
    var menuExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text(text = "Détails Conduite", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(24.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f))) {
            Column(Modifier.padding(20.dp)) {
                Text(conductData.student.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Application automatique : ${conductData.applicationPercentage}%", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(32.dp))
        ExposedDropdownMenuBox(expanded = menuExpanded, onExpandedChange = { menuExpanded = !menuExpanded }) {
            OutlinedTextField(
                value = conductData.conduct.label,
                onValueChange = {},
                readOnly = true,
                label = { Text("Cote de Conduite") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                ConductCode.entries.forEach { code ->
                    DropdownMenuItem(text = { Text(code.label) }, onClick = { onConductChange(code); menuExpanded = false })
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = conductData.comments,
            onValueChange = onCommentChange,
            label = { Text("Remarques & Appréciations") },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            minLines = 5
        )

        Spacer(Modifier.weight(1f))
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth().height(56.dp), enabled = !isSaving) {
            if (isSaving) CircularProgressIndicator(Modifier.size(24.dp)) else Text("Valider la Fiche")
        }
    }
}
