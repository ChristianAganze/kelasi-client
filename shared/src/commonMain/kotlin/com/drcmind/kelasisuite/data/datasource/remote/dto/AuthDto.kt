package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(
    val token: String, val username: String, val roles: List<String>
)

@Serializable
data class UserResponseDTO(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val roles: List<String>,
    val schoolId: Long? = null
)