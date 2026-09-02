package com.drcmind.kelasisuite.domain.model

data class UserInfo(
    val token: String?,
    val username: String?,
    val firstName: String?,
    val lastName: String?,
    val role: String?,
    val userId: Long?,
    val schoolId: Long?
) {
    val displayName: String
        get() = when {
            !firstName.isNullOrBlank() && !lastName.isNullOrBlank() -> "$firstName $lastName"
            !firstName.isNullOrBlank() -> firstName
            !username.isNullOrBlank() -> username
            else -> "Utilisateur"
        }

    val preferredFirstName: String
        get() = when {
            !firstName.isNullOrBlank() -> firstName
            !username.isNullOrBlank() -> username
            else -> "Utilisateur"
        }
}