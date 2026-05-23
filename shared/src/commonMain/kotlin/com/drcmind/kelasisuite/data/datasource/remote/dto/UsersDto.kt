package com.drcmind.kelasisuite.data.datasource.remote.dto

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


/* [To be used]
@Serializable
data class UserCreationRequest(
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val schoolId: Long,
    val roleIds: Set<Long>
)

@Serializable
data class UserCreationResponse(
    val user: UserDTO,
    val temporaryPassword: String
)
*/