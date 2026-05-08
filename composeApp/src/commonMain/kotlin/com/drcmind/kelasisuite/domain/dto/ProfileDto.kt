package com.drcmind.kelasisuite.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phone: String?,
    val isActive: Boolean,
    val schoolId: Long?,
    val roles: Set<String>
)
