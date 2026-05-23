package com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.enrollment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.data.datasource.remote.dto.AcademicYearDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.EnrollmentRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.GradeLevelDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.MajorDto
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolClassDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.SectionDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentDTO
import com.drcmind.kelasisuite.data.repository.enrollment.EnrollmentRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class EnrollmentViewModel(
    private val studentsRepository: StudentsRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val schoolRepository: SchoolRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        EnrollmentUiState()
    )

    val uiState = _uiState.asStateFlow()

    private var allStudents: List<StudentDTO> = emptyList()
    private var allEnrollments: List<EnrollmentDto> = emptyList()

    init {
        loadStudents()
        loadEnrollments()
        loadSchoolSections()
        loadAcademicYears()
    }

    fun loadEnrollments() {
        enrollmentRepository.getEnrolledStudents().onEach { resource ->
            when (resource) {
                is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }

                is Resource.Success -> {
                    allEnrollments = resource.data ?: emptyList()
                    filterEnrollments()
                }

                is Resource.Error -> _uiState.update { it.copy(isLoading = false) }
            }
        }.launchIn(viewModelScope)
    }
    private fun filterEnrollments() {
        val query = _uiState.value.searchQueryEnrollment.lowercase()
        val filtered = allEnrollments.filter { enrolledStudent ->
            enrolledStudent.student.firstName.lowercase().contains(query) ||
                    enrolledStudent.student.lastName.lowercase().contains(query)
        }
        _uiState.update {
            it.copy(
                isLoading = false,
                enrollments = filtered,
            )
        }
    }

    private fun loadAcademicYears() {
        val cachedAcademicYearId =
            settingsStorage
                .getActiveAcademicYear()
                ?.id

        schoolRepository
            .getAcademicYears()
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                placement = it.placement.copy(isLoadingAcademicYears = true)
                            )
                        }
                    }
                    is Resource.Success -> {
                        val academicYears =
                            resource.data ?: emptyList()
                        val defaultAcademicYear =
                            academicYears.find {
                                it.id == cachedAcademicYearId
                            }?: academicYears.find {
                                    it.isActive
                                }
                        _uiState.update {

                            it.copy(
                                placement = it.placement.copy(
                                    academicYears = academicYears,
                                    selectedAcademicYear = defaultAcademicYear,
                                    isLoadingAcademicYears = false
                                )
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                placement = it.placement.copy(
                                    isLoadingAcademicYears = false
                                ),
                                error = resource.message
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun selectAcademicYear(
        academicYear: AcademicYearDTO
    ) {
        _uiState.update {
            it.copy(
                placement = it.placement.copy(
                    selectedAcademicYear = academicYear
                )
            )
        }
    }

    private fun loadSchoolSections() {
        schoolRepository
            .getSchoolSections()
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                placement = it.placement.copy(
                                    isLoadingSections = true
                                )
                            )
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                placement = it.placement.copy(
                                    schoolSections = resource.data ?: emptyList(),
                                    isLoadingSections = false
                                )
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                placement = it.placement.copy(
                                    isLoadingSections = false
                                ),
                                error = resource.message
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun selectSchoolSection(
        schoolSection: SchoolSectionDTO
    ) {
        _uiState.update {
            it.copy(
                placement = it.placement.copy(
                    selectedSchoolSection = schoolSection,
                    // reset dependent levels
                    selectedSection = null,
                    selectedMajor = null,
                    selectedGradeLevel = null,
                    selectedClass = null,

                    sections = emptyList(),
                    majors = emptyList(),
                    gradeLevels = emptyList(),
                    classes = emptyList()
                )
            )
        }

        loadSections(
            schoolSection.id
        )
    }

    private fun loadSections(
        schoolSectionId: Long
    ) {

        schoolRepository
            .getSectionBySchoolSectionAndSchool(
                schoolSectionId
            )
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update {

                            it.copy(
                                placement = it.placement.copy(
                                    isLoadingSections = true
                                )
                            )
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                placement = it.placement.copy(
                                    sections = resource.data ?: emptyList(),
                                    isLoadingSections = false
                                )
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                placement = it.placement.copy(
                                    isLoadingSections = false
                                ),
                                error = resource.message
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun selectSection(
        section: SectionDTO
    ) {

        _uiState.update {

            it.copy(
                placement = it.placement.copy(

                    selectedSection = section,

                    // reset dependent levels
                    selectedMajor = null,
                    selectedGradeLevel = null,
                    selectedClass = null,

                    majors = emptyList(),
                    gradeLevels = emptyList(),
                    classes = emptyList()
                )
            )
        }

        loadMajors(
            section.id
        )
    }

    private fun loadMajors(
        sectionId: Long
    ) {

        schoolRepository
            .getOfferedMajorsForSchoolAndSection(
                sectionId
            )
            .onEach { resource ->

                when (resource) {

                    is Resource.Loading -> {

                        _uiState.update {

                            it.copy(
                                placement = it.placement.copy(
                                    isLoadingMajors = true
                                )
                            )
                        }
                    }

                    is Resource.Success -> {

                        _uiState.update {

                            it.copy(
                                placement = it.placement.copy(
                                    majors = resource.data ?: emptyList(),
                                    isLoadingMajors = false
                                )
                            )
                        }
                    }

                    is Resource.Error -> {

                        _uiState.update {

                            it.copy(
                                placement = it.placement.copy(
                                    isLoadingMajors = false
                                ),
                                error = resource.message
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun selectMajor(
        major: MajorDto
    ) {

        _uiState.update {

            it.copy(
                placement = it.placement.copy(

                    selectedMajor = major,

                    selectedGradeLevel = null,
                    selectedClass = null,

                    gradeLevels = emptyList(),
                    classes = emptyList()
                )
            )
        }

        loadGradeLevels(
            major.id
        )
    }

    private fun loadGradeLevels(
        majorId: Long
    ) {

        schoolRepository
            .getGradeLevelsBySchoolAndByMajor(
                majorId
            )
            .onEach { resource ->

                when (resource) {

                    is Resource.Loading -> {

                        _uiState.update {

                            it.copy(
                                placement = it.placement.copy(
                                    isLoadingGradeLevels = true
                                )
                            )
                        }
                    }

                    is Resource.Success -> {

                        _uiState.update {

                            it.copy(
                                placement = it.placement.copy(
                                    gradeLevels = resource.data ?: emptyList(),
                                    isLoadingGradeLevels = false
                                )
                            )
                        }
                    }

                    is Resource.Error -> {

                        _uiState.update {

                            it.copy(
                                placement = it.placement.copy(
                                    isLoadingGradeLevels = false
                                ),
                                error = resource.message
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun selectGradeLevel(
        gradeLevel: GradeLevelDTO
    ) {

        _uiState.update {

            it.copy(
                placement = it.placement.copy(

                    selectedGradeLevel = gradeLevel,

                    selectedClass = null,

                    classes = emptyList()
                )
            )
        }

        loadClasses(
            gradeLevel.id
        )
    }

    private fun loadClasses(
        gradeLevelId: Long
    ) {

        schoolRepository
            .getClassesBySchoolAndGradeLevel(
                gradeLevelId
            )
            .onEach { resource ->

                when (resource) {

                    is Resource.Loading -> {

                        _uiState.update {

                            it.copy(
                                placement = it.placement.copy(
                                    isLoadingClasses = true
                                )
                            )
                        }
                    }

                    is Resource.Success -> {

                        _uiState.update {

                            it.copy(
                                placement = it.placement.copy(
                                    classes = resource.data ?: emptyList(),
                                    isLoadingClasses = false
                                )
                            )
                        }
                    }

                    is Resource.Error -> {

                        _uiState.update {

                            it.copy(
                                placement = it.placement.copy(
                                    isLoadingClasses = false
                                ),
                                error = resource.message
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun selectClass(
        schoolClass: SchoolClassDTO
    ) {

        _uiState.update {

            it.copy(
                placement = it.placement.copy(
                    selectedClass = schoolClass
                )
            )
        }
    }

    private fun loadStudents() {
        val schoolId =
            settingsStorage.getUserInfo().schoolId ?: return

        studentsRepository
            .getStudents(schoolId)
            .onEach { resource ->

                when (resource) {

                    is Resource.Loading -> {

                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }

                    is Resource.Success -> {

                        allStudents = resource.data ?: emptyList()

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                students = allStudents,
                                filteredStudents = allStudents
                            )
                        }
                    }

                    is Resource.Error -> {

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)

    }

    fun onStudentSearchQueryChange(query: String) {

        _uiState.update {
            it.copy(
                searchQueryStudent = query
            )
        }

        filterStudents()
    }

    fun onEnrollmentSearchQueryChange(query: String) {

        _uiState.update {
            it.copy(
                searchQueryEnrollment = query
            )
        }

        filterStudents()
    }

    private fun filterStudents() {

        val query = _uiState.value.searchQueryStudent

        val filtered = if (query.isBlank()) {

            allStudents

        } else {

            allStudents.filter { student ->

                student.fullName.contains(
                    query,
                    ignoreCase = true
                ) ||
                        student.studentIdNumber.contains(
                            query,
                            ignoreCase = true
                        ) ||

                        (student.sernieNumber?.contains(
                            query,
                            ignoreCase = true
                        ) == true)
            }
        }
        _uiState.update {
            it.copy(
                filteredStudents = filtered,
                isLoading = false
            )
        }
    }

    fun selectStudent(student: StudentDTO) {
        _uiState.update {
            it.copy(
                selectedStudent = student
            )
        }
    }


    fun selectEnrollment(enrollment: EnrollmentDto) {
        _uiState.update {
            it.copy(
                selectedEnrollment = enrollment
            )
        }
    }

    fun submitEnrollment() {

        val state = _uiState.value
        val placement = state.placement

        val student =
            state.selectedStudent ?: return

        val selectedClass =
            placement.selectedClass ?: return

        val academicYear =
            placement.selectedAcademicYear ?: return

        val request = EnrollmentRequest(
            studentId = student.id,
            classId = selectedClass.id,
            academicYearId = academicYear.id
        )

        enrollmentRepository
            .createEnrollment(request)
            .onEach { resource ->

                when (resource) {
                    is Resource.Loading -> {

                        _uiState.update {

                            it.copy(
                                isSubmitting = true,
                                error = null
                            )
                        }
                    }

                    is Resource.Success -> {

                        _uiState.update {

                            it.copy(
                                isSubmitting = false,
                                submissionSuccess = true
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }



    fun resetState() {
        _uiState.value = EnrollmentUiState()
    }
}


data class UploadedDocumentUi(
    val id: String,
    val name: String,
    val uri: String? = null,
    val uploaded: Boolean = false
)

data class AcademicPlacementUi(

    // Selected values
    val selectedAcademicYear: AcademicYearDTO? = null,
    val selectedSchoolSection: SchoolSectionDTO? = null,
    val selectedSection: SectionDTO? = null,
    val selectedMajor: MajorDto? = null,
    val selectedGradeLevel: GradeLevelDTO? = null,
    val selectedClass: SchoolClassDTO? = null,

    // Lists
    val academicYears: List<AcademicYearDTO> = emptyList(),
    val schoolSections: List<SchoolSectionDTO> = emptyList(),
    val sections: List<SectionDTO> = emptyList(),
    val majors: List<MajorDto> = emptyList(),
    val gradeLevels: List<GradeLevelDTO> = emptyList(),
    val classes: List<SchoolClassDTO> = emptyList(),

    // Loading
    val isLoadingAcademicYears: Boolean = false,
    val isLoadingSections: Boolean = false,
    val isLoadingMajors: Boolean = false,
    val isLoadingGradeLevels: Boolean = false,
    val isLoadingClasses: Boolean = false
)

data class EnrollmentUiState(

    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,

    // STUDENTS
    val searchQueryStudent: String = "",
    val students: List<StudentDTO> = emptyList(),
    val filteredStudents: List<StudentDTO> = emptyList(),
    val selectedStudent: StudentDTO? = null,

    // ENNROLLMENTS
    val searchQueryEnrollment: String = "",
    val enrollments: List<EnrollmentDto> = emptyList(),
    val filteredEnrollments: List<EnrollmentDto> = emptyList(),
    val selectedEnrollment: EnrollmentDto? = null,

    // ACADEMICS
    val placement: AcademicPlacementUi = AcademicPlacementUi(),

    // DOCUMENTS
    val documents: List<UploadedDocumentUi> = emptyList(),

    // RESULT
    val submissionSuccess: Boolean = false,
)
