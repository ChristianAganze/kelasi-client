package com.drcmind.kelasisuite.ui.schooladmin.academics.calendar_periods

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.drcmind.kelasisuite.data.datasource.remote.dto.EvaluationPeriodDTO
import com.drcmind.kelasisuite.domain.model.EvaluationStatus
import com.drcmind.kelasisuite.navigation.Route
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalendarPeriodsScreen(
    viewModel: CalendarPeriodsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedDestination by rememberSaveable {
        mutableStateOf(Route.SchoolAdmin.Academics.CalendarPeriod.TabDestination.CALENDAR_PERIOD.ordinal)
    }

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Academics.CalendarPeriod.AcademicPeriod::class,
                        Route.SchoolAdmin.Academics.CalendarPeriod.AcademicPeriod.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.CalendarPeriod.LearningTime ::class,
                        Route.SchoolAdmin.Academics.CalendarPeriod.LearningTime.serializer()
                    )
                }
            }
        }, Route.SchoolAdmin.Academics.CalendarPeriod.AcademicPeriod
    )
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    Column(modifier = Modifier.fillMaxSize()) {
        SecondaryTabRow(selectedTabIndex = selectedDestination, containerColor = Color.Transparent) {
            Route.SchoolAdmin.Academics.CalendarPeriod.TabDestination.entries.forEachIndexed { index, destination ->
                Tab(
                    selected = selectedDestination == index,
                    onClick = {
                        backStack.add(destination.route)
                        selectedDestination = index
                    },
                    text = {
                        Text(
                            text = destination.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<Route.SchoolAdmin.Academics.CalendarPeriod.AcademicPeriod>() {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Column(
                            modifier = Modifier.weight(0.625f).verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            AcademicYearCard(uiState)

                            EvaluationPeriodsCard(uiState)

                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        if(windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)){
                            OutlinedCard(
                                modifier = Modifier.weight(0.375f).padding(horizontal = 16.dp),
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
                    }
                }
                entry<Route.SchoolAdmin.Academics.CalendarPeriod.LearningTime>{
                    TimeSlotsConfigurationScreen(
                        isLoading = uiState.isLoading,
                        schoolSectionConfigs = uiState.schoolSectionConfigs,
                        schoolSections = uiState.schoolSections,
                        onLoadLearningTimeConfig = {
                            viewModel.loadLearningTimeConfigsBySchoolSectionConfigId(it)
                        },
                        learningTimeConfigs = uiState.learningTimeConfigs,
                        onCreateSchoolSectionConfig = {
                            viewModel.createSchoolSectionConfig(it)
                        },
                        onCreateLearningTimeConfig = {
                            viewModel.createLearningTimeConfig(it)
                        },
                        onDeleteLearningTimeConfig = {
                            viewModel.deleteLearningTimeConfig(it)
                        }
                    )
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicYearCard(uiState: CalendarPeriodsUiState) {
    val dateFormat = LocalDate.Format {
        dayOfMonth(); char('/'); monthNumber(); char('/'); year()
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
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
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

            uiState.evaluationPeriods.forEach { (sectionName, periods) ->
                EvaluationSectionHeader(sectionName = sectionName)
                periods.forEachIndexed { index, period ->
                    EvaluationPeriodRow(period = period, onStartDateChange = {}, onEndDateChange = {})
                    if (index < periods.size - 1) {
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
    val format = LocalDate.Format { dayOfMonth(); char('/'); monthNumber(); char('/'); year() }

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
