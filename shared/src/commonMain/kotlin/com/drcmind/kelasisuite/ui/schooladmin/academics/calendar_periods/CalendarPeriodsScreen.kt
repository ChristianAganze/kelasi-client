package com.drcmind.kelasisuite.ui.schooladmin.academics.calendar_periods

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberSupportingPaneSceneStrategy
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.LearningTimeConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionConfigDto
import com.drcmind.kelasisuite.domain.model.EvaluationStatus
import com.drcmind.kelasisuite.navigation.Route
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

enum class CalendarPeriodsTab {
    CALENDAR_PERIODS, TIME_SLOTS_CONFIGURATION
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalendarPeriodsScreen(
    viewModel: CalendarPeriodsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableStateOf(CalendarPeriodsTab.CALENDAR_PERIODS) }

    Scaffold(
        containerColor = Color.Transparent, topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Calendrier Académique",
                            style = MaterialTheme.typography.displaySmall,
                        )
                        Text(
                            text = "Définissez les périodes, les filières et le cycle de l'année scolaire en cours.",
                            style = MaterialTheme.typography.labelLarge,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }, actions = {
                    TextButton(onClick = { }) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = { }, shape = MaterialTheme.shapes.large
                    ) {
                        Text("Sauvegarder")
                    }
                    Spacer(Modifier.width(16.dp))
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }) { paddingValues ->
        val backStack = rememberNavBackStack(
            configuration = SavedStateConfiguration {
                serializersModule = SerializersModule {
                    polymorphic(NavKey::class) {
                        subclass(
                            Route.SchoolAdmin.Academics.CalendarPeriod.Main::class,
                            Route.SchoolAdmin.Academics.CalendarPeriod.Main.serializer()
                        )
                        subclass(
                            Route.SchoolAdmin.Academics.CalendarPeriod.Supporting::class,
                            Route.SchoolAdmin.Academics.CalendarPeriod.Supporting.serializer()
                        )
                    }
                }
            }, Route.SchoolAdmin.Academics.CalendarPeriod.Supporting, Route.SchoolAdmin.Academics.CalendarPeriod.Main
        )
        val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
        val directive = remember(windowAdaptiveInfo) {
            calculatePaneScaffoldDirective(windowAdaptiveInfo).copy(
                horizontalPartitionSpacerSize = 0.dp,
                verticalPartitionSpacerSize = 0.dp
            )
        }
        val supportingPaneStrategy = rememberSupportingPaneSceneStrategy<NavKey>(
            backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange, directive = directive
        )

        NavDisplay(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            sceneStrategies = listOf(supportingPaneStrategy),
            entryProvider = entryProvider {
                entry<Route.SchoolAdmin.Academics.CalendarPeriod.Main>(
                    metadata = SupportingPaneSceneStrategy.mainPane()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)
                    ) {
                        TabRow(selectedTabIndex = selectedTab.ordinal) {
                            Tab(
                                selected = selectedTab == CalendarPeriodsTab.CALENDAR_PERIODS,
                                onClick = { selectedTab = CalendarPeriodsTab.CALENDAR_PERIODS },
                                text = { Text("Périodes du Calendrier") })
                            Tab(selected = selectedTab == CalendarPeriodsTab.TIME_SLOTS_CONFIGURATION, onClick = {
                                selectedTab = CalendarPeriodsTab.TIME_SLOTS_CONFIGURATION
                            }, text = { Text("Configuration des Créneaux Horaires") })
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        when (selectedTab) {
                            CalendarPeriodsTab.CALENDAR_PERIODS -> {
                                Column(
                                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(32.dp)
                                ) {
                                    // --- Active Academic Year Card ---
                                    AcademicYearCard(uiState)

                                    // --- Majors and Cycles Card ---
                                    OutlinedCard(
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Majors Offered",
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                IconButton(onClick = { /* TODO: Implement add major dialog */ }) {
                                                    Icon(
                                                        Icons.Default.Add, contentDescription = "Add Major"
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            if (uiState.majors.isEmpty()) {
                                                Text(
                                                    "No majors configured.", style = MaterialTheme.typography.bodySmall
                                                )
                                            } else {
                                                FlowRow( // Use FlowRow for a flexible layout of majors
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    uiState.majors.forEach { major ->
                                                        SuggestionChip(
                                                            onClick = { /* TODO: Implement major edit/details */ },
                                                            label = { Text(major.name) },
                                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                            )
                                                        )
                                                    }
                                                }
                                            }

                                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                                            Text(
                                                text = "School Sections / Cycles",
                                                style = MaterialTheme.typography.titleMedium,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            if (uiState.schoolSections.isEmpty()) {
                                                Text(
                                                    "No sections/cycles configured.",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            } else {
                                                FlowRow( // Use FlowRow for a flexible layout of sections
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    uiState.schoolSections.forEach { section ->
                                                        SuggestionChip(
                                                            onClick = { /* TODO: Implement section edit/details */ },
                                                            label = { Text(section.name) },
                                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                                                labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // --- Evaluation Period Table Card ---
                                    EvaluationPeriodsCard(uiState)

                                    Spacer(modifier = Modifier.height(32.dp))
                                }
                            }

                            CalendarPeriodsTab.TIME_SLOTS_CONFIGURATION -> {
                                TimeSlotsConfigurationTab(viewModel, uiState)
                            }
                        }
                    }
                }
                entry<Route.SchoolAdmin.Academics.CalendarPeriod.Supporting>(
                    metadata = SupportingPaneSceneStrategy.supportingPane()
                ) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "All Academic Years",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            if (uiState.academicYears.isEmpty()) {
                                Text(
                                    "No academic years configured.", style = MaterialTheme.typography.bodySmall
                                )
                            } else {
                                LazyColumn {
                                    items(uiState.academicYears) { academicYear ->
                                        HorizontalDivider()
                                        ListItem(
                                            headlineContent = {
                                                Text(
                                                    text = academicYear.label,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                )
                                                if (academicYear.isActive) {
                                                    Text(
                                                        text = "Active",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                        modifier = Modifier.background(
                                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                                            shape = CircleShape
                                                        ).padding(
                                                            horizontal = 6.dp, vertical = 2.dp
                                                        )
                                                    )
                                                }
                                            })
                                    }
                                }
                            }
                        }
                    }
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicYearCard(uiState: CalendarPeriodsUiState) {
    val dateFormat = LocalDate.Format {
        day(); char('/'); monthNumber(); char('/'); year()
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf(LocalDate(2025, 9, 1)) }
    var selectedEndDate by remember { mutableStateOf(LocalDate(2026, 6, 30)) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Année Académique Active",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            ExposedDropdownMenuBox(
                expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = "2025-2026",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Année Scolaire") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }) {
                    uiState.academicYears.forEach { year ->
                        DropdownMenuItem(text = { Text(year.label) }, onClick = { expanded = false })
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = selectedStartDate.format(dateFormat),
                    onValueChange = {},
                    modifier = Modifier.weight(1f).clickable { showStartDatePicker = true },
                    readOnly = true,
                    label = { Text("Date de début") },
                    trailingIcon = { Icon(Icons.Default.DateRange, null) },
                )

                OutlinedTextField(
                    value = selectedEndDate.format(dateFormat),
                    onValueChange = {},
                    modifier = Modifier.weight(1f).clickable { showEndDatePicker = true },
                    readOnly = true,
                    label = { Text("Date de fin") },
                    trailingIcon = { Icon(Icons.Default.DateRange, null) },
                )
            }
        }
    }

    // Date Picker Dialogs
    if (showStartDatePicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = selectedStartDate.toEpochDays() * 24L * 60L * 60L * 1000L
        )
        DatePickerDialog(onDismissRequest = { showStartDatePicker = false }, confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { millis ->
                    selectedStartDate = LocalDate.fromEpochDays((millis / (24L * 60L * 60L * 1000L)).toInt())
                }
                showStartDatePicker = false
            }) { Text("OK") }
        }, dismissButton = {
            TextButton(onClick = { showStartDatePicker = false }) { Text("Annuler") }
        }) { DatePicker(state = state) }
    }

    if (showEndDatePicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = selectedEndDate.toEpochDays() * 24L * 60L * 60L * 1000L
        )
        DatePickerDialog(onDismissRequest = { showEndDatePicker = false }, confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { millis ->
                    selectedEndDate = LocalDate.fromEpochDays((millis / (24L * 60L * 60L * 1000L)).toInt())
                }
                showEndDatePicker = false
            }) { Text("OK") }
        }, dismissButton = {
            TextButton(onClick = { showEndDatePicker = false }) { Text("Annuler") }
        }) { DatePicker(state = state) }
    }
}

@Composable
fun EvaluationPeriodsCard(uiState: CalendarPeriodsUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = "Configuration des Périodes d'Évaluation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(24.dp)
            )

            // Header
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "LIBELLÉ",
                    modifier = Modifier.weight(0.3f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    "DÉBUT",
                    modifier = Modifier.weight(0.25f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
                Text(
                    "FIN",
                    modifier = Modifier.weight(0.25f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
                Text(
                    "STATUT",
                    modifier = Modifier.weight(0.2f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            }

            uiState.evaluationPeriods.forEach { sectionGroup ->
                EvaluationSectionHeader(sectionName = sectionGroup.schoolSectionName)
                sectionGroup.evaluationPeriods.forEachIndexed { index, period ->
                    EvaluationPeriodRow(period = period, onStartDateChange = {}, onEndDateChange = {})
                    if (index < sectionGroup.evaluationPeriods.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EvaluationSectionHeader(sectionName: String) {
    Box(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = sectionName.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationPeriodRow(
    period: EvaluationPeriodDTO, onStartDateChange: (LocalDate) -> Unit, onEndDateChange: (LocalDate) -> Unit
) {
    val format = LocalDate.Format { day(); char('/'); monthNumber(); char('/'); year() }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            period.label,
            modifier = Modifier.weight(0.3f),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        var showStartDatePicker by remember { mutableStateOf(false) }
        Text(
            text = period.startDate?.format(format) ?: "--/--/----",
            modifier = Modifier.weight(0.25f).clickable { showStartDatePicker = true },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        if (showStartDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = period.startDate?.toEpochDays()?.times(24L * 60L * 60L * 1000L)
            )
            DatePickerDialog(onDismissRequest = { showStartDatePicker = false }, confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onStartDateChange(LocalDate.fromEpochDays((millis / (24L * 60L * 60L * 1000L)).toInt()))
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            }, dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Annuler") }
            }) { DatePicker(state = datePickerState) }
        }

        var showEndDatePicker by remember { mutableStateOf(false) }
        Text(
            text = period.endDate?.format(format) ?: "--/--/----",
            modifier = Modifier.weight(0.25f).clickable { showEndDatePicker = true },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        if (showEndDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = period.endDate?.toEpochDays()?.times(24L * 60L * 60L * 1000L)
            )
            DatePickerDialog(onDismissRequest = { showEndDatePicker = false }, confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onEndDateChange(LocalDate.fromEpochDays((millis / (24L * 60L * 60L * 1000L)).toInt()))
                    }
                    showEndDatePicker = false
                }) { Text("OK") }
            }, dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Annuler") }
            }) { DatePicker(state = datePickerState) }
        }


        // Status Dropdown
        var statusExpanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.weight(0.2f).clickable {
            statusExpanded = true

        }, contentAlignment = Alignment.Center) {
            val status = getEvaluationStatus(period.startDate, period.endDate)
            StatusBadge(status)


            DropdownMenu(

                expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {


                EvaluationStatus.entries.forEach { status ->
                    DropdownMenuItem(text = { Text(status.displayName, textAlign = TextAlign.Center) }, onClick = {
                        statusExpanded = false
                    })
                }
            }
        }


    }
}

@Composable
fun StatusBadge(status: EvaluationStatus) {
    val color = when (status) {
        EvaluationStatus.ONGOING -> Color(0xFF10B981)
        EvaluationStatus.FINISHED -> MaterialTheme.colorScheme.outline
        EvaluationStatus.NOT_YET_ACTIVE -> Color(0xFFF59E0B)
    }
    Surface(
        color = color.copy(alpha = 0.1f), shape = CircleShape, border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun ActiveYearBadge() {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Text(
            text = "ACTIVE",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

fun getEvaluationStatus(
    startDate: LocalDate?, endDate: LocalDate?
): EvaluationStatus {

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    return when {
        startDate == null || endDate == null -> EvaluationStatus.NOT_YET_ACTIVE

        today < startDate -> EvaluationStatus.NOT_YET_ACTIVE

        today > endDate -> EvaluationStatus.FINISHED

        else -> EvaluationStatus.ONGOING
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlotsConfigurationTab(
    viewModel: CalendarPeriodsViewModel, uiState: CalendarPeriodsUiState, modifier: Modifier = Modifier
) {
    var selectedSectionExpanded by remember { mutableStateOf(false) }
    var selectedSectionId by remember { mutableStateOf<Long?>(null) }

    var showSectionConfigDialog by remember { mutableStateOf(false) }
    var editingSectionConfig by remember { mutableStateOf<SchoolSectionConfigDto?>(null) }
    var sectionStartTime by remember { mutableStateOf("") }
    var sectionEndTime by remember { mutableStateOf("") }

    var showLearningTimeDialog by remember { mutableStateOf(false) }
    var editingLearningConfig by remember { mutableStateOf<LearningTimeConfigDto?>(null) }
    var learningLabel by remember { mutableStateOf("") }
    var learningDayOfWeek by remember { mutableStateOf("") }
    var learningStartTime by remember { mutableStateOf("") }
    var learningEndTime by remember { mutableStateOf("") }
    val expandedDays = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(uiState.schoolSections) {
        if (uiState.schoolSections.isNotEmpty() && selectedSectionId == null) {
            selectedSectionId = uiState.schoolSections.first().id
        }
    }

    val currentSectionConfig = uiState.schoolSectionConfigs.find { it.schoolSectionId == selectedSectionId }

    LaunchedEffect(currentSectionConfig) {
        currentSectionConfig?.id?.let {
            viewModel.loadLearningTimeConfigsBySchoolSectionConfigId(it)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            SectionSelector(
                uiState = uiState,
                selectedSectionId = selectedSectionId,
                expanded = selectedSectionExpanded,
                onExpandedChange = { selectedSectionExpanded = it },
                onSectionSelected = { selectedSectionId = it })
        }
        if (selectedSectionId != null) {
            item {
                SectionConfigurationCard(
                    uiState = uiState, config = currentSectionConfig, onEditClick = {
                        editingSectionConfig = currentSectionConfig
                        sectionStartTime = currentSectionConfig?.dayStartTime?.toString() ?: "08:00"
                        sectionEndTime = currentSectionConfig?.dayEndTime?.toString() ?: "16:00"
                        showSectionConfigDialog = true
                    })
            }
        }

        if (currentSectionConfig != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CRÉNEAUX HORAIRES CONFIGURÉS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {
                            editingLearningConfig = null
                            learningLabel = ""
                            learningDayOfWeek = "MONDAY"
                            learningStartTime = ""
                            learningEndTime = ""
                            showLearningTimeDialog = true
                        },
                        shape = MaterialTheme.shapes.large,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter un créneau")
                    }
                }
            }

            if (uiState.learningTimeConfigs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun créneau horaire configuré pour cette section.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {

                DayOfWeekNames.ENGLISH_FULL.names.forEach { dayName ->
                    val configsForDay = uiState.learningTimeConfigs.filter {
                        it.dayOfWeek.name.equals(dayName, ignoreCase = true)
                    }
                    if (configsForDay.isNotEmpty()) {
                        val isExpanded = expandedDays[dayName] ?: true
                        item(key = "header_$dayName") {
                            Row(
                                modifier = modifier
                                    .fillMaxWidth().clip(MaterialTheme.shapes.extraLarge)
                                    .clickable {
                                        expandedDays[dayName] = !isExpanded
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                Icon(
                                    imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Replier" else "Déplier",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 16.dp)
                                )

                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }

                        if (isExpanded) {
                            items(
                                items = configsForDay,
                                key = { it.id ?: it.hashCode() }
                            ) { config ->
                                LearningTimeItemRow(
                                    config = config,
                                    onEdit = {
                                        editingLearningConfig = config
                                        learningDayOfWeek = config.dayOfWeek.name
                                        learningStartTime = config.startDayHourTime
                                        learningEndTime = config.endDayHourTime
                                        learningLabel = config.label
                                        showLearningTimeDialog = true
                                    },
                                    onDelete = { config.id?.let { viewModel.deleteLearningTimeConfig(it) } },
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.width(30.dp))
        }
    }

    // Gestionnaires de Dialogues déportés de la vue principale
    if (showSectionConfigDialog) {
        SchoolSectionConfigDialog(
            editingConfig = editingSectionConfig,
            startTime = sectionStartTime,
            endTime = sectionEndTime,
            onStartTimeChange = { sectionStartTime = it },
            onEndTimeChange = { sectionEndTime = it },
            onDismiss = { showSectionConfigDialog = false },
            onConfirm = {
                val config = SchoolSectionConfigDto(
                    id = editingSectionConfig?.id,
                    dayStartTime = LocalTime.parse(sectionStartTime),
                    dayEndTime = LocalTime.parse(sectionEndTime),
                    schoolSectionId = selectedSectionId!!
                )
                if (editingSectionConfig == null) viewModel.createSchoolSectionConfig(config)
                else viewModel.updateSchoolSectionConfig(editingSectionConfig!!.id!!, config)
                showSectionConfigDialog = false
            })
    }

    if (showLearningTimeDialog) {
        LearningTimeConfigDialog(
            editingConfig = editingLearningConfig,
            label = learningLabel,
            dayOfWeek = learningDayOfWeek,
            startTime = learningStartTime,
            endTime = learningEndTime,
            onLabelChange = { learningLabel = it },
            onDayOfWeekChange = { learningDayOfWeek = it },
            onStartTimeChange = { learningStartTime = it },
            onEndTimeChange = { learningEndTime = it },
            onDismiss = { showLearningTimeDialog = false },
            onConfirm = {
                currentSectionConfig?.id?.let { configId ->
                    val configDto = LearningTimeConfigDto(
                        id = editingLearningConfig?.id,
                        dayOfWeek = DayOfWeek.valueOf(learningDayOfWeek),
                        startDayHourTime = learningStartTime,
                        endDayHourTime = learningEndTime,
                        schoolSectionConfigId = configId,
                        label = learningLabel
                    )
                    if (editingLearningConfig == null) viewModel.createLearningTimeConfig(configDto)
                    else editingLearningConfig?.id?.let { id ->
                        viewModel.updateLearningTimeConfig(
                            id, configDto
                        )
                    }
                    showLearningTimeDialog = false
                }
            })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SectionSelector(
    uiState: CalendarPeriodsUiState,
    selectedSectionId: Long?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSectionSelected: (Long) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = onExpandedChange, modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = uiState.schoolSections.find { it.id == selectedSectionId }?.name ?: "Sélectionner une section",
            onValueChange = {},
            readOnly = true,
            label = { Text("Section Scolaire") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape = MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth().menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            uiState.schoolSections.forEach { section ->
                DropdownMenuItem(text = { Text(section.name, style = MaterialTheme.typography.bodyLarge) }, onClick = {
                    onSectionSelected(section.id)
                    onExpandedChange(false)
                })
            }
        }
    }
}

@Composable
private fun SectionConfigurationCard(
    uiState: CalendarPeriodsUiState, config: SchoolSectionConfigDto?, onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Configuration de la section ${
                            uiState.schoolSections.find { it.id == config!!.schoolSectionId }!!.name
                        }",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Plage journalière globale de travail",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = if (config == null) Icons.Default.Add else Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (config != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    Column {
                        Text(
                            "DÉBUT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            config.dayStartTime.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Column {
                        Text(
                            "FIN",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            config.dayEndTime.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            } else {
                Text(
                    text = "Aucune configuration de base pour cette section.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun LearningTimeItemRow(
    config: LearningTimeConfigDto, onEdit: () -> Unit, onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = config.dayOfWeek.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = config.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${config.startDayHourTime} — ${config.endDayHourTime}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit, contentDescription = "Modifier", tint = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun SchoolSectionConfigDialog(
    editingConfig: SchoolSectionConfigDto?,
    startTime: String,
    endTime: String,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (editingConfig == null) "Ajouter Configuration" else "Modifier Configuration",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = onStartTimeChange,
                    label = { Text("Heure de début (HH:MM)") },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = onEndTimeChange,
                    label = { Text("Heure de fin (HH:MM)") },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { Button(onClick = onConfirm) { Text("Confirmer") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LearningTimeConfigDialog(
    editingConfig: LearningTimeConfigDto?,
    label: String,
    dayOfWeek: String,
    startTime: String,
    endTime: String,
    onLabelChange: (String) -> Unit,
    onDayOfWeekChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var dayDropdownExpanded by remember { mutableStateOf(false) }
    val days = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (editingConfig == null) "Nouveau créneau" else "Modifier le créneau", fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = onLabelChange,
                    label = { Text("Libellé (ex: 1ère Heure)") },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(
                    expanded = dayDropdownExpanded,
                    onExpandedChange = { dayDropdownExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = dayOfWeek,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jour de la semaine") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                dayDropdownExpanded
                            )
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = dayDropdownExpanded,
                        onDismissRequest = { dayDropdownExpanded = false }) {
                        days.forEach { day ->
                            DropdownMenuItem(text = { Text(day) }, onClick = {
                                onDayOfWeekChange(day)
                                dayDropdownExpanded = false
                            })
                        }
                    }
                }
                OutlinedTextField(
                    value = startTime,
                    onValueChange = onStartTimeChange,
                    label = { Text("Heure de début (HH:MM)") },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = onEndTimeChange,
                    label = { Text("Heure de fin (HH:MM)") },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { Button(onClick = onConfirm) { Text(if (editingConfig == null) "Ajouter" else "Sauvegarder") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } })
}
