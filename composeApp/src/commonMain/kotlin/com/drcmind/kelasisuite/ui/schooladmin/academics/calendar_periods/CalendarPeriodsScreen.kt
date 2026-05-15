
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = MaterialTheme.shapes.large
                            ) {

                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {

                                    Text(
                                        text = "Active Academic Year",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    // Academic year selector
                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = { expanded = !expanded }
                                    ) {

                                        OutlinedTextField(
                                            value = "2025-2026",
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Academic Year") },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor()
                                        )

                                        ExposedDropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {

                                            uiState.academicYears.forEach { year ->

                                                DropdownMenuItem(
                                                    text = {
                                                        Text(year.label)
                                                    },
                                                    onClick = {
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    // Dates row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {

                                        // Start date
                                        OutlinedTextField(
                                            value = selectedStartDate.format(dateFormat),
                                            onValueChange = {},
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    showStartDatePicker = true
                                                },
                                            readOnly = true,
                                            label = {
                                                Text("Start Date")
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    Icons.Default.DateRange,
                                                    contentDescription = null
                                                )
                                            }
                                        )

                                        // End date
                                        OutlinedTextField(
                                            value = selectedEndDate.format(dateFormat),
                                            onValueChange = {},
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    showEndDatePicker = true
                                                },
                                            readOnly = true,
                                            label = {
                                                Text("End Date")
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    Icons.Default.DateRange,
                                                    contentDescription = null
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            if (showStartDatePicker) {

                                val state = rememberDatePickerState(
                                    initialSelectedDateMillis =
                                        selectedStartDate.toEpochDays()
                                            .times(24L * 60L * 60L * 1000L)
                                )

                                DatePickerDialog(
                                    onDismissRequest = {
                                        showStartDatePicker = false
                                    },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {

                                                state.selectedDateMillis?.let { millis ->

                                                    selectedStartDate =
                                                        LocalDate.fromEpochDays(
                                                            (millis / (24L * 60L * 60L * 1000L)).toInt()
                                                        )
                                                }

                                                showStartDatePicker = false
                                            }
                                        ) {
                                            Text("OK")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(
                                            onClick = {
                                                showStartDatePicker = false
                                            }
                                        ) {
                                            Text("Cancel")
                                        }
                                    }
                                ) {
                                    DatePicker(state = state)
                                }
                            }

                            // END DATE PICKER

                            if (showEndDatePicker) {

                                val state = rememberDatePickerState(
                                    initialSelectedDateMillis =
                                        selectedEndDate.toEpochDays()
                                            .times(24L * 60L * 60L * 1000L)
                                )

                                DatePickerDialog(
                                    onDismissRequest = {
                                        showEndDatePicker = false
                                    },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {

                                                state.selectedDateMillis?.let { millis ->

                                                    selectedEndDate =
                                                        LocalDate.fromEpochDays(
                                                            (millis / (24L * 60L * 60L * 1000L)).toInt()
                                                        )
                                                }

                                                showEndDatePicker = false
                                            }
                                        ) {
                                            Text("OK")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(
                                            onClick = {
                                                showEndDatePicker = false
                                            }
                                        ) {
                                            Text("Cancel")
                                        }
                                    }
                                ) {
                                    DatePicker(state = state)
                                }
                            }

                            // --- Section 2: Majors and School Sections/Cycles ---
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Sub-section: Majors Offered
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
                                            Icon(Icons.Default.Add, contentDescription = "Add Major")
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (uiState.majors.isEmpty()) {
                                        Text("No majors configured.", style = MaterialTheme.typography.bodySmall)
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

                                    // Sub-section: School Sections / Cycles
                                    Text(
                                        text = "School Sections / Cycles",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    if (uiState.schoolSections.isEmpty()) {
                                        Text("No sections/cycles configured.", style = MaterialTheme.typography.bodySmall)
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

                            // --- Section 3: Evaluation Period Configuration Table ---
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Evaluation Period Configuration",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    // Table Header
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                            .padding(horizontal = 8.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Period Label",
                                            modifier = Modifier.weight(0.3f),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Start Date",
                                            modifier = Modifier.weight(0.25f),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "End Date",
                                            modifier = Modifier.weight(0.25f),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Status",
                                            modifier = Modifier.weight(0.2f),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                                    // Table Rows grouped by School Section
                                    LazyColumn(
                                        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)
                                    ) {
                                        uiState.evaluationPeriods.forEach { sectionGroup ->
                                            item(key = sectionGroup.schoolSectionName) { // Unique key for section header
                                                EvaluationSectionHeader(sectionName = sectionGroup.schoolSectionName)
                                            }
                                            items(sectionGroup.evaluationPeriods) { period ->
                                                EvaluationPeriodRow(
                                                    period = period,
                                                    onStartDateChange = { newDate ->
                                                        // Find and update the specific period in the nested list
                                                        val sectionIndex =
                                                            uiState.evaluationPeriods.indexOfFirst { it.schoolSectionName == sectionGroup.schoolSectionName }
                                                        if (sectionIndex != -1) {
                                                            val periodIndex =
                                                                uiState.evaluationPeriods[sectionIndex].evaluationPeriods.indexOfFirst { it.id == period.id }
                                                            if (periodIndex != -1) {
                                                                val updatedPeriods =
                                                                    uiState.evaluationPeriods[sectionIndex].evaluationPeriods.toMutableList()
                                                                updatedPeriods[periodIndex] =
                                                                    period.copy(startDate = newDate)
                                                                //uiState.evaluationPeriods[sectionIndex] = uiState.evaluationPeriods[sectionIndex].copy(evaluationPeriods = updatedPeriods)
                                                            }
                                                        }
                                                    },
                                                    onEndDateChange = { newDate ->
                                                        // Find and update the specific period in the nested list
                                                        val sectionIndex =
                                                            uiState.evaluationPeriods.indexOfFirst { it.schoolSectionName == sectionGroup.schoolSectionName }
                                                        if (sectionIndex != -1) {
                                                            val periodIndex =
                                                                uiState.evaluationPeriods[sectionIndex].evaluationPeriods.indexOfFirst { it.id == period.id }
                                                            if (periodIndex != -1) {
                                                                val updatedPeriods =
                                                                    uiState.evaluationPeriods[sectionIndex].evaluationPeriods.toMutableList()
                                                                updatedPeriods[periodIndex] = period.copy(endDate = newDate)
                                                                //uiState.evaluationPeriods[sectionIndex] = uiState.evaluationPeriods[sectionIndex].copy(evaluationPeriods = updatedPeriods)
                                                            }
                                                        }
                                                    },
                                                )
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                                                    thickness = DividerDefaults.Thickness,
                                                    color = DividerDefaults.color
                                                )
                                                // Indent divider
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    entry<Route.SchoolAdmin.Academics.CalendarPeriod.Supporting>(
                        metadata = SupportingPaneSceneStrategy.supportingPane()
                    )
                    {
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = MaterialTheme.shapes.large
                        )
                        {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "All Academic Years",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                if (uiState.academicYears.isEmpty()) {
                                    Text("No academic years configured.", style = MaterialTheme.typography.bodySmall)
                                } else {
                                    LazyColumn {
                                        items(uiState.academicYears) { academicYear ->
                                            ListItem(
                                                headlineContent = {
                                                    Text(
                                                        text = academicYear.label,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        modifier = Modifier.padding(vertical = 4.dp)
                                                    )
                                                    if (academicYear.isActive){
                                                        Text(
                                                            text = "Active",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                            modifier = Modifier
                                                                .background(
                                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                                    shape = RoundedCornerShape(4.dp)
                                                                )
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
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
            )

    }
}

@Composable
fun EvaluationSectionHeader(sectionName: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = sectionName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationPeriodRow(
    period: EvaluationPeriodDTO,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate) -> Unit
) {

    val format = LocalDate.Format {
        day()
        char('/')
        monthNumber()
        char('/')
        year()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Section Name
        Text(
            period.schoolSectionName!!,
            modifier = Modifier.weight(0.3f),
            style = MaterialTheme.typography.bodyMedium
        )

        // Start Date Picker
        var showStartDatePicker by remember { mutableStateOf(false) }
        Text(
            text = period.startDate?.format(format) ?: "",
            modifier = Modifier
                .weight(0.25f)
                .clickable { showStartDatePicker = true },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        if (showStartDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = period.startDate?.toEpochDays()
                ?.times(24)?.times(60)?.times(60)?.times(1000)
            )
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onStartDateChange(LocalDate.fromEpochDays (millis / (24 * 60 * 60 * 1000)))
                        }
                        showStartDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // End Date Picker
        var showEndDatePicker by remember { mutableStateOf(false) }
        Text(
            text = period.endDate?.format(format) ?: "",
            modifier = Modifier
                .weight(0.25f)
                .clickable { showEndDatePicker = true },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        if (showEndDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = period.endDate?.toEpochDays()
                ?.times(24)?.times(60)?.times(60)?.times(1000)
            )
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onEndDateChange(LocalDate.fromEpochDays(millis / (24 * 60 * 60 * 1000)))
                        }
                        showEndDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Status Dropdown
        var statusExpanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.weight(0.2f)) {
            val status = getEvaluationStatus(period.startDate, period.endDate)
            Text(
                text = status.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { statusExpanded = true }
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = when (status) {
                    EvaluationStatus.ONGOING -> MaterialTheme.colorScheme.primary
                    EvaluationStatus.FINISHED -> MaterialTheme.colorScheme.tertiary
                    EvaluationStatus.NOT_YET_ACTIVE -> MaterialTheme.colorScheme.outline
                }
            )
            DropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false }
            ) {
                EvaluationStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status.displayName) },
                        onClick = {
                            statusExpanded = false
                        }
                    )
                }
            }
        }
    }
}

fun getEvaluationStatus(
    startDate: LocalDate?,
    endDate: LocalDate?
): EvaluationStatus {

    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    return when {
        startDate == null || endDate == null ->
            EvaluationStatus.NOT_YET_ACTIVE

        today < startDate ->
            EvaluationStatus.NOT_YET_ACTIVE

        today > endDate ->
            EvaluationStatus.FINISHED

        else ->
            EvaluationStatus.ONGOING
    }
}