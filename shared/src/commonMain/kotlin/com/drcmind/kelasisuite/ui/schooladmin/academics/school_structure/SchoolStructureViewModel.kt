package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
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

    fun loadEnrolledStudents() {
        val schoolId = settingsStorage.getUserInfo().schoolId
        if (schoolId == null) {
            _enrolledStudents.value = emptyList()
            return
        }
        studentsRepository.getEnrolledStudents(schoolId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isLoadingEnrolledStudents.value = true
                is Resource.Success -> {
                    _enrolledStudents.value = resource.data ?: emptyList()
                    _isLoadingEnrolledStudents.value = false
                }

                is Resource.Error -> _isLoadingEnrolledStudents.value = false
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

    fun enrollStudent(studentId: Long, classId: Long, academicYearId: Long) {
        val request = EnrollmentRequest(studentId, classId, academicYearId)
        studentsRepository.enrollStudent(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isLoadingEnrollment.value = true
                is Resource.Success -> {
                    _isLoadingEnrollment.value = false
                    loadStudents() // Refresh to update status if needed
                    loadEnrolledStudents()
                }

                is Resource.Error -> _isLoadingEnrollment.value = false
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    val visibleNodes: State<List<VisibleNode>> = derivedStateOf {
        println("derivedStateOf: Re-evaluating visibleNodes. Current 'nodes' size: ${nodes.size}. Content: $nodes")
        val builtNodes = buildVisibleNodes(nodes)
        println("buildVisibleNodes returned ${builtNodes.size} nodes.")
        builtNodes
    }

    private fun loadRoots() {
        println("loadRoots: Initial nodes state before loading roots: $nodes")
        schoolRepository.getSchoolSections().onEach { schoolSection ->
            when (schoolSection) {

                is Resource.Success -> {
                    val data = schoolSection.data
                    if (data != null) {
                        println("SUCCESS: Received ${data.size} school sections. Data: $data")
                        val newRootNodes = data.map { it.toSchoolTreeNode() }
                        val existingNodeKeys = nodes.map { "${it.id}-${it.type}" }.toSet()
                        val nodesToAdd =
                            newRootNodes.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                        if (nodesToAdd.isNotEmpty()) {
                            nodes.addAll(nodesToAdd)
                            println("Nodes list size after addAll: ${nodes.size}")
                            println("Nodes added by loadRoots: $nodesToAdd")
                            nodesToAdd.forEach {
                                println("loadRoots: Added node ${it.title} (id: ${it.id}) childrenLoaded: ${it.childrenLoaded}")
                            }
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
        println("onToggle: Initial node state received: $node")
        println("onToggle called for: ${node.title}, id: ${node.id}, expanded: ${node.expanded}")
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
            println("Expanding node: ${node.title}, id: ${node.id}, current expanded: ${node.expanded}, loading: ${node.loading}, childrenLoaded: ${node.childrenLoaded}")
            updateNode(node.originalId, node.type) { // MODIFIED: Pass node.type
                it.copy(loading = true)
            }
            println("Node ${node.title} (id: ${node.id}) set to loading=true")

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
                                        println("SUCCESS fetching children for ${node.title} (type: ${node.type}). Received ${section.data?.size ?: 0} items.")
                                        val newChildren =
                                            section.data?.map { it.toSchoolTreeNode() }
                                                ?: emptyList()
                                        println("expand (CYCLE): newChildren for ${node.title}: $newChildren")
                                        // MODIFIED: Use composite key for existing nodes
                                        val existingNodeKeys =
                                            nodes.map { "${it.id}-${it.type}" }.toSet()
                                        println("expand (CYCLE): existingNodeKeys: $existingNodeKeys") // Log the new set
                                        val childrenToAdd =
                                            newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                        println("expand (CYCLE): childrenToAdd for ${node.title}: $childrenToAdd")
                                        if (childrenToAdd.isNotEmpty()) {
                                            nodes.addAll(childrenToAdd)
                                            println("Children added for ${node.title}. Total nodes size: ${nodes.size}")
                                        }
                                        updateNode(node.originalId, node.type) {
                                            it.copy(
                                                expanded = true,
                                                loading = false,
                                                childrenLoaded = true,
                                            )
                                        }
                                        println("Node ${node.title} (id: ${node.id}) set to expanded=true, loading=false, childrenLoaded=true")
                                    }

                                    is Resource.Error -> {
                                        updateNode(node.originalId, node.type) {
                                            it.copy(loading = false)
                                        }
                                        println("ERROR fetching children for ${node.title}. Message: ${section.message}. Set loading=false.")
                                    }

                                    else -> {}
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
                                        println("SUCCESS fetching children for ${node.title} (type: ${node.type}). Received ${major.data?.size ?: 0} items.")
                                        val newChildren =
                                            major.data?.map { it.toSchoolTreeNode() } ?: emptyList()
                                        println("expand (SECTION): newChildren for ${node.title}: $newChildren")
                                        // MODIFIED: Use composite key for existing nodes
                                        val existingNodeKeys =
                                            nodes.map { "${it.id}-${it.type}" }.toSet()
                                        println("expand (SECTION): existingNodeKeys: $existingNodeKeys") // Log the new set
                                        val childrenToAdd =
                                            newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                        println("expand (SECTION): childrenToAdd for ${node.title}: $childrenToAdd")
                                        if (childrenToAdd.isNotEmpty()) {
                                            nodes.addAll(childrenToAdd)
                                            println("Children added for ${node.title}. Total nodes size: ${nodes.size}")
                                        }
                                        updateNode(node.originalId, node.type) {
                                            it.copy(
                                                expanded = true,
                                                loading = false,
                                                childrenLoaded = true
                                            )
                                        }
                                        println("Node ${node.title} (id: ${node.id}) set to expanded=true, loading=false, childrenLoaded=true")
                                    }

                                    is Resource.Error -> {
                                        updateNode(node.originalId, node.type) {
                                            it.copy(loading = false)
                                        }
                                        println("ERROR fetching children for ${node.title}. Message: ${major.message}. Set loading=false.")
                                    }

                                    else -> {}
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
                                        println("SUCCESS fetching children for ${node.title} (type: ${node.type}). Received ${gradeLevel.data?.size ?: 0} items.")
                                        val newChildren =
                                            gradeLevel.data?.map { it.toSchoolTreeNode() }
                                                ?: emptyList()
                                        println("expand (MAJOR): newChildren for ${node.title}: $newChildren")
                                        // MODIFIED: Use composite key for existing nodes
                                        val existingNodeKeys =
                                            nodes.map { "${it.id}-${it.type}" }.toSet()
                                        println("expand (MAJOR): existingNodeKeys: $existingNodeKeys") // Log the new set
                                        val childrenToAdd =
                                            newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                        println("expand (MAJOR): childrenToAdd for ${node.title}: $childrenToAdd")
                                        if (childrenToAdd.isNotEmpty()) {
                                            nodes.addAll(childrenToAdd)
                                            println("Children added for ${node.title}. Total nodes size: ${nodes.size}")
                                        }
                                        updateNode(node.originalId, node.type) {
                                            it.copy(
                                                expanded = true,
                                                loading = false,
                                                childrenLoaded = true
                                            )
                                        }
                                        println("Node ${node.title} (id: ${node.id}) set to expanded=true, loading=false, childrenLoaded=true")
                                    }

                                    is Resource.Error -> {
                                        updateNode(node.originalId, node.type) {
                                            it.copy(loading = false)
                                        }
                                        println("ERROR fetching children for ${node.title}. Message: ${gradeLevel.message}. Set loading=false.")
                                    }

                                    else -> {}
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
                                        println("SUCCESS fetching children for ${node.title} (type: ${node.type}). Received ${schoolClass.data?.size ?: 0} items.")
                                        val newChildren =
                                            schoolClass.data?.map { it.toSchoolTreeNode() }
                                                ?: emptyList()
                                        println("expand (GRADE_LEVEL): newChildren for ${node.title}: $newChildren")
                                        // MODIFIED: Use composite key for existing nodes
                                        val existingNodeKeys =
                                            nodes.map { "${it.id}-${it.type}" }.toSet()
                                        println("expand (GRADE_LEVEL): existingNodeKeys: $existingNodeKeys") // Log the new set
                                        val childrenToAdd =
                                            newChildren.filter { "${it.id}-${it.type}" !in existingNodeKeys }
                                        println("expand (GRADE_LEVEL): childrenToAdd for ${node.title}: $childrenToAdd")
                                        if (childrenToAdd.isNotEmpty()) {
                                            nodes.addAll(childrenToAdd)
                                            println("Children added for ${node.title}. Total nodes size: ${nodes.size}")
                                        }
                                        updateNode(node.originalId, node.type) {
                                            it.copy(
                                                expanded = true,
                                                loading = false,
                                                childrenLoaded = true
                                            )
                                        }
                                        println("Node ${node.title} (id: ${node.id}) set to expanded=true, loading=false, childrenLoaded=true")
                                    }

                                    is Resource.Error -> {
                                        updateNode(node.originalId, node.type) {
                                            it.copy(loading = false)
                                        }
                                        println("ERROR fetching children for ${node.title}. Message: ${schoolClass.message}. Set loading=false.")
                                    }

                                    else -> {}
                                }

                            }.launchIn(viewModelScope)
                    }

                    else -> {
                        // If no children are expected or loaded, set loading to false and expanded to true
                        updateNode(node.originalId, node.type) {
                            it.copy(
                                expanded = true,
                                loading = false,
                                childrenLoaded = true
                            )
                        }
                        println("Node ${node.title} (id: ${node.id}) type is ${node.type}, no children to load. Set expanded=true, loading=false, childrenLoaded=true")
                    }
                }
            } else {
                // If children are already loaded, just expand the node and set loading to false
                updateNode(node.originalId, node.type) {
                    it.copy(expanded = true, loading = false)
                }
                println("Node ${node.title} (id: ${node.id}) children already loaded. Set expanded=true, loading=false.")
            }
        }
    }

    private fun updateNode(
        nodeId: Long,
        nodeType: NodeType, // MODIFIED: Accept nodeType
        transform: (SchoolTreeNode) -> SchoolTreeNode
    ) {
        // MODIFIED: Find node using both ID and Type
        val index = nodes.indexOfFirst { it.originalId == nodeId && it.type == nodeType }
        if (index == -1) {
            println("WARN: updateNode failed. Node with id $nodeId and type $nodeType not found.")
            return
        }
        val oldNode = nodes[index]
        val newNode = transform(oldNode)
        println("updateNode: Applying transform for node $nodeId (type: $nodeType). Old: $oldNode, New: $newNode")
        nodes[index] = newNode
        println("Node with id $nodeId (type: $nodeType) updated. Old: $oldNode, New: $newNode")
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
    val errorMessage: String? = null,
    val totalCycles: Int = 2,
    val totalSections: Int = 2,
    val totalGradeLevels: Int = 8,
    val totalClasses: Int = 12,
)

fun SchoolSectionDTO.toSchoolTreeNode() = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.CYCLE,
    parentId = null,
    expanded = false,
    loading = false, // Explicitly set
    childrenLoaded = false // Explicitly set
)

fun SectionDTO.toSchoolTreeNode() = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.SECTION,
    parentId = "${NodeType.CYCLE}-${this.schoolSectionId}",
)

fun MajorDto.toSchoolTreeNode() = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.MAJOR,
    parentId = "${NodeType.SECTION}-${this.sectionId}",
)

fun GradeLevelDTO.toSchoolTreeNode() = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.GRADE_LEVEL,
    parentId = "${NodeType.MAJOR}-${this.majorId}",
)

fun SchoolClassDTO.toSchoolTreeNode(): SchoolTreeNode = SchoolTreeNode(
    originalId = this.id,
    title = this.name,
    type = NodeType.CLASSROOM,
    parentId = "${NodeType.GRADE_LEVEL}-${this.gradeLevelId}"
)
