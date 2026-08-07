package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.print.pdf.PrintedPdfDocument
import android.util.Log
import com.example.data.model.PrintSettings
import com.example.data.model.PrintableFile
import java.io.FileOutputStream
import java.io.InputStream

object AndroidSystemPrintEngine {

    fun printViaSystemSpooler(
        context: Context,
        file: PrintableFile,
        settings: PrintSettings
    ): Boolean {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        if (printManager == null) {
            Log.e("SystemPrintEngine", "PrintManager service not found on this device.")
            return false
        }

        val cleanName = file.name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val jobName = "Mobile_Print_$cleanName"

        val printAttributes = PrintAttributes.Builder().apply {
            val mediaSize = when (settings.paperSize) {
                "Letter" -> PrintAttributes.MediaSize.NA_LETTER
                "Legal" -> PrintAttributes.MediaSize.NA_LEGAL
                "A5" -> PrintAttributes.MediaSize.ISO_A5
                "4x6 Photo" -> PrintAttributes.MediaSize.NA_INDEX_4X6
                else -> PrintAttributes.MediaSize.ISO_A4
            }

            setMediaSize(
                if (settings.orientation == "Landscape") mediaSize.asLandscape()
                else mediaSize.asPortrait()
            )

            setColorMode(
                if (settings.colorMode == "Black & White") PrintAttributes.COLOR_MODE_MONOCHROME
                else PrintAttributes.COLOR_MODE_COLOR
            )

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val duplex = when (settings.duplexMode) {
                    "Duplex Long Edge" -> PrintAttributes.DUPLEX_MODE_LONG_EDGE
                    "Duplex Short Edge" -> PrintAttributes.DUPLEX_MODE_SHORT_EDGE
                    else -> PrintAttributes.DUPLEX_MODE_NONE
                }
                setDuplexMode(duplex)
            }
        }.build()

        val adapter = object : PrintDocumentAdapter() {
            private var pdfDocument: PrintedPdfDocument? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback,
                extras: Bundle?
            ) {
                pdfDocument = PrintedPdfDocument(context, newAttributes)

                if (cancellationSignal?.isCanceled == true) {
                    callback.onLayoutCancelled()
                    return
                }

                val info = PrintDocumentInfo.Builder("${file.name}.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(file.pagesCount.coerceAtLeast(1))
                    .build()

                callback.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback
            ) {
                val pdf = pdfDocument ?: run {
                    callback.onWriteFailed("PrintedPdfDocument initialization failed.")
                    return
                }

                try {
                    val totalPages = file.pagesCount.coerceAtLeast(1)
                    for (i in 0 until totalPages) {
                        if (cancellationSignal?.isCanceled == true) {
                            callback.onWriteCancelled()
                            return
                        }

                        val page = pdf.startPage(i)
                        val canvas = page.canvas

                        drawPageContent(context, canvas, file, settings, i)

                        pdf.finishPage(page)
                    }

                    pdf.writeTo(FileOutputStream(destination.fileDescriptor))
                    callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    Log.e("SystemPrintEngine", "Write failed in system print adapter", e)
                    callback.onWriteFailed(e.localizedMessage)
                } finally {
                    pdf.close()
                    pdfDocument = null
                }
            }
        }

        printManager.print(jobName, adapter, printAttributes)
        return true
    }

    private fun drawPageContent(
        context: Context,
        canvas: Canvas,
        file: PrintableFile,
        settings: PrintSettings,
        pageIndex: Int
    ) {
        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()

        canvas.drawColor(Color.WHITE)

        val paint = Paint().apply { isAntiAlias = true }

        var bitmap: Bitmap? = null
        val uriString = file.uriString

        if (!uriString.isNull_or_empty_or_blank()) {
            try {
                val uri = Uri.parse(uriString)
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                }
            } catch (e: Exception) {
                Log.e("SystemPrintEngine", "Could not open Uri bitmap for $uriString", e)
            }
        }

        if (bitmap != null) {
            val margin = 16f
            val destRect = RectF(margin, margin, width - margin, height - margin)

            val srcW = bitmap.width.toFloat()
            val srcH = bitmap.height.toFloat()
            val srcRatio = srcW / srcH
            val destRatio = destRect.width() / destRect.height()

            val drawRect = RectF()
            if (settings.fitToPage == "Fill Page") {
                drawRect.set(destRect)
            } else {
                if (srcRatio > destRatio) {
                    val drawH = destRect.width() / srcRatio
                    val top = destRect.centerY() - (drawH / 2f)
                    drawRect.set(destRect.left, top, destRect.right, top + drawH)
                } else {
                    val drawW = destRect.height() * srcRatio
                    val left = destRect.centerX() - (drawW / 2f)
                    drawRect.set(left, destRect.top, left + drawW, destRect.bottom)
                }
            }

            if (settings.colorMode == "Black & White") {
                val matrix = ColorMatrix()
                matrix.setSaturation(0f)
                paint.colorFilter = ColorMatrixColorFilter(matrix)
            }

            canvas.drawBitmap(bitmap, null, drawRect, paint)
        } else {
            val titlePaint = Paint().apply {
                isAntiAlias = true
                color = Color.BLACK
                textSize = 24f
                isFakeBoldText = true
            }

            val textPaint = Paint().apply {
                isAntiAlias = true
                color = if (settings.colorMode == "Black & White") Color.BLACK else Color.DKGRAY
                textSize = 16f
            }

            var currentY = 50f
            canvas.drawText(file.name, 40f, currentY, titlePaint)
            currentY += 35f

            paint.color = Color.LTGRAY
            paint.strokeWidth = 2f
            canvas.drawLine(40f, currentY, width - 40f, currentY, paint)
            currentY += 30f

            val lines = DocumentPreviewEngine.getPagePreviewLines(file, pageIndex)
            lines.forEach { line ->
                canvas.drawText(line, 40f, currentY, textPaint)
                currentY += 26f
            }
        }
    }

    private fun String?.isNull_or_empty_or_blank(): Boolean {
        return this.isNullOrBlank() || this == "null"
    }
}
