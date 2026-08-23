package com.drcmind.kelasisuite.ui.parentadmin.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.FeeDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.PaymentDTO
import com.drcmind.kelasisuite.data.repository.parent.ParentFinanceRepository
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FinanceState(
    val currentParentId: Long = -1L,
    val isLoading: Boolean = false,
    val fees: List<FeeDTO> = emptyList(),
    val payments: List<PaymentDTO> = emptyList(),
    val errorMessage: String? = null
)

class FinanceViewModel(
    private val repository: ParentFinanceRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _state = MutableStateFlow(FinanceState())
    val state: StateFlow<FinanceState> = _state.asStateFlow()

    init {
        val parentId = settingsStorage.getUserInfo().userId
        if (parentId != null) {
            _state.update { it.copy(currentParentId = parentId) }
            fetchFinanceData(parentId)
        } else {
            _state.update { it.copy(errorMessage = "Utilisateur non connecté") }
        }
    }

    fun fetchFinanceData(parentId: Long) {
        viewModelScope.launch {
            // Fetch Fees
            launch {
                repository.getFees(parentId).collect { resource ->
                    when (resource) {
                        is Resource.Error -> _state.update { it.copy(isLoading = false, errorMessage = resource.message) }
                        is Resource.Loading -> _state.update { it.copy(isLoading = true, errorMessage = null) }
                        is Resource.Success -> _state.update { it.copy(isLoading = false, fees = resource.data ?: emptyList()) }
                    }
                }
            }
            // Fetch Payments
            launch {
                repository.getPayments(parentId).collect { resource ->
                    when (resource) {
                        is Resource.Error -> _state.update { it.copy(isLoading = false, errorMessage = resource.message) }
                        is Resource.Loading -> _state.update { it.copy(isLoading = true, errorMessage = null) }
                        is Resource.Success -> _state.update { it.copy(isLoading = false, payments = resource.data ?: emptyList()) }
                    }
                }
            }
        }
    }
}
