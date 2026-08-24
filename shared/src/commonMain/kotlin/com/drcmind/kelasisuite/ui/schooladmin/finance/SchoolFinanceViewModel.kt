package com.drcmind.kelasisuite.ui.schooladmin.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.repository.finance.SchoolFinanceRepository
import com.drcmind.kelasisuite.domain.model.finance.*
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class FinanceTab(val label: String) {
    DASHBOARD("Tableau de bord"),
    CASH_REGISTER("Caisse & Encaissement"),
    SOLVENCY("Solvabilité & Impayés"),
    FEE_STRUCTURE("Grille Tarifaire")
}

data class PaymentDialogFormState(
    val studentId: Long = 1L,
    val studentName: String = "Kabila Marc",
    val classroomName: String = "6ème Math-Physique A",
    val feeItemId: Long = 1L,
    val feeDescription: String = "Minerval 1er Trimestre",
    val amountInput: String = "150",
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val notes: String = ""
)

data class FeeStructureFormState(
    val title: String = "",
    val category: FeeCategory = FeeCategory.TUITION,
    val amountInput: String = "",
    val targetClass: String = "Toutes les classes",
    val dueDate: String = "2026-03-30"
)

data class SchoolFinanceUiState(
    val activeTab: FinanceTab = FinanceTab.DASHBOARD,
    val dashboardSummary: SchoolFinanceDashboardSummary? = null,
    val recentTransactions: List<PaymentTransaction> = emptyList(),
    val solvencyList: List<StudentSolvencyItem> = emptyList(),
    val feeStructures: List<FeeStructureItem> = emptyList(),
    val solvencyFilter: SolvencyFilter = SolvencyFilter.ALL,
    val searchQuery: String = "",
    val isPaymentDialogOpen: Boolean = false,
    val paymentForm: PaymentDialogFormState = PaymentDialogFormState(),
    val isFeeDialogOpen: Boolean = false,
    val feeForm: FeeStructureFormState = FeeStructureFormState(),
    val isSubmitting: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
) {
    val filteredSolvencyList: List<StudentSolvencyItem>
        get() = solvencyList.filter { item ->
            val matchesFilter = when (solvencyFilter) {
                SolvencyFilter.ALL -> true
                SolvencyFilter.SOLVENT -> item.isFullyPaid
                SolvencyFilter.PARTIAL -> item.totalPaid > 0 && !item.isFullyPaid
                SolvencyFilter.INSOLVENT -> item.totalPaid == 0.0
            }
            val matchesSearch = searchQuery.isBlank() ||
                    item.studentName.contains(searchQuery, ignoreCase = true) ||
                    item.rollNumber.contains(searchQuery, ignoreCase = true) ||
                    item.classroomName.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
}

class SchoolFinanceViewModel(
    private val financeRepository: SchoolFinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SchoolFinanceUiState())
    val uiState: StateFlow<SchoolFinanceUiState> = _uiState.asStateFlow()

    init {
        loadAllData()
    }

    fun setTab(tab: FinanceTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun setSolvencyFilter(filter: SolvencyFilter) {
        _uiState.update { it.copy(solvencyFilter = filter) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openPaymentDialog(student: StudentSolvencyItem? = null) {
        val form = if (student != null) {
            PaymentDialogFormState(
                studentId = student.studentId,
                studentName = student.studentName,
                classroomName = student.classroomName,
                amountInput = student.balanceRemaining.toInt().toString().ifEmpty { "150" }
            )
        } else {
            PaymentDialogFormState()
        }
        _uiState.update { it.copy(isPaymentDialogOpen = true, paymentForm = form) }
    }

    fun closePaymentDialog() {
        _uiState.update { it.copy(isPaymentDialogOpen = false) }
    }

    fun updatePaymentForm(transform: PaymentDialogFormState.() -> PaymentDialogFormState) {
        _uiState.update { it.copy(paymentForm = it.paymentForm.transform()) }
    }

    fun submitPayment() {
        val form = _uiState.value.paymentForm
        val amount = form.amountInput.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            _uiState.update { it.copy(errorMessage = "Veuillez entrer un montant valide supérieur à 0.") }
            return
        }

        val transaction = PaymentTransaction(
            transactionRef = "",
            studentId = form.studentId,
            studentName = form.studentName,
            classroomName = form.classroomName,
            feeItemId = form.feeItemId,
            feeDescription = form.feeDescription,
            amountPaid = amount,
            currency = "USD",
            paymentMethod = form.paymentMethod,
            paymentDate = "",
            receivedBy = "Comptable",
            notes = form.notes.ifBlank { null }
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            when (val res = financeRepository.recordPayment(transaction)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            isPaymentDialogOpen = false,
                            successMessage = "Paiement de ${amount} USD enregistré avec succès (Réf: ${res.data?.transactionRef}). Quittance générée."
                        )
                    }
                    loadAllData()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = res.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun openFeeDialog() {
        _uiState.update { it.copy(isFeeDialogOpen = true, feeForm = FeeStructureFormState()) }
    }

    fun closeFeeDialog() {
        _uiState.update { it.copy(isFeeDialogOpen = false) }
    }

    fun updateFeeForm(transform: FeeStructureFormState.() -> FeeStructureFormState) {
        _uiState.update { it.copy(feeForm = it.feeForm.transform()) }
    }

    fun submitFeeStructure() {
        val form = _uiState.value.feeForm
        val amount = form.amountInput.toDoubleOrNull() ?: 0.0
        if (form.title.isBlank() || amount <= 0) {
            _uiState.update { it.copy(errorMessage = "Veuillez remplir le libellé et un montant valide.") }
            return
        }

        val fee = FeeStructureItem(
            title = form.title,
            category = form.category,
            amount = amount,
            targetClassName = form.targetClass,
            dueDate = form.dueDate
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            financeRepository.saveFeeStructure(fee)
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    isFeeDialogOpen = false,
                    successMessage = "Frais ajouté à la grille tarifaire avec succès."
                )
            }
            loadAllData()
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    private fun loadAllData() {
        viewModelScope.launch {
            financeRepository.getFinanceDashboard(1L).collect { res ->
                if (res is Resource.Success) _uiState.update { it.copy(dashboardSummary = res.data) }
            }
        }
        viewModelScope.launch {
            financeRepository.getRecentTransactions(1L).collect { res ->
                if (res is Resource.Success) _uiState.update { it.copy(recentTransactions = res.data ?: emptyList()) }
            }
        }
        viewModelScope.launch {
            financeRepository.getStudentsSolvency(null).collect { res ->
                if (res is Resource.Success) _uiState.update { it.copy(solvencyList = res.data ?: emptyList()) }
            }
        }
        viewModelScope.launch {
            financeRepository.getFeeStructures(1L).collect { res ->
                if (res is Resource.Success) _uiState.update { it.copy(feeStructures = res.data ?: emptyList()) }
            }
        }
    }
}
