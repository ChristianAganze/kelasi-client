package com.drcmind.kelasisuite.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class JwtPayload(
    val exp: Long? = null,
    val roles : List<String> = listOf(),
    val username: String? = null
)