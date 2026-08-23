package com.drcmind.kelasisuite.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FeeDTO(
    val id: Long,
    val description: String,
    val amountDue: Double,
    val amountPaid: Double,
    val dueDate: String,
    val isFullyPaid: Boolean
)

@Serializable
data class PaymentDTO(
    val id: Long,
    val feeId: Long,
    val amount: Double,
    val paymentDate: String,
    val receiptUrl: String? = null
)
