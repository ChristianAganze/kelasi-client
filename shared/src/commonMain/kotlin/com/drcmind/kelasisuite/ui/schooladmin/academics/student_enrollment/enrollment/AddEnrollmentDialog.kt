@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)

package com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.enrollment

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.data.datasource.remote.dto.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

@Serializable
sealed interface EnrollmentRoute : NavKey {
    @Serializable
    data object SelectStudent : EnrollmentRoute
    @Serializable
    data object AcademicPlacement : EnrollmentRoute
    @Serializable
    data object Review : EnrollmentRoute
}

enum class EnrollmentStep(private val route: EnrollmentRoute){
    SELECT_STUDENT(EnrollmentRoute.SelectStudent),
    ACADEMIC_PLACEMENT(EnrollmentRoute.AcademicPlacement),
    REVIEW(EnrollmentRoute.Review),
}

@Composable
fun EnrollmentDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: EnrollmentViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var currentStep by rememberSaveable{ // Reverted to mutableStateOf
        mutableStateOf(EnrollmentStep.SELECT_STUDENT)
    }

    val enrollmentBackstack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        EnrollmentRoute.SelectStudent::class,
                        EnrollmentRoute.SelectStudent.serializer()
                    )
                    subclass(
                        EnrollmentRoute.AcademicPlacement::class,
                        EnrollmentRoute.AcademicPlacement.serializer()
                    )
                    subclass(
                        EnrollmentRoute.Review::class,
                        EnrollmentRoute.Review.serializer()
                    )
                }
            }
        },
        EnrollmentRoute.SelectStudent
    )

    LaunchedEffect(state.submissionSuccess) {
        if (state.submissionSuccess) {
            onSuccess()
            viewModel.resetState()
        }
    }

    // Helper function for validation
    val isCurrentStepValid = remember(state, currentStep) {
        when (currentStep) {
            EnrollmentStep.SELECT_STUDENT -> state.selectedStudent != null
            EnrollmentStep.ACADEMIC_PLACEMENT -> {
                val placement = state.placement
                placement.selectedAcademicYear != null &&
                        placement.selectedSchoolSection != null &&
                        placement.selectedSection != null &&
                        placement.selectedMajor != null &&
                        placement.selectedGradeLevel != null &&
                        placement.selectedClass != null
            }
            EnrollmentStep.REVIEW -> true // Always valid to proceed to submission
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth().fillMaxHeight(0.9f)
        ) {

            Column {
                Column(modifier = Modifier.weight(1f)) {
                    EnrollmentTopBar(
                        enrollmentStep = currentStep,
                        onDismiss = onDismiss
                    )

                    EnrollmentStepper(
                        enrollmentStep = currentStep
                    )

                    HorizontalDivider()

                    NavDisplay(
                        backStack = enrollmentBackstack,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        entryProvider = entryProvider {
                            entry<EnrollmentRoute.SelectStudent> {
                                StudentSelectionStep(
                                    state = state,
                                    onSearchChange = viewModel::onStudentSearchQueryChange,
                                    onStudentSelected = viewModel::selectStudent
                                )
                            }
                            entry<EnrollmentRoute.AcademicPlacement> {
                                AcademicPlacementStep(
                                    state = state,
                                    onAcademicYearSelected = viewModel::selectAcademicYear,
                                    onSchoolSectionSelected = viewModel::selectSchoolSection,
                                    onSectionSelected = viewModel::selectSection,
                                    onMajorSelected = viewModel::selectMajor,
                                    onGradeLevelSelected = viewModel::selectGradeLevel,
                                    onClassSelected = viewModel::selectClass
                                )
                            }
                            entry<EnrollmentRoute.Review> {
                                ReviewStep(
                                    state = state
                                )
                            }
                        },
                        transitionSpec = {
                            // Slide in from right when navigating forward
                            slideInHorizontally(initialOffsetX = { it }) togetherWith
                                    slideOutHorizontally(targetOffsetX = { -it })
                        },
                        popTransitionSpec = {
                            // Slide in from left when navigating back
                            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                                    slideOutHorizontally(targetOffsetX = { it })
                        },
                        predictivePopTransitionSpec = {
                            // Slide in from left when navigating back
                            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                                    slideOutHorizontally(targetOffsetX = { it })
                        },
                    )
                    HorizontalDivider()
                }

                EnrollmentBottomBar(
                    currentStep = currentStep,
                    onPrevious = {
                        enrollmentBackstack.removeLastOrNull()
                        // Manually update currentStep to the previous one
                        currentStep = EnrollmentStep.entries.getOrElse(currentStep.ordinal - 1) { EnrollmentStep.SELECT_STUDENT }
                    },
                    isSubmitting = state.isSubmitting,
                    isCurrentStepValid = isCurrentStepValid, // Pass validation state
                    onNext = {
                        if (currentStep == EnrollmentStep.REVIEW) {
                            viewModel.submitEnrollment()
                        } else {
                            when (currentStep) {
                                EnrollmentStep.SELECT_STUDENT -> {
                                    enrollmentBackstack.add(EnrollmentRoute.AcademicPlacement)
                                    currentStep = EnrollmentStep.ACADEMIC_PLACEMENT // Manual update
                                }
                                EnrollmentStep.ACADEMIC_PLACEMENT -> {
                                    enrollmentBackstack.add(EnrollmentRoute.Review)
                                    currentStep = EnrollmentStep.REVIEW // Manual update
                                }
                                else -> {} // Should not happen if all steps are covered
                            }
                        }
                    }
                )

            }
        }
    }
}

@Composable
private fun EnrollmentTopBar(
    enrollmentStep: EnrollmentStep,
    onDismiss: () -> Unit
) {
    TopAppBar(
        modifier = Modifier,
        title = {
            Column {
                Text(
                    "Nouvelle inscription",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    when (enrollmentStep) {
                        EnrollmentStep.SELECT_STUDENT ->
                            "Sélectionner l'élève"

                        EnrollmentStep.ACADEMIC_PLACEMENT ->
                            "Placement académique"

                        EnrollmentStep.REVIEW ->
                            "Révision & Spumission"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {

            IconButton(
                onClick = onDismiss
            ) {
                Icon(Icons.Default.Close, null)
            }
        }
    )
}


@Composable
private fun EnrollmentStepper(
    enrollmentStep: EnrollmentStep
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        EnrollmentStep.entries.forEachIndexed { index, step ->

            val active = index <= enrollmentStep.ordinal

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {

                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = if (active)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            "${index + 1}",
                            color = if (active)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    step.name,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentSelectionStep(
    state: EnrollmentUiState,
    onSearchChange: (String) -> Unit,
    onStudentSelected: (StudentDTO) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        SearchBar(
            inputField = {

                InputField(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    query = state.searchQueryStudent,
                    onQueryChange = onSearchChange,
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = {
                        Text("Search student...")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null)
                    }
                )
            },
            expanded = false,
            onExpandedChange = {}
        ) {}

        Spacer(modifier = Modifier.height(20.dp))

        if (state.isLoading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }

        } else {

            LazyColumn {

                items(state.filteredStudents) { student ->

                    val selected =
                        state.selectedStudent?.id == student.id

                    Card(
                        onClick = { onStudentSelected(student) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (selected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {

                        ListItem(
                            headlineContent = {
                                Text(student.fullName)
                            },
                            supportingContent = {

                                Text(
                                    "ID ${student.studentIdNumber}"
                                )
                            },
                            leadingContent = {

                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {

                                    Icon(
                                        Icons.Default.Person,
                                        null,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            },
                            trailingContent = {

                                if (selected) {

                                    Icon(
                                        Icons.Default.CheckCircle,
                                        null,
                                        tint = MaterialTheme.colorScheme.primary
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

@Composable
private fun AcademicPlacementStep(
    state: EnrollmentUiState,
    onAcademicYearSelected: (AcademicYearDTO) -> Unit,
    onSchoolSectionSelected: (SchoolSectionDTO) -> Unit,
    onSectionSelected: (SectionDTO) -> Unit,
    onMajorSelected: (MajorDto) -> Unit,
    onGradeLevelSelected: (GradeLevelDTO) -> Unit,
    onClassSelected: (SchoolClassDTO) -> Unit
) {
    val placement = state.placement
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {

            SelectionDropdown(
                title = "Academic Year",
                value = placement.selectedAcademicYear?.label,
                items = placement.academicYears,
                itemLabel = { it.label },
                onItemSelected = onAcademicYearSelected
            )
        }

        item {

            SelectionDropdown(
                title = "Cycle",
                value = placement.selectedSchoolSection?.name,
                items = placement.schoolSections,
                itemLabel = { it.name },
                onItemSelected = onSchoolSectionSelected
            )
        }

        item {

            SelectionDropdown(
                title = "Section",
                value = placement.selectedSection?.name,
                items = placement.sections,
                itemLabel = { it.name },
                enabled = placement.selectedSchoolSection != null,
                loading = placement.isLoadingSections,
                onItemSelected = onSectionSelected
            )
        }

        item {

            SelectionDropdown(
                title = "Major",
                value = placement.selectedMajor?.name,
                items = placement.majors,
                itemLabel = { it.name },
                enabled = placement.selectedSection != null,
                loading = placement.isLoadingMajors,
                onItemSelected = onMajorSelected
            )
        }

        item {

            SelectionDropdown(
                title = "Grade Level",
                value = placement.selectedGradeLevel?.name,
                items = placement.gradeLevels,
                itemLabel = { it.name },
                enabled = placement.selectedMajor != null,
                loading = placement.isLoadingGradeLevels,
                onItemSelected = onGradeLevelSelected
            )
        }

        item {

            SelectionDropdown(
                title = "Class",
                value = placement.selectedClass?.name,
                items = placement.classes,
                itemLabel = { it.name },
                enabled = placement.selectedGradeLevel != null,
                loading = placement.isLoadingClasses,
                onItemSelected = onClassSelected
            )
        }
    }
}

@Composable
private fun ReviewStep(
    state: EnrollmentUiState
) {

    val placement = state.placement

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            ReviewCard(
                title = "Student"
            ) {
                ReviewItem(
                    "Student",
                    state.selectedStudent?.fullName ?: "--"
                )
                ReviewItem(
                    "Student ID",
                    state.selectedStudent?.studentIdNumber ?: "--"
                )
            }
        }

        item {
            ReviewCard(
                title = "Academic Placement"
            ) {
                ReviewItem(
                    "Academic Year",
                    placement.selectedAcademicYear?.label ?: "--"
                )
                ReviewItem(
                    "Cycle",
                    placement.selectedSchoolSection?.name ?: "--"
                )
                ReviewItem(
                    "Section",
                    placement.selectedSection?.name ?: "--"
                )
                ReviewItem(
                    "Major",
                    placement.selectedMajor?.name ?: "--"
                )
                ReviewItem(
                    "Grade Level",
                    placement.selectedGradeLevel?.name ?: "--"
                )
                ReviewItem(
                    "Class",
                    placement.selectedClass?.name ?: "--"
                )
            }
        }
    }
}


@Composable
private fun EnrollmentBottomBar(
    currentStep: EnrollmentStep,
    isSubmitting: Boolean,
    isCurrentStepValid: Boolean, // New parameter for validation
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        OutlinedButton(
            enabled = currentStep != EnrollmentStep.SELECT_STUDENT,
            onClick = onPrevious
        ) {

            Text("Back")
        }

        Button(
            enabled = !isSubmitting && isCurrentStepValid, // Use validation state here
            onClick = onNext
        ) {

            if (isSubmitting) {

                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )

            } else {

                Text(
                    if (currentStep == EnrollmentStep.REVIEW)
                        "Submit Enrollment"
                    else
                        "Continue"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SelectionDropdown(
    title: String,
    value: String?,
    items: List<T>,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {

            if (enabled) {
                expanded = !expanded
            }
        }
    ) {

        OutlinedTextField(
            value = value ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = {
                Text(title)
            },
            trailingIcon = {

                if (loading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )

                } else {

                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                }
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {

            items.forEach { item ->

                DropdownMenuItem(
                    text = {
                        Text(
                            itemLabel(item)
                        )
                    },
                    onClick = {

                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ReviewCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {

    OutlinedCard {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth()
        ) {

            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
private fun ReviewItem(
    label: String,
    value: String
) {

    Column(
        modifier = Modifier.padding(vertical = 6.dp)
    ) {

        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}