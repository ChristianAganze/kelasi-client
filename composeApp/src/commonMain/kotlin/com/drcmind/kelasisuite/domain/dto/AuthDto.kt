package com.drcmind.kelasisuite.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(
    val token: String, val username: String, val roles: List<String>
)