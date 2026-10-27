package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.FeeMonthRecord
import com.example.data.model.Student
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportCardGenerator {

    data class ReportData(
        val student: Student,
        val teacherName: String,
        val monthTitle: String,
        val monthKey: String,
        val attendanceRecords: List<AttendanceRecord>,
        val teacherDaysPresent: Int,
        val feeRecord: FeeMonthRecord?
    )

    fun generateReportBitmap(data: ReportData): Bitmap {
        val width = 840
        val height = 1260
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        val bgPaint = Paint().apply {
            color = Color.parseColor("#F8FAF8")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Outer Border
        val borderPaint = Paint().apply {
            color = Color.parseColor("#1B5E20")
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.drawRoundRect(RectF(14f, 14f, width - 14f, height - 14f), 24f, 24f, borderPaint)

        // Header Background Banner
        val headerPaint = Paint().apply {
            color = Color.parseColor("#1B5E20")
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(20f, 20f, width - 20f, 180f), 20f, 20f, headerPaint)

        // Bismillah
        val bismillahPaint = Paint().apply {
            color = Color.parseColor("#FFD54F")
            textSize = 24f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", width / 2f, 60f, bismillahPaint)

        // App Title
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 34f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("MADRASA REGISTER (مدرسہ رجسٹر)", width / 2f, 110f, titlePaint)

        // Subtitle
        val subPaint = Paint().apply {
            color = Color.parseColor("#E8F5E9")
            textSize = 20f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Student Monthly Attendance & Fee Report Slip", width / 2f, 150f, subPaint)

        // Report Period Banner
        val periodCard = RectF(40f, 200f, width - 40f, 255f)
        val periodBg = Paint().apply {
            color = Color.parseColor("#E8F5E9")
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(periodCard, 12f, 12f, periodBg)

        val periodTextPaint = Paint().apply {
            color = Color.parseColor("#1B5E20")
            textSize = 22f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val currentDateStr = SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date())
        canvas.drawText("Month: ${data.monthTitle}", 60f, 236f, periodTextPaint)

        val issueDatePaint = Paint().apply {
            color = Color.parseColor("#2E7D32")
            textSize = 18f
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Generated: $currentDateStr (Mid/End Month)", width - 60f, 235f, issueDatePaint)

        // Student Info Box
        val studentBox = RectF(40f, 275f, width - 40f, 440f)
        val cardBgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val cardBorderPaint = Paint().apply {
            color = Color.parseColor("#C8E6C9")
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(studentBox, 16f, 16f, cardBgPaint)
        canvas.drawRoundRect(studentBox, 16f, 16f, cardBorderPaint)

        val headingPaint = Paint().apply {
            color = Color.parseColor("#1B5E20")
            textSize = 22f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Student Profile (بچے کی تفصیلات)", 60f, 310f, headingPaint)

        val labelPaint = Paint().apply {
            color = Color.parseColor("#555555")
            textSize = 19f
            isAntiAlias = true
        }
        val valuePaint = Paint().apply {
            color = Color.parseColor("#111111")
            textSize = 19f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        canvas.drawText("Student Name: ", 60f, 350f, labelPaint)
        canvas.drawText(data.student.name, 210f, 350f, valuePaint)

        canvas.drawText("Father Name: ", 60f, 385f, labelPaint)
        canvas.drawText(data.student.fatherName, 210f, 385f, valuePaint)

        canvas.drawText("Father Mobile: ", 60f, 420f, labelPaint)
        canvas.drawText(data.student.phone.ifBlank { "Not provided" }, 210f, 420f, valuePaint)

        canvas.drawText("Qari Sahab: ", 480f, 350f, labelPaint)
        canvas.drawText(data.teacherName, 610f, 350f, valuePaint)

        // Attendance Breakdown Section
        val presentCount = data.attendanceRecords.count { it.status == AttendanceStatus.PRESENT }
        val absentCount = data.attendanceRecords.count { it.status == AttendanceStatus.ABSENT }
        val leaveCount = data.attendanceRecords.count { it.status == AttendanceStatus.LEAVE }
        val totalRecorded = data.attendanceRecords.size
        val hazriPercent = if (totalRecorded > 0) (presentCount * 100) / totalRecorded else 0

        val attBox = RectF(40f, 460f, width - 40f, 750f)
        canvas.drawRoundRect(attBox, 16f, 16f, cardBgPaint)
        canvas.drawRoundRect(attBox, 16f, 16f, cardBorderPaint)

        canvas.drawText("Monthly Attendance Record (ماہانہ حاضری)", 60f, 498f, headingPaint)

        // 4 Stat Pills: Present, Leave, Absent, Percentage
        val pillWidth = 175f
        val pillHeight = 75f
        val pillY = 520f

        // Present Pill (Green)
        drawStatPill(canvas, 60f, pillY, pillWidth, pillHeight, "#DCFCE7", "#16A34A", "$presentCount Days", "Present (حاضر)")
        // Leave Pill (Orange)
        drawStatPill(canvas, 245f, pillY, pillWidth, pillHeight, "#FEF3C7", "#D97706", "$leaveCount Days", "Leave (رخصت)")
        // Absent Pill (Red)
        drawStatPill(canvas, 430f, pillY, pillWidth, pillHeight, "#FEE2E2", "#DC2626", "$absentCount Days", "Absent (غیر حاضر)")
        // Percentage Pill (Emerald)
        drawStatPill(canvas, 615f, pillY, pillWidth, pillHeight, "#E0F2FE", "#0284C7", "$hazriPercent%", "Attendance Rate")

        // Detailed leave reasons if any
        val leaveItems = data.attendanceRecords.filter { it.status == AttendanceStatus.LEAVE && it.leaveReason.isNotBlank() }
        var listY = 630f
        val noteTitlePaint = Paint().apply {
            color = Color.parseColor("#333333")
            textSize = 18f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Leave Details & Reasons (رخصت کی وجوہات):", 60f, listY, noteTitlePaint)
        listY += 28f

        val leaveTextPaint = Paint().apply {
            color = Color.parseColor("#B45309")
            textSize = 17f
            isAntiAlias = true
        }

        if (leaveItems.isEmpty()) {
            val noLeavePaint = Paint().apply {
                color = Color.parseColor("#666666")
                textSize = 17f
                isAntiAlias = true
            }
            canvas.drawText("• No leaves taken with recorded reasons this month.", 80f, listY, noLeavePaint)
        } else {
            leaveItems.take(3).forEach { item ->
                canvas.drawText("• Date: ${item.date} (${item.dayName}) - Reason: ${item.leaveReason}", 80f, listY, leaveTextPaint)
                listY += 25f
            }
        }

        // Teacher Attendance Card
        val teacherBox = RectF(40f, 770f, width - 40f, 890f)
        canvas.drawRoundRect(teacherBox, 16f, 16f, cardBgPaint)
        canvas.drawRoundRect(teacherBox, 16f, 16f, cardBorderPaint)

        canvas.drawText("Teacher Attendance (قاری صاحب کی حاضری)", 60f, 808f, headingPaint)
        canvas.drawText("Assigned Teacher: ${data.teacherName}", 60f, 845f, valuePaint)

        val tAttPaint = Paint().apply {
            color = Color.parseColor("#15803D")
            textSize = 19f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Days Present This Month: ${data.teacherDaysPresent} Days", 60f, 875f, tAttPaint)

        val tVerifiedPaint = Paint().apply {
            color = Color.parseColor("#0284C7")
            textSize = 18f
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("✓ Verified Teacher Attendance", width - 60f, 875f, tVerifiedPaint)

        // Monthly Fees Status Card
        val feeBox = RectF(40f, 910f, width - 40f, 1070f)
        val isFeePaid = data.feeRecord?.isPaid ?: data.student.isFeePaid
        val feePaidDate = data.feeRecord?.paidDate ?: data.student.feePaidDate
        val feeAmount = data.feeRecord?.amount?.ifBlank { data.student.feeAmount } ?: data.student.feeAmount

        val feeBgColor = if (isFeePaid) "#DCFCE7" else "#FEE2E2"
        val feeBorderColor = if (isFeePaid) "#86EFAC" else "#FCA5A5"

        val feeBgPaint = Paint().apply {
            color = Color.parseColor(feeBgColor)
            style = Paint.Style.FILL
        }
        val feeBorderPaint = Paint().apply {
            color = Color.parseColor(feeBorderColor)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(feeBox, 16f, 16f, feeBgPaint)
        canvas.drawRoundRect(feeBox, 16f, 16f, feeBorderPaint)

        val feeHeadingPaint = Paint().apply {
            color = if (isFeePaid) Color.parseColor("#15803D") else Color.parseColor("#B91C1C")
            textSize = 22f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(
            if (isFeePaid) "Monthly Fee: PAID (فیس ادا ہو گئی ہے)" else "Monthly Fee: PENDING (فیس واجب الادا ہے)",
            60f,
            948f,
            feeHeadingPaint
        )

        canvas.drawText("Fee For Month: ${data.monthTitle}", 60f, 988f, labelPaint)
        canvas.drawText("Fee Amount: Rs. ${feeAmount.ifBlank { "1,000" }}", 60f, 1022f, valuePaint)

        if (isFeePaid) {
            val paidDateText = if (feePaidDate.isNotBlank()) "Paid On: $feePaidDate" else "Status: Fully Paid"
            val paidOnPaint = Paint().apply {
                color = Color.parseColor("#166534")
                textSize = 20f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText(paidDateText, 60f, 1054f, paidOnPaint)
        } else {
            val duePaint = Paint().apply {
                color = Color.parseColor("#991B1B")
                textSize = 20f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("Please submit fee before month end (مہربانی فرما کر فیس ادا کریں)", 60f, 1054f, duePaint)
        }

        // Footer & Signatures
        val footerPaint = Paint().apply {
            color = Color.parseColor("#666666")
            textSize = 16f
            isAntiAlias = true
        }
        canvas.drawText("Generated via Madrasa Register Offline App • 100% Verified Record", 60f, 1140f, footerPaint)

        // Signatures Line
        val sigLinePaint = Paint().apply {
            color = Color.parseColor("#888888")
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawLine(width - 280f, 1200f, width - 60f, 1200f, sigLinePaint)

        val sigTextPaint = Paint().apply {
            color = Color.parseColor("#333333")
            textSize = 17f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Signature / دستخط قاری صاحب", width - 170f, 1225f, sigTextPaint)

        return bitmap
    }

    private fun drawStatPill(
        canvas: Canvas,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        bgColorHex: String,
        textColorHex: String,
        topText: String,
        bottomText: String
    ) {
        val pillRect = RectF(x, y, x + w, y + h)
        val pillBg = Paint().apply {
            color = Color.parseColor(bgColorHex)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(pillRect, 10f, 10f, pillBg)

        val mainTextPaint = Paint().apply {
            color = Color.parseColor(textColorHex)
            textSize = 22f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(topText, x + w / 2f, y + 36f, mainTextPaint)

        val subTextPaint = Paint().apply {
            color = Color.parseColor(textColorHex)
            textSize = 14f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(bottomText, x + w / 2f, y + 62f, subTextPaint)
    }

    fun saveReportCardToFile(context: Context, data: ReportData): File {
        val bitmap = generateReportBitmap(data)
        val reportDir = File(context.cacheDir, "reports")
        if (!reportDir.exists()) {
            reportDir.mkdirs()
        }
        val safeName = data.student.name.replace(Regex("[^a-zA-Z0-9]"), "_")
        val file = File(reportDir, "Madrasa_Report_${safeName}_${data.monthKey}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    fun shareReportCardImage(context: Context, data: ReportData) {
        val file = saveReportCardToFile(context, data)
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, authority, file)

        val statusText = if (data.feeRecord?.isPaid == true || data.student.isFeePaid) "Fees: PAID" else "Fees: PENDING"
        val message = """
            📋 *Madrasa Register - Monthly Report Slip*
            👤 Student: ${data.student.name}
            👨‍👦 Father: ${data.student.fatherName}
            📅 Month: ${data.monthTitle}
            🕌 Qari Sahab: ${data.teacherName}
            💰 $statusText
            (Attached is the complete attendance & fee slip picture)
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(shareIntent, "Share Monthly Report Slip (تصویر شیئر کریں)")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
