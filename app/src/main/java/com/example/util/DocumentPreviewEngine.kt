package com.example.util

import com.example.data.model.PrintableFile

object DocumentPreviewEngine {

    val samplePrintableFiles = emptyList<PrintableFile>()

    fun getPagePreviewLines(file: PrintableFile, pageIndex: Int): List<String> {
        val text = file.sampleTextContent ?: "Sample printable document content line for page ${pageIndex + 1}."
        val lines = text.split("\n")
        val linesPerPage = 12
        val startIndex = pageIndex * linesPerPage
        if (startIndex >= lines.size) return listOf("Page ${pageIndex + 1} - End of Document")
        val endIndex = (startIndex + linesPerPage).coerceAtMost(lines.size)
        return lines.subList(startIndex, endIndex)
    }
}
