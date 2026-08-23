package com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teachers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.data.repository.users.UsersRepository
import com.drcmind.kelasisuite.data.datasource.remote.dto.Address
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeacherProfileDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeacherProfileRequest
import com.drcmind.kelasisuite.data.datasource.remote.dto.UserDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock

class TeachersViewModel(
    private val teachersRepository: TeachersRepository,
    private val usersRepository: UsersRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    // --- Teachers List State ---
    private val _listState = MutableStateFlow(TeachersUiState())
    val listState: StateFlow<TeachersUiState> = _listState.asStateFlow()
    private var allTeachers: List<TeacherProfileDTO> = emptyList()

    // --- Teacher Detail State ---
    private val _detailState = MutableStateFlow(TeacherDetailsState())
    val detailState: StateFlow<TeacherDetailsState> = _detailState.asStateFlow()

    val sampleTeacherProfile = TeacherProfileUiState(

        teacherName = "Jean-Claude Mukendi",
        matricule = "ENS-2021-0045",

        photoUrl = null,

        specialization = "Mathématiques & Physique",

        grade = "Enseignant Senior",

        experienceYears = 12,

        /*
        ========================================================
        TEACHING LOAD
        ========================================================
        */

        classes = listOf(

            TeachingClassUi(
                className = "8ème Sciences A",
                studentsCount = 42,
                course = "Mathématiques",
                hoursPerWeek = 6
            ),

            TeachingClassUi(
                className = "7ème Commerciale",
                studentsCount = 38,
                course = "Mathématiques Financières",
                hoursPerWeek = 4
            ),

            TeachingClassUi(
                className = "6ème Scientifique",
                studentsCount = 35,
                course = "Physique",
                hoursPerWeek = 5
            ),

            TeachingClassUi(
                className = "5ème Technique Industrielle",
                studentsCount = 29,
                course = "Électricité Générale",
                hoursPerWeek = 3
            )
        ),

        /*
        ========================================================
        CURRICULUM PROGRESS
        ========================================================
        */

        curriculumProgress = listOf(

            CurriculumProgressUi(
                className = "8ème Sciences A",
                currentProgress = 0.72f,
                nationalProgress = 0.68f
            ),

            CurriculumProgressUi(
                className = "7ème Commerciale",
                currentProgress = 0.61f,
                nationalProgress = 0.64f
            ),

            CurriculumProgressUi(
                className = "6ème Scientifique",
                currentProgress = 0.81f,
                nationalProgress = 0.75f
            ),

            CurriculumProgressUi(
                className = "5ème Technique Industrielle",
                currentProgress = 0.57f,
                nationalProgress = 0.52f
            )
        ),

        /*
        ========================================================
        RECENT ACTIVITIES
        ========================================================
        */

        activities = listOf(

            TeacherActivityUi(
                title = "Préparation de leçon",
                description = "Préparation du chapitre sur les équations différentielles pour la 8ème Sciences.",
                timestamp = "Aujourd'hui • 08:15",
                type = ActivityType.LESSON_PREPARATION
            ),

            TeacherActivityUi(
                title = "Journal de classe enregistré",
                description = "Cours de Physique validé pour la 6ème Scientifique.",
                timestamp = "Aujourd'hui • 10:40",
                type = ActivityType.CLASS_LOG
            ),

            TeacherActivityUi(
                title = "Encodage des notes",
                description = "Publication des résultats du devoir de Mathématiques.",
                timestamp = "Hier • 16:20",
                type = ActivityType.GRADING
            ),

            TeacherActivityUi(
                title = "Présences enregistrées",
                description = "Présences complétées pour la 7ème Commerciale.",
                timestamp = "Hier • 07:55",
                type = ActivityType.ATTENDANCE
            ),

            TeacherActivityUi(
                title = "Préparation d'examen",
                description = "Création de l'évaluation du second trimestre.",
                timestamp = "Lundi • 18:10",
                type = ActivityType.LESSON_PREPARATION
            ),

            TeacherActivityUi(
                title = "Correction des copies",
                description = "Correction de 42 copies pour la 8ème Sciences A.",
                timestamp = "Dimanche • 14:30",
                type = ActivityType.GRADING
            )
        )
    )

    // --- Add/Edit Teacher State ---
    private val _formState = MutableStateFlow(AddTeacherState())
    val formState: StateFlow<AddTeacherState> = _formState.asStateFlow()
    private var allUsers: List<UserDTO> = emptyList()
    private var teacherUserIds: Set<Long> = emptySet()

    init {
        loadTeachers()
        loadUsersData()
    }

    // --- List Logic ---
    fun loadTeachers() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        teachersRepository.getTeachers(schoolId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _listState.update { it.copy(isLoading = true) }
                is Resource.Success -> {
                    allTeachers = resource.data ?: emptyList()
                    filterTeachers()
                }
                is Resource.Error -> _listState.update { it.copy(isLoading = false) }
            }
        }.launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _listState.update { it.copy(searchQuery = query) }
        filterTeachers()
    }

    private fun filterTeachers() {
        val query = _listState.value.searchQuery.lowercase()
        val filtered = allTeachers.filter { teacher ->
            teacher.fullName.lowercase().contains(query) ||
                    (teacher.payrollId?.lowercase()?.contains(query) ?: false) ||
                    teacher.qualifications.lowercase().contains(query)
        }
        _listState.update {
            it.copy(
                isLoading = false,
                teachers = filtered,
                totalTeachers = allTeachers.size
            )
        }
    }

    // --- Detail Logic ---
    fun loadTeacherDetail(teacherId: Long) {
        teachersRepository.getTeacher(teacherId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _detailState.update { it.copy(isLoading = true, error = null) }
                is Resource.Success -> _detailState.update { it.copy(isLoading = false, teacher = resource.data) }
                is Resource.Error -> _detailState.update { it.copy(isLoading = false, error = resource.message) }
            }
        }.launchIn(viewModelScope)
    }

    // --- Form Logic (Add/Edit) ---
    private fun loadUsersData() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        _formState.update { it.copy(isLoading = true) }

        teachersRepository.getTeachers(schoolId).onEach { resource ->
            if (resource is Resource.Success) {
                teacherUserIds = resource.data?.map { it.userId }?.toSet() ?: emptySet()
                filterAndDisplayUsers()
            }
        }.launchIn(viewModelScope)

        usersRepository.getUserBySchoolId(schoolId).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    allUsers = resource.data ?: emptyList()
                    filterAndDisplayUsers()
                    _formState.update { it.copy(isLoading = false) }
                }
                is Resource.Error -> _formState.update { it.copy(isLoading = false, error = resource.message) }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun filterAndDisplayUsers() {
        val query = _formState.value.searchQuery.lowercase()
        val filtered = allUsers.filter { user ->
            !teacherUserIds.contains(user.id) &&
                    (user.firstName.lowercase().contains(query) ||
                            user.lastName.lowercase().contains(query) ||
                            (user.email?.lowercase()?.contains(query) ?: false) ||
                            (user.phone?.contains(query) ?: false))
        }
        _formState.update { it.copy(users = filtered) }
    }

    fun onUserSearchQueryChange(query: String) {
        _formState.update { it.copy(searchQuery = query) }
        filterAndDisplayUsers()
    }

    fun onUserSelected(user: UserDTO) {
        _formState.update {
            it.copy(
                selectedUser = user,
                userId = user.id,
                fullName = "${user.firstName} ${user.lastName}",
                showUserList = false
            )
        }
    }

    fun onBackToUserList() {
        _formState.update { it.copy(showUserList = true, selectedUser = null, userId = null) }
    }

    fun onFullNameChange(value: String) = _formState.update { it.copy(fullName = value) }
    fun onQualificationsChange(value: String) = _formState.update { it.copy(qualifications = value) }
    fun onMaxHoursChange(value: String) = _formState.update { it.copy(maxWeeklyHours = value) }
    fun onAddressChange(value: String) = _formState.update { it.copy(streetAddress = value) }
    fun onCityChange(value: String) = _formState.update { it.copy(city = value) }
    fun onHireDateChange(value: String) = _formState.update { it.copy(hireDate = value) }
    fun onProvinceChange(value: String) = _formState.update { it.copy(province = value) }

    fun prepareFormForEdit(teacherId: Long) {
        teachersRepository.getTeacher(teacherId).onEach { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { teacher ->
                    _formState.update {
                        it.copy(
                            selectedUser = allUsers.firstOrNull { user -> user.id == teacher.userId },
                            userId = teacher.userId,
                            fullName = teacher.fullName,
                            qualifications = teacher.qualifications,
                            payrollId = teacher.payrollId ?: "",
                            city = teacher.address.cityTerritory,
                            province = teacher.address.province,
                            streetAddress = teacher.address.streetAndNumber ?: "",
                            showUserList = false,
                            isSuccess = false,
                            error = null
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun resetForm() {
        _formState.value = AddTeacherState()
        loadUsersData()
    }

    fun saveTeacher(teacherId: Long? = null) {
        val currentState = _formState.value
        val userId = currentState.userId

        if (teacherId == null && userId == null) {
            _formState.update { it.copy(error = "Veuillez sélectionner un utilisateur") }
            return
        }

        if (currentState.fullName.isBlank() || currentState.qualifications.isBlank() || currentState.hireDate.isBlank()) {
            _formState.update { it.copy(error = "Veuillez remplir tous les champs obligatoires") }
            return
        }

        val validatedHireDate = try {
            LocalDate.parse(currentState.hireDate)
        } catch (e: Exception) {
            _formState.update { it.copy(error = "Format de date invalide (AAAA-MM-JJ)") }
            return
        }

        val request = TeacherProfileRequest(
            userId = userId ?: 0,
            payrollId = currentState.payrollId,
            qualifications = currentState.qualifications,
            hireDate = validatedHireDate,
            maxWeeklyHours = currentState.maxWeeklyHours.toIntOrNull() ?: 40,
            resumeUrl = null,
            address = Address(
                province = currentState.province,
                cityTerritory = currentState.city,
                streetAndNumber = currentState.streetAddress
            )
        )

        val flow = if (teacherId == null) {
            teachersRepository.createTeacher(request)
        } else {
            teachersRepository.updateTeacher(teacherId, request)
        }

        flow.onEach { resource ->
            when (resource) {
                is Resource.Loading -> _formState.update { it.copy(isLoading = true, error = null) }
                is Resource.Success -> _formState.update { it.copy(isLoading = false, isSuccess = true) }
                is Resource.Error -> _formState.update { it.copy(isLoading = false, error = resource.message) }
            }
        }.launchIn(viewModelScope)
    }
}

data class TeachersUiState(
    val teachers: List<TeacherProfileDTO> = emptyList(),
    val totalTeachers: Int = 0,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

data class TeacherDetailsState(
    val teacher: TeacherProfileDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class AddTeacherState(
    val users: List<UserDTO> = emptyList(),
    val selectedUser: UserDTO? = null,
    val showUserList: Boolean = true,
    val fullName: String = "",
    val qualifications: String = "",
    val maxWeeklyHours: String = "",
    val streetAddress: String = "",
    val city: String = "",
    val province: String = "",
    val hireDate: String = "",
    val payrollId: String = "TCH-${(100..999).random()}-${Clock.System.now().toEpochMilliseconds()}",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val userId: Long? = null
)
