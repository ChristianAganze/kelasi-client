package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.data.datasource.remote.dto.LearningTimeConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.ScheduleEntryDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.ui.components.AppIcons
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.format.DayOfWeekNames

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showAssignDialog by remember { mutableStateOf(false) }
    var selectedSlotConfigId by remember { mutableStateOf<Long?>(null) }
    var selectedEntryToEdit by remember { mutableStateOf<DetailedScheduleEntry?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showEntryOptionsDialog by remember { mutableStateOf(false) }
    var showQuickAddDialog by remember { mutableStateOf(false) }
    var selectedQuickSlot by remember { mutableStateOf<LearningTimeConfigDto?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Emploi du Temps",
                            style = MaterialTheme.typography.displaySmall,
                        )
                        Text(
                            text = "Visualisez et gérez l'emploi du temps des classes.",
                            style = MaterialTheme.typography.labelLarge,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }, actions = {
                    if (uiState.selectedClass != null && uiState.allLearningTimeConfigs.isNotEmpty()) {
                        Button(
                            onClick = {
                                selectedEntryToEdit = null
                                showQuickAddDialog = true
                            },
                        ) {
                            Icon(AppIcons.add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Planifier")
                        }

                    }
                    if (uiState.selectedClass != null) {
                        IconButton(onClick = {
                            viewModel.clearWeek(uiState.selectedClass!!.id, uiState.currentWeekNumber)
                        }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Vider la semaine")
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },

        ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 32.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // School Section Selector
                StructureSelector(
                    label = "Cycle",
                    items = uiState.schoolSections,
                    selectedItem = uiState.selectedSchoolSection,
                    onItemSelected = viewModel::selectSchoolSection,
                    itemDisplayName = { it.name },
                    isLoading = uiState.isLoadingSchoolSections
                )

                // Section Selector
                StructureSelector(
                    label = "Section",
                    items = uiState.sections,
                    selectedItem = uiState.selectedSection,
                    onItemSelected = viewModel::selectSection,
                    itemDisplayName = { it.name },
                    isLoading = uiState.isLoadingSections,
                    enabled = uiState.selectedSchoolSection != null
                )

                // Major Selector
                StructureSelector(
                    label = "Major",
                    items = uiState.majors,
                    selectedItem = uiState.selectedMajor,
                    onItemSelected = viewModel::selectMajor,
                    itemDisplayName = { it.name },
                    isLoading = uiState.isLoadingMajors,
                    enabled = uiState.selectedSection != null
                )

                // Grade Level Selector
                StructureSelector(
                    label = "Grade Level",
                    items = uiState.gradeLevels,
                    selectedItem = uiState.selectedGradeLevel,
                    onItemSelected = viewModel::selectGradeLevel,
                    itemDisplayName = { it.name },
                    isLoading = uiState.isLoadingGradeLevels,
                    enabled = uiState.selectedMajor != null
                )

                // Class Selector
                StructureSelector(
                    label = "Class",
                    items = uiState.classes,
                    selectedItem = uiState.selectedClass,
                    onItemSelected = viewModel::selectClass,
                    itemDisplayName = { it.name },
                    isLoading = uiState.isLoadingClasses,
                    enabled = uiState.selectedGradeLevel != null
                )

                // Week Navigation
                WeekNavigator(
                    currentWeekNumber = uiState.currentWeekNumber,
                    onPreviousWeek = viewModel::goToPreviousWeek,
                    onNextWeek = viewModel::goToNextWeek,
                    isLoading = uiState.isLoadingSchedule
                )
            }

            // Schedule Display
            if (uiState.isLoadingClasses || uiState.isLoadingSchedule) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
            }

            if (uiState.selectedClass == null) {
                Text(
                    "Veuillez sélectionner une classe pour afficher l'emploi du temps.",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else if (!uiState.isLoadingSchedule && uiState.allLearningTimeConfigs.isEmpty()) {
                Text(
                    "Aucune configuration de temps d'étude n'a été trouvée pour ce cycle. Veuillez d'abord les configurer dans 'Calendrier et périodes'.",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (!uiState.isLoadingSchedule && uiState.error == null) {
                ScheduleTable(
                    learningTimeConfigs = uiState.allLearningTimeConfigs,
                    scheduleEntries = uiState.schedule,
                    onEntryClick = { entry ->
                        selectedEntryToEdit = entry
                        showEntryOptionsDialog = true
                    },
                    onEmptySlotClick = { day, start, end ->
                        val config =
                            uiState.allLearningTimeConfigs.find { it.dayOfWeek == day && it.startDayHourTime == start && it.endDayHourTime == end }
                        selectedSlotConfigId = config?.id
                        selectedEntryToEdit = null
                        showAssignDialog = true
                    })
            }
        }
    }

    // Dialog to choose between Edit and Delete
    if (showEntryOptionsDialog && selectedEntryToEdit != null) {
        AlertDialog(
            onDismissRequest = { showEntryOptionsDialog = false },
            title = { Text("Options du cours") },
            text = {
                Column {
                    Text("Cours: ${selectedEntryToEdit?.teachingAssignment?.subjectName ?: "Inconnu"}")
                    Text("Enseignant: ${selectedEntryToEdit?.teachingAssignment?.teacherName ?: "Inconnu"}")
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            selectedSlotConfigId = selectedEntryToEdit?.scheduleEntry?.learningTimeConfigId
                            showEntryOptionsDialog = false
                            showAssignDialog = true
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Modifier l'attribution")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showEntryOptionsDialog = false
                            showDeleteConfirm = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Supprimer du planning")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showEntryOptionsDialog = false }) { Text("Fermer") }
            })
    }

    if (showAssignDialog && selectedSlotConfigId != null) {
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text(if (selectedEntryToEdit == null) "Attribuer un cours" else "Modifier l'attribution") },
            text = {
                LazyColumn {
                    items(uiState.assignments) { assignment ->
                        ListItem(
                            headlineContent = { Text(assignment.subjectName) },
                            supportingContent = { Text(assignment.teacherName) },
                            modifier = Modifier.clickable {
                                if (selectedEntryToEdit == null) {
                                    viewModel.createScheduleEntry(
                                        ScheduleEntryDto(
                                            id = null,
                                            learningTimeConfigId = selectedSlotConfigId!!,
                                            teachingAssignmentId = assignment.id,
                                            weekNumber = uiState.currentWeekNumber
                                        )
                                    )
                                } else {
                                    viewModel.updateScheduleEntry(
                                        selectedEntryToEdit!!.scheduleEntry.id!!, ScheduleEntryDto(
                                            id = selectedEntryToEdit!!.scheduleEntry.id,
                                            learningTimeConfigId = selectedSlotConfigId!!,
                                            teachingAssignmentId = assignment.id,
                                            weekNumber = uiState.currentWeekNumber
                                        )
                                    )
                                }
                                showAssignDialog = false
                                selectedEntryToEdit = null
                            })
                    }
                    if (uiState.assignments.isEmpty()) {
                        item { Text("Aucune attribution de cours disponible pour cette classe.") }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    showAssignDialog = false
                    selectedEntryToEdit = null
                }) { Text("Annuler") }
            })
    }

    if (showDeleteConfirm && selectedEntryToEdit != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Supprimer l'entrée ?") },
            text = { Text("Voulez-vous supprimer ce cours de l'emploi du temps ?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteScheduleEntry(selectedEntryToEdit!!.scheduleEntry.id!!)
                    showDeleteConfirm = false
                    selectedEntryToEdit = null
                }) { Text("Supprimer", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    selectedEntryToEdit = null
                }) { Text("Annuler") }
            })
    }

    if (showQuickAddDialog) {
        var expandedSlots by remember { mutableStateOf(false) }
        var assignment by remember { mutableStateOf<TeachingAssignmentDTO?>(null) }
        AlertDialog(
            onDismissRequest = { showQuickAddDialog = false },
            title = { Text("Planification rapide") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = expandedSlots, onExpandedChange = { expandedSlots = !expandedSlots }) {
                        OutlinedTextField(
                            value = selectedQuickSlot?.let { "${it.dayOfWeek.name.take(3)} ${it.startDayHourTime}" }
                                ?: "Choisir un créneau",
                            onValueChange = {},
                            readOnly = true,
                            shape = MaterialTheme.shapes.large,
                            label = { Text("Créneau horaire") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSlots) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )

                        val expandedDropdownDays = remember { mutableStateMapOf<String, Boolean>() }

                        ExposedDropdownMenu(
                            expanded = expandedSlots, onDismissRequest = { expandedSlots = false }) {
                            DayOfWeekNames.ENGLISH_FULL.names.forEach { dayName ->

                                val configsForDay = uiState.allLearningTimeConfigs.filter {
                                    it.dayOfWeek.name.equals(dayName, ignoreCase = true)
                                }

                                if (configsForDay.isNotEmpty()) {
                                    val isDayExpanded = expandedDropdownDays[dayName] ?: false

                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = dayName,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Icon(
                                                    imageVector = if (isDayExpanded) AppIcons.arrowUp else AppIcons.arrowDown,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        },
                                        onClick = {
                                            expandedDropdownDays[dayName] = !isDayExpanded
                                        },

                                        )

                                    if (isDayExpanded) {
                                        configsForDay.forEach { slot ->
                                            DropdownMenuItem(text = {
                                                // Contenu stylisé pour le slot
                                                Column(modifier = Modifier.padding(2.dp)) {
                                                    Text(
                                                        text = "${slot.startDayHourTime} - ${slot.endDayHourTime}",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )

                                                    Text(
                                                        text = slot.label,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )

                                                }
                                            }, onClick = {
                                                selectedQuickSlot = slot
                                                expandedSlots = false
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (selectedQuickSlot != null) {
                        Text("Attribuer un cours :", style = MaterialTheme.typography.labelLarge)
                        LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                            items(uiState.assignments) { it ->
                                ListItem(

                                    headlineContent = { Text(it.subjectName) },
                                    supportingContent = { Text(it.teacherName) },
                                    modifier = Modifier.border(
                                        width = 1.dp,
                                        color = if (assignment == it) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = MaterialTheme.shapes.large
                                    ).clip(MaterialTheme.shapes.large).clickable {
                                        assignment = it
                                    })
                                Spacer(Modifier.height(4.dp))
                            }
                            if (uiState.assignments.isEmpty()) {
                                item { Text("Aucune attribution disponible.") }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (assignment != null && selectedQuickSlot != null) {
                    Button(onClick = {
                        viewModel.createScheduleEntry(
                            ScheduleEntryDto(
                                id = 2,
                                learningTimeConfigId = selectedQuickSlot!!.id!!,
                                teachingAssignmentId = assignment!!.id,
                                weekNumber = uiState.currentWeekNumber
                            )
                        )
                        showQuickAddDialog = false
                        selectedQuickSlot = null
                    }) { Text("Confirmer") }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showQuickAddDialog = false
                    selectedQuickSlot = null
                }) { Text("Annuler") }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> StructureSelector(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemDisplayName: (T) -> String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedItem?.let(itemDisplayName) ?: "Sélectionner un $label",
            onValueChange = {},
            readOnly = true,
            label = {
                Text(label)
            },
            shape = MaterialTheme.shapes.large,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            enabled = !isLoading && enabled
        )
        ExposedDropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {
            if (isLoading) {
                DropdownMenuItem(
                    text = { Text("Chargement des $label...") },
                    onClick = { /* Do nothing */ },
                    enabled = false
                )
            } else if (items.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Aucun $label disponible") },
                    onClick = { /* Do nothing */ },
                    enabled = false
                )
            } else {
                items.forEach { item ->
                    DropdownMenuItem(text = { Text(itemDisplayName(item)) }, onClick = {
                        onItemSelected(item)
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun WeekNavigator(
    currentWeekNumber: Int,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousWeek, enabled = !isLoading) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Semaine précédente")
        }
        Text(
            text = "Semaine $currentWeekNumber",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextWeek, enabled = !isLoading) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Semaine suivante")
        }
    }
}

@Composable
fun ScheduleTable(
    learningTimeConfigs: List<LearningTimeConfigDto>,
    scheduleEntries: List<DetailedScheduleEntry>,
    onEntryClick: (DetailedScheduleEntry) -> Unit,
    onEmptySlotClick: (DayOfWeek, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = DayOfWeek.entries // MONDAY to SUNDAY

    // Sort learningTimeConfigs by start time
    val sortedLearningTimeConfigs = remember(learningTimeConfigs) {
        learningTimeConfigs.sortedWith(compareBy({ it.dayOfWeek }, { it.startDayHourTime }))
    }

    // Extract unique time slots across all days
    val uniqueTimeSlots = remember(sortedLearningTimeConfigs) {
        sortedLearningTimeConfigs.map { it.startDayHourTime to it.endDayHourTime }.distinct().sortedBy { it.first }
    }

    if (uniqueTimeSlots.isEmpty()) {
        Text("Aucun créneau horaire configuré pour ce cycle.", modifier = Modifier.padding(16.dp))
        return
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                // Empty corner for time slot header
                Spacer(modifier = Modifier.width(100.dp).padding(vertical = 12.dp))
                daysOfWeek.forEach { day ->
                    Text(
                        day.name.take(3), // Mon, Tue, etc.
                        modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            uniqueTimeSlots.forEachIndexed { slotIndex, (startTime, endTime) ->
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Time Slot Label
                    Column(
                        modifier = Modifier.width(100.dp).padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = startTime.substringBeforeLast(":"), // HH:mm
                            style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = endTime.substringBeforeLast(":"), // HH:mm
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    VerticalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 1.dp
                    )

                    daysOfWeek.forEach { day ->
                        val entriesForSlot = scheduleEntries.filter { detailedEntry ->
                            detailedEntry.learningTimeConfig?.dayOfWeek == day && detailedEntry.learningTimeConfig.startDayHourTime == startTime && detailedEntry.learningTimeConfig.endDayHourTime == endTime
                        }

                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().padding(2.dp).background(
                                MaterialTheme.colorScheme.surfaceContainerLow, shape = MaterialTheme.shapes.small
                            ).clickable {
                                if (entriesForSlot.isEmpty()) {
                                    onEmptySlotClick(day, startTime, endTime)
                                }
                            }, contentAlignment = Alignment.Center
                        ) {
                            if (entriesForSlot.isNotEmpty()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    entriesForSlot.forEach { entry ->
                                        ScheduleEntryCell(entry = entry, onClick = { onEntryClick(entry) })
                                    }
                                }
                            }
                        }
                        VerticalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 1.dp
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun ScheduleEntryCell(entry: DetailedScheduleEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(2.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = entry.teachingAssignment?.subjectName ?: "Matière inconnue",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            entry.teachingAssignment?.teacherName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
