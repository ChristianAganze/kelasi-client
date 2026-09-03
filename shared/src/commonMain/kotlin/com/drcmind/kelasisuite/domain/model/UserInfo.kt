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
            !username.isNullOrBlank() -> formatUsername(username)
            else -> "Utilisateur"
        }

    val preferredFirstName: String
        get() = when {
            !firstName.isNullOrBlank() -> firstName
            !username.isNullOrBlank() -> formatFirstName(username)
            else -> "Professeur"
        }

    private fun formatFirstName(u: String): String {
        return if (u.contains("@")) {
            val namePart = u.substringBefore("@")
            val firstSegment = namePart.split(".", "_", "-").firstOrNull { it.isNotBlank() } ?: namePart
            firstSegment.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } else {
            val firstSegment = u.split(".", "_", "-", " ").firstOrNull { it.isNotBlank() } ?: u
            firstSegment.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    private fun formatUsername(u: String): String {
        return if (u.contains("@")) {
            val namePart = u.substringBefore("@")
            namePart.split(".", "_", "-")
                .filter { it.isNotBlank() }
                .joinToString(" ") { part ->
                    part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
        } else {
            u.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}
