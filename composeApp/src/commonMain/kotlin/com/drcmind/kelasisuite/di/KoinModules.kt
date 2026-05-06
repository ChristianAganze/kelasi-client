package com.drcmind.kelasisuite.di

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorageImpl
import com.drcmind.kelasisuite.data.datasource.remote.AuthAPIService
import com.drcmind.kelasisuite.data.datasource.remote.AuthAPIServiceImpl
import com.drcmind.kelasisuite.data.repository.AuthRepository
import com.drcmind.kelasisuite.data.repository.AuthRepositoryImpl
import com.drcmind.kelasisuite.domain.util.BASE_URL
import com.drcmind.kelasisuite.ui.auth.AuthViewModel
import com.drcmind.kelasisuite.ui.schooladmin.AcademicManagement.AcademicRepository
import com.drcmind.kelasisuite.ui.schooladmin.AcademicManagement.AddClassViewModel
import com.drcmind.kelasisuite.ui.schooladmin.AcademicManagement.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.ui.schooladmin.Dashboard.SchoolDashboardViewModel
import com.drcmind.kelasisuite.ui.schooladmin.SchoolAdminHostViewModel
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
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
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
    single<AuthAPIService> { AuthAPIServiceImpl(get()) }
}

val localStorageModule = module {
    single { Settings() }
    single<SettingsStorage> {
        SettingsStorageImpl(get())
    }
}

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<AcademicRepository> {
        object : AcademicRepository {
            override suspend fun createClassFromTemplate(request: CreateClassFromTemplateRequest): Any? {
                return null
            }
        }
    }
}

val viewModelModule = module {
    single { AuthViewModel(get()) }
    single { SchoolDashboardViewModel() }
    single { AddClassViewModel(get()) }
    single { SchoolAdminHostViewModel(get()) }
}

private fun createKtorHttpClient(settingsStorage: SettingsStorage) : HttpClient {
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