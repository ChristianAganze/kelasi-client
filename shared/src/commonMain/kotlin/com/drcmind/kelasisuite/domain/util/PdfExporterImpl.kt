package com.drcmind.kelasisuite.domain.util

class PdfExporterImpl : PdfExporter {
    override fun exportToPdf(title: String, content: String, fileName: String): Boolean {
        // En mode multiplateforme (MVP), nous simulons l'export PDF.
        // In multiplatform MVP mode, we simulate PDF export.
        println("Exporting PDF: $fileName")
        println("Title: $title")
        println("Content length: ${content.length}")
        
        // Simule un succès
        // Simulate success
        return true
    }
}
