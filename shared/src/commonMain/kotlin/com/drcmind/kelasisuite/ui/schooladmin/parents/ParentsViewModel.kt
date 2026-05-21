package com.drcmind.kelasisuite.ui.schooladmin.parents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.repository.parents.ParentsRepository
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.data.repository.users.UsersRepository
import com.drcmind.kelasisuite.domain.dto.*
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.*

class ParentsViewModel(
    private val parentsRepository: ParentsRepository,
    private val usersRepository: UsersRepository,
    private val studentsRepository: StudentsRepository,
    private val schoolRepository: SchoolRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow(ParentsState())
    val uiState: StateFlow<ParentsState> = _uiState.asStateFlow()

    private val _detailState = MutableStateFlow(ParentDetailState())
    val detailState: StateFlow<ParentDetailState> = _detailState.asStateFlow()

    private val _users = MutableStateFlow<List<UserDTO>>(emptyList())
    val users: StateFlow<List<UserDTO>> = _users.asStateFlow()

    private val _students = MutableStateFlow<List<StudentDTO>>(emptyList())
    val students: StateFlow<List<StudentDTO>> = _students.asStateFlow()

    private val _academicYears = MutableStateFlow<List<AcademicYearDTO>>(emptyList())
    val academicYears: StateFlow<List<AcademicYearDTO>> = _academicYears.asStateFlow()

    init {
        loadSchoolParent()
        loadUsers()
        loadStudents()
        loadAcademicYears()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun loadSchoolParent() {
        parentsRepository.getParentsBySchool().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            list = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                    println("HHHHHHHHHH" + result.message)

                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    println("HHHHHHHHHH" + result.message)

                }

                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }


            }
        }.launchIn(viewModelScope)
    }

    private fun loadUsers() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        usersRepository.getUserBySchoolId(schoolId).onEach { result ->
            if (result is Resource.Success) {
                _users.value = result.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }

    private fun loadStudents() {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        studentsRepository.getStudents(schoolId).onEach { result ->
            if (result is Resource.Success) {
                _students.value = result.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }

    private fun loadAcademicYears() {
        schoolRepository.getAcademicYears().onEach { result ->
            if (result is Resource.Success) {
                _academicYears.value = result.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }

    fun loadParentDetail(parentId: Long) {
        parentsRepository.getParentById(parentId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _detailState.update {
                        it.copy(
                            parent = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                }

                is Resource.Error -> {
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }

                is Resource.Loading -> {
                    _detailState.update { it.copy(isLoading = true, error = null) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun createParent(userId: Long, address: String, occupation: String, onSuccess: () -> Unit) {
        val request = CreateParentRequest(userId, address, occupation)
        parentsRepository.createParent(request).onEach { result ->
            if (result is Resource.Success) {
                loadSchoolParent()
                onSuccess()
            }
        }.launchIn(viewModelScope)
    }

    fun updateParent(
        parentId: Long,
        userId: Long,
        address: String,
        occupation: String,
        onSuccess: () -> Unit
    ) {
        val request = CreateParentRequest(userId, address, occupation)
        parentsRepository.updateParent(parentId, request).onEach { result ->
            if (result is Resource.Success) {
                loadSchoolParent()
                loadParentDetail(parentId)
                onSuccess()
            }
        }.launchIn(viewModelScope)
    }

    fun deleteParent(parentId: Long, onSuccess: () -> Unit) {
        parentsRepository.deleteParent(parentId).onEach { result ->
            if (result is Resource.Success) {
                loadSchoolParent()
                onSuccess()
            }
        }.launchIn(viewModelScope)
    }

    fun linkStudent(
        parentId: Long,
        studentId: Long,
        academicYearId: Long,
        relationshipType: String,
        isPrimaryPayer: Boolean
    ) {
        val schoolId = settingsStorage.getUserInfo().schoolId ?: return
        val request = ParentStudentLinkageRequest(
            parentId = parentId,
            studentId = studentId,
            academicYearId = academicYearId,
            schoolId = schoolId,
            relationshipType = relationshipType,
            isPrimaryPayer = isPrimaryPayer
        )
        parentsRepository.linkStudentToParent(request).onEach { result ->
            if (result is Resource.Success) {
                loadParentDetail(parentId)
            }
        }.launchIn(viewModelScope)
    }

    fun unlinkStudent(parentId: Long, linkageId: Long) {
        parentsRepository.unlinkStudentFromParent(linkageId).onEach { result ->
            if (result is Resource.Success) {
                loadParentDetail(parentId)
            }
        }.launchIn(viewModelScope)
    }
}

data class ParentsState(
    val list: List<ParentDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

data class ParentDetailState(
    val parent: ParentDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)