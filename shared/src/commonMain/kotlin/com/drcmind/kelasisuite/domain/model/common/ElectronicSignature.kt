package com.drcmind.kelasisuite.domain.model.common

data class PathPoint(
    val x: Float,
    val y: Float
)

data class SignatureStroke(
    val points: List<PathPoint>
)

data class ElectronicSignature(
    val signerName: String,
    val signerRole: String,
    val signedAt: String,
    val signatureToken: String, // e.g. "SIG-KELASI-2026-X8F2"
    val strokes: List<SignatureStroke> = emptyList(),
    val isVerified: Boolean = true
)
