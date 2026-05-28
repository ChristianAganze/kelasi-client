package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import kotlinx.datetime.DayOfWeek
import org.koin.compose.viewmodel.koinViewModel
import com.drcmind.kelasisuite.data.datasource.remote.dto.LearningTimeConfigDto


@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.Transparent, topBar = {
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
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }) { paddingValues ->
        FlowRow(

            modifier = Modifier.padding(paddingValues).padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
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
            } else if (!uiState.isLoadingSchedule && uiState.schedule.isEmpty() && uiState.error == null) {
                Text(
                    "Aucun horaire configuré pour cette classe cette semaine.",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else if (!uiState.isLoadingSchedule && uiState.error == null) {
                ScheduleTable(
                    learningTimeConfigs = uiState.allLearningTimeConfigs, scheduleEntries = uiState.schedule
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
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
        onExpandedChange = { expanded = !expanded },

        ) {
        OutlinedTextField(
            value = selectedItem?.let(itemDisplayName) ?: "Sélectionner un $label",
            onValueChange = {},
            readOnly = true,
            label = {
                Text(label)
            },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassSelector(
    classes: List<SchoolClassDTO>,
    selectedClassId: Long?,
    onClassSelected: (Long) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = classes.find { it.id == selectedClassId }?.name ?: "Sélectionner une classe",
            onValueChange = {},
            readOnly = true,
            label = { Text("Classe") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            enabled = !isLoading
        )
        ExposedDropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {
            if (isLoading) {
                DropdownMenuItem(
                    text = { Text("Chargement des classes...") },
                    onClick = { /* Do nothing */ },
                    enabled = false
                )
            } else if (classes.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Aucune classe disponible") },
                    onClick = { /* Do nothing */ },
                    enabled = false
                )
            } else {
                classes.forEach { classItem ->
                    DropdownMenuItem(text = { Text(classItem.name) }, onClick = {
                        onClassSelected(classItem.id)
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
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
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
        Text("Aucun créneau horaire configuré pour cette section.", modifier = Modifier.padding(16.dp))
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
                Spacer(modifier = Modifier.width(120.dp).padding(vertical = 12.dp))
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
                    modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Time Slot Label
                    Column(
                        modifier = Modifier.width(120.dp).padding(horizontal = 8.dp),
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
                                ), contentAlignment = Alignment.Center
                        ) {
                            if (entriesForSlot.isNotEmpty()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    entriesForSlot.forEach { entry ->
                                        ScheduleEntryCell(entry = entry)
                                    }
                                }
                            } else {
                                // Optional: Placeholder for empty slots
                                Text("", style = MaterialTheme.typography.labelSmall)
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
fun ScheduleEntryCell(entry: DetailedScheduleEntry) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(2.dp),
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