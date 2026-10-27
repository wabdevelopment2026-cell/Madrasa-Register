package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppLanguage
import com.example.data.model.AppThemeMode
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.LeaveReasonDialog
import com.example.ui.components.ReportCardPreviewDialog
import com.example.ui.components.StudentDetailDashboardDialog
import com.example.ui.components.StudentFormDialog
import com.example.ui.components.TeacherFormDialog
import com.example.ui.components.TeacherStudentsDialog
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.screens.TeachersScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MadrasaViewModel
import com.example.ui.viewmodel.ScreenTab
import com.example.util.AppStrings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MadrasaViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDark) {
                MadrasaApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MadrasaApp(
    viewModel: MadrasaViewModel
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val language by viewModel.language.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    // Dialog states
    val isTeacherFormOpen by viewModel.isTeacherFormOpen.collectAsState()
    val editingTeacher by viewModel.editingTeacher.collectAsState()

    val isStudentFormOpen by viewModel.isStudentFormOpen.collectAsState()
    val editingStudent by viewModel.editingStudent.collectAsState()

    val viewingTeacherForStudents by viewModel.viewingTeacherForStudents.collectAsState()
    val viewingStudentDetails by viewModel.viewingStudentDetails.collectAsState()
    val reportCardTargetStudent by viewModel.reportCardTargetStudent.collectAsState()
    val leaveTargetStudent by viewModel.leaveTargetStudent.collectAsState()
    val leaveTargetTeacher by viewModel.leaveTargetTeacher.collectAsState()
    val deleteTarget by viewModel.deleteTarget.collectAsState()

    val allTeachers by viewModel.allTeachers.collectAsState()

    // Handle Android system back button
    BackHandler(enabled = currentTab != ScreenTab.HOME) {
        viewModel.setTab(ScreenTab.HOME)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentTab) {
                            ScreenTab.HOME -> "Madrasa Register (مدرسہ رجسٹر)"
                            ScreenTab.TEACHERS -> "Teachers (Qari Sahab)"
                            ScreenTab.STUDENTS -> "Students (Bachay)"
                            ScreenTab.ATTENDANCE -> "Attendance (حاضری)"
                            ScreenTab.SETTINGS -> AppStrings.settings(language)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Quick Language Switcher
                    IconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("top_bar_language_toggle")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (language == AppLanguage.ENGLISH) "اردو" else "EN",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Quick Theme Switcher
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("top_bar_theme_toggle")
                    ) {
                        Icon(
                            imageVector = when (themeMode) {
                                AppThemeMode.DARK -> Icons.Default.DarkMode
                                AppThemeMode.LIGHT -> Icons.Default.LightMode
                                AppThemeMode.SYSTEM -> Icons.Default.DarkMode
                            },
                            contentDescription = "Toggle Theme"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            // Bottom navigation with Home in the CENTER!
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // 1. Teachers
                NavigationBarItem(
                    selected = currentTab == ScreenTab.TEACHERS,
                    onClick = { viewModel.setTab(ScreenTab.TEACHERS) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Teachers",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = { Text("Teachers", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_teachers")
                )

                // 2. Students
                NavigationBarItem(
                    selected = currentTab == ScreenTab.STUDENTS,
                    onClick = { viewModel.setTab(ScreenTab.STUDENTS) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Students",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = { Text("Students", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_students")
                )

                // 3. HOME (Center Button!)
                NavigationBarItem(
                    selected = currentTab == ScreenTab.HOME,
                    onClick = { viewModel.setTab(ScreenTab.HOME) },
                    icon = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (currentTab == ScreenTab.HOME) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primaryContainer
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                tint = if (currentTab == ScreenTab.HOME) Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            "Home",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentTab == ScreenTab.HOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("nav_item_home_center")
                )

                // 4. Attendance
                NavigationBarItem(
                    selected = currentTab == ScreenTab.ATTENDANCE,
                    onClick = { viewModel.setTab(ScreenTab.ATTENDANCE) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = "Attendance",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = { Text("Attendance", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_attendance")
                )

                // 5. Settings
                NavigationBarItem(
                    selected = currentTab == ScreenTab.SETTINGS,
                    onClick = { viewModel.setTab(ScreenTab.SETTINGS) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = { Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_settings")
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (currentTab) {
            ScreenTab.HOME -> HomeScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            ScreenTab.TEACHERS -> TeachersScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            ScreenTab.STUDENTS -> StudentsScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            ScreenTab.ATTENDANCE -> AttendanceScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            ScreenTab.SETTINGS -> SettingsScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
        }
    }

    // Teacher Form Dialog (Add / Edit)
    if (isTeacherFormOpen) {
        TeacherFormDialog(
            initialTeacher = editingTeacher,
            onDismiss = { viewModel.closeTeacherForm() },
            onSave = { name, phone, address ->
                viewModel.saveTeacher(name, phone, address)
            }
        )
    }

    // Student Form Dialog (Add / Edit - Simplified: Name, Father Name, Mobile Number, Fee)
    if (isStudentFormOpen) {
        StudentFormDialog(
            initialStudent = editingStudent,
            teachersList = allTeachers,
            onDismiss = { viewModel.closeStudentForm() },
            onSave = { name, fatherName, phone, teacherId, feeAmount ->
                viewModel.saveStudent(name, fatherName, phone, teacherId, feeAmount)
            }
        )
    }

    // Viewing Teacher's Students Dialog
    viewingTeacherForStudents?.let { teacher ->
        TeacherStudentsDialog(
            teacher = teacher,
            students = viewModel.getStudentsForTeacher(teacher.id),
            onDismiss = { viewModel.closeTeacherStudents() },
            onCallPhone = { ctx, phone -> viewModel.callPhone(ctx, phone) },
            onWhatsApp = { ctx, phone -> viewModel.openWhatsApp(ctx, phone) }
        )
    }

    // Viewing Student Details & Attendance/Fees History Dashboard Dialog
    viewingStudentDetails?.let { student ->
        val teacherDays = viewModel.getTeacherDaysPresentInMonth(student.teacherId, viewModel.getCurrentMonthKey())
        StudentDetailDashboardDialog(
            student = student,
            teacherName = viewModel.getTeacherName(student.teacherId),
            teacherDaysPresent = teacherDays,
            attendanceHistory = viewModel.getStudentAttendanceHistory(student.id),
            onDismiss = { viewModel.closeStudentDetails() },
            onUpdateMonthlyFee = { monthKey, monthTitle, isPaid, paidDate, amount ->
                viewModel.updateStudentMonthlyFee(student.id, monthKey, monthTitle, isPaid, paidDate, amount)
            },
            onGenerateReportPic = { viewModel.openReportCard(student) },
            onCallPhone = { ctx, phone -> viewModel.callPhone(ctx, phone) },
            onWhatsApp = { ctx, phone -> viewModel.openWhatsApp(ctx, phone) }
        )
    }

    // Report Card Picture Generator & Share Dialog (can be opened anytime mid-month or month-end!)
    reportCardTargetStudent?.let { student ->
        val teacherDays = viewModel.getTeacherDaysPresentInMonth(student.teacherId, viewModel.getCurrentMonthKey())
        ReportCardPreviewDialog(
            student = student,
            teacherName = viewModel.getTeacherName(student.teacherId),
            attendanceHistory = viewModel.getStudentAttendanceHistory(student.id),
            teacherDaysPresent = teacherDays,
            onDismiss = { viewModel.closeReportCard() },
            onShareImage = { reportData ->
                viewModel.shareReportCard(context, reportData)
            }
        )
    }

    // Student Leave Reason Dialog (auto-saved upon confirmation)
    leaveTargetStudent?.let { student ->
        LeaveReasonDialog(
            studentName = student.name,
            onDismiss = { viewModel.closeLeaveReasonDialog() },
            onConfirm = { reason ->
                viewModel.confirmLeave(reason)
            }
        )
    }

    // Teacher Leave Reason Dialog (auto-saved upon confirmation)
    leaveTargetTeacher?.let { teacher ->
        LeaveReasonDialog(
            studentName = teacher.name,
            onDismiss = { viewModel.closeTeacherLeaveDialog() },
            onConfirm = { reason ->
                viewModel.confirmTeacherLeave(reason)
            }
        )
    }

    // Deletion Confirmation Dialog
    deleteTarget?.let { target ->
        ConfirmationDialog(
            target = target,
            onConfirm = { viewModel.executeDelete() },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }
}
