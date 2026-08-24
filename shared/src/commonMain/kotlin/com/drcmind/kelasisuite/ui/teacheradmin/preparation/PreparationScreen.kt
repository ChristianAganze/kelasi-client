package com.drcmind.kelasisuite.ui.teacheradmin.preparation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.domain.model.teacher.LessonPreparation
import com.drcmind.kelasisuite.domain.model.teacher.PreparationStatus
import com.drcmind.kelasisuite.domain.model.teacher.labelFr
import com.drcmind.kelasisuite.ui.components.EmptyStateCard
import com.drcmind.kelasisuite.ui.components.ErrorStateCard
import com.drcmind.kelasisuite.ui.components.LoadingState
import com.drcmind.kelasisuite.ui.components.signature.ElectronicSignatureDialog
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun preparationStatusColor(status: PreparationStatus): Color = when (status) {
    PreparationStatus.DRAFT -> MaterialTheme.colorScheme.secondary
    PreparationStatus.SUBMITTED -> MaterialTheme.colorScheme.tertiary
    PreparationStatus.APPROVED -> MaterialTheme.colorScheme.primary
    PreparationStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
    PreparationStatus.ARCHIVED -> MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun preparationStatusContentColor(status: PreparationStatus): Color = when (status) {
    PreparationStatus.DRAFT -> MaterialTheme.colorScheme.onSecondary
    PreparationStatus.SUBMITTED -> MaterialTheme.colorScheme.onTertiary
    PreparationStatus.APPROVED -> MaterialTheme.colorScheme.onPrimary
    PreparationStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer
    PreparationStatus.ARCHIVED -> MaterialTheme.colorScheme.onSurfaceVariant
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreparationScreen(
    modifier: Modifier = Modifier,
    viewModel: PreparationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saveSuccess, state.saveError) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar("Préparation sauvegardée avec succès.")
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
        Box(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
                targetState = state.isCreating,
                label = "Preparation Screen Transition"
            ) { creating ->
                if (creating) {
                    PreparationCreateScreen(
                        state = state,
                        viewModel = viewModel
                    )
                } else {
                    PreparationListScreen(
                        state = state,
                        viewModel = viewModel,
                        onCreateClick = { viewModel.startCreating() }
                    )
                }
            }
        }
    }

    if (state.isWordPreviewOpen && state.previewPreparation != null) {
        WordPreparationPreviewDialog(
            preparation = state.previewPreparation!!,
            onDismiss = { viewModel.closePreview() }
        )
    }

    if (state.showTemplateSelector) {
        TemplateSelectorDialog(
            onDismiss = { viewModel.closeTemplateSelector() },
            onSelectTemplate = { viewModel.applyTemplate(it) }
        )
    }

    if (state.showSignatureDialog && state.signingPreparation != null) {
        val prep = state.signingPreparation!!
        ElectronicSignatureDialog(
            signerName = "Enseignant titulaire",
            signerRole = "${prep.header.branch} • ${prep.header.className}",
            documentTitle = "Fiche de préparation - ${prep.header.lessonSubject}",
            onDismiss = { viewModel.closeSignatureDialog() },
            onConfirmSignature = { signature ->
                viewModel.applySignatureAndSubmit(signature)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreparationListScreen(
    state: PreparationState,
    viewModel: PreparationViewModel,
    onCreateClick: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(Icons.Default.Add,
                    contentDescription = "Nouvelle préparation")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fiches de Préparation",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(
                        onClick = { viewModel.openTemplateSelector() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Modèles (Templates)")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (state.availableAssignments.isNotEmpty()) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.availableAssignments) { assignment ->
                            FilterChip(
                                selected = state.selectedAssignment?.id == assignment.id,
                                onClick = { viewModel.selectAssignment(assignment) },
                                label = { Text("${assignment.className} - ${assignment.subjectName}") }
                            )
                        }
                    }
                }
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
            } else if (state.preparations.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "Aucune fiche de préparation",
                        subtitle = "Créez votre première fiche pour cette classe avec le bouton +."
                    )
                }
            } else {
                items(state.preparations) { prep ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(prep.header.lessonSubject,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    color = preparationStatusColor(prep.status),
                                    shape = MaterialTheme.shapes.small,
                                    contentColor = preparationStatusContentColor(prep.status)
                                ) {
                                    Text(
                                        text = prep.status.labelFr(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${prep.header.branch} - ${prep.header.className}", style = MaterialTheme.typography.bodyMedium)
                            Text("Obj: ${prep.header.operationalObjective.take(50)}...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.openPreview(prep) },
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text("Aperçu Document (Word)")
                                }

                                if (prep.status == PreparationStatus.DRAFT) {
                                    TextButton(
                                        onClick = { viewModel.submitPreparation(prep.id) }
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Soumettre")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreparationCreateScreen(
    state: PreparationState,
    viewModel: PreparationViewModel
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val steps = listOf("En-tête", "Objectifs & Matériels", "Marche de la leçon")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouvelle Fiche") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.cancelCreating() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    if (currentStep == steps.size - 1) {
                        IconButton(onClick = { viewModel.savePreparation() }, enabled = !state.isSaving) {
                            if (state.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary)
                            } else {
                                Icon(Icons.Default.Save, contentDescription = "Sauvegarder")
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { if (currentStep > 0) currentStep-- }, enabled = currentStep > 0) {
                        Text("Précédent")
                    }
                    TextButton(onClick = { if (currentStep < steps.size - 1) currentStep++ }, enabled = currentStep < steps.size - 1) {
                        Text("Suivant")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = currentStep) {
                steps.forEachIndexed { index, title ->
                    Tab(
                        selected = currentStep == index,
                        onClick = { currentStep = index },
                        text = { Text(title) }
                    )
                }
            }
            
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                AnimatedContent(targetState = currentStep, label = "Step Transition") { step ->
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (step) {
                            0 -> HeaderStep(state, viewModel)
                            1 -> ObjectivesStep(state, viewModel)
                            2 -> StepsStep(state, viewModel)
                        }
                        Spacer(modifier = Modifier.height(32.dp)) // padding for bottom bar
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderStep(state: PreparationState, viewModel: PreparationViewModel) {
    OutlinedTextField(
        value = state.draftBranch, onValueChange = viewModel::updateDraftBranch,
        label = { Text("Branche") }, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = state.draftSubBranch, onValueChange = viewModel::updateDraftSubBranch,
        label = { Text("Sous-branche") }, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = state.draftClass, onValueChange = viewModel::updateDraftClass,
        label = { Text("Classe") }, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = state.draftRevisionSubject, onValueChange = viewModel::updateDraftRevisionSubject,
        label = { Text("Sujet de la révision") }, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = state.draftLessonSubject, onValueChange = viewModel::updateDraftLessonSubject,
        label = { Text("Sujet de la leçon (Sujet du jour)") }, modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ObjectivesStep(state: PreparationState, viewModel: PreparationViewModel) {
    OutlinedTextField(
        value = state.draftObjective, onValueChange = viewModel::updateDraftObjective,
        label = { Text("Objectif opérationnel") }, modifier = Modifier.fillMaxWidth(),
        supportingText = { Text("L'élève sera capable de...") }, minLines = 3
    )
    OutlinedTextField(
        value = state.draftMaterial, onValueChange = viewModel::updateDraftMaterial,
        label = { Text("Matériel didactique") }, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = state.draftBibliography, onValueChange = viewModel::updateDraftBibliography,
        label = { Text("Références bibliographiques") }, modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun StepsStep(state: PreparationState, viewModel: PreparationViewModel) {
    Text("I. Introduction", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    StepInputRow(
        dur = state.draftIntroDuration, onDur = viewModel::updateIntroDuration,
        met = state.draftIntroMethod, onMet = viewModel::updateIntroMethod,
        con = state.draftIntroContent, onCon = viewModel::updateIntroContent,
        conLabel = "Rappel, Motivation, Annonce"
    )

    Spacer(modifier = Modifier.height(16.dp))
    Text("II. Développement", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    StepInputRow(
        dur = state.draftDevDuration, onDur = viewModel::updateDevDuration,
        met = state.draftDevMethod, onMet = viewModel::updateDevMethod,
        con = state.draftDevContent, onCon = viewModel::updateDevContent,
        conLabel = "Exposé, Analyse, Participation"
    )

    Spacer(modifier = Modifier.height(16.dp))
    Text("III. Synthèse", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    StepInputRow(
        dur = state.draftSynthDuration, onDur = viewModel::updateSynthDuration,
        met = state.draftSynthMethod, onMet = viewModel::updateSynthMethod,
        con = state.draftSynthContent, onCon = viewModel::updateSynthContent,
        conLabel = "Résumé"
    )

    Spacer(modifier = Modifier.height(16.dp))
    Text("IV. Application", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    StepInputRow(
        dur = state.draftAppDuration, onDur = viewModel::updateAppDuration,
        met = state.draftAppMethod, onMet = viewModel::updateAppMethod,
        con = state.draftAppContent, onCon = viewModel::updateAppContent,
        conLabel = "Exercices, Tâches"
    )
}

@Composable
fun StepInputRow(
    dur: String, onDur: (String) -> Unit,
    met: String, onMet: (String) -> Unit,
    con: String, onCon: (String) -> Unit,
    conLabel: String
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = dur, onValueChange = onDur, label = { Text("Durée") }, modifier = Modifier.weight(1f))
        OutlinedTextField(value = met, onValueChange = onMet, label = { Text("Méthode") }, modifier = Modifier.weight(2f))
    }
    OutlinedTextField(value = con, onValueChange = onCon, label = { Text(conLabel) }, modifier = Modifier.fillMaxWidth(), minLines = 3)
}