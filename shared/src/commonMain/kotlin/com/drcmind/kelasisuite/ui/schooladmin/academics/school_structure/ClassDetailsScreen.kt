package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.SearchBarDefaults.InputField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.data.datasource.remote.dto.HomeroomAssignmentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.domain.dto.CombinedAssignmentModel
import com.drcmind.kelasisuite.domain.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.domain.dto.TemplateSubjectDTO
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.student.StudentStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel
import kotlin.collections.plus

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailsScreen(
    classId: Long,
    className: String,
    viewModel: SchoolStructureViewModel,
    onBack: () -> Unit,
    onNavigateToStudentDetail: (Long) -> Unit = {},
    onNavigateToTeacherDetail: (Long) -> Unit = {}
) {

    var showAssignTeacherDialog by remember { mutableStateOf(false) }

    val homeroomAssignment by viewModel.homeroomAssignment.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                title = {
                    Column {
                        Text(
                            text = className, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More menu")
                    }
                },
            )
        },

        ) { padding ->

        Column(modifier = Modifier.padding(padding)) {
            val backStack = rememberNavBackStack(
                configuration = SavedStateConfiguration {
                    serializersModule = SerializersModule {
                        polymorphic(NavKey::class) {
                            subclass(
                                Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Main::class,
                                Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Main.serializer()
                            )
                            subclass(
                                Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Supporting::class,
                                Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Supporting.serializer()
                            )
                        }
                    }
                },
                Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Supporting,
                Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Main
            )
            val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
            val directive = remember(windowAdaptiveInfo) {
                calculatePaneScaffoldDirective(windowAdaptiveInfo).copy(
                    horizontalPartitionSpacerSize = 0.dp, verticalPartitionSpacerSize = 0.dp
                )
            }
            val supportingPaneStrategy = rememberSupportingPaneSceneStrategy<NavKey>(
                backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange, directive = directive
            )

            NavDisplay(
                modifier = Modifier.fillMaxSize(),
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                sceneStrategies = listOf(supportingPaneStrategy),
                entryProvider = entryProvider {
                    entry<Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Main>(
                        metadata = SupportingPaneSceneStrategy.mainPane()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 32.dp)) {
                            var selectedDestination by rememberSaveable {
                                mutableStateOf(SchoolClassDetailsScreenTabs.StudentList.ordinal)
                            }
                            SecondaryScrollableTabRow(
                                selectedTabIndex = selectedDestination, edgePadding = 0.dp, divider = {}) {
                                SchoolClassDetailsScreenTabs.entries.forEachIndexed { index, destination ->
                                    Tab(selected = selectedDestination == index, onClick = {
                                        selectedDestination = index
                                    }, text = {
                                        Text(
                                            text = if (destination == SchoolClassDetailsScreenTabs.StudentList) "${destination.name} (${viewModel.assignments.value.size})" else destination.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = if (selectedDestination == index) FontWeight.Bold else FontWeight.Normal,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    })

                                }
                            }

                            when (selectedDestination) {
                                0 -> {
                                    StudentsList(viewModel, classId, onNavigateToStudentDetail)
                                }

                                1 -> {
                                    SubjectAssignmentsScreen(
                                        viewModel, classId, onNavigateToTeacherDetail
                                    )
                                }

                                2 -> { /* Settings placeholder */
                                }
                            }
                        }
                    }
                    entry<Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Supporting>(
                        metadata = SupportingPaneSceneStrategy.supportingPane()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                DailyPlanningCard()
                                TeacherMiniCard(
                                    homeroomAssignment,
                                    onAssignClick = { showAssignTeacherDialog = true },
                                    onTeacherClick = {
                                        homeroomAssignment?.teacherProfileId?.let(
                                            onNavigateToTeacherDetail
                                        )
                                    })
                            }
                        }
                    }
                })
        }
    }


    if (showAssignTeacherDialog) {
        HomeroomTeacherAssignmentDialog(
            viewModel = viewModel, classId = classId, onDismiss = { showAssignTeacherDialog = false })

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeroomTeacherAssignmentDialog(
    viewModel: SchoolStructureViewModel, classId: Long, onDismiss: () -> Unit
) {
    val teachers by viewModel.teachers.collectAsState()
    val homeroomAssignment by viewModel.homeroomAssignment.collectAsState()
    val isAssigning by viewModel.isAssigningHomeroomTeacher.collectAsState()

    var selectedTeacherId by remember { mutableStateOf(homeroomAssignment?.teacherProfileId) }
    var teacherExpanded by remember { mutableStateOf(false) }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Affecter un titulaire") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ExposedDropdownMenuBox(
                expanded = teacherExpanded, onExpandedChange = { teacherExpanded = it }) {
                OutlinedTextField(
                    value = teachers.find { it.id == selectedTeacherId }?.fullName ?: "Sélectionner un professeur",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Professeur") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(teacherExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = MaterialTheme.shapes.large,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = teacherExpanded, onDismissRequest = { teacherExpanded = false }) {
                    teachers.forEach { teacher ->
                        DropdownMenuItem(text = { Text(teacher.fullName) }, onClick = {
                            selectedTeacherId = teacher.id
                            teacherExpanded = false
                        })
                    }
                }
            }
        }
    }, confirmButton = {
        Button(
            enabled = selectedTeacherId != null && !isAssigning, onClick = {
                viewModel.assignHomeroomTeacher(selectedTeacherId!!, classId)
                onDismiss()
            }) {
            if (isAssigning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Affecter")
            }
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) { Text("Annuler") }
    })
}

@Composable
fun StudentsList(
    viewModel: SchoolStructureViewModel, classId: Long, onNavigateToStudentDetail: (Long) -> Unit = {}
) {
    val students by viewModel.clasStudents.collectAsState()
    val isLoading by viewModel.isLoadingClassStudents.collectAsState()

    Column(modifier = Modifier.padding(top = 8.dp).fillMaxWidth()) {

        OutlinedCard(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
        ) {
            Column {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    students.isEmpty() -> {
                        Text(
                            text = "Aucun étudiant inscrit",
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    else -> students.forEachIndexed { index, student ->
                        StudentRowItem(student, onNavigateToStudentDetail)
                        if (index < students.size - 1) {
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
}

@Composable
fun StudentRowItem(student: StudentDTO, onClick: (Long) -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick(student.id) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(
                student.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = {
            Text(
                "ID: ${student.studentIdNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.fullName.take(1),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        trailingContent = {
            val statusColor = when (student.status) {
                StudentStatus.ACTIVE -> Color(
                    0xFF10B981
                )

                StudentStatus.PROBATION -> Color(
                    0xFFF59E0B
                )

                StudentStatus.INACTIVE -> MaterialTheme.colorScheme.outline
            }
            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = CircleShape,
                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(Modifier.size(6.dp).background(statusColor, CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = student.status.name,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectAssignmentsScreen(
    viewModel: SchoolStructureViewModel, classId: Long, onNavigateToTeacherDetail: (Long) -> Unit = {}
) {
    val assignments by viewModel.assignments.collectAsState()
    val combinedAssignment by viewModel.combinedAssignmentAndPendings.collectAsState()
    val pendingSubjects by viewModel.pendingAssignmentsSubjects.collectAsState()
    val teachers by viewModel.teachers.collectAsState()
    val isLoadingAssignments by viewModel.isLoadingAssignments.collectAsState()
    val isLoadingPendingAssignments by viewModel.isLoadingPendingAssignments.collectAsState()
    val isAssigning by viewModel.isAssigningTeachingAssignment.collectAsState()
    val isDeleting by viewModel.isDeletingTeachingAssignment.collectAsState()

    var showAssignDialog by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf<TemplateSubjectDTO?>(null) }

    val subjectFilterOptions = listOf("Tous", "Assignés", "En attennte")
    var selectedSubjectFilterOption by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(classId) {
        viewModel.loadClassTeachingAssignments(classId)
        viewModel.loadPendingTeachingAssignments(classId)
        viewModel.loadCombinedAssignments()
    }

    val combinedFilteredAssigned = remember(combinedAssignment, searchQuery) {
        if (searchQuery.isBlank()) combinedAssignment
        else combinedAssignment.filter {
            it.subjectName.contains(searchQuery, true) || it.subjectCode.contains(
                searchQuery, true
            )
        }
    }

    val filteredAssigned = remember(assignments, searchQuery) {
        if (searchQuery.isBlank()) assignments
        else assignments.filter {
            it.subjectName.contains(searchQuery, true) || it.subjectCode.contains(
                searchQuery, true
            )
        }
    }

    val filteredPending = remember(pendingSubjects, searchQuery) {
        if (searchQuery.isBlank()) pendingSubjects
        else pendingSubjects.filter {
            it.name.contains(searchQuery, true) || it.code.contains(searchQuery, true)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(modifier = Modifier.weight(1f).padding(end = 8.dp), inputField = {
                InputField(
                    modifier = Modifier.height(44.dp).padding(horizontal = 12.dp),
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = {
                        Text("Rechercher cours, code ou enseignant...")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null)
                    })
            }, expanded = false, onExpandedChange = {}) {}


            SingleChoiceSegmentedButtonRow(modifier = Modifier.weight(1f)) {
                subjectFilterOptions.forEachIndexed { index, label ->

                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index, count = subjectFilterOptions.size
                        ),
                        onClick = { selectedSubjectFilterOption = index },
                        selected = index == selectedSubjectFilterOption,
                        label = { Text(label) })
                }

            }

        }


        if (selectedSubjectFilterOption == 0) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tous les cours (${combinedAssignment.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    when {
                        isLoadingAssignments || isLoadingPendingAssignments -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        combinedFilteredAssigned.isEmpty() -> {
                            Text(
                                text = "Aucun cours trouvé.",
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        else -> {
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                itemsIndexed(combinedFilteredAssigned) { index, assignment ->
                                    HorizontalDivider()
                                    SubjectAssignmentRow(
                                        assignment,
                                        onDelete = {
                                            viewModel.deleteTeachingAssignment(assignment.id, classId)
                                        },
                                        deleteEnabled = !isDeleting,
                                        onNavigateToTeacherDetail = onNavigateToTeacherDetail,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }


        if (selectedSubjectFilterOption == 1) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cours assignés (${assignments.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    when {
                        isLoadingAssignments -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        filteredAssigned.isEmpty() -> {
                            Text(
                                text = "Aucun cours trouvé.",
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        else -> {
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                itemsIndexed(filteredAssigned) { index, assignment ->
                                    HorizontalDivider()
                                    SubjectAssignmentRow(
                                        assignment.toCombinedModel(), onNavigateToTeacherDetail, onDelete = {
                                            viewModel.deleteTeachingAssignment(assignment.id, classId)
                                        }, deleteEnabled = !isDeleting
                                    )

                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedSubjectFilterOption == 2) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cours à affecter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    when {
                        isLoadingPendingAssignments -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        filteredPending.isEmpty() -> {
                            Text(
                                text = "Aucun cours à affecter trouvé.",
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        else -> {
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                itemsIndexed(filteredPending) { index, subject ->
                                    HorizontalDivider()
                                    PendingSubjectRow(subject) {
                                        selectedSubject = subject
                                        showAssignDialog = true
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAssignDialog && selectedSubject != null) {
        TeachingAssignmentDialog(
            subject = selectedSubject!!,
            teachers = teachers,
            isSubmitting = isAssigning,
            onConfirm = { teacherId ->
                viewModel.assignTeacherToSubject(selectedSubject!!.id, teacherId, classId)
                selectedSubject = null
                showAssignDialog = false
            },
            onDismiss = {
                selectedSubject = null
                showAssignDialog = false
            })
    }
}

@Composable
fun SubjectAssignmentRow(
    assignment: CombinedAssignmentModel,
    onNavigateToTeacherDetail: (Long) -> Unit,
    onDelete: () -> Unit,
    deleteEnabled: Boolean
) {
    ListItem(modifier = Modifier.fillMaxWidth(), headlineContent = {
        Text(
            assignment.subjectName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }, supportingContent = {
        Column {
            Text(
                assignment.teacherName ?: "--",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                assignment.subjectCode,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }, trailingContent = {
        Row {
            if (assignment.teacherId != null) {
                IconButton(onClick = { onNavigateToTeacherDetail(assignment.teacherId) }) {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
                Spacer(Modifier.width(5.dp))
                IconButton(onClick = onDelete, enabled = deleteEnabled) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer l'affectation",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

        }
    })
}

@Composable
fun PendingSubjectRow(subject: TemplateSubjectDTO, onAssign: () -> Unit) {
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(
                subject.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = {
            Text(
                subject.code, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline
            )
        },
        trailingContent = {
            TextButton(onClick = onAssign) {
                Text("Affecter")
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeachingAssignmentDialog(
    subject: TemplateSubjectDTO,
    teachers: List<TeacherProfileDTO>,
    isSubmitting: Boolean,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTeacherId by remember { mutableStateOf<Long?>(null) }
    var teacherExpanded by remember { mutableStateOf(false) }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Affecter un enseignant") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                "Cours : ${subject.name}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium
            )
            ExposedDropdownMenuBox(
                expanded = teacherExpanded, onExpandedChange = { teacherExpanded = it }) {
                OutlinedTextField(
                    value = teachers.find { it.id == selectedTeacherId }?.fullName ?: "Sélectionner un enseignant",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Enseignant") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(teacherExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = MaterialTheme.shapes.large,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = teacherExpanded, onDismissRequest = { teacherExpanded = false }) {
                    teachers.forEach { teacher ->
                        DropdownMenuItem(text = { Text(teacher.fullName) }, onClick = {
                            selectedTeacherId = teacher.id
                            teacherExpanded = false
                        })
                    }
                }
            }
        }
    }, confirmButton = {
        Button(
            enabled = selectedTeacherId != null && !isSubmitting, onClick = { onConfirm(selectedTeacherId!!) }) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Affecter")
            }
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) { Text("Annuler") }
    })
}

@Composable
fun DailyPlanningCard() {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ), border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    AppIcons.curriculum, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Planning du jour", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            val items = listOf(
                "08:00" to "Chimie", "09:00" to "Géo", "10:00" to "Histoire", "11:00" to "Ed. Vie", "12:00" to "Maths"
            )
            items.forEach { (time, subject) ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(48.dp)
                    )
                    Text(
                        text = subject, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun TeacherMiniCard(
    homeroomAssignment: HomeroomAssignmentDTO?, onAssignClick: () -> Unit, onTeacherClick: (Long) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.clickable {
                homeroomAssignment?.teacherProfileId?.let(onTeacherClick)
            }.fillMaxWidth().padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        AppIcons.person,
                        null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        homeroomAssignment?.teacherName ?: "Aucun titulaire assigné",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Titulaire de la classe",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            TextButton(onClick = onAssignClick) {
                Text(if (homeroomAssignment != null) "Modifier" else "Affecter")
            }
        }
    }
}



