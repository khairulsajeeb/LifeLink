package com.bmu.lifelink.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.bmu.lifelink.data.models.BloodRequest
import java.io.File
import java.io.FileOutputStream

object RequestCardGenerator {

    /**
     * Generates a 1080x1350 Bitmap card matching the website's canvas card layout.
     * CRITICAL PRIVACY REQUIREMENT:
     * Student ID is NEVER included on the card.
     * Name and phone are included ONLY if includeContact is true and phone is not blank.
     */
    fun createCardBitmap(
        request: BloodRequest,
        includeContact: Boolean
    ): Bitmap {
        val width = 1080
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background gradient (#fff8f3 to #f3ded7)
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                android.graphics.Color.parseColor("#fff8f3"),
                android.graphics.Color.parseColor("#f3ded7"),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Inner rounded red card (#bd3f36, corner radius 42)
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#bd3f36")
        }
        val cardRect = RectF(54f, 54f, width - 54f, height - 54f)
        canvas.drawRoundRect(cardRect, 42f, 42f, cardPaint)

        // 3. Subtle background circles
        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.argb((255 * 0.07).toInt(), 255, 255, 255)
        }
        canvas.drawCircle(width - 80f, 88f, 245f, circlePaint)
        canvas.drawCircle(70f, height - 90f, 195f, circlePaint)

        // 4. Header: LIFELINK · BMU STUDENTS PLATFORM
        val topSubtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#ffe1d8")
            textSize = 28f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        canvas.drawText("LIFELINK  ·  BMU STUDENTS PLATFORM", 112f, 150f, topSubtitlePaint)

        // 5. Title: BLOOD NEEDED
        val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 67f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        canvas.drawText("BLOOD NEEDED", 112f, 285f, headingPaint)

        // 6. Blood type badge
        val badgeCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
        }
        val badgeX = width - 245f
        val badgeY = 420f
        canvas.drawCircle(badgeX, badgeY, 112f, badgeCirclePaint)

        val bloodTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#bd3f36")
            textSize = 76f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val bloodType = if (request.bloodType.isNotBlank()) request.bloodType else "Any"
        canvas.drawText(bloodType, badgeX, badgeY + 27f, bloodTextPaint)

        // 7. UNITS NEEDED
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#f8dcd4")
            textSize = 27f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        canvas.drawText("UNITS NEEDED", 112f, 390f, labelPaint)

        val unitsTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 43f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val unitsVal = if (request.units == "4") "4+ units" else "${request.units} ${if (request.units == "1") "unit" else "units"}"
        canvas.drawText(unitsVal, 112f, 448f, unitsTextPaint)

        // 8. WHEN
        labelPaint.textSize = 25f
        canvas.drawText("WHEN", 112f, 575f, labelPaint)

        val contentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 37f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val urgencyText = if (request.urgency.isNotBlank()) request.urgency else "Contact coordinator"
        drawWrappedText(canvas, urgencyText, 112f, 630f, 850f, 48f, 2, contentPaint)

        // 9. HOSPITAL / LOCATION
        canvas.drawText("HOSPITAL / LOCATION", 112f, 770f, labelPaint)
        contentPaint.textSize = 35f
        val hospitalText = if (request.hospital.isNotBlank()) request.hospital else "Location not specified"
        drawWrappedText(canvas, hospitalText, 112f, 825f, 850f, 45f, 2, contentPaint)

        // 10. CONTACT (Only if opted in and phone is not empty - NEVER student ID!)
        if (includeContact && request.phone.isNotBlank()) {
            canvas.drawText("CONTACT", 112f, 980f, labelPaint)
            contentPaint.textSize = 32f
            val contactLine = if (request.name.isNotBlank()) "${request.name} · ${request.phone}" else request.phone
            drawWrappedText(canvas, contactLine, 112f, 1035f, 850f, 43f, 2, contentPaint)
        }

        // 11. Footer notes
        val footerPaint1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#f8dcd4")
            textSize = 22f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
        canvas.drawText("Please share to help reach a compatible donor.", 112f, 1154f, footerPaint1)

        val footerPaint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#ffe1d8")
            textSize = 19f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
        canvas.drawText("Community coordination only · Contact healthcare services for emergencies", 112f, 1203f, footerPaint2)

        return bitmap
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        maxWidth: Float,
        lineHeight: Float,
        maxLines: Int,
        paint: Paint
    ) {
        val words = text.split(Regex("\\s+"))
        var line = ""
        var lineIndex = 0

        for (word in words) {
            val nextLine = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(nextLine) > maxWidth && line.isNotEmpty()) {
                canvas.drawText(line, x, y + lineIndex * lineHeight, paint)
                line = word
                lineIndex++
                if (lineIndex >= maxLines) break
            } else {
                line = nextLine
            }
        }
        if (lineIndex < maxLines && line.isNotEmpty()) {
            canvas.drawText(line, x, y + lineIndex * lineHeight, paint)
        }
    }

    /**
     * Saves the card bitmap to cache and creates a share intent.
     */
    fun shareRequestCard(
        context: Context,
        request: BloodRequest,
        includeContact: Boolean
    ) {
        val bitmap = createCardBitmap(request, includeContact)
        val imagesFolder = File(context.cacheDir, "images").apply { mkdirs() }
        val cleanBlood = request.bloodType.replace(Regex("[^a-zA-Z0-9]"), "")
        val file = File(imagesFolder, "lifelink-blood-request-$cleanBlood.png")

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "LifeLink Blood Request: ${request.bloodType}")
            putExtra(
                Intent.EXTRA_TEXT,
                "Urgent blood request for ${request.bloodType} at ${request.hospital} (${request.urgency}). LifeLink BMU Community."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Request Card"))
    }
}
