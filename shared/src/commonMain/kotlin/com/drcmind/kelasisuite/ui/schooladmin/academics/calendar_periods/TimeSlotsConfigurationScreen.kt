package com.drcmind.kelasisuite.ui.schooladmin.academics.calendar_periods

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.data.datasource.remote.dto.LearningTimeConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionConfigDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.domain.util.toFrench
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import com.drcmind.kelasisuite.ui.components.EmptyDetailPlaceholder
import com.drcmind.kelasisuite.ui.schooladmin.component.SectionCard
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimeSlotsConfigurationScreen(
    isLoading: Boolean,
    schoolSectionConfigs: List<SchoolSectionConfigDto>,
    learningTimeConfigs: List<LearningTimeKey>,
    schoolSections: List<SchoolSectionDTO>,
    onLoadLearningTimeConfig: (Long) -> Unit,
    onCreateSchoolSectionConfig: (SchoolSectionConfigDto) -> Unit,
    onCreateLearningTimeConfig: (LearningTimeConfigDto) -> Unit,
    onDeleteLearningTimeConfig: (Long) -> Unit,
) {
    var showSchoolSectionConfigDialog by remember { mutableStateOf(false) }
    var showLearningTimeConfigDialog by remember { mutableStateOf(false) }
    var selectedSchoolSectionConfigIdForLtc by remember { mutableStateOf<Long?>(null) }

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Academics.CalendarPeriod.LearningTime.SchoolSectionConfig::class,
                        Route.SchoolAdmin.Academics.CalendarPeriod.LearningTime.SchoolSectionConfig.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.CalendarPeriod.LearningTime.SchoolSectionConfigDetails::class,
                        Route.SchoolAdmin.Academics.CalendarPeriod.LearningTime.SchoolSectionConfigDetails.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Academics.CalendarPeriod.LearningTime.SchoolSectionConfig
    )
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(
                horizontalPartitionSpacerSize = 0.dp,
                verticalPartitionSpacerSize = 0.dp,
            )
    }
    val listDetailsStrateggy = rememberListDetailSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
        directive = directive,
        adaptStrategies = ListDetailPaneScaffoldDefaults.adaptStrategies()
    )

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(listDetailsStrateggy),
        entryProvider = entryProvider {
            entry<Route.SchoolAdmin.Academics.CalendarPeriod.LearningTime.SchoolSectionConfig>(
                metadata = ListDetailSceneStrategy.listPane(

                    detailPlaceholder = {
                        EmptyDetailPlaceholder(
                            icon = Icons.Filled.NoFood,
                            title = "Aucune configuration de section selectionnée",
                            subtitle = "Sélectionner une configuration",
                        )
                    }
                )
            )
            {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("Configuration sections")
                            },
                            actions = {
                                IconButton(onClick = { showSchoolSectionConfigDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                }
                                IconButton(onClick = {}){
                                    Icon(Icons.Default.MoreVert, contentDescription = null)
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(innerPadding)
                                .padding(start = 16.dp, end = 16.dp),
                            shape = MaterialTheme.shapes.medium.copy(
                                bottomEnd = CornerSize(0.dp),
                                bottomStart = CornerSize(0.dp)
                            )
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    when (schoolSectionConfigs.isEmpty()) {
                                        true -> Column(
                                            modifier = Modifier.fillMaxSize().padding(vertical = 40.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(AppIcons.peoples, contentDescription = null, Modifier.size(100.dp))
                                            Spacer(Modifier.height(10.dp))
                                            Text("Aucune configuation encore disponnible")
                                        }

                                        false -> LazyColumn {
                                            items(schoolSectionConfigs) { ssc ->
                                                ListItem(
                                                    modifier = Modifier.clickable {
                                                        onLoadLearningTimeConfig(ssc.id!!)
                                                        backStack.add(Route.SchoolAdmin.Academics.CalendarPeriod.LearningTime.SchoolSectionConfigDetails(
                                                            ssc.id, ssc.schoolSectionName, ssc.dayStartTime, ssc.dayEndTime
                                                        ))
                                                    },
                                                    headlineContent = {
                                                        Text(ssc.schoolSectionName)
                                                    },
                                                    trailingContent = {
                                                        Text(
                                                            "${ssc.dayStartTime} - ${ssc.dayEndTime}",
                                                            style = MaterialTheme.typography.labelSmall
                                                        )
                                                    }
                                                )
                                                HorizontalDivider()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            entry<Route.SchoolAdmin.Academics.CalendarPeriod.LearningTime.SchoolSectionConfigDetails>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) {key->
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = key.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${key.startHour} - ${key.endHour}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    backStack.removeLastOrNull()
                                }){
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Column(Modifier.padding(innerPadding).padding(horizontal = 8.dp)) {
                        SectionCard(
                            title = "Table de rythme hebdomadaire",
                            icon = Icons.Default.Info,
                            actions = {
                                IconButton(onClick = {
                                    selectedSchoolSectionConfigIdForLtc = key.id
                                    showLearningTimeConfigDialog = true
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                }
                            }
                        ){
                            learningTimeConfigs.forEach { ltc->
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Row(modifier = Modifier.weight(2f)) {
                                        Text(ltc.label + " : " + ltc.startDayHourTime + " - " + ltc.endDayHourTime, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Row (modifier = Modifier.weight(5f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        ltc.daysWithIds.forEach { (dow, id) ->
                                            AssistChip(
                                                label = {
                                                    Text(dow.toFrench().take(2))
                                                },
                                                onClick = {},
                                                trailingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp).clickable {
                                                            onDeleteLearningTimeConfig(id)
                                                        }
                                                    )
                                                }
                                            )
                                        }
                                        if(ltc.daysWithIds.size < 7){
                                            IconButton(onClick = {
                                                val nextDay = DayOfWeek.entries.firstOrNull { it !in ltc.daysWithIds.keys }
                                                if(nextDay != null) {
                                                    onCreateLearningTimeConfig(
                                                        LearningTimeConfigDto(
                                                            id = null,
                                                            label = ltc.label,
                                                            startDayHourTime = ltc.startDayHourTime,
                                                            endDayHourTime = ltc.endDayHourTime,
                                                            dayOfWeek = nextDay,
                                                            schoolSectionConfigId = ltc.schoolSectionConfigId
                                                        )
                                                    )
                                                }
                                            }) {
                                                Icon(Icons.Default.Add, contentDescription = null)
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
    )

    if (showSchoolSectionConfigDialog) {
        SchoolSectionConfigDialog(
            schoolSections = schoolSections,
            onDismiss = { showSchoolSectionConfigDialog = false },
            onConfirm = { config ->
                onCreateSchoolSectionConfig(config)
                showSchoolSectionConfigDialog = false
            }
        )
    }

    if (showLearningTimeConfigDialog) {
        LearningTimeConfigDialog(
            schoolSectionConfigId = selectedSchoolSectionConfigIdForLtc!!,
            onDismiss = { showLearningTimeConfigDialog = false },
            onConfirm = { config ->
                onCreateLearningTimeConfig(config)
                showLearningTimeConfigDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchoolSectionConfigDialog(
    schoolSections: List<SchoolSectionDTO>,
    onDismiss: () -> Unit,
    onConfirm: (SchoolSectionConfigDto) -> Unit
) {
    var selectedSectionId by remember { mutableStateOf<Long?>(null) }
    var startTime by remember { mutableStateOf(LocalTime(8, 0)) }
    var endTime by remember { mutableStateOf(LocalTime(16, 0)) }
    var expanded by remember { mutableStateOf(false) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    if (showStartTimePicker) {
        TimePickerDialog(
            initialTime = startTime,
            onDismiss = { showStartTimePicker = false },
            onConfirm = {
                startTime = it
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            initialTime = endTime,
            onDismiss = { showEndTimePicker = false },
            onConfirm = {
                endTime = it
                showEndTimePicker = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Ajouter Configuration de Section", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = schoolSections.find { it.id == selectedSectionId }?.name
                            ?: "Sélectionner une section",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Section Scolaire") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        schoolSections.forEach { section ->
                            DropdownMenuItem(
                                text = { Text(section.name) },
                                onClick = {
                                    selectedSectionId = section.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = startTime.toString().take(5),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Heure de début") },
                    modifier = Modifier.fillMaxWidth().clickable { showStartTimePicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                OutlinedTextField(
                    value = endTime.toString().take(5),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Heure de fin") },
                    modifier = Modifier.fillMaxWidth().clickable { showEndTimePicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        confirmButton = {
            Button(
                enabled = selectedSectionId != null,
                onClick = {
                    onConfirm(
                        SchoolSectionConfigDto(
                            id = null,
                            dayStartTime = startTime,
                            dayEndTime = endTime,
                            schoolSectionId = selectedSectionId!!,
                            schoolSectionName = schoolSections.find { it.id == selectedSectionId }?.name ?: ""
                        )
                    )
                }
            ) { Text("Confirmer") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LearningTimeConfigDialog(
    schoolSectionConfigId: Long,
    onDismiss: () -> Unit,
    onConfirm: (LearningTimeConfigDto) -> Unit
) {
    var label by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf(DayOfWeek.MONDAY) }
    var startTime by remember { mutableStateOf(LocalTime(8, 0)) }
    var endTime by remember { mutableStateOf(LocalTime(8, 50)) }
    var expanded by remember { mutableStateOf(false) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    if (showStartTimePicker) {
        TimePickerDialog(
            initialTime = startTime,
            onDismiss = { showStartTimePicker = false },
            onConfirm = {
                startTime = it
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            initialTime = endTime,
            onDismiss = { showEndTimePicker = false },
            onConfirm = {
                endTime = it
                showEndTimePicker = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Ajouter Créneau de Temps", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Libellé (ex: 1ère Heure)") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedDay.toFrench(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jour de la semaine") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DayOfWeek.entries.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day.toFrench()) },
                                onClick = {
                                    selectedDay = day
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = startTime.toString().take(5),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Heure de début") },
                    modifier = Modifier.fillMaxWidth().clickable { showStartTimePicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                OutlinedTextField(
                    value = endTime.toString().take(5),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Heure de fin") },
                    modifier = Modifier.fillMaxWidth().clickable { showEndTimePicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        confirmButton = {
            Button(
                enabled = label.isNotBlank(),
                onClick = {
                    onConfirm(
                        LearningTimeConfigDto(
                            id = null,
                            label = label,
                            startDayHourTime = startTime.toString().take(5),
                            endDayHourTime = endTime.toString().take(5),
                            dayOfWeek = selectedDay,
                            schoolSectionConfigId = schoolSectionConfigId
                        )
                    )
                }
            ) { Text("Confirmer") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier.width(IntrinsicSize.Min).height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sélectionner l'heure",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                )
                TimePicker(state = timePickerState)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    TextButton(
                        onClick = {
                            onConfirm(LocalTime(timePickerState.hour, timePickerState.minute))
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
