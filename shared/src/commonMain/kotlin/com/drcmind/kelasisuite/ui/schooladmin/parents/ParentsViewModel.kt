package com.drcmind.kelasisuite.ui.schooladmin.parents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.parents.ParentsRepository
import com.drcmind.kelasisuite.domain.dto.ParentDto
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.*

class ParentsViewModel(
    private val parentsRepository: ParentsRepository
) : ViewModel() {
    val uiState : StateFlow<ParentsState>
        field = MutableStateFlow(ParentsState())

    init {
        loadSchoolParent()
    }

    fun onSearchQueryChange(query: String) {
        uiState.update { it.copy(searchQuery = query) }
    }


    private fun loadSchoolParent(){
        parentsRepository.getParentsBySchool().onEach { result ->
            when(result){
                is Resource.Success -> {
                    uiState.value = uiState.value.copy(
                        list = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    uiState.value = uiState.value.copy(
                        isLoading = true,
                        error = null
                    )
                }
            }
        }.launchIn(viewModelScope)
    }


}

data class ParentsState(
    val list: List<ParentDto> = emptyList(),
    val isLoading : Boolean = false,
    val error :String? = null,
    val searchQuery: String = ""
)