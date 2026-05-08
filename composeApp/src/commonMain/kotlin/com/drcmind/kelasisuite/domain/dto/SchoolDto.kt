package com.drcmind.kelasisuite.domain.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class SchoolDTO(
    val id: Long,
    val officialName: String,
    val address: Address,
    val educationalProvince: String,
    val supervisoryAuthority: String,
    val legalStatus: String,
    val phone: String?,
    val email: String?,
    val legalRepresentativeName: String,
    val isActive: Boolean,
    val accreditationNumber: String,
    val approvalNumber: String?,
    val approvalDate: LocalDate?,
    val approvalUrl: String?,
    val operatingAuthorizationNumber: String?,
    val operatingAuthorizationDate: LocalDate?,
    val operatingAuthorizationUrl: String?,
    val nationalIdentificationNumber: String?,
    val othersImportantsDocsUrl: String?
)

@Serializable
data class Address(
    val province: String,
    val cityTerritory: String,
    val municipality: String? = null,
    val neighborhood: String? = null,
    val streetAndNumber: String? = null
)