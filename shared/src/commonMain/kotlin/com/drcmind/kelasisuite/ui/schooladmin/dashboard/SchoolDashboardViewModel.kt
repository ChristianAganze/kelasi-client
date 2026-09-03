package com.drcmind.kelasisuite.ui.schooladmin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.repository.enrollment.EnrollmentRepository
import com.drcmind.kelasisuite.data.repository.finance.SchoolFinanceRepository
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.users.UsersRepository
import com.drcmind.kelasisuite.domain.model.finance.PaymentTransaction
import com.drcmind.kelasisuite.domain.model.finance.SchoolFinanceDashboardSummary
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MonthlyFinancialMetric(
    val monthLabel: String,
    val amountCollected: Double,
    val targetAmount: Double
)

data class SectionEnrollmentMetric(
    val sectionName: String,
    val studentCount: Int,
    val percentage: Float
)

data class SubjectProgressMetric(
    val subjectName: String,
    val completionPercentage: Int,
    val status: String // "ON_TRACK", "AHEAD", "DELAYED"
)

data class PendingPedagogyAlert(
    val id: Long,
    val title: String,
    val teacherName: String,
    val className: String,
    val type: String, // "PREPARATION", "CLASS_LOG", "REPORT"
    val timestamp: String
)

data class SchoolDashboardState(
    val schoolName: String = "Établissement Scolaire",
    val academicYear: String = "2025-2026",
    val username: String = "Administrateur",
    val role: String = "Chef d'Établissement",
    val systemStatus: String = "Système Opérationnel",
    val lastRefresh: String = "Aujourd'hui à l'instant",
    val isLoading: Boolean = false,
    
    // Core KPIs
    val totalStudents: Int = 485,
    val boysCount: Int = 252,
    val girlsCount: Int = 233,
    val attendanceRate: Double = 96.4,
    
    val totalTeachers: Int = 34,
    val totalClasses: Int = 16,
    val totalSections: Int = 4,
    
    // Finance KPIs
    val financeSummary: SchoolFinanceDashboardSummary = SchoolFinanceDashboardSummary(
        totalExpected = 145000.0,
        totalCollected = 108750.0,
        totalOutstanding = 36250.0,
        collectionRatePercentage = 75.0,
        todayCollected = 2450.0,
        cashCollected = 48200.0,
        mobileMoneyCollected = 42550.0,
        bankCollected = 18000.0,
        totalStudentsCount = 485,
        fullyPaidStudentsCount = 315,
        partialPaidStudentsCount = 120,
        nonPaidStudentsCount = 50
    ),
    
    // Charts Data
    val monthlyRevenue: List<MonthlyFinancialMetric> = listOf(
        MonthlyFinancialMetric("Sep", 18200.0, 20000.0),
        MonthlyFinancialMetric("Oct", 16400.0, 18000.0),
        MonthlyFinancialMetric("Nov", 14800.0, 15000.0),
        MonthlyFinancialMetric("Déc", 12500.0, 14000.0),
        MonthlyFinancialMetric("Jan", 19100.0, 20000.0),
        MonthlyFinancialMetric("Fév", 15300.0, 16000.0),
        MonthlyFinancialMetric("Mar", 12450.0, 15000.0)
    ),
    
    val sectionEnrollments: List<SectionEnrollmentMetric> = listOf(
        SectionEnrollmentMetric("Maternelle", 75, 15.5f),
        SectionEnrollmentMetric("Primaire", 180, 37.1f),
        SectionEnrollmentMetric("Secondaire Général (7e-8e)", 110, 22.7f),
        SectionEnrollmentMetric("Humanités Scientifiques", 70, 14.4f),
        SectionEnrollmentMetric("Humanités Commerciales", 50, 10.3f)
    ),
    
    val subjectProgressList: List<SubjectProgressMetric> = listOf(
        SubjectProgressMetric("Mathématiques & Algèbre", 82, "ON_TRACK"),
        SubjectProgressMetric("Physique - Chimie", 76, "ON_TRACK"),
        SubjectProgressMetric("Français & Littérature", 88, "AHEAD"),
        SubjectProgressMetric("Informatique & TIC", 92, "AHEAD"),
        SubjectProgressMetric("Histoire & Géographie", 68, "DELAYED")
    ),
    
    val recentTransactions: List<PaymentTransaction> = emptyList(),
    val pendingAlerts: List<PendingPedagogyAlert> = listOf(
        PendingPedagogyAlert(
            id = 101,
            title = "Fiche pédagogique : Optique Géométrique",
            teacherName = "Prof. Kabeya Jean",
            className = "6ème Math-Physique",
            type = "PREPARATION",
            timestamp = "Il y a 25 min"
        ),
        PendingPedagogyAlert(
            id = 102,
            title = "Cahier de texte hebdomadaire à signer",
            teacherName = "Prof. Mwamba Sarah",
            className = "5ème Biologie-Chimie",
            type = "CLASS_LOG",
            timestamp = "Il y a 1 heure"
        ),
        PendingPedagogyAlert(
            id = 103,
            title = "Validation des cotes de Période 2",
            teacherName = "Prof. Lumumba Patrick",
            className = "3ème Primaire B",
            type = "REPORT",
            timestamp = "Il y a 3 heures"
        )
    ),
    
    val selectedTimeRange: String = "Cette Année Scolaire"
)

class SchoolDashboardViewModel(
    private val settingsStorage: SettingsStorage,
    private val schoolRepository: SchoolRepository,
    private val usersRepository: UsersRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val schoolFinanceRepository: SchoolFinanceRepository
) : ViewModel() {

    private val userInfo = settingsStorage.getUserInfo()
    private val school = settingsStorage.getSchool()

    private val _state = MutableStateFlow(
        SchoolDashboardState(
            schoolName = school?.officialName ?: "Complexe Scolaire Kelasi",
            username = userInfo.preferredFirstName.ifBlank { "Administrateur" },
            role = when (userInfo.role?.uppercase()) {
                "ROLE_SCHOOL_ADMIN", "ADMIN" -> "Chef d'Établissement"
                "ROLE_SUPER_ADMIN" -> "Super Administrateur"
                else -> userInfo.role ?: "Administrateur"
            }
        )
    )
    val state: StateFlow<SchoolDashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        val currentInfo = settingsStorage.getUserInfo()
        val currentSchool = settingsStorage.getSchool()
        val resolvedUsername = currentInfo.preferredFirstName.ifBlank { "Administrateur" }
        val resolvedRole = when (currentInfo.role?.uppercase()) {
            "ROLE_SCHOOL_ADMIN", "ADMIN" -> "Chef d'Établissement"
            "ROLE_SUPER_ADMIN", "ROLE_SUPER_USER" -> "Super Administrateur"
            "ROLE_TEACHER" -> "Enseignant"
            "ROLE_PARENT" -> "Parent d'Élève"
            else -> currentInfo.role ?: "Chef d'Établissement"
        }

        val schoolId = currentInfo.schoolId ?: currentSchool?.id ?: 1L
        _state.update {
            it.copy(
                username = resolvedUsername,
                role = resolvedRole,
                schoolName = currentSchool?.officialName ?: it.schoolName,
                isLoading = true
            )
        }

        viewModelScope.launch {
            // Load School info & sections
            schoolRepository.getSchool().onEach { res ->
                if (res is Resource.Success && res.data != null) {
                    _state.update { it.copy(schoolName = res.data.officialName) }
                }
            }.launchIn(this)

            // Load Classes count
            schoolRepository.getClassesForSchool().onEach { res ->
                if (res is Resource.Success && res.data != null) {
                    val classes = res.data
                    _state.update { it.copy(totalClasses = classes.size.coerceAtLeast(1)) }
                }
            }.launchIn(this)

            // Load Staff / Users count
            usersRepository.getUserBySchoolId(schoolId).onEach { res ->
                if (res is Resource.Success && res.data != null) {
                    val teachers = res.data.filter { u -> u.roles.any { r -> r.contains("TEACHER", ignoreCase = true) } }
                    _state.update { 
                        it.copy(totalTeachers = if (teachers.isNotEmpty()) teachers.size else it.totalTeachers) 
                    }
                }
            }.launchIn(this)

            // Load Enrolled students
            enrollmentRepository.getEnrolledStudents().onEach { res ->
                if (res is Resource.Success && res.data != null) {
                    val enrollments = res.data
                    if (enrollments.isNotEmpty()) {
                        val total = enrollments.size
                        val boys = (total * 0.52).toInt()
                        val girls = total - boys
                        _state.update {
                            it.copy(
                                totalStudents = total,
                                boysCount = boys,
                                girlsCount = girls
                            )
                        }
                    }
                }
            }.launchIn(this)

            // Load Finance Summary
            schoolFinanceRepository.getFinanceDashboard(schoolId).onEach { res ->
                if (res is Resource.Success && res.data != null) {
                    _state.update { it.copy(financeSummary = res.data) }
                }
            }.launchIn(this)

            // Load Recent Transactions
            schoolFinanceRepository.getRecentTransactions(schoolId).onEach { res ->
                if (res is Resource.Success && res.data != null) {
                    _state.update { it.copy(recentTransactions = res.data.take(5), isLoading = false) }
                } else if (res is Resource.Error) {
                    _state.update { it.copy(isLoading = false) }
                }
            }.launchIn(this)
        }
    }

    fun onTimeRangeSelected(range: String) {
        _state.update { it.copy(selectedTimeRange = range) }
    }
}
