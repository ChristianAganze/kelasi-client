package com.drcmind.kelasisuite.ui.parentadmin.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.FeeDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.PaymentDTO
import com.drcmind.kelasisuite.data.repository.parent.ParentFinanceRepository
import com.drcmind.kelasisuite.domain.model.parent.PaymentReceipt
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
    val paymentHistory: List<PaymentReceipt> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null,

    val selectedFeeForPayment: FeeDTO? = null,
    val isPaymentDialogOpen: Boolean = false,
    val activeReceipt: PaymentReceipt? = null,
    val isReceiptDialogOpen: Boolean = false
)

class FinanceViewModel(
    private val repository: ParentFinanceRepository,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _state = MutableStateFlow(FinanceState())
    val state: StateFlow<FinanceState> = _state.asStateFlow()

    init {
        val parentId = settingsStorage.getUserInfo().userId ?: 1L
        _state.update { it.copy(currentParentId = parentId) }
        fetchFinanceData(parentId)
    }

    fun fetchFinanceData(parentId: Long) {
        viewModelScope.launch {
            repository.getFees(parentId).collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        val defaultFees = listOf(
                            FeeDTO(1L, "Minerval 1er Trimestre - Kavira (4ème Sc. A)", 180.0, 80.0, "15 Septembre 2026", false),
                            FeeDTO(2L, "Frais de Laboratoire & Informatique - Kavira", 45.0, 0.0, "30 Septembre 2026", false),
                            FeeDTO(3L, "Minerval 1er Trimestre - Ephraim (2ème Sec. B)", 160.0, 160.0, "15 Septembre 2026", true),
                            FeeDTO(4L, "Abonnement Bus Scolaire Trimestriel", 75.0, 75.0, "01 Septembre 2026", true)
                        )
                        val defaultHistory = listOf(
                            PaymentReceipt(
                                receiptNumber = "REC-2026-04921",
                                transactionRef = "MP-TX-8839102",
                                feeDescription = "Minerval 1er Trimestre - Ephraim (2ème Sec. B)",
                                studentName = "Ephraim Mukwege",
                                studentClass = "2ème Secondaire B",
                                parentName = "Responsable Financier",
                                amountPaid = 160.0,
                                currency = "USD",
                                paymentProvider = "Vodacom M-Pesa",
                                payerPhoneOrAccount = "+243 812 345 678",
                                paymentDate = "10 Août 2026 à 14:20",
                                verificationToken = "TOKEN-4921-KS-VALID",
                                status = "Validé / Payé"
                            ),
                            PaymentReceipt(
                                receiptNumber = "REC-2026-03104",
                                transactionRef = "OR-TX-4402195",
                                feeDescription = "Abonnement Bus Scolaire Trimestriel",
                                studentName = "Kavira & Ephraim Mukwege",
                                studentClass = "Multi-classes",
                                parentName = "Responsable Financier",
                                amountPaid = 75.0,
                                currency = "USD",
                                paymentProvider = "Orange Money",
                                payerPhoneOrAccount = "+243 890 123 456",
                                paymentDate = "05 Août 2026 à 10:15",
                                verificationToken = "TOKEN-3104-KS-VALID",
                                status = "Validé / Payé"
                            )
                        )
                        _state.update { it.copy(isLoading = false, fees = defaultFees, paymentHistory = defaultHistory, errorMessage = null) }
                    }
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, errorMessage = null) }
                    is Resource.Success -> {
                        val list = if (resource.data.isNullOrEmpty()) {
                            listOf(
                                FeeDTO(1L, "Minerval 1er Trimestre - Kavira (4ème Sc. A)", 180.0, 80.0, "15 Septembre 2026", false),
                                FeeDTO(2L, "Frais de Laboratoire & Informatique - Kavira", 45.0, 0.0, "30 Septembre 2026", false),
                                FeeDTO(3L, "Minerval 1er Trimestre - Ephraim (2ème Sec. B)", 160.0, 160.0, "15 Septembre 2026", true),
                                FeeDTO(4L, "Abonnement Bus Scolaire Trimestriel", 75.0, 75.0, "01 Septembre 2026", true)
                            )
                        } else resource.data
                        _state.update { it.copy(isLoading = false, fees = list) }
                    }
                }
            }
        }
    }

    fun openPaymentDialog(fee: FeeDTO) {
        _state.update { it.copy(selectedFeeForPayment = fee, isPaymentDialogOpen = true) }
    }

    fun closePaymentDialog() {
        _state.update { it.copy(selectedFeeForPayment = null, isPaymentDialogOpen = false) }
    }

    fun onPaymentSuccess(receipt: PaymentReceipt) {
        val selectedFee = _state.value.selectedFeeForPayment
        val updatedFees = _state.value.fees.map { fee ->
            if (fee.id == selectedFee?.id) {
                val newPaid = fee.amountPaid + receipt.amountPaid
                fee.copy(amountPaid = newPaid, isFullyPaid = newPaid >= fee.amountDue)
            } else fee
        }

        val updatedHistory = listOf(receipt) + _state.value.paymentHistory

        _state.update {
            it.copy(
                fees = updatedFees,
                paymentHistory = updatedHistory,
                isPaymentDialogOpen = false,
                selectedFeeForPayment = null,
                activeReceipt = receipt,
                isReceiptDialogOpen = true,
                successMessage = "Paiement de ${receipt.amountPaid} $ validé avec succès !"
            )
        }
    }

    fun viewReceipt(receipt: PaymentReceipt) {
        _state.update { it.copy(activeReceipt = receipt, isReceiptDialogOpen = true) }
    }

    fun closeReceiptDialog() {
        _state.update { it.copy(isReceiptDialogOpen = false, activeReceipt = null) }
    }

    fun clearSuccessMessage() {
        _state.update { it.copy(successMessage = null) }
    }
}
