package com.drcmind.kelasisuite.ui.schooladmin.AcademicManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class AddClassViewModel(private val academicRepository: AcademicRepository) : ViewModel() {
    private val _state = MutableStateFlow(AddClassState())
    val state: StateFlow<AddClassState> = _state.asStateFlow()

    fun selectSection(section: AcademicSection) {
        _state.value = _state.value.copy(selectedSection = section)
    }

    fun selectOption(option: AcademicOption) {
        _state.value = _state.value.copy(selectedOption = option)
    }

    fun setCapacity(value: Int) {
        _state.value = _state.value.copy(capacity = value.coerceIn(10, 60))
    }

    fun createClass(onResult: (CreateClassResult) -> Unit) {
        val current = _state.value
        val request = CreateClassFromTemplateRequest(
            schoolId = 123,
            templateGradeLevelId = mapTemplateGradeLevelId(
                current.selectedSection,
                current.selectedOption
            ),
            name = buildClassName(current.selectedSection, current.selectedOption),
            capacity = current.capacity
        )

        _state.value = current.copy(isLoading = true, errorMessage = null, successResult = null)
        onResult(CreateClassResult.Loading)

        viewModelScope.launch {
            try {
                val response = academicRepository.createClassFromTemplate(request)
                _state.value = _state.value.copy(isLoading = false, successResult = response)
                onResult(CreateClassResult.Success(response))
            } catch (exception: Exception) {
                val message = exception.message ?: "Impossible de créer la classe"
                _state.value = _state.value.copy(isLoading = false, errorMessage = message)
                onResult(CreateClassResult.Error(message))
            }
        }
    }

    private fun buildClassName(section: AcademicSection, option: AcademicOption): String {
        return when (section) {
            AcademicSection.Scientifique -> "Classe Scientifique"
            AcademicSection.Littéraire -> "Classe Littéraire"
            AcademicSection.Économique -> "Classe Économique"
        } + " ${option.name.replace("Physique", " & Physique").replace("ChimieBio", "Chimie & Bio")}"
    }

    private fun mapTemplateGradeLevelId(section: AcademicSection, option: AcademicOption): Int {
        return when (section) {
            AcademicSection.Scientifique -> when (option) {
                AcademicOption.MathPhysique -> 1
                AcademicOption.ChimieBio -> 2
                AcademicOption.Informatique -> 3
                AcademicOption.Robotique -> 4
            }
            AcademicSection.Littéraire -> when (option) {
                AcademicOption.MathPhysique -> 5
                AcademicOption.ChimieBio -> 6
                AcademicOption.Informatique -> 7
                AcademicOption.Robotique -> 8
            }
            AcademicSection.Économique -> when (option) {
                AcademicOption.MathPhysique -> 9
                AcademicOption.ChimieBio -> 10
                AcademicOption.Informatique -> 11
                AcademicOption.Robotique -> 12
            }
        }
    }
}

interface AcademicRepository {
    suspend fun createClassFromTemplate(request: CreateClassFromTemplateRequest): Any?
}

@Serializable
data class CreateClassFromTemplateRequest(
    val schoolId: Int,
    val templateGradeLevelId: Int,
    val name: String,
    val capacity: Int
)

data class AddClassState(
    val selectedSection: AcademicSection = AcademicSection.Scientifique,
    val selectedOption: AcademicOption = AcademicOption.MathPhysique,
    val capacity: Int = 30,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successResult: Any? = null
)

enum class AcademicSection {
    Scientifique,
    Littéraire,
    Économique
}

enum class AcademicOption {
    MathPhysique,
    ChimieBio,
    Informatique,
    Robotique
}

sealed class CreateClassResult {
    data object Loading : CreateClassResult()
    data class Success(val response: Any?) : CreateClassResult()
    data class Error(val message: String) : CreateClassResult()
}
