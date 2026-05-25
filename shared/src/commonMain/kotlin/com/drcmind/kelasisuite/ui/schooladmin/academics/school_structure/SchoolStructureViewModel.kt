package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.AcademicYearDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeLevelDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.HomeroomAssignmentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.HomeroomAssignmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.MajorDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepository
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

    // --- États généraux ---
    val state: StateFlow<ClassesState>
        field = MutableStateFlow(ClassesState())

    val classes: StateFlow<List<SchoolClassDTO>>
        field = MutableStateFlow(emptyList())

    val academicYears: StateFlow<List<AcademicYearDTO>>
        field = MutableStateFlow(emptyList())

    val students: StateFlow<List<StudentDTO>>
        field = MutableStateFlow(emptyList())

    val teachers: StateFlow<List<TeacherProfileDTO>>
        field = MutableStateFlow(emptyList())


    // --- Titulaire de classe (Homeroom) ---
    val homeroomAssignment: StateFlow<HomeroomAssignmentDTO?>
        field = MutableStateFlow(null)

    val isLoadingHomeroomTeacher: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val isAssigningHomeroomTeacher: StateFlow<Boolean>
        field = MutableStateFlow(false)


    // --- Étudiants de la classe ---
    val clasStudents: StateFlow<List<StudentDTO>>
        field = MutableStateFlow(emptyList())

    val isLoadingClassStudents: StateFlow<Boolean>
        field = MutableStateFlow(false)


    // --- Assignations et matières (Séparées) ---
    val assignments: StateFlow<List<TeachingAssignmentDTO>>
        field = MutableStateFlow(emptyList())

    val pendingAssignmentsSubjects: StateFlow<List<TemplateSubjectDTO>>
        field = MutableStateFlow(emptyList())

    val isLoadingAssignments: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val isLoadingPendingAssignments: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val isAssigningTeachingAssignment: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val isDeletingTeachingAssignment: StateFlow<Boolean>
        field = MutableStateFlow(false)


    // --- Inscriptions (Enrollments) ---
    val enrolledStudents: StateFlow<List<StudentDTO>>
        field = MutableStateFlow(emptyList())

    val isLoadingEnrolledStudents: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val isLoadingEnrollment: StateFlow<Boolean>
        field = MutableStateFlow(false)


    // --- Le Backing Field Combiné ---
    val combinedAssignmentAndPendings: StateFlow<List<CombinedAssignmentModel>>
        field = MutableStateFlow(
            assignments.value.map { it.toCombinedModel() } +
                    pendingAssignmentsSubjects.value.map { it.toCombinedModel() }
        )


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
                classes.value = resource.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }

    private fun loadAcademicYears() {
        schoolRepository.getAcademicYears().onEach { resource ->
            if (resource is Resource.Success) {
                academicYears.value = resource.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }

    fun loadClassStudents(classId: Long) {
        studentsRepository.getStudentsForClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> isLoadingClassStudents.value = true
                is Resource.Success -> {
                    clasStudents.value = resource.data ?: emptyList()
                    isLoadingClassStudents.value = false
                }

                is Resource.Error -> isLoadingClassStudents.value = false
            }
        }.launchIn(viewModelScope)
    }

    private fun loadStudents() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        studentsRepository.getStudents(schoolId).onEach { resource ->
            if (resource is Resource.Success) {
                students.value = resource.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }


    fun loadTeachers() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        teachersRepository.getTeachers(schoolId).onEach { resource ->
            if (resource is Resource.Success) {
                teachers.value = resource.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }

    fun loadHomeroomTeacher(classId: Long) {
        isLoadingHomeroomTeacher.value = true
        teachersRepository.getHomeroomTeacherForClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> isLoadingHomeroomTeacher.value = true
                is Resource.Success -> {
                    homeroomAssignment.value = resource.data
                    isLoadingHomeroomTeacher.value = false
                }

                is Resource.Error -> {
                    homeroomAssignment.value = null
                    isLoadingHomeroomTeacher.value = false
                }
            }
        }.launchIn(viewModelScope)
    }

    fun assignHomeroomTeacher(teacherProfileId: Long, classId: Long) {
        isAssigningHomeroomTeacher.value = true
        val request = HomeroomAssignmentRequest(teacherProfileId, classId)
        teachersRepository.assignHomeroomTeacher(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> isAssigningHomeroomTeacher.value = true
                is Resource.Success -> {
                    homeroomAssignment.value = resource.data
                    isAssigningHomeroomTeacher.value = false
                    loadHomeroomTeacher(classId)
                }

                is Resource.Error -> isAssigningHomeroomTeacher.value = false
            }
        }.launchIn(viewModelScope)
    }

    fun loadCombinedAssignments() {
        combinedAssignmentAndPendings.value =
            assignments.value.map { it.toCombinedModel() } +
                    pendingAssignmentsSubjects.value.map { it.toCombinedModel() }
    }

    fun loadClassTeachingAssignments(classId: Long) {
        isLoadingAssignments.value = true
        assignmentRepository.getAssignmentsForClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> isLoadingAssignments.value = true
                is Resource.Success -> {
                    assignments.value = resource.data ?: emptyList()
                    isLoadingAssignments.value = false
                }

                is Resource.Error -> {
                    assignments.value = emptyList()
                    isLoadingAssignments.value = false
                }
            }
        }.launchIn(viewModelScope)
    }

    fun loadPendingTeachingAssignments(classId: Long) {
        isLoadingPendingAssignments.value = true
        assignmentRepository.getPendingAssignmentsForClass(classId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> isLoadingPendingAssignments.value = true
                is Resource.Success -> {
                    pendingAssignmentsSubjects.value = resource.data ?: emptyList()
                    isLoadingPendingAssignments.value = false
                }

                is Resource.Error -> {
                    pendingAssignmentsSubjects.value = emptyList()
                    isLoadingPendingAssignments.value = false
                }
            }
        }.launchIn(viewModelScope)
    }

    fun assignTeacherToSubject(subjectId: Long, teacherProfileId: Long, classId: Long) {
        isAssigningTeachingAssignment.value = true
        val academicYearId = settingsStorage.getActiveAcademicYear()?.id
        if (academicYearId == null) {
            isAssigningTeachingAssignment.value = false
            return
        }
        val request = TeachingAssignmentRequest(classId, subjectId, teacherProfileId, academicYearId)
        assignmentRepository.createTeachingAssignment(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> isAssigningTeachingAssignment.value = true
                is Resource.Success -> {
                    isAssigningTeachingAssignment.value = false
                    loadClassTeachingAssignments(classId)
                    loadPendingTeachingAssignments(classId)
                }

                is Resource.Error -> isAssigningTeachingAssignment.value = false
            }
        }.launchIn(viewModelScope)
    }

    fun deleteTeachingAssignment(assignmentId: Long, classId: Long) {
        isDeletingTeachingAssignment.value = true
        assignmentRepository.deleteTeachingAssignment(assignmentId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> isDeletingTeachingAssignment.value = true
                is Resource.Success -> {
                    isDeletingTeachingAssignment.value = false
                    loadClassTeachingAssignments(classId)
                    loadPendingTeachingAssignments(classId)
                }

                is Resource.Error -> isDeletingTeachingAssignment.value = false
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