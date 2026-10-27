package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.Student
import com.example.data.model.Teacher
import com.example.ui.viewmodel.AttendanceViewMode
import com.example.ui.viewmodel.MadrasaViewModel
import com.example.util.AppStrings

@Composable
fun AttendanceScreen(
    viewModel: MadrasaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val viewMode by viewModel.attendanceViewMode.collectAsState()

    val allStudents by viewModel.allStudents.collectAsState()
    val allTeachers by viewModel.allTeachers.collectAsState()
    val attendanceStudents by viewModel.attendanceStudents.collectAsState()
    val searchQuery by viewModel.attendanceSearchQuery.collectAsState()
    val teacherFilter by viewModel.attendanceTeacherFilter.collectAsState()

    val dayName = viewModel.getDayNameForDate(selectedDate)
    val currentTime = viewModel.getCurrentTimeString()
    val studentSummary = viewModel.getAttendanceSummary(selectedDate)
    val teacherSummary = viewModel.getTeacherAttendanceSummary(selectedDate)

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("attendance_screen")
    ) {
        // Mode Selector: Bachay (Students) vs Qari Sahab (Teachers)
        TabRow(
            selectedTabIndex = if (viewMode == AttendanceViewMode.STUDENTS) 0 else 1,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = viewMode == AttendanceViewMode.STUDENTS,
                onClick = { viewModel.setAttendanceViewMode(AttendanceViewMode.STUDENTS) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bachay (Students)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
            Tab(
                selected = viewMode == AttendanceViewMode.TEACHERS,
                onClick = { viewModel.setAttendanceViewMode(AttendanceViewMode.TEACHERS) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Qari Sahab (Teachers)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
        }

        // Date Selector Bar with Calendar Pop-up Trigger
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.goToPreviousDay() },
                        modifier = Modifier.testTag("attendance_prev_day")
                    ) {
                        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Day")
                    }

                    // Tappable Calendar Pop-up Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clickable { viewModel.openDatePicker(context) }
                            .testTag("calendar_popup_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Pick Date from Calendar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$dayName, $selectedDate",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Tap to open Calendar (کیلنڈر پاپ اپ)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.goToNextDay() },
                            modifier = Modifier.testTag("attendance_next_day")
                        ) {
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Day")
                        }

                        FilledTonalButton(
                            onClick = { viewModel.goToToday() },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("attendance_today_button")
                        ) {
                            Icon(imageVector = Icons.Default.Today, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Today", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // Auto-selected Live Time & Auto-Save Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Time: $currentTime • Auto-Saved to device instantly (خود بخود محفوظ)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Summary Stats Row
        val activeSummary = if (viewMode == AttendanceViewMode.STUDENTS) studentSummary else teacherSummary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AttendanceStatPill(
                label = AppStrings.present(language),
                count = activeSummary.present,
                color = Color(0xFF16A34A),
                containerColor = Color(0xFFDCFCE7),
                modifier = Modifier.weight(1f)
            )
            AttendanceStatPill(
                label = AppStrings.absent(language),
                count = activeSummary.absent,
                color = Color(0xFFDC2626),
                containerColor = Color(0xFFFEE2E2),
                modifier = Modifier.weight(1f)
            )
            AttendanceStatPill(
                label = AppStrings.leave(language),
                count = activeSummary.leave,
                color = Color(0xFFD97706),
                containerColor = Color(0xFFFEF3C7),
                modifier = Modifier.weight(1f)
            )
            AttendanceStatPill(
                label = AppStrings.unmarked(language),
                count = activeSummary.unmarked,
                color = Color(0xFF4B5563),
                containerColor = Color(0xFFF3F4F6),
                modifier = Modifier.weight(1f)
            )
        }

        if (viewMode == AttendanceViewMode.STUDENTS) {
            // Student Mode Controls: Search & Teacher Filter & Mark All
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setAttendanceSearchQuery(it) },
                        placeholder = { Text(AppStrings.search(language)) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setAttendanceSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("attendance_search_input")
                    )

                    if (attendanceStudents.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.markAllPresent() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("attendance_mark_all_button")
                        ) {
                            Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mark All Present", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Teacher filter chips
                if (allTeachers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = teacherFilter == null,
                                onClick = { viewModel.setAttendanceTeacherFilter(null) },
                                label = { Text("All Teachers") }
                            )
                        }
                        items(allTeachers) { teacher ->
                            FilterChip(
                                selected = teacherFilter == teacher.id,
                                onClick = {
                                    if (teacherFilter == teacher.id) viewModel.setAttendanceTeacherFilter(null)
                                    else viewModel.setAttendanceTeacherFilter(teacher.id)
                                },
                                label = { Text(teacher.name) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Student Attendance List
            if (allStudents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventBusy,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Students Registered Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.openAddStudentDialog() }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.addStudent(language))
                        }
                    }
                }
            } else if (attendanceStudents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No students matched your search or teacher filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(attendanceStudents, key = { it.id }) { student ->
                        val record = viewModel.getStudentAttendanceRecord(selectedDate, student.id)
                        StudentAttendanceRowCard(
                            student = student,
                            record = record,
                            teacherName = viewModel.getTeacherName(student.teacherId),
                            onOpenDetails = { viewModel.openStudentDetails(student) },
                            onGeneratePic = { viewModel.openReportCard(student) },
                            onSelectPresent = { viewModel.markAttendance(student.id, AttendanceStatus.PRESENT) },
                            onSelectAbsent = { viewModel.markAttendance(student.id, AttendanceStatus.ABSENT) },
                            onSelectLeave = { viewModel.openLeaveReasonDialog(student) }
                        )
                    }
                }
            }
        } else {
            // Teacher Mode Controls
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Qari Sahab Daily Attendance (اساتذہ کی حاضری)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    if (allTeachers.isNotEmpty()) {
                        Button(
                            onClick = { viewModel.markAllTeachersPresent() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mark All Present", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Teachers List
            if (allTeachers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Teachers Registered Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.openAddTeacherDialog() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.addTeacher(language))
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allTeachers, key = { it.id }) { teacher ->
                        val record = viewModel.getTeacherAttendanceRecord(selectedDate, teacher.id)
                        val daysPresent = viewModel.getTeacherDaysPresentInMonth(teacher.id, viewModel.getCurrentMonthKey())
                        TeacherAttendanceRowCard(
                            teacher = teacher,
                            record = record,
                            daysPresentThisMonth = daysPresent,
                            onSelectPresent = { viewModel.markTeacherAttendance(teacher.id, AttendanceStatus.PRESENT) },
                            onSelectAbsent = { viewModel.markTeacherAttendance(teacher.id, AttendanceStatus.ABSENT) },
                            onSelectLeave = { viewModel.openTeacherLeaveDialog(teacher) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentAttendanceRowCard(
    student: Student,
    record: AttendanceRecord?,
    teacherName: String,
    onOpenDetails: () -> Unit,
    onGeneratePic: () -> Unit,
    onSelectPresent: () -> Unit,
    onSelectAbsent: () -> Unit,
    onSelectLeave: () -> Unit
) {
    val status = record?.status ?: AttendanceStatus.UNMARKED

    ElevatedCard(
        onClick = onOpenDetails,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Student info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                when (status) {
                                    AttendanceStatus.PRESENT -> Color(0xFFDCFCE7)
                                    AttendanceStatus.ABSENT -> Color(0xFFFEE2E2)
                                    AttendanceStatus.LEAVE -> Color(0xFFFEF3C7)
                                    AttendanceStatus.UNMARKED -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                    ) {
                        Icon(
                            imageVector = when (status) {
                                AttendanceStatus.PRESENT -> Icons.Default.Check
                                AttendanceStatus.ABSENT -> Icons.Default.Clear
                                AttendanceStatus.LEAVE -> Icons.Default.EventBusy
                                AttendanceStatus.UNMARKED -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = when (status) {
                                AttendanceStatus.PRESENT -> Color(0xFF16A34A)
                                AttendanceStatus.ABSENT -> Color(0xFFDC2626)
                                AttendanceStatus.LEAVE -> Color(0xFFD97706)
                                AttendanceStatus.UNMARKED -> MaterialTheme.colorScheme.outline
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = student.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (student.isFeePaid) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "Fees Paid",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Walid: ${student.fatherName} • $teacherName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Actions: Pic generator + P, A, L
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Quick Pic slip button
                    FilledTonalButton(
                        onClick = onGeneratePic,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "Pic Slip", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Pic", fontSize = 11.sp)
                    }

                    // Present
                    AttendanceButton(
                        label = "P",
                        tooltip = "Present",
                        isSelected = status == AttendanceStatus.PRESENT,
                        selectedColor = Color(0xFF16A34A),
                        selectedTextColor = Color.White,
                        onClick = onSelectPresent
                    )

                    // Absent
                    AttendanceButton(
                        label = "A",
                        tooltip = "Absent",
                        isSelected = status == AttendanceStatus.ABSENT,
                        selectedColor = Color(0xFFDC2626),
                        selectedTextColor = Color.White,
                        onClick = onSelectAbsent
                    )

                    // Leave (opens Leave Reason pop-up)
                    AttendanceButton(
                        label = "L",
                        tooltip = "Leave (رخصت)",
                        isSelected = status == AttendanceStatus.LEAVE,
                        selectedColor = Color(0xFFD97706),
                        selectedTextColor = Color.White,
                        onClick = onSelectLeave
                    )
                }
            }

            // If Leave Reason exists, show it under student row
            if (status == AttendanceStatus.LEAVE && !record?.leaveReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7).copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Wajah (Reason): ${record?.leaveReason}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TeacherAttendanceRowCard(
    teacher: Teacher,
    record: AttendanceRecord?,
    daysPresentThisMonth: Int,
    onSelectPresent: () -> Unit,
    onSelectAbsent: () -> Unit,
    onSelectLeave: () -> Unit
) {
    val status = record?.status ?: AttendanceStatus.UNMARKED

    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Teacher info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                when (status) {
                                    AttendanceStatus.PRESENT -> Color(0xFFDCFCE7)
                                    AttendanceStatus.ABSENT -> Color(0xFFFEE2E2)
                                    AttendanceStatus.LEAVE -> Color(0xFFFEF3C7)
                                    AttendanceStatus.UNMARKED -> MaterialTheme.colorScheme.secondaryContainer
                                }
                            )
                    ) {
                        Icon(
                            imageVector = when (status) {
                                AttendanceStatus.PRESENT -> Icons.Default.Check
                                AttendanceStatus.ABSENT -> Icons.Default.Clear
                                AttendanceStatus.LEAVE -> Icons.Default.EventBusy
                                AttendanceStatus.UNMARKED -> Icons.Default.School
                            },
                            contentDescription = null,
                            tint = when (status) {
                                AttendanceStatus.PRESENT -> Color(0xFF16A34A)
                                AttendanceStatus.ABSENT -> Color(0xFFDC2626)
                                AttendanceStatus.LEAVE -> Color(0xFFD97706)
                                AttendanceStatus.UNMARKED -> MaterialTheme.colorScheme.secondary
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = teacher.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Phone: ${teacher.phone.ifBlank { "N/A" }} • $daysPresentThisMonth Days Present this month",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Actions: P, A, L
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AttendanceButton(
                        label = "P",
                        tooltip = "Present",
                        isSelected = status == AttendanceStatus.PRESENT,
                        selectedColor = Color(0xFF16A34A),
                        selectedTextColor = Color.White,
                        onClick = onSelectPresent
                    )

                    AttendanceButton(
                        label = "A",
                        tooltip = "Absent",
                        isSelected = status == AttendanceStatus.ABSENT,
                        selectedColor = Color(0xFFDC2626),
                        selectedTextColor = Color.White,
                        onClick = onSelectAbsent
                    )

                    AttendanceButton(
                        label = "L",
                        tooltip = "Leave (رخصت)",
                        isSelected = status == AttendanceStatus.LEAVE,
                        selectedColor = Color(0xFFD97706),
                        selectedTextColor = Color.White,
                        onClick = onSelectLeave
                    )
                }
            }

            if (status == AttendanceStatus.LEAVE && !record?.leaveReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7).copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Wajah (Reason): ${record?.leaveReason}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceStatPill(
    label: String,
    count: Int,
    color: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = color
            )
        }
    }
}

@Composable
fun AttendanceButton(
    label: String,
    tooltip: String,
    isSelected: Boolean,
    selectedColor: Color,
    selectedTextColor: Color,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) selectedColor else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.ExtraBold,
            color = if (isSelected) selectedTextColor else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
    }
}
