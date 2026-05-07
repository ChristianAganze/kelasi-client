package com.drcmind.kelasisuite.domain.dto

import kotlinx.serialization.Serializable

//CREATE
@Serializable
data class CreateStudentRequest(val username: String, val password: String)

@Serializable
data class CreateStudentResponse(val username: String, val password: String)

//READ
@Serializable
data class GetStudentRequest(val username: String, val password: String)

@Serializable
data class GetStudentResponse(
    val token: String, val username: String, val roles: List<String>
)