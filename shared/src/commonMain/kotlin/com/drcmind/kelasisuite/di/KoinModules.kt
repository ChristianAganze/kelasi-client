package com.drcmind.kelasisuite.di

import com.drcmind.kelasisuite.AppViewModel
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorageImpl
import com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin.SchoolAdminApiService
import com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin.SchoolAdminApiServiceImpl
import com.drcmind.kelasisuite.data.datasource.remote.communication.CommunicationApiService
import com.drcmind.kelasisuite.data.datasource.remote.communication.CommunicationApiServiceImpl
import com.drcmind.kelasisuite.data.datasource.remote.SystemApiService
import com.drcmind.kelasisuite.data.datasource.remote.SystemApiServiceImpl
import com.drcmind.kelasisuite.data.datasource.remote.parent.ParentApiService
import com.drcmind.kelasisuite.data.datasource.remote.parent.ParentApiServiceImpl
import com.drcmind.kelasisuite.data.repository.auth.AuthRepository
import com.drcmind.kelasisuite.data.repository.auth.AuthRepositoryImpl
import com.drcmind.kelasisuite.data.repository.parent.ParentDashboardRepository
import com.drcmind.kelasisuite.data.repository.parent.ParentDashboardRepositoryImpl
import com.drcmind.kelasisuite.data.repository.parent.ParentChildrenRepository
import com.drcmind.kelasisuite.data.repository.parent.ParentChildrenRepositoryImpl
import com.drcmind.kelasisuite.data.repository.parent.ParentFinanceRepository
import com.drcmind.kelasisuite.data.repository.parent.ParentFinanceRepositoryImpl
import com.drcmind.kelasisuite.data.repository.communication.CommunicationRepository
import com.drcmind.kelasisuite.data.repository.communication.CommunicationRepositoryImpl
import com.drcmind.kelasisuite.data.repository.teacher.ReportsRepository
import com.drcmind.kelasisuite.data.repository.teacher.ReportsRepositoryImpl
import com.drcmind.kelasisuite.domain.util.PdfExporter
import com.drcmind.kelasisuite.domain.util.PdfExporterImpl
import com.drcmind.kelasisuite.data.repository.enrollment.EnrollmentRepository
import com.drcmind.kelasisuite.data.repository.enrollment.EnrollmentRepositoryImpl
import com.drcmind.kelasisuite.data.repository.parents.ParentsRepository
import com.drcmind.kelasisuite.data.repository.parents.ParentsRepositoryImp
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepository
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepositoryImpl
import com.drcmind.kelasisuite.data.repository.profile.ProfileRepository
import com.drcmind.kelasisuite.data.repository.profile.ProfileRepositoryImpl
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepository
import com.drcmind.kelasisuite.data.repository.schools.SchoolRepositoryImpl
import com.drcmind.kelasisuite.data.repository.students.StudentsRepository
import com.drcmind.kelasisuite.data.repository.students.StudentsRepositoryImpl
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepositoryImpl
import com.drcmind.kelasisuite.data.repository.users.UsersRepository
import com.drcmind.kelasisuite.data.repository.users.UsersRepositoryImpl
import com.drcmind.kelasisuite.domain.util.BASE_URL
import com.drcmind.kelasisuite.ui.auth.AuthViewModel
import com.drcmind.kelasisuite.ui.schooladmin.SchoolAdminViewModel
import com.drcmind.kelasisuite.ui.schooladmin.academics.calendar_periods.CalendarPeriodsViewModel
import com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure.SchoolStructureViewModel
import com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.enrollment.EnrollmentViewModel
import com.drcmind.kelasisuite.ui.schooladmin.dashboard.SchoolDashboardViewModel
import com.drcmind.kelasisuite.ui.schooladmin.parents.ParentsViewModel
import com.drcmind.kelasisuite.ui.schooladmin.academics.grading.EvaluationGradingViewModel
import com.drcmind.kelasisuite.ui.schooladmin.academics.deliberation.DeliberationsConductViewModel
import com.drcmind.kelasisuite.ui.schooladmin.academics.reports.ReportCardsViewModel
import com.drcmind.kelasisuite.ui.schooladmin.finance.SchoolFinanceViewModel
import com.drcmind.kelasisuite.data.repository.finance.SchoolFinanceRepository
import com.drcmind.kelasisuite.data.repository.finance.SchoolFinanceRepositoryImpl
import com.drcmind.kelasisuite.ui.schooladmin.communication.SchoolAdminCommunicationViewModel
import com.drcmind.kelasisuite.data.repository.communication.SchoolAdminCommunicationRepository
import com.drcmind.kelasisuite.data.repository.communication.SchoolAdminCommunicationRepositoryImpl
import com.drcmind.kelasisuite.ui.schooladmin.profile.ProfileViewModel
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teachers.TeachersViewModel
import com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.student.StudentsViewModel
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.classlog.ClassLogsViewModel
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.preparation.PreparationsViewModel
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.program_radar.ProgramRadarViewModel
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.schedule.ScheduleViewModel
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.inspections.InspectionsViewModel
import com.drcmind.kelasisuite.data.repository.pedagogy.SchoolInspectionRepository
import com.drcmind.kelasisuite.data.repository.pedagogy.SchoolInspectionRepositoryImpl
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teaching_assignment.TeachingAssignmentViewModel
import com.drcmind.kelasisuite.ui.teacheradmin.classlog.ClassLogViewModel
import com.drcmind.kelasisuite.ui.teacheradmin.TeacherAdminViewModel
import com.drcmind.kelasisuite.ui.teacheradmin.dashboard.TeacherDashboardViewModel
import com.drcmind.kelasisuite.ui.teacheradmin.preparation.PreparationViewModel
import com.drcmind.kelasisuite.ui.teacheradmin.classes.ClassesViewModel
import com.drcmind.kelasisuite.ui.teacheradmin.communication.CommunicationViewModel
import com.drcmind.kelasisuite.ui.teacheradmin.reports.ReportsViewModel
import com.drcmind.kelasisuite.ui.teacheradmin.schedule.TeacherScheduleViewModel
import com.drcmind.kelasisuite.ui.teacheradmin.settings.TeacherSettingsViewModel
import com.drcmind.kelasisuite.ui.parentadmin.dashboard.ParentDashboardViewModel
import com.drcmind.kelasisuite.ui.parentadmin.children.ChildrenViewModel
import com.drcmind.kelasisuite.ui.parentadmin.finance.FinanceViewModel
import com.drcmind.kelasisuite.ui.parentadmin.settings.ParentSettingsViewModel
import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun commonModule() = listOf(
    platformModule,
    localStorageModule,
    networkModule,
    repositoryModule,
    viewModelModule
)

expect val platformModule: Module

val networkModule = module {
    single { createKtorHttpClient(get()) }
    single<CommunicationApiService> { CommunicationApiServiceImpl(get()) }
    single<SystemApiService> { SystemApiServiceImpl(get()) }
    single<SchoolAdminApiService> { SchoolAdminApiServiceImpl(get()) }
    single<com.drcmind.kelasisuite.data.datasource.remote.teacher.TeacherApiService> { com.drcmind.kelasisuite.data.datasource.remote.teacher.TeacherApiServiceImpl(get()) }
    single<ParentApiService> { ParentApiServiceImpl(get()) }
}

val localStorageModule = module {
    single { Settings() }
    single<SettingsStorage> {
        SettingsStorageImpl(get())
    }
}

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<StudentsRepository> { StudentsRepositoryImpl(get(), get()) }
    single<SchoolRepository> { SchoolRepositoryImpl(get(), get()) } // Updated constructor
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }
    single<TeachersRepository> { TeachersRepositoryImpl(get(), get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }
    single<TeachersRepository> {
        TeachersRepositoryImpl(
            get(),
            get(),
        )
    }
    single<UsersRepository> { UsersRepositoryImpl(get()) }
    single <ParentsRepository>{ ParentsRepositoryImp(get(), get()) }
    single <EnrollmentRepository>{ EnrollmentRepositoryImpl(get(),get()) }
    single<ParentsRepository> { ParentsRepositoryImp(get(), get()) }
    single<AssignmentRepository> { AssignmentRepositoryImpl(get(), get()) }
    single<com.drcmind.kelasisuite.data.repository.teacher.PreparationRepository> { com.drcmind.kelasisuite.data.repository.teacher.PreparationRepositoryImpl(get()) }
    single<com.drcmind.kelasisuite.data.repository.teacher.ClassLogRepository> { com.drcmind.kelasisuite.data.repository.teacher.ClassLogRepositoryImpl(get()) }
    single<com.drcmind.kelasisuite.data.repository.teacher.EvaluationRepository> { com.drcmind.kelasisuite.data.repository.teacher.EvaluationRepositoryImpl(get()) }
    single<CommunicationRepository> { CommunicationRepositoryImpl(get()) }
    single<ReportsRepository> { ReportsRepositoryImpl(get()) }
    single<PdfExporter> { PdfExporterImpl() }
    single<ParentDashboardRepository> { ParentDashboardRepositoryImpl(get()) }
    single<ParentChildrenRepository> { ParentChildrenRepositoryImpl(get()) }
    single<ParentFinanceRepository> { ParentFinanceRepositoryImpl(get()) }
    single<SchoolFinanceRepository> { SchoolFinanceRepositoryImpl() }
    single<SchoolAdminCommunicationRepository> { SchoolAdminCommunicationRepositoryImpl() }
    single<SchoolInspectionRepository> { SchoolInspectionRepositoryImpl() }
}

val viewModelModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::SchoolDashboardViewModel)
    viewModelOf(::StudentsViewModel)
    viewModelOf (::SchoolStructureViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::CalendarPeriodsViewModel)
    viewModelOf(::TeachersViewModel)
    viewModelOf(::ParentsViewModel)
    viewModelOf(::EnrollmentViewModel)
    viewModelOf(::SchoolAdminViewModel)
    viewModelOf(::TeachingAssignmentViewModel)
    viewModelOf(::ScheduleViewModel)
    viewModelOf(::ProgramRadarViewModel)
    viewModelOf(::PreparationsViewModel)
    viewModelOf(::ClassLogsViewModel)
    viewModelOf(::InspectionsViewModel)
    viewModelOf(::EvaluationGradingViewModel)
    viewModelOf(::DeliberationsConductViewModel)
    viewModelOf(::ReportCardsViewModel)
    viewModelOf(::SchoolFinanceViewModel)
    viewModelOf(::SchoolAdminCommunicationViewModel)
    viewModelOf(::TeacherAdminViewModel)
    viewModelOf(::TeacherDashboardViewModel)
    viewModelOf(::TeacherScheduleViewModel)
    viewModelOf(::CommunicationViewModel)
    viewModelOf(::ReportsViewModel)
    viewModelOf(::PreparationViewModel)
    viewModelOf(::ClassLogViewModel)
    viewModelOf(::ClassesViewModel)
    viewModelOf(::TeacherSettingsViewModel)
    viewModelOf(::ParentDashboardViewModel)
    viewModelOf(::ChildrenViewModel)
    viewModelOf(::FinanceViewModel)
    viewModelOf(::ParentSettingsViewModel)
}

private fun createKtorHttpClient(settingsStorage: SettingsStorage): HttpClient {
    return HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        install(Logging) {
            level = LogLevel.BODY
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 300000
            connectTimeoutMillis = 60000
            socketTimeoutMillis = 300000
        }
        install(Auth) {
            bearer {
                loadTokens {
                    settingsStorage.getToken()?.let { BearerTokens(it, "") }
                }
                sendWithoutRequest { request ->
                    // Don't send token for login
                    !request.url.pathSegments.contains("auth/login")
                }
            }
        }
        defaultRequest {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
        }
    }
}