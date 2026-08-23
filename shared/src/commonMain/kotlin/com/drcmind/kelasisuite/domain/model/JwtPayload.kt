package com.drcmind.kelasisuite.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class JwtPayload(
    val exp: Long? = null,
    val roles : List<String> = listOf(),
    val username: String? = null
)
