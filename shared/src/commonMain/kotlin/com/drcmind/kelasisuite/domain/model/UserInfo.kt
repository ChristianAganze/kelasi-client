package com.drcmind.kelasisuite.domain.model

data class UserInfo(
    val token: String?,
    val username: String?,
    val role: String?,
    val userId: Long?,
    val schoolId: Long?
)