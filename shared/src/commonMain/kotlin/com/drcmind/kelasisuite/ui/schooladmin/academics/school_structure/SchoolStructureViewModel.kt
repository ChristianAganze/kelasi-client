package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.AcademicYearDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeLevelDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.MajorDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.data.repository.assignments.AssignmentRepository
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.domain.dto.*
import com.drcmind.kelasisuite.domain.model.SchoolTreeNode
import com.drcmind.kelasisuite.domain.util.NodeType
import com.drcmind.kelasisuite.domain.util.Resource
import com.drcmind.kelasisuite.domain.util.buildVisibleNodes
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SchoolStructureViewModel(
    private val schoolRepository: SchoolRepository,
    private val studentsRepository: StudentsRepository,
    private val settingsStorage: SettingsStorage,
    private val teachersRepository: TeachersRepository,
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ClassesState())
    val state: StateFlow<ClassesState> = _state.asStateFlow()

    private val _classes = MutableStateFlow<List<SchoolClassDTO>>(emptyList())
    val classes: StateFlow<List<SchoolClassDTO>> = _classes.asStateFlow()

    private val _academicYears = MutableStateFlow<List<AcademicYearDTO>>(emptyList())
    val academicYears: StateFlow<List<AcademicYearDTO>> = _academicYears.asStateFlow()

    private val _students = MutableStateFlow<List<StudentDTO>>(emptyList())
    val students: StateFlow<List<StudentDTO>> = _students.asStateFlow()

    private val _teachers = MutableStateFlow<List<TeacherProfileDTO>>(emptyList())
    val teachers: StateFlow<List<TeacherProfileDTO>> = _teachers.asStateFlow()

    private val _homeroomAssignment = MutableStateFlow<HomeroomAssignmentDTO?>(null)
    val homeroomAssignment: StateFlow<HomeroomAssignmentDTO?> = _homeroomAssignment.asStateFlow()

    private val _isLoadingHomeroomTeacher = MutableStateFlow(false)
    val isLoadingHomeroomTeacher = _isLoadingHomeroomTeacher.asStateFlow()

    private val _isAssigningHomeroomTeacher = MutableStateFlow(false)
    val isAssigningHomeroomTeacher = _isAssigningHomeroomTeacher.asStateFlow()

    private val _clasStudents = MutableStateFlow<List<StudentDTO>>(emptyList())
    val clasStudents: StateFlow<List<StudentDTO>> = _clasStudents.asStateFlow()

    private val _isLoadingClassStudents = MutableStateFlow(false)
    val isLoadingClassStudents = _isLoadingClassStudents.asStateFlow()

    private val _assignments = MutableStateFlow<List<TeachingAssignmentDTO>>(emptyList())
    val assignments: StateFlow<List<TeachingAssignmentDTO>> = _assignments.asStateFlow()

    private val _pendingAssignmentsSubjects = MutableStateFlow<List<TemplateSubjectDTO>>(emptyList())
    val pendingAssignmentsSubjects: StateFlow<List<TemplateSubjectDTO>> = _pendingAssignmentsSubjects.asStateFlow()

    private val _isLoadingAssignments = MutableStateFlow(false)
    val isLoadingAssignments = _isLoadingAssignments.asStateFlow()

    private val _isLoadingPendingAssignments = MutableStateFlow(false)
    val isLoadingPendingAssignments = _isLoadingPendingAssignments.asStateFlow()

    private val _isAssigningTeachingAssignment = MutableStateFlow(false)
    val isAssigningTeachingAssignment = _isAssigningTeachingAssignment.asStateFlow()

    private val _isDeletingTeachingAssignment = MutableStateFlow(false)
    val isDeletingTeachingAssignment = _isDeletingTeachingAssignment.asStateFlow()

    private val _enrolledStudents = MutableStateFlow<List<StudentDTO>>(emptyList())
    val enrolledStudents: StateFlow<List<StudentDTO>> = _enrolledStudents.asStateFlow()

    private val _isLoadingEnrolledStudents = MutableStateFlow(false)
    val isLoadingEnrolledStudents = _isLoadingEnrolledStudents.asStateFlow()

    private val _isLoadingEnrollment = MutableStateFlow(false)
    val isLoadingEnrollment = _isLoadingEnrollment.asStateFlow()

    var nodes = mutableStateListOf<SchoolTreeNode>()

    init {
        loadRoots()
        loadClasses()
        loadAcademicYears()
        loadStudents()
        loadTeachers()
    }

    private fun loadClasses() {
        schoolRepository.getClassesForSchool().onEach { resource ->
            if (resource is Resource.Success) {
                _classes.value = resource.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }

    private fun loadAcademicYears() {
        schoolRepository.getAcademicYears().onEach { resource ->
            if (resource is Resource.Success) {
                _academicYears.value = resource.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }

    fun loadClassStudents(classId: Long) {
        studentsRepository.getStudentsForClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isLoadingClassStudents.value = true
                is Resource.Success -> {
                    _clasStudents.value = resource.data ?: emptyList()
                    _isLoadingClassStudents.value = false
                }

                is Resource.Error -> _isLoadingClassStudents.value = false
            }
        }.launchIn(viewModelScope)
    }

    private fun loadStudents() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        studentsRepository.getStudents(schoolId).onEach { resource ->
            if (resource is Resource.Success) {
                _students.value = resource.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }



    fun loadTeachers() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        teachersRepository.getTeachers(schoolId).onEach { resource ->
            if (resource is Resource.Success) {
                _teachers.value = resource.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }

    fun loadHomeroomTeacher(classId: Long) {
        _isLoadingHomeroomTeacher.value = true
        teachersRepository.getHomeroomTeacherForClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isLoadingHomeroomTeacher.value = true
                is Resource.Success -> {
                    _homeroomAssignment.value = resource.data
                    _isLoadingHomeroomTeacher.value = false
                }

                is Resource.Error -> {
                    _homeroomAssignment.value = null
                    _isLoadingHomeroomTeacher.value = false
                }
            }
        }.launchIn(viewModelScope)
    }

    fun assignHomeroomTeacher(teacherProfileId: Long, classId: Long) {
        _isAssigningHomeroomTeacher.value = true
        val request = HomeroomAssignmentRequest(teacherProfileId, classId)
        teachersRepository.assignHomeroomTeacher( request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isAssigningHomeroomTeacher.value = true
                is Resource.Success -> {
                    _homeroomAssignment.value = resource.data
                    _isAssigningHomeroomTeacher.value = false
                    loadHomeroomTeacher(classId)
                }

                is Resource.Error -> _isAssigningHomeroomTeacher.value = false
            }
        }.launchIn(viewModelScope)
    }

    fun loadClassTeachingAssignments(classId: Long) {
        _isLoadingAssignments.value = true
        assignmentRepository.getAssignmentsForClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isLoadingAssignments.value = true
                is Resource.Success -> {
                    _assignments.value = resource.data ?: emptyList()
                    _isLoadingAssignments.value = false
                }

                is Resource.Error -> {
                    _assignments.value = emptyList()
                    _isLoadingAssignments.value = false
                }
            }
        }.launchIn(viewModelScope)
    }

    fun loadPendingTeachingAssignments(classId: Long) {
        _isLoadingPendingAssignments.value = true
        assignmentRepository.getPendingAssignmentsForClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isLoadingPendingAssignments.value = true
                is Resource.Success -> {
                    _pendingAssignmentsSubjects.value = resource.data ?: emptyList()
                    _isLoadingPendingAssignments.value = false
                }

                is Resource.Error -> {
                    _pendingAssignmentsSubjects.value = emptyList()
                    _isLoadingPendingAssignments.value = false
                }
            }
        }.launchIn(viewModelScope)
    }

    fun assignTeacherToSubject(subjectId: Long, teacherProfileId: Long, classId: Long) {
        _isAssigningTeachingAssignment.value = true
        val academicYearId = settingsStorage.getActiveAcademicYear()?.id
        if (academicYearId == null) {
            _isAssigningTeachingAssignment.value = false
            return
        }
        val request = TeachingAssignmentRequest(classId, subjectId, teacherProfileId, academicYearId)
        assignmentRepository.createTeachingAssignment(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isAssigningTeachingAssignment.value = true
                is Resource.Success -> {
                    _isAssigningTeachingAssignment.value = false
                    loadClassTeachingAssignments(classId)
                    loadPendingTeachingAssignments(classId)
                }

                is Resource.Error -> _isAssigningTeachingAssignment.value = false
            }
        }.launchIn(viewModelScope)
    }

    fun deleteTeachingAssignment(assignmentId: Long, classId: Long) {
        _isDeletingTeachingAssignment.value = true
        assignmentRepository.deleteTeachingAssignment(assignmentId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isDeletingTeachingAssignment.value = true
                is Resource.Success -> {
                    _isDeletingTeachingAssignment.value = false
                    loadClassTeachingAssignments(classId)
                    loadPendingTeachingAssignments(classId)
                }

                is Resource.Error -> _isDeletingTeachingAssignment.value = false
            }
        }.launchIn(viewModelScope)
    }



    val visibleNodes: State<List<VisibleNode>> = derivedStateOf {
        val builtNodes = buildVisibleNodes(nodes)
        builtNodes
    }

    private fun loadRoots() {
        schoolRepository.getSchoolSections().onEach { schoolSection ->
            when (schoolSection) {

                is Resource.Success -> {
                    val data = schoolSection.data
                    if (data != null) {
                        val newRootNodes = data.map { it.toSchoolTreeNode() }
                        val existingNodeKeys = nodes.map { "${it.id}-${it.type}" }.toSet()
                        val nodesToAdd =
                            newRootNodes.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                        if (nodesToAdd.isNotEmpty()) {
                            nodes.addAll(nodesToAdd)
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
                                            nodes.map { "${it.id}-${it.type}" }.toSet()
                                        val childrenToAdd =
                                            newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                        if (childrenToAdd.isNotEmpty()) {
                                            nodes.addAll(childrenToAdd)
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
                                            nodes.map { "${it.id}-${it.type}" }.toSet()
                                        val childrenToAdd =
                                            newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                        if (childrenToAdd.isNotEmpty()) {
                                            nodes.addAll(childrenToAdd)
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
                                            nodes.map { "${it.id}-${it.type}" }.toSet()
                                        val childrenToAdd =
                                            newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                        if (childrenToAdd.isNotEmpty()) {
                                            nodes.addAll(childrenToAdd)
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
                                            nodes.map { "${it.id}-${it.type}" }.toSet()
                                        val childrenToAdd =
                                            newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                        if (childrenToAdd.isNotEmpty()) {
                                            nodes.addAll(childrenToAdd)
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
        val index = nodes.indexOfFirst { it.originalId == nodeId && it.type == nodeType }
        if (index == -1) {
            return
        }
        val oldNode = nodes[index]
        val newNode = transform(oldNode)
        nodes[index] = newNode
    }

    fun onAction(
        node: SchoolTreeNode,
        action: NodeAction
    ) {

        when (action) {
            NodeAction.ADD_CLASS -> {
                println("Add class to ${node.title}")
            }

            NodeAction.DELETE_CLASS -> {
                println("Delete class from ${node.title}")
            }

            NodeAction.INFO_CYCLE -> TODO()
            NodeAction.INFO_SECTION -> TODO()
            NodeAction.INFO_MAJOR -> TODO()
            NodeAction.INFO_GRADE_LEVEL -> TODO()
            NodeAction.INFO_CLASS -> TODO()
            NodeAction.EDIT_CLASS -> TODO()
        }
    }
}

data class ClassesState(
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null
)

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
