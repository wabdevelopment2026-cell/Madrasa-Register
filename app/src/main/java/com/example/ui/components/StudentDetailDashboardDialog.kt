package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.Student
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun StudentDetailDashboardDialog(
    student: Student,
    teacherName: String,
    teacherDaysPresent: Int,
    attendanceHistory: List<AttendanceRecord>,
    onDismiss: () -> Unit,
    onUpdateMonthlyFee: (monthKey: String, monthTitle: String, isPaid: Boolean, paidDate: String, amount: String) -> Unit,
    onGenerateReportPic: () -> Unit,
    onCallPhone: (context: Context, phone: String) -> Unit,
    onWhatsApp: (context: Context, phone: String) -> Unit
) {
    val context = LocalContext.current

    // Month selector for fee tracking
    val monthOptions = remember {
        val list = mutableListOf<Pair<String, String>>()
        val cal = Calendar.getInstance()
        val keyFormat = SimpleDateFormat("yyyy-MM", Locale.US)
        val titleFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
        for (i in 0..5) {
            val d = cal.time
            list.add(Pair(keyFormat.format(d), titleFormat.format(d)))
            cal.add(Calendar.MONTH, -1)
        }
        list
    }

    var selectedMonthIndex by remember { mutableStateOf(0) }
    val currentMonthKey = monthOptions[selectedMonthIndex].first
    val currentMonthTitle = monthOptions[selectedMonthIndex].second

    // Fee record for current selected month
    val monthFeeRecord = student.monthlyFees[currentMonthKey]
    val isFeePaid = monthFeeRecord?.isPaid ?: (if (selectedMonthIndex == 0) student.isFeePaid else false)
    val feePaidDate = monthFeeRecord?.paidDate ?: (if (selectedMonthIndex == 0) student.feePaidDate else "")
    val feeAmount = monthFeeRecord?.amount?.ifBlank { student.feeAmount } ?: student.feeAmount

    var isEditingFee by remember { mutableStateOf(false) }
    var feeAmountInput by remember { mutableStateOf(feeAmount.ifBlank { "1000" }) }
    var feePaidDateInput by remember {
        mutableStateOf(
            feePaidDate.ifBlank {
                SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            }
        )
    }

    val totalDays = attendanceHistory.size
    val presentDays = attendanceHistory.count { it.status == AttendanceStatus.PRESENT }
    val leaveDays = attendanceHistory.count { it.status == AttendanceStatus.LEAVE }
    val absentDays = attendanceHistory.count { it.status == AttendanceStatus.ABSENT }
    val attendanceRate = if (totalDays > 0) (presentDays * 100) / totalDays else 0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = student.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Walid: ${student.fatherName} • $teacherName",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Row: Call, WhatsApp, and "Generate Report Pic" Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (student.phone.isNotBlank()) {
                        FilledTonalButton(
                            onClick = { onCallPhone(context, student.phone) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { onWhatsApp(context, student.phone) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WhatsApp", fontSize = 12.sp)
                        }
                    }

                    // Button to create Picture anytime!
                    Button(
                        onClick = onGenerateReportPic,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("dialog_generate_report_pic_button")
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Report Pic (تصویر)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                // Monthly Fees Tracking (Kis month ki di thi, kab di thi)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFeePaid) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isFeePaid) Icons.Default.Paid else Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = if (isFeePaid) Color(0xFF16A34A) else Color(0xFFDC2626),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "$currentMonthTitle Fees: ${if (isFeePaid) "PAID (ادا شدہ)" else "PENDING (واجب الادا)"}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (isFeePaid) Color(0xFF16A34A) else Color(0xFFDC2626)
                                    )
                                    if (isFeePaid && feePaidDate.isNotBlank()) {
                                        Text(
                                            text = "Kab di thi: $feePaidDate (Rs. ${feeAmount.ifBlank { "1000" }})",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF15803D),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    } else {
                                        Text(
                                            text = "Month: $currentMonthTitle • Rs. ${feeAmount.ifBlank { "1000" }} due",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFB91C1C)
                                        )
                                    }
                                }
                            }

                            FilledTonalButton(
                                onClick = { isEditingFee = !isEditingFee },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(if (isEditingFee) "Done" else "Update", fontSize = 11.sp)
                            }
                        }

                        // Month selection chips
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(monthOptions.indices.toList()) { idx ->
                                val pair = monthOptions[idx]
                                FilterChip(
                                    selected = idx == selectedMonthIndex,
                                    onClick = { selectedMonthIndex = idx },
                                    label = { Text(pair.second, fontSize = 11.sp) }
                                )
                            }
                        }

                        if (isEditingFee) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = feePaidDateInput,
                                    onValueChange = { feePaidDateInput = it },
                                    label = { Text("Payment Date (Kab di)") },
                                    modifier = Modifier.weight(1.2f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = feeAmountInput,
                                    onValueChange = { feeAmountInput = it },
                                    label = { Text("Amount (Rs)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onUpdateMonthlyFee(
                                            currentMonthKey,
                                            currentMonthTitle,
                                            true,
                                            feePaidDateInput,
                                            feeAmountInput
                                        )
                                        isEditingFee = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Mark Paid (ادا ہو گئی)", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        onUpdateMonthlyFee(
                                            currentMonthKey,
                                            currentMonthTitle,
                                            false,
                                            "",
                                            feeAmountInput
                                        )
                                        isEditingFee = false
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Mark Pending", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Teacher Attendance Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Teacher (قاری صاحب): $teacherName",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "$teacherDaysPresent Days Present",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Daily Attendance History Header & Rate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Attendance History (حاضری ریکارڈ)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$attendanceRate% Hazri",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Attendance stats summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Present: $presentDays",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A),
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Leave: $leaveDays",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706),
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEE2E2),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Absent: $absentDays",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Timeline List
                if (attendanceHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No attendance recorded yet for this student.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(attendanceHistory, key = { "${it.date}_${it.studentId}" }) { record ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${record.date} ${if (record.dayName.isNotBlank()) "(${record.dayName})" else ""}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (record.time.isNotBlank()) {
                                            Text(
                                                text = "Time: ${record.time}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                        if (record.status == AttendanceStatus.LEAVE && record.leaveReason.isNotBlank()) {
                                            Text(
                                                text = "Wajah: ${record.leaveReason}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFD97706)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = when (record.status) {
                                            AttendanceStatus.PRESENT -> Color(0xFFDCFCE7)
                                            AttendanceStatus.ABSENT -> Color(0xFFFEE2E2)
                                            AttendanceStatus.LEAVE -> Color(0xFFFEF3C7)
                                            AttendanceStatus.UNMARKED -> Color(0xFFF3F4F6)
                                        }
                                    ) {
                                        Text(
                                            text = when (record.status) {
                                                AttendanceStatus.PRESENT -> "Present (حاضر)"
                                                AttendanceStatus.ABSENT -> "Absent (غیر حاضر)"
                                                AttendanceStatus.LEAVE -> "Leave (رخصت)"
                                                AttendanceStatus.UNMARKED -> "Unmarked"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = when (record.status) {
                                                AttendanceStatus.PRESENT -> Color(0xFF16A34A)
                                                AttendanceStatus.ABSENT -> Color(0xFFDC2626)
                                                AttendanceStatus.LEAVE -> Color(0xFFD97706)
                                                AttendanceStatus.UNMARKED -> Color(0xFF4B5563)
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
