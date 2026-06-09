package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.schedule

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.data.datasource.remote.dto.CreateScheduleEntryDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.LearningTimeConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.domain.util.dateFormatterOnlyDay
import com.drcmind.kelasisuite.domain.util.toFrench
import com.drcmind.kelasisuite.ui.components.AppIcons
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusDays
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DayOfWeekNames
import org.koin.compose.viewmodel.koinViewModel

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
    var showDeleteWeekConfirm by remember { mutableStateOf(false) }
    var showDuplicateWeekDialog by remember { mutableStateOf(false) }
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
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = {
                        expanded = true
                    }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Dupliquer la semaine") },
                            onClick = {
                                expanded = false
                                showDuplicateWeekDialog = true
                            },
                            leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                            enabled = uiState.selectedClass != null
                        )
                        DropdownMenuItem(
                            text = { Text("Supprimer la semaine") },
                            onClick = {
                                expanded = false
                                showDeleteWeekConfirm = true
                            },
                            leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null) },
                            enabled = uiState.selectedClass != null
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },

        ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 32.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedCard {
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)
                )
                {
                    // School Section Selector

                    StructureSelector(
                        label = "Cycle",
                        items = uiState.schoolSections,
                        selectedItem = uiState.selectedSchoolSection,
                        onItemSelected = viewModel::selectSchoolSection,
                        itemDisplayName = { it.name },
                        isLoading = uiState.isLoadingSchoolSections,
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
                        label = "Option",
                        items = uiState.majors,
                        selectedItem = uiState.selectedMajor,
                        onItemSelected = viewModel::selectMajor,
                        itemDisplayName = { it.name },
                        isLoading = uiState.isLoadingMajors,
                        enabled = uiState.selectedSection != null
                    )

                    // Grade Level Selector
                    StructureSelector(
                        label = "Niveau",
                        items = uiState.gradeLevels,
                        selectedItem = uiState.selectedGradeLevel,
                        onItemSelected = viewModel::selectGradeLevel,
                        itemDisplayName = { it.name },
                        isLoading = uiState.isLoadingGradeLevels,
                        enabled = uiState.selectedMajor != null
                    )

                    // Class Selector
                    StructureSelector(
                        label = "Classe",
                        items = uiState.classes,
                        selectedItem = uiState.selectedClass,
                        onItemSelected = viewModel::selectClass,
                        itemDisplayName = { it.name },
                        isLoading = uiState.isLoadingClasses,
                        enabled = uiState.selectedGradeLevel != null
                    )
                }
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
                val currentDate = remember { LocalDate.now() }
                val startDate = remember { currentDate.minusDays(500) }
                val endDate = remember { currentDate.plusDays(500) }
                var selection by remember { mutableStateOf(currentDate) }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme .background),
                ) {
                    val state = rememberWeekCalendarState(
                        startDate = startDate,
                        endDate = endDate,
                        firstVisibleWeekDate = currentDate,
                    )

                    LaunchedEffect(uiState.currentWeekNumber) {
                        val daysToAdd = (uiState.currentWeekNumber - (currentDate.dayOfYear / 7 + 1)) * 7
                        val targetDate = currentDate.plusDays(daysToAdd)
                        state.animateScrollToWeek(targetDate)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.goToPreviousWeek() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Semaine précédente")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Semaine ${uiState.currentWeekNumber}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            val firstDate = state.firstVisibleWeek.days.first().date
                            val lastDate = state.firstVisibleWeek.days.last().date
                            val monthText = if (firstDate.month == lastDate.month) {
                                firstDate.month.toFrench()
                            } else {
                                "${firstDate.month.toFrench()} - ${lastDate.month.toFrench()}"
                            }
                            Text(
                                text = monthText,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { viewModel.goToNextWeek() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Semaine suivante")
                        }
                    }

                    Row {
                        Column(modifier = Modifier.width(88.dp)) {
                            Box(
                                modifier = Modifier.height(59.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Crénaux",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Light,
                                )
                            }
                            HorizontalDivider()
                            uiState.allLearningTimeConfigs.distinctBy { it.label }.forEach {
                                ListItem(
                                    modifier = Modifier.height(80.dp),
                                    headlineContent = {
                                        Column {
                                            Text(it.label, fontSize = 9.sp)
                                            Text("${it.startDayHourTime} - ${it.endDayHourTime}", fontSize = 9.sp)
                                        }
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                        WeekCalendar(
                            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
                            state = state,
                            dayContent = { day ->
                                val dailyEntries = uiState.schedule.filter { it.scheduleEntry.dayOfWeek == day.date.dayOfWeek }

                                Day(
                                    date = day.date,
                                    isSelected = selection == day.date,
                                    dailyEntries = dailyEntries,
                                    learningTimeConfigs = uiState.allLearningTimeConfigs,
                                    onSlotClick = { configId ->
                                        selectedSlotConfigId = configId
                                        showAssignDialog = true
                                    },
                                    onEntryClick = { entry ->
                                        selectedEntryToEdit = entry
                                        showEntryOptionsDialog = true
                                    }
                                ) { clicked ->
                                    if (selection != clicked) {
                                        selection = clicked
                                    }
                                }
                            },
                        )
                    }
                }


            }
        }
    }

    // Dialog to choose between Edit and Delete
    if (showEntryOptionsDialog && selectedEntryToEdit != null) {
        val selectedSlot = selectedEntryToEdit?.learningTimeConfig
        AlertDialog(
            onDismissRequest = { showEntryOptionsDialog = false },
            title = {
                Column {
                    Text("Options du cours")
                    selectedSlot?.let { slot ->
                        Text(
                            text = "Semaine ${uiState.currentWeekNumber} - ${slot.dayOfWeek.toFrench()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${slot.label} : ${slot.startDayHourTime} - ${slot.endDayHourTime}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            },
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
        val selectedSlot = uiState.allLearningTimeConfigs.find { it.id == selectedSlotConfigId }
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = {
                Column {
                    Text(if (selectedEntryToEdit == null) "Attribuer un cours" else "Modifier l'attribution")
                    selectedSlot?.let { slot ->
                        Text(
                            text = "Semaine ${uiState.currentWeekNumber} - ${slot.dayOfWeek.toFrench()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${slot.label} : ${slot.startDayHourTime} - ${slot.endDayHourTime}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            },
            text = {
                LazyColumn {
                    items(uiState.assignments) { assignment ->
                        ListItem(
                            headlineContent = { Text(assignment.subjectName) },
                            supportingContent = { Text(assignment.teacherName) },
                            modifier = Modifier.clickable {
                                if (selectedEntryToEdit == null) {
                                    viewModel.createScheduleEntry(
                                        CreateScheduleEntryDto(
                                            id = null,
                                            learningTimeConfigId = selectedSlotConfigId!!,
                                            teachingAssignmentId = assignment.id,
                                            weekNumber = uiState.currentWeekNumber
                                        )
                                    )
                                } else {
                                    viewModel.updateScheduleEntry(
                                        selectedEntryToEdit!!.scheduleEntry.id!!, CreateScheduleEntryDto(
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
            title = {
                Column {
                    Text("Planification rapide")
                    selectedQuickSlot?.let { slot ->
                        Text(
                            text = "Semaine ${uiState.currentWeekNumber} - ${slot.dayOfWeek.toFrench()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${slot.label} : ${slot.startDayHourTime} - ${slot.endDayHourTime}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            },
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
                            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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
                            CreateScheduleEntryDto(
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

    if (showDeleteWeekConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteWeekConfirm = false },
            title = { Text("Supprimer la semaine ${uiState.currentWeekNumber} ?") },
            text = { Text("Voulez-vous supprimer toutes les entrées de l'emploi du temps pour cette semaine ? Cette action est irréversible.") },
            confirmButton = {
                TextButton(onClick = {
                    uiState.selectedClass?.id?.let { classId ->
                        viewModel.clearWeek(classId, uiState.currentWeekNumber)
                    }
                    showDeleteWeekConfirm = false
                }) { Text("Supprimer", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteWeekConfirm = false }) { Text("Annuler") }
            })
    }

    if (showDuplicateWeekDialog) {
        var selectedWeeks by remember { mutableStateOf(setOf<Int>()) }
        AlertDialog(
            onDismissRequest = { showDuplicateWeekDialog = false },
            title = { Text("Dupliquer la semaine ${uiState.currentWeekNumber}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Vers quelles semaines voulez-vous copier ce planning ?")
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).verticalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (1..52).forEach { week ->
                            FilterChip(
                                selected = selectedWeeks.contains(week),
                                onClick = {
                                    selectedWeeks = if (selectedWeeks.contains(week)) {
                                        selectedWeeks - week
                                    } else {
                                        selectedWeeks + week
                                    }
                                },
                                label = { Text("S$week") }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        uiState.selectedClass?.id?.let { classId ->
                            viewModel.duplicateWeek(
                                sourceWeek = uiState.currentWeekNumber,
                                classId = classId,
                                targetWeeks = selectedWeeks.toList()
                            )
                        }
                        showDuplicateWeekDialog = false
                    },
                    enabled = selectedWeeks.isNotEmpty()
                ) { Text("Dupliquer") }
            },
            dismissButton = {
                TextButton(onClick = { showDuplicateWeekDialog = false }) { Text("Annuler") }
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

    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) expanded = !expanded },
            modifier = modifier,
        ) {
            AssistChip(
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                label = {Text(selectedItem?.let(itemDisplayName) ?: "Sélectionner un $label")},
                onClick = {},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                enabled = !isLoading && enabled
            )


            ExposedDropdownMenu(
                matchAnchorWidth = true,
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


}

@Composable
fun ScheduleEntryCell(entry: DetailedScheduleEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(2.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp), 
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = entry.teachingAssignment?.subjectName ?: entry.scheduleEntry.subjectName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            val teacherName = entry.teachingAssignment?.teacherName ?: entry.scheduleEntry.teacherName
            if (teacherName.isNotBlank()) {
                Text(
                    text = teacherName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun Day(
    date: LocalDate,
    isSelected: Boolean,
    dailyEntries: List<DetailedScheduleEntry>,
    learningTimeConfigs: List<LearningTimeConfigDto>,
    onSlotClick: (Long) -> Unit,
    onEntryClick: (DetailedScheduleEntry) -> Unit,
    onClick: (LocalDate) -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clickable { onClick(date) },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = date.dayOfWeek.toFrench().take(2),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Light,
                )
                Text(
                    text = dateFormatterOnlyDay.format(date),
                    fontSize = 14.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer)
                        .align(Alignment.BottomCenter),
                )

            }
            HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))

        }
        learningTimeConfigs.distinctBy { it.label }.forEach { slotLabel ->
            val actualConfig = learningTimeConfigs.find { it.label == slotLabel.label && it.dayOfWeek == date.dayOfWeek }
            val entry = dailyEntries.find { it.scheduleEntry.learningTimeConfigId == actualConfig?.id }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                if (entry != null) {
                    ScheduleEntryCell(entry = entry) {
                        onEntryClick(entry)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { actualConfig?.id?.let { onSlotClick(it) } },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Ajouter",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}