package com.drcmind.kelasisuite.di

import com.drcmind.kelasisuite.AppViewModel
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorageImpl
import com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin.SchoolAdminApiService
import com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin.SchoolAdminApiServiceImpl
import com.drcmind.kelasisuite.data.datasource.remote.SystemApiService
import com.drcmind.kelasisuite.data.datasource.remote.SystemApiServiceImpl
import com.drcmind.kelasisuite.data.repository.auth.AuthRepository
import com.drcmind.kelasisuite.data.repository.auth.AuthRepositoryImpl
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
import com.drcmind.kelasisuite.ui.schooladmin.profile.ProfileViewModel
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teachers.TeachersViewModel
import com.drcmind.kelasisuite.ui.schooladmin.academics.student_enrollment.student.StudentsViewModel
import com.drcmind.kelasisuite.ui.schooladmin.pedagogy.teacher_assignments.teaching_assignment.TeachingAssignmentViewModel
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
    single<SystemApiService> { SystemApiServiceImpl(get()) }
    single<SchoolAdminApiService> { SchoolAdminApiServiceImpl(get()) }
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
}

private fun createKtorHttpClient(settingsStorage: SettingsStorage): HttpClient {
    return HttpClient {
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