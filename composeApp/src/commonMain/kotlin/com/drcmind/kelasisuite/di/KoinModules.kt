package com.drcmind.kelasisuite.di

import com.drcmind.kelasisuite.AppViewModel
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorageImpl
import com.drcmind.kelasisuite.data.datasource.remote.auth.AuthAPIService
import com.drcmind.kelasisuite.data.datasource.remote.auth.AuthAPIServiceImpl
import com.drcmind.kelasisuite.data.datasource.remote.profile.ProfileAPIService
import com.drcmind.kelasisuite.data.datasource.remote.profile.ProfileAPIServiceImpl
import com.drcmind.kelasisuite.data.datasource.remote.schools.SchoolsAPIService
import com.drcmind.kelasisuite.data.datasource.remote.schools.SchoolsAPIServiceImpl
import com.drcmind.kelasisuite.data.datasource.remote.students.StudentsAPIService
import com.drcmind.kelasisuite.data.datasource.remote.students.StudentsAPIServiceImpl
import com.drcmind.kelasisuite.data.datasource.remote.teachers.TeachersAPIService
import com.drcmind.kelasisuite.data.datasource.remote.teachers.TeachersAPIServiceImpl
import com.drcmind.kelasisuite.data.datasource.remote.users.UsersAPIService
import com.drcmind.kelasisuite.data.datasource.remote.users.UsersAPIServiceImpl
import com.drcmind.kelasisuite.data.repository.auth.AuthRepository
import com.drcmind.kelasisuite.data.repository.auth.AuthRepositoryImpl
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
import com.drcmind.kelasisuite.ui.schooladmin.academicManagement.AddClassViewModel
import com.drcmind.kelasisuite.ui.schooladmin.academicManagement.ClassesViewModel
import com.drcmind.kelasisuite.ui.schooladmin.dashboard.SchoolDashboardViewModel
import com.drcmind.kelasisuite.ui.schooladmin.profile.ProfileViewModel
import com.drcmind.kelasisuite.ui.schooladmin.students.AddStudentViewModel
import com.drcmind.kelasisuite.ui.schooladmin.students.StudentDetailViewModel
import com.drcmind.kelasisuite.ui.schooladmin.students.StudentsViewModel
import com.drcmind.kelasisuite.ui.schooladmin.teachers.AddTeacherViewModel
import com.drcmind.kelasisuite.ui.schooladmin.teachers.TeacherDetailsViewModel
import com.drcmind.kelasisuite.ui.schooladmin.teachers.TeachersViewModel
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import io.ktor.serialization.kotlinx.json.*
import org.koin.core.module.dsl.viewModelOf

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
    single<AuthAPIService> { AuthAPIServiceImpl(get()) }
    single<StudentsAPIService> { StudentsAPIServiceImpl(get()) }
    single<SchoolsAPIService> { SchoolsAPIServiceImpl(get()) }
    single<ProfileAPIService> { ProfileAPIServiceImpl(get()) }
    single<TeachersAPIService> { TeachersAPIServiceImpl(get()) }
    single<UsersAPIService> { UsersAPIServiceImpl(get()) }
}

val localStorageModule = module {
    single { Settings() }
    single<SettingsStorage> {
        SettingsStorageImpl(get())
    }
}

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<StudentsRepository> { StudentsRepositoryImpl(get()) }
    single<SchoolRepository> { SchoolRepositoryImpl(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    single<TeachersRepository> { TeachersRepositoryImpl(get()) }
    single<UsersRepository> { UsersRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::SchoolDashboardViewModel)
    viewModelOf(::StudentsViewModel)
    viewModelOf(::AddStudentViewModel)
    viewModelOf(::StudentDetailViewModel)
    viewModelOf(::AddClassViewModel)
    viewModelOf(::ClassesViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::TeachersViewModel)
    viewModelOf(::AddTeacherViewModel)
    viewModelOf(::TeacherDetailsViewModel)
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