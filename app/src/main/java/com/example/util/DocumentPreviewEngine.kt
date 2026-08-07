package com.example.util

import com.example.data.model.PrintableFile

object DocumentPreviewEngine {

    val samplePrintableFiles = listOf(
        PrintableFile(
            name = "Mobile_Gallery_Photo_4x6.jpg",
            type = "JPG",
            uriString = "android.resource://com.example/drawable/ic_launcher_background",
            sizeBytes = 1420 * 1024L,
            pagesCount = 1,
            isSample = true,
            sampleTextContent = "HD Color Photo Print • 4x6 inch Glossy Photo Paper\n300 DPI Resolution • Full Bleed Borderless Print"
        ),
        PrintableFile(
            name = "Gallery_Portrait_Photo.png",
            type = "PNG",
            uriString = "android.resource://com.example/drawable/ic_launcher_foreground",
            sizeBytes = 2100 * 1024L,
            pagesCount = 1,
            isSample = true,
            sampleTextContent = "High Definition Portrait Photo • A4 Photo Glossy\nNatural Tone Color Profile • Maximum Quality"
        ),
        PrintableFile(
            name = "Print_Invoice_Receipt.pdf",
            type = "PDF",
            uriString = null,
            sizeBytes = 450 * 1024L,
            pagesCount = 2,
            isSample = true,
            sampleTextContent = "TAX INVOICE & RECEIPT\nDate: August 07, 2026\nItem: Smart Wi-Fi Printer Order\nStatus: Paid via UPI/Card\nThank you for printing with Wi-Fi AirPrint!"
        )
    )

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
