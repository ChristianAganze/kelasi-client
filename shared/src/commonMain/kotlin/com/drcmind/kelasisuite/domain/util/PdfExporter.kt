package com.drcmind.kelasisuite.domain.util

interface PdfExporter {
    fun exportToPdf(title: String, content: String, fileName: String): Boolean
}
