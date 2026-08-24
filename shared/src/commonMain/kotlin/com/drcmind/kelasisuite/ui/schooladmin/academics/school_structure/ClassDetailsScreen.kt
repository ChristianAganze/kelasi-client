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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import com.drcmind.kelasisuite.data.datasource.remote.dto.AssignmentStatus
import com.drcmind.kelasisuite.data.datasource.remote.dto.CombinedAssignmentModel
import com.drcmind.kelasisuite.data.datasource.remote.dto.TemplateSubjectDTO
import com.drcmind.kelasisuite.domain.util.UtilsFunctions
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.student.StudentStatus
import com.drcmind.kelasisuite.ui.schooladmin.component.CircularProfile
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

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
    val uiState by viewModel.uiState.collectAsState()

    var showAssignTeacherDialog by remember { mutableStateOf(false) }

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
            val windowAdaptiveInfo = currentWindowAdaptiveInfo()
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
                        ClassDetailMainPane(
                            uiState = uiState,
                            classId = classId,
                            viewModel = viewModel,
                            onNavigateToStudentDetail = onNavigateToStudentDetail,
                            onNavigateToTeacherDetail = onNavigateToTeacherDetail
                        )
                    }
                    entry<Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.Supporting>(
                        metadata = SupportingPaneSceneStrategy.supportingPane()
                    ) {
                        ClassDetailSupportingPane(
                            homeroomAssignment = uiState.homeroomAssignment,
                            onAssignClick = { showAssignTeacherDialog = true },
                            onNavigateToTeacherDetail = onNavigateToTeacherDetail
                        )
                    }
                })
        }
    }

    if (showAssignTeacherDialog) {
        HomeroomTeacherAssignmentDialog(
            teachers = uiState.teachers,
            homeroomAssignment = uiState.homeroomAssignment,
            isAssigning = uiState.isAssigningHomeroomTeacher,
            assignHomeroomTeacher = { viewModel.assignHomeroomTeacher(it, classId) },
            onDismiss = { showAssignTeacherDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassDetailMainPane(
    uiState: SchoolStructureState,
    classId: Long,
    viewModel: SchoolStructureViewModel,
    onNavigateToStudentDetail: (Long) -> Unit,
    onNavigateToTeacherDetail: (Long) -> Unit
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
                        text = if (destination == SchoolClassDetailsScreenTabs.StudentList) "${destination.name} (${uiState.assignments.size})" else destination.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selectedDestination == index) FontWeight.Bold else FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis
                    )
                })

            }
        }

        when (selectedDestination) {
            0 -> {
                StudentsList(
                    classStudents = uiState.classStudents,
                    isLoadingClassStudents = uiState.isLoadingClassStudents,
                    onNavigateToStudentDetail = onNavigateToStudentDetail
                )
            }

            1 -> {
                SubjectAssignmentsList(
                    teachers = uiState.teachers,
                    isAssigning = uiState.isAssigningTeachingAssignment,
                    assignTeacherToSubject = { subjectId, teacherId ->
                        viewModel.assignTeacherToSubject(subjectId, teacherId, classId)
                    },
                    deleteTeachingAssignment = {
                        viewModel.deleteTeachingAssignment(it, classId)
                    },
                    isDeleting = uiState.isDeleting,
                    onNavigateToTeacherDetail = onNavigateToTeacherDetail,
                    combinedAssignment = uiState.filteredCombinedAssignmentAndPendings,
                    isLoadingAssignments = uiState.isLoadingAssignments,
                    onFilterClicked = {
                        when (it) {
                            0 -> {
                                viewModel.getAllAllSubjectsForClass()
                            }

                            1 -> {
                                viewModel.getAssignedSubjectsForClass()
                            }

                            2 -> {
                                viewModel.getPendingSubjectsForClass()
                            }
                        }
                    },
                )
            }

            2 -> {
                TeachersList(
                    classTeachers = uiState.classTeachers
                )
            }
        }
    }
}

@Composable
private fun ClassDetailSupportingPane(
    homeroomAssignment: HomeroomAssignmentDTO?,
    onAssignClick: () -> Unit,
    onNavigateToTeacherDetail: (Long) -> Unit
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
                onAssignClick = onAssignClick,
                onTeacherClick = {
                    homeroomAssignment?.teacherProfileId?.let(
                        onNavigateToTeacherDetail
                    )
                })
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeroomTeacherAssignmentDialog(
    teachers: List<TeacherProfileDTO>,
    homeroomAssignment: HomeroomAssignmentDTO?,
    isAssigning: Boolean,
    assignHomeroomTeacher: (teacherId: Long) -> Unit,
    onDismiss: () -> Unit
) {


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
                assignHomeroomTeacher(selectedTeacherId!!)
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
    classStudents: List<StudentDTO>, isLoadingClassStudents: Boolean, onNavigateToStudentDetail: (id: Long) -> Unit
) {
    Column(modifier = Modifier.padding(top = 8.dp).fillMaxWidth()) {

        OutlinedCard(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
        ) {
            Column {
                when {
                    isLoadingClassStudents -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    classStudents.isEmpty() -> {
                        Text(
                            text = "Aucun étudiant inscrit",
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    else -> classStudents.forEachIndexed { index, student ->
                        StudentRowItem(student, onNavigateToStudentDetail)
                        if (index < classStudents.size - 1) {
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

@Composable
fun TeachersList(
    classTeachers: List<CombinedAssignmentModel>,
) {
    Column(modifier = Modifier.padding(top = 8.dp).fillMaxWidth()) {

        OutlinedCard(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
        ) {
            Column {
                when {

                    classTeachers.isEmpty() -> {
                        Text(
                            text = "Aucun enseignant affecté",
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    else -> classTeachers.forEachIndexed { index, teacher ->
                        TeacherRowItem(teacher)
                        if (index < classTeachers.size - 1) {
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
fun TeacherRowItem(teacher: CombinedAssignmentModel) {
    ListItem(colors = ListItemDefaults.colors(containerColor = Color.Transparent), headlineContent = {
        Text(
            teacher.teacherName!!,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }, supportingContent = {
        Text(
            "ID: ${teacher.teacherId}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }, leadingContent = {
        CircularProfile(text = teacher.teacherName!!.take(1))
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectAssignmentsList(
    combinedAssignment: List<CombinedAssignmentModel>,
    isLoadingAssignments: Boolean,
    teachers: List<TeacherProfileDTO>,
    isAssigning: Boolean,
    assignTeacherToSubject: (subjectId: Long, teacherId: Long) -> Unit,
    deleteTeachingAssignment: (id: Long) -> Unit,
    isDeleting: Boolean,
    onNavigateToTeacherDetail: (id: Long) -> Unit,
    onFilterClicked: (Int) -> Unit
) {
    var showConfirmDeletationDialog by remember { mutableStateOf(false) }
    var showAssignDialog by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf<TemplateSubjectDTO?>(null) }

    val subjectFilterOptions = listOf("Tous", "Assignés", "En attennte")
    var selectedSubjectFilterOption by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }


    val combinedFilteredAssigned = remember(combinedAssignment, searchQuery) {
        if (searchQuery.isBlank()) combinedAssignment
        else combinedAssignment.filter {
            it.subjectName.contains(searchQuery, true) || it.subjectCode.contains(
                searchQuery, true
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
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
                        ), onClick = {
                            selectedSubjectFilterOption = index
                            onFilterClicked(index)
                        }, selected = index == selectedSubjectFilterOption, label = { Text(label) })
                }
            }
        }
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tous les cours (${combinedAssignment.size})",
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
                            itemsIndexed(combinedFilteredAssigned) { _, subject ->
                                HorizontalDivider()
                                ClassSubjectRow(
                                    subject,
                                    onDelete = {
                                        showConfirmDeletationDialog = true
                                        selectedSubject = TemplateSubjectDTO(
                                            id = subject.id,
                                            code = subject.subjectCode,
                                            name = subject.subjectName,
                                            domain = subject.domain,
                                            subDomain = subject.subDomain,
                                        )
                                    },
                                    deleteEnabled = !isDeleting,
                                    onNavigateToTeacherDetail = onNavigateToTeacherDetail,
                                    onAssignTeacherClicked = {
                                        selectedSubject = TemplateSubjectDTO(
                                            id = subject.id,
                                            code = subject.subjectCode,
                                            name = subject.subjectName,
                                            domain = subject.domain,
                                            subDomain = subject.subDomain,
                                        )
                                        showAssignDialog = true
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

    }
    if (showConfirmDeletationDialog && selectedSubject != null) {
        UtilsFunctions.ConfirmationDialog(
            onDismissRequest = { showConfirmDeletationDialog = false },
            onConfirm = {
                deleteTeachingAssignment(selectedSubject!!.id)
            },
            title = "Desassignement",
            text = "Voulez-vous vraiment désassigner ce cours ?",
        )
    }
    if (showAssignDialog && selectedSubject != null) {
        TeachingAssignmentDialog(
            subject = selectedSubject!!,
            teachers = teachers,
            isSubmitting = isSubmitting,
            onConfirm = { teacherId ->
                assignTeacherToSubject(selectedSubject!!.id, teacherId)
                selectedSubject = null
                showAssignDialog = false
                isSubmitting = false
            },
            onDismiss = {
                selectedSubject = null
                showAssignDialog = false
            })
    }
}


@Composable
fun ClassSubjectRow(
    subject: CombinedAssignmentModel,
    onNavigateToTeacherDetail: (Long) -> Unit,
    onAssignTeacherClicked: (id: Long) -> Unit,
    onDelete: () -> Unit,
    deleteEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            subject.subjectName,
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Box(
            modifier = Modifier.weight(1f).padding(end = 8.dp), contentAlignment = Alignment.Center
        ) {
            if (subject.status == AssignmentStatus.ASSIGNED) {

                CircularProfile(
                    modifier = Modifier.clickable(
                        onClick = { onNavigateToTeacherDetail(subject.teacherId!!) },
                    ),
                    text = subject.teacherName?.take(1)?.uppercase() ?: "",
                )

            } else {
                AssistChip(modifier = Modifier, onClick = {}, label = {
                    Text("En attente")
                }, leadingIcon = {

                    Box(
                        modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outline)
                    )
                })
            }
        }

        Box(
            modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd
        ) {
            if (subject.status == AssignmentStatus.PENDING) {
                IconButton(onClick = { onAssignTeacherClicked(subject.subjectId) }) {
                    Icon(AppIcons.personAdd, null)
                }
            } else {
                IconButton(onClick = onDelete, enabled = deleteEnabled) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer l'affectation",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

    }
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
                val fillMaxWidth = Modifier.fillMaxWidth()
                OutlinedTextField(
                    value = teachers.find { it.id == selectedTeacherId }?.fullName ?: "Sélectionner un enseignant",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Enseignant") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(teacherExpanded) },
                    modifier = fillMaxWidth.menuAnchor(),
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
