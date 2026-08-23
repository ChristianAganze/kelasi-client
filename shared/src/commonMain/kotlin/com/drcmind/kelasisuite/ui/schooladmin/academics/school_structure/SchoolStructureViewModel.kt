package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.*
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepository
import com.drcmind.kelasisuite.domain.model.SchoolTreeNode
import com.drcmind.kelasisuite.domain.util.NodeType
import com.drcmind.kelasisuite.domain.util.Resource
import com.drcmind.kelasisuite.domain.util.buildVisibleNodes
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class SchoolStructureState(

    // Global state
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,

    // Main data
    val classes: List<SchoolClassDTO> = emptyList(),
    val academicYears: List<AcademicYearDTO> = emptyList(),
    val students: List<StudentDTO> = emptyList(),
    val teachers: List<TeacherProfileDTO> = emptyList(),
    val classTeachers: List<CombinedAssignmentModel> = emptyList(),

    // Homeroom teacher
    val homeroomAssignment: HomeroomAssignmentDTO? = null,
    val isLoadingHomeroomTeacher: Boolean = false,
    val isAssigningHomeroomTeacher: Boolean = false,

    // Class students
    val classStudents: List<StudentDTO> = emptyList(),
    val isLoadingClassStudents: Boolean = false,

    // Teaching assignments
    val assignments: List<TeachingAssignmentDTO> = emptyList(),
    val pendingAssignmentsSubjects: List<TemplateSubjectDTO> = emptyList(),

    val isLoadingAssignments: Boolean = false,
    val isLoadingPendingAssignments: Boolean = false,

    val isAssigningTeachingAssignment: Boolean = false,
    val isDeletingTeachingAssignment: Boolean = false,

    // Combined UI models
    val combinedAssignmentAndPendings: List<CombinedAssignmentModel> = emptyList(),
    val filteredCombinedAssignmentAndPendings: List<CombinedAssignmentModel> = emptyList(),

    // Tree nodes
    val nodes: SnapshotStateList<SchoolTreeNode> = mutableStateListOf(),

    // Node info dialog
    val infoNode: SchoolTreeNode? = null
)


class SchoolStructureViewModel(
    private val schoolRepository: SchoolRepository,
    private val studentsRepository: StudentsRepository,
    private val settingsStorage: SettingsStorage,
    private val teachersRepository: TeachersRepository,
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    // --- États généraux ---
    private val _uiState = MutableStateFlow(SchoolStructureState())
    val uiState: StateFlow<SchoolStructureState> = _uiState.asStateFlow()

    init {
        loadRoots()
        loadClasses()
        loadAcademicYears()
        loadTeachers()
    }

    fun getAllAllSubjectsForClass() {
        _uiState.update { it.copy(filteredCombinedAssignmentAndPendings = it.combinedAssignmentAndPendings) }
    }

    fun getAssignedSubjectsForClass() {
        _uiState.update { it.copy(filteredCombinedAssignmentAndPendings = it.combinedAssignmentAndPendings.filter { it.status == AssignmentStatus.ASSIGNED }) }
    }

    fun getPendingSubjectsForClass() {
        _uiState.update { it.copy(filteredCombinedAssignmentAndPendings = it.combinedAssignmentAndPendings.filter { it.status == AssignmentStatus.PENDING }) }
    }

    private fun loadClasses() {
        schoolRepository.getClassesForSchool().onEach { resource ->
            if (resource is Resource.Success) {
                _uiState.update { it.copy(classes = resource.data ?: emptyList()) }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadAcademicYears() {
        schoolRepository.getAcademicYears().onEach { resource ->
            if (resource is Resource.Success) {
                _uiState.update { it.copy(academicYears = resource.data ?: emptyList()) }
            }
        }.launchIn(viewModelScope)
    }

    fun loadClassStudents(classId: Long) {
        studentsRepository.getStudentsForClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading<*> -> _uiState.update { it.copy(isLoading = true) }
                is Resource.Success<*> -> {
                    _uiState.update { it.copy(isLoading = false, classStudents = resource.data ?: emptyList()) }
                }

                is Resource.Error<*> -> _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
            }
        }.launchIn(viewModelScope)
    }


    fun loadTeachers() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        teachersRepository.getTeachers(schoolId).onEach { resource ->
            if (resource is Resource.Success) {
                _uiState.update { it.copy(teachers = resource.data ?: emptyList()) }
            }
        }.launchIn(viewModelScope)
    }

    fun loadHomeroomTeacher(classId: Long) {
        teachersRepository.getHomeroomTeacherForClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            homeroomAssignment = resource.data,
                            isLoadingHomeroomTeacher = resource.data != null
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun assignHomeroomTeacher(teacherProfileId: Long, classId: Long) {
        val request = HomeroomAssignmentRequest(teacherProfileId, classId)
        teachersRepository.assignHomeroomTeacher(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _uiState.update { it.copy(isAssigningHomeroomTeacher = true) }
                is Resource.Success -> {
                    _uiState.update { it.copy(isAssigningHomeroomTeacher = false, homeroomAssignment = resource.data) }
                    loadHomeroomTeacher(classId)
                }

                is Resource.Error -> _uiState.update {
                    it.copy(
                        isAssigningHomeroomTeacher = false,
                        errorMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun loadClassTeachingAssignments(classId: Long) {
        assignmentRepository.getAssignmentsForClass(classId).onEach { resource1 ->
            when (resource1) {
                is Resource.Loading -> _uiState.update { it.copy(isLoadingAssignments = true) }
                is Resource.Success -> {

                    assignmentRepository.getPendingAssignmentsForClass(classId).onEach { resource2 ->
                        when (resource2) {
                            is Resource.Loading -> _uiState.update { it.copy(isLoadingPendingAssignments = true) }
                            is Resource.Success -> {
                                _uiState.update {
                                    it.copy(
                                        isLoadingAssignments = false,
                                        combinedAssignmentAndPendings = ((resource1.data
                                            ?: emptyList()).map { subject1 -> subject1.toCombinedModel() } + (resource2.data
                                            ?: emptyList()).map { subject1 -> subject1.toCombinedModel() }).sortedBy { subject1 -> subject1.subjectName },
                                        filteredCombinedAssignmentAndPendings = ((resource1.data
                                            ?: emptyList()).map { subject2 -> subject2.toCombinedModel() } + (resource2.data
                                            ?: emptyList()).map { subject2 -> subject2.toCombinedModel() }).sortedBy { subject2 -> subject2.subjectName },
                                        classTeachers = ((resource1.data
                                            ?: emptyList()).map { subject2 -> subject2.toCombinedModel() } + (resource2.data
                                            ?: emptyList()).map { subject2 -> subject2.toCombinedModel() }).filter { it.status == AssignmentStatus.ASSIGNED }
                                            .distinctBy { it.teacherName }.sortedBy { it.teacherName }
                                    )

                                }
                            }

                            is Resource.Error -> {
                                _uiState.update {
                                    it.copy(
                                        isLoadingPendingAssignments = false,
                                        errorMessage = resource2.message
                                    )
                                }
                            }
                        }
                    }.launchIn(viewModelScope)
                }

                is Resource.Error -> {
                    _uiState.update { it.copy(isLoadingAssignments = false, errorMessage = resource1.message) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun loadPendingTeachingAssignments(classId: Long) {
        assignmentRepository.getPendingAssignmentsForClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _uiState.update { it.copy(isLoadingPendingAssignments = true) }
                is Resource.Success -> {

                }

                is Resource.Error -> {
                    _uiState.update { it.copy(isLoadingPendingAssignments = false, errorMessage = resource.message) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun assignTeacherToSubject(subjectId: Long, teacherProfileId: Long, classId: Long) {
        val academicYearId = settingsStorage.getActiveAcademicYear()?.id
        if (academicYearId == null) {
            _uiState.update { it.copy(isAssigningTeachingAssignment = false) }
            return
        }
        val request = TeachingAssignmentRequest(classId, subjectId, teacherProfileId, academicYearId)
        assignmentRepository.createTeachingAssignment(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _uiState.update { it.copy(isAssigningTeachingAssignment = false) }
                is Resource.Success -> {
                    _uiState.update { it.copy(isAssigningTeachingAssignment = true) }
                    loadClassTeachingAssignments(classId)
                    loadPendingTeachingAssignments(classId)
                }

                is Resource.Error -> _uiState.update {
                    it.copy(
                        isLoadingAssignments = false,
                        errorMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deleteTeachingAssignment(assignmentId: Long, classId: Long) {
        assignmentRepository.deleteTeachingAssignment(assignmentId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _uiState.update { it.copy(isDeletingTeachingAssignment = true) }
                is Resource.Success -> {
                    _uiState.update { it.copy(isDeletingTeachingAssignment = false) }
                    loadClassTeachingAssignments(classId)
                    loadPendingTeachingAssignments(classId)
                }

                is Resource.Error -> _uiState.update {
                    it.copy(
                        isLoadingAssignments = false,
                        errorMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }


    val visibleNodes: State<List<VisibleNode>> = derivedStateOf {
        val builtNodes = buildVisibleNodes(uiState.value.nodes)
        builtNodes
    }

    private fun loadRoots() {
        schoolRepository.getSchoolSections().onEach { schoolSection ->
            when (schoolSection) {

                is Resource.Success -> {
                    val data = schoolSection.data
                    if (data != null) {
                        val newRootNodes = data.map { it.toSchoolTreeNode() }
                        val existingNodeKeys = uiState.value.nodes.map { "${it.id}-${it.type}" }.toSet()
                        val nodesToAdd =
                            newRootNodes.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                        if (nodesToAdd.isNotEmpty()) {
                            uiState.value.nodes.addAll(nodesToAdd)
                        }
                    } else {
                        println("SUCCESS: Received null data for school sections.")
                    }
                }

                is Resource.Error -> {
                    println("error ${schoolSection.message}")
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun onToggle(
        node: SchoolTreeNode
    ) {
        if (node.type == NodeType.CLASSROOM) {
            return
        }
        if (node.expanded) {
            collapse(node.originalId, node.type) // MODIFIED: Pass node.type
        } else {
            expand(node)
        }
    }

    private fun collapse(nodeId: Long, nodeType: NodeType) { // MODIFIED: Accept nodeType
        updateNode(nodeId, nodeType) { // MODIFIED: Pass nodeType
            it.copy(expanded = false)
        }
        println("Node with id $nodeId and type $nodeType collapsed.")
    }


    private fun expand(node: SchoolTreeNode) {
        viewModelScope.launch {
            updateNode(node.originalId, node.type) { // MODIFIED: Pass node.type
                it.copy(loading = true)
            }

            if (!node.childrenLoaded) {
                when (node.type) {
                    NodeType.CYCLE -> {
                        schoolRepository.getSectionBySchoolSectionAndSchool(node.originalId)
                            .onEach { section ->
                                when (section) {
                                    is Resource.Loading -> {
                                        println("LOADING children for ${node.title} (type: ${node.type})")
                                    }

                                    is Resource.Success -> {
                                        val newChildren =
                                            section.data?.map { it.toSchoolTreeNode() }
                                                ?: emptyList()
                                        val existingNodeKeys =
                                            uiState.value.nodes.map { "${it.id}-${it.type}" }.toSet()
                                        val childrenToAdd =
                                            newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                        if (childrenToAdd.isNotEmpty()) {
                                            uiState.value.nodes.addAll(childrenToAdd)
                                        }
                                        updateNode(node.originalId, node.type) {
                                            it.copy(
                                                expanded = true,
                                                loading = false,
                                                childrenLoaded = true,
                                            )
                                        }
                                    }

                                    is Resource.Error -> {
                                        updateNode(node.originalId, node.type) {
                                            it.copy(loading = false)
                                        }
                                        println("ERROR fetching children for ${node.title}. Message: ${section.message}. Set loading=false.")
                                    }
                                }
                            }.launchIn(viewModelScope)
                    }

                    NodeType.SECTION -> {
                        schoolRepository.getOfferedMajorsForSchoolAndSection(node.originalId)
                            .onEach { major ->
                                when (major) {
                                    is Resource.Loading -> {
                                        println("LOADING children for ${node.title} (type: ${node.type})")
                                    }

                                    is Resource.Success -> {
                                        val newChildren =
                                            major.data?.map { it.toSchoolTreeNode() } ?: emptyList()
                                        val existingNodeKeys =
                                            uiState.value.nodes.map { "${it.id}-${it.type}" }.toSet()
                                        val childrenToAdd =
                                            newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                        if (childrenToAdd.isNotEmpty()) {
                                            uiState.value.nodes.addAll(childrenToAdd)
                                        }
                                        updateNode(node.originalId, node.type) {
                                            it.copy(
                                                expanded = true,
                                                loading = false,
                                                childrenLoaded = true
                                            )
                                        }
                                    }

                                    is Resource.Error -> {
                                        updateNode(node.originalId, node.type) {
                                            it.copy(loading = false)
                                        }
                                    }
                                }
                            }.launchIn(viewModelScope)
                    }

                    NodeType.MAJOR -> {
                        schoolRepository.getGradeLevelsBySchoolAndByMajor(node.originalId)
                            .onEach { gradeLevel ->
                                when (gradeLevel) {
                                    is Resource.Loading -> {
                                        println("LOADING children for ${node.title} (type: ${node.type})")
                                    }

                                    is Resource.Success -> {
                                        val newChildren =
                                            gradeLevel.data?.map { it.toSchoolTreeNode() }
                                                ?: emptyList()
                                        val existingNodeKeys =
                                            uiState.value.nodes.map { "${it.id}-${it.type}" }.toSet()
                                        val childrenToAdd =
                                            newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                        if (childrenToAdd.isNotEmpty()) {
                                            uiState.value.nodes.addAll(childrenToAdd)
                                        }
                                        updateNode(node.originalId, node.type) {
                                            it.copy(
                                                expanded = true,
                                                loading = false,
                                                childrenLoaded = true
                                            )
                                        }
                                    }

                                    is Resource.Error -> {
                                        updateNode(node.originalId, node.type) {
                                            it.copy(loading = false)
                                        }
                                    }
                                }

                            }.launchIn(viewModelScope)
                    }

                    NodeType.GRADE_LEVEL -> {
                        schoolRepository.getClassesBySchoolAndGradeLevel(node.originalId)
                            .onEach { schoolClass ->
                                when (schoolClass) {
                                    is Resource.Loading -> {
                                        println("LOADING children for ${node.title} (type: ${node.type})")
                                    }

                                    is Resource.Success -> {
                                        val newChildren =
                                            schoolClass.data?.map { it.toSchoolTreeNode() }
                                                ?: emptyList()
                                        val existingNodeKeys =
                                            uiState.value.nodes.map { "${it.id}-${it.type}" }.toSet()
                                        val childrenToAdd =
                                            newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                        if (childrenToAdd.isNotEmpty()) {
                                            uiState.value.nodes.addAll(childrenToAdd)
                                        }
                                        updateNode(node.originalId, node.type) {
                                            it.copy(
                                                expanded = true,
                                                loading = false,
                                                childrenLoaded = true
                                            )
                                        }
                                    }

                                    is Resource.Error -> {
                                        updateNode(node.originalId, node.type) {
                                            it.copy(loading = false)
                                        }
                                        println("ERROR fetching children for ${node.title}. Message: ${schoolClass.message}. Set loading=false.")
                                    }
                                }

                            }.launchIn(viewModelScope)
                    }

                    else -> {
                        updateNode(node.originalId, node.type) {
                            it.copy(
                                expanded = true,
                                loading = false,
                                childrenLoaded = true
                            )
                        }
                    }
                }
            } else {
                updateNode(node.originalId, node.type) {
                    it.copy(expanded = true, loading = false)
                }
            }
        }
    }

    private fun updateNode(
        nodeId: Long,
        nodeType: NodeType,
        transform: (SchoolTreeNode) -> SchoolTreeNode
    ) {
        val index = uiState.value.nodes.indexOfFirst { it.originalId == nodeId && it.type == nodeType }
        if (index == -1) {
            return
        }
        val oldNode = uiState.value.nodes[index]
        val newNode = transform(oldNode)
        uiState.value.nodes[index] = newNode
    }

    fun onAction(
        node: SchoolTreeNode,
        action: NodeAction
    ) {

        when (action) {
            NodeAction.ADD_CLASS -> {
                // Creation is handled by UpdateSchoolClassDialog -> createClassFromTemplate
            }

            NodeAction.DELETE_CLASS -> {
                if (node.type == NodeType.CLASSROOM) {
                    deleteClass(node.originalId)
                }
            }

            NodeAction.INFO_CYCLE,
            NodeAction.INFO_SECTION,
            NodeAction.INFO_MAJOR,
            NodeAction.INFO_GRADE_LEVEL,
            NodeAction.INFO_CLASS,
            NodeAction.EDIT_CLASS -> {
                _uiState.update { it.copy(infoNode = node) }
            }
        }
    }

    fun dismissInfoNode() {
        _uiState.update { it.copy(infoNode = null) }
    }

    fun createClassFromTemplate(request: CreateClassFromTemplateRequest) {
        _uiState.update { it.copy(isLoading = true) }
        schoolRepository.createClass(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    refreshGradeLevelChildren(request.templateGradeLevelId)
                }

                is Resource.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = resource.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deleteClass(classId: Long) {
        _uiState.update { it.copy(isDeleting = true) }
        schoolRepository.deleteClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    _uiState.update { it.copy(isDeleting = false) }
                    _uiState.value.nodes.removeAll { it.originalId == classId && it.type == NodeType.CLASSROOM }
                }

                is Resource.Error -> _uiState.update {
                    it.copy(isDeleting = false, errorMessage = resource.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun refreshGradeLevelChildren(gradeLevelId: Long) {
        schoolRepository.getClassesBySchoolAndGradeLevel(gradeLevelId).onEach { schoolClass ->
            if (schoolClass is Resource.Success) {
                val newChildren = schoolClass.data?.map { it.toSchoolTreeNode() } ?: emptyList()
                val existingNodeKeys = uiState.value.nodes.map { "${it.id}-${it.type}" }.toSet()
                val nodesToAdd = newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                if (nodesToAdd.isNotEmpty()) {
                    uiState.value.nodes.addAll(nodesToAdd)
                }
            }
        }.launchIn(viewModelScope)
    }
}

fun SchoolSectionDTO.toSchoolTreeNode() = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.CYCLE,
    parentId = null,
    parentTitle = null,
    expanded = false,
    loading = false, // Explicitly set
    childrenLoaded = false // Explicitly set
)

fun SectionDTO.toSchoolTreeNode() = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.SECTION,
    parentId = "${NodeType.CYCLE}-${this.schoolSectionId}",
    parentTitle = this.schoolSectionName
)

fun MajorDto.toSchoolTreeNode() = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.MAJOR,
    parentId = "${NodeType.SECTION}-${this.sectionId}",
    parentTitle = this.sectionName
)

fun GradeLevelDTO.toSchoolTreeNode() = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.GRADE_LEVEL,
    parentId = "${NodeType.MAJOR}-${this.majorId}",
    parentTitle = "${this.majorName} - ${this.sectionName}"
)

fun SchoolClassDTO.toSchoolTreeNode(): SchoolTreeNode = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.CLASSROOM,
    parentId = "${NodeType.GRADE_LEVEL}-${this.gradeLevelId}",
    parentTitle = "${this.gradeLevelLabel} - ${this.majorName} - ${this.sectionName}"
)

fun TeachingAssignmentDTO.toCombinedModel() = CombinedAssignmentModel(
    id = this.id, // The unique assignment ID
    subjectId = this.subjectId,
    subjectName = this.subjectName,
    subjectCode = this.subjectCode,
    status = AssignmentStatus.ASSIGNED,
    teacherId = this.teacherId,
    teacherName = this.teacherName,
    classId = this.classId,
    className = this.className,
    academicYearId = this.academicYearId
)

fun TemplateSubjectDTO.toCombinedModel() = CombinedAssignmentModel(
    id = this.id, // The unique template subject ID
    subjectId = this.id,
    subjectName = this.name,
    subjectCode = this.code,
    status = AssignmentStatus.PENDING,
    domain = this.domain,
    subDomain = this.subDomain
)