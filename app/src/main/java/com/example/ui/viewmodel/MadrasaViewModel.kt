package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppLanguage
import com.example.data.model.AppThemeMode
import com.example.data.model.AttendanceStatus
import com.example.data.model.Student
import com.example.data.model.Teacher
import com.example.data.repository.MadrasaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class ScreenTab {
    TEACHERS,
    STUDENTS,
    HOME,       // In the Center!
    ATTENDANCE,
    SETTINGS
}

data class AttendanceSummary(
    val total: Int,
    val present: Int,
    val absent: Int,
    val leave: Int,
    val unmarked: Int
)

sealed class DeleteTarget {
    data class TeacherTarget(val teacher: Teacher, val studentCount: Int) : DeleteTarget()
    data class StudentTarget(val student: Student) : DeleteTarget()
}

class MadrasaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MadrasaRepository(application.applicationContext)

    val allTeachers: StateFlow<List<Teacher>> = repository.teachers
    val allStudents: StateFlow<List<Student>> = repository.students
    val attendanceMap = repository.attendanceMap
    val themeMode: StateFlow<AppThemeMode> = repository.themeMode
    val language: StateFlow<AppLanguage> = repository.language

    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _teacherSearchQuery = MutableStateFlow("")
    val teacherSearchQuery: StateFlow<String> = _teacherSearchQuery.asStateFlow()

    private val _studentSearchQuery = MutableStateFlow("")
    val studentSearchQuery: StateFlow<String> = _studentSearchQuery.asStateFlow()

    private val _attendanceSearchQuery = MutableStateFlow("")
    val attendanceSearchQuery: StateFlow<String> = _attendanceSearchQuery.asStateFlow()

    private val _attendanceTeacherFilter = MutableStateFlow<String?>(null)
    val attendanceTeacherFilter: StateFlow<String?> = _attendanceTeacherFilter.asStateFlow()

    // Date for attendance (YYYY-MM-DD)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val _selectedDate = MutableStateFlow(dateFormat.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Filtered teachers
    val filteredTeachers: StateFlow<List<Teacher>> = combine(allTeachers, _teacherSearchQuery) { teachers, query ->
        if (query.isBlank()) teachers
        else teachers.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.phone.contains(query, ignoreCase = true) ||
            it.address.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered students
    val filteredStudents: StateFlow<List<Student>> = combine(allStudents, _studentSearchQuery) { students, query ->
        if (query.isBlank()) students
        else students.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.fatherName.contains(query, ignoreCase = true) ||
            it.phone.contains(query, ignoreCase = true) ||
            it.address.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered students for Attendance screen
    val attendanceStudents: StateFlow<List<Student>> = combine(allStudents, _attendanceSearchQuery, _attendanceTeacherFilter) { students, query, teacherId ->
        var list = students
        if (!teacherId.isNullOrBlank()) {
            list = list.filter { it.teacherId == teacherId }
        }
        if (query.isNotBlank()) {
            list = list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.fatherName.contains(query, ignoreCase = true)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dialog States
    private val _isTeacherFormOpen = MutableStateFlow(false)
    val isTeacherFormOpen: StateFlow<Boolean> = _isTeacherFormOpen.asStateFlow()

    private val _editingTeacher = MutableStateFlow<Teacher?>(null)
    val editingTeacher: StateFlow<Teacher?> = _editingTeacher.asStateFlow()

    private val _isStudentFormOpen = MutableStateFlow(false)
    val isStudentFormOpen: StateFlow<Boolean> = _isStudentFormOpen.asStateFlow()

    private val _editingStudent = MutableStateFlow<Student?>(null)
    val editingStudent: StateFlow<Student?> = _editingStudent.asStateFlow()

    private val _viewingTeacherForStudents = MutableStateFlow<Teacher?>(null)
    val viewingTeacherForStudents: StateFlow<Teacher?> = _viewingTeacherForStudents.asStateFlow()

    private val _deleteTarget = MutableStateFlow<DeleteTarget?>(null)
    val deleteTarget: StateFlow<DeleteTarget?> = _deleteTarget.asStateFlow()

    fun setTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun setTeacherSearchQuery(query: String) {
        _teacherSearchQuery.value = query
    }

    fun setStudentSearchQuery(query: String) {
        _studentSearchQuery.value = query
    }

    fun setAttendanceSearchQuery(query: String) {
        _attendanceSearchQuery.value = query
    }

    fun setAttendanceTeacherFilter(teacherId: String?) {
        _attendanceTeacherFilter.value = teacherId
    }

    // Teacher CRUD
    fun openAddTeacherDialog() {
        _editingTeacher.value = null
        _isTeacherFormOpen.value = true
    }

    fun openEditTeacherDialog(teacher: Teacher) {
        _editingTeacher.value = teacher
        _isTeacherFormOpen.value = true
    }

    fun closeTeacherForm() {
        _isTeacherFormOpen.value = false
        _editingTeacher.value = null
    }

    fun saveTeacher(name: String, phone: String, address: String) {
        val current = _editingTeacher.value
        if (current == null) {
            val newTeacher = Teacher(name = name.trim(), phone = phone.trim(), address = address.trim())
            repository.addTeacher(newTeacher)
        } else {
            val updated = current.copy(name = name.trim(), phone = phone.trim(), address = address.trim())
            repository.updateTeacher(updated)
        }
        closeTeacherForm()
    }

    fun confirmDeleteTeacher(teacher: Teacher) {
        val count = repository.getStudentsForTeacher(teacher.id).size
        _deleteTarget.value = DeleteTarget.TeacherTarget(teacher, count)
    }

    // Student CRUD
    fun openAddStudentDialog() {
        _editingStudent.value = null
        _isStudentFormOpen.value = true
    }

    fun openEditStudentDialog(student: Student) {
        _editingStudent.value = student
        _isStudentFormOpen.value = true
    }

    fun closeStudentForm() {
        _isStudentFormOpen.value = false
        _editingStudent.value = null
    }

    fun saveStudent(
        name: String,
        fatherName: String,
        teacherId: String,
        phone: String,
        address: String,
        age: String
    ) {
        val current = _editingStudent.value
        if (current == null) {
            val newStudent = Student(
                name = name.trim(),
                fatherName = fatherName.trim(),
                teacherId = teacherId,
                phone = phone.trim(),
                address = address.trim(),
                age = age.trim()
            )
            repository.addStudent(newStudent)
        } else {
            val updated = current.copy(
                name = name.trim(),
                fatherName = fatherName.trim(),
                teacherId = teacherId,
                phone = phone.trim(),
                address = address.trim(),
                age = age.trim()
            )
            repository.updateStudent(updated)
        }
        closeStudentForm()
    }

    fun confirmDeleteStudent(student: Student) {
        _deleteTarget.value = DeleteTarget.StudentTarget(student)
    }

    fun dismissDeleteDialog() {
        _deleteTarget.value = null
    }

    fun executeDelete() {
        when (val target = _deleteTarget.value) {
            is DeleteTarget.TeacherTarget -> {
                repository.deleteTeacher(target.teacher.id)
            }
            is DeleteTarget.StudentTarget -> {
                repository.deleteStudent(target.student.id)
            }
            null -> Unit
        }
        _deleteTarget.value = null
    }

    // Teacher's students view dialog
    fun openTeacherStudents(teacher: Teacher) {
        _viewingTeacherForStudents.value = teacher
    }

    fun closeTeacherStudents() {
        _viewingTeacherForStudents.value = null
    }

    fun getStudentsForTeacher(teacherId: String): List<Student> {
        return repository.getStudentsForTeacher(teacherId)
    }

    fun getTeacherName(teacherId: String): String {
        return allTeachers.value.firstOrNull { it.id == teacherId }?.name ?: "Unassigned"
    }

    // Attendance Operations
    fun setAttendanceDate(date: String) {
        _selectedDate.value = date
    }

    fun goToPreviousDay() {
        try {
            val cal = Calendar.getInstance()
            dateFormat.parse(_selectedDate.value)?.let { cal.time = it }
            cal.add(Calendar.DAY_OF_YEAR, -1)
            _selectedDate.value = dateFormat.format(cal.time)
        } catch (e: Exception) {
            // fallback
        }
    }

    fun goToNextDay() {
        try {
            val cal = Calendar.getInstance()
            dateFormat.parse(_selectedDate.value)?.let { cal.time = it }
            cal.add(Calendar.DAY_OF_YEAR, 1)
            _selectedDate.value = dateFormat.format(cal.time)
        } catch (e: Exception) {
            // fallback
        }
    }

    fun goToToday() {
        _selectedDate.value = dateFormat.format(Date())
    }

    fun getStudentAttendanceStatus(date: String, studentId: String): AttendanceStatus {
        return attendanceMap.value[date]?.get(studentId) ?: AttendanceStatus.UNMARKED
    }

    fun markAttendance(studentId: String, status: AttendanceStatus) {
        repository.setStudentAttendance(_selectedDate.value, studentId, status)
    }

    fun markAllPresent() {
        val studentIds = attendanceStudents.value.map { it.id }
        repository.markAllPresentForDate(_selectedDate.value, studentIds)
    }

    fun getAttendanceSummary(date: String): AttendanceSummary {
        val total = allStudents.value.size
        val dateMap = attendanceMap.value[date] ?: emptyMap()
        var present = 0
        var absent = 0
        var leave = 0

        for (student in allStudents.value) {
            when (dateMap[student.id]) {
                AttendanceStatus.PRESENT -> present++
                AttendanceStatus.ABSENT -> absent++
                AttendanceStatus.LEAVE -> leave++
                else -> Unit
            }
        }
        val unmarked = total - (present + absent + leave)
        return AttendanceSummary(total, present, absent, leave, unmarked.coerceAtLeast(0))
    }

    // Settings
    fun setThemeMode(mode: AppThemeMode) {
        repository.setThemeMode(mode)
    }

    fun toggleTheme() {
        val next = when (themeMode.value) {
            AppThemeMode.SYSTEM -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.SYSTEM
        }
        setThemeMode(next)
    }

    fun setLanguage(lang: AppLanguage) {
        repository.setLanguage(lang)
    }

    fun toggleLanguage() {
        val next = if (language.value == AppLanguage.ENGLISH) AppLanguage.URDU else AppLanguage.ENGLISH
        setLanguage(next)
    }

    fun clearAllData() {
        repository.clearAllData()
    }

    // Phone & WhatsApp Actions
    fun callPhone(context: Context, phone: String) {
        val clean = phone.replace(Regex("[^0-9+]"), "")
        if (clean.isBlank()) {
            Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$clean")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open dialer", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWhatsApp(context: Context, phone: String) {
        val clean = phone.replace(Regex("[^0-9]"), "")
        if (clean.isBlank()) {
            Toast.makeText(context, "No WhatsApp number available", Toast.LENGTH_SHORT).show()
            return
        }
        val internationalNumber = when {
            clean.startsWith("00") -> clean.substring(2)
            clean.startsWith("03") -> "92" + clean.substring(1)
            clean.startsWith("92") -> clean
            clean.length == 10 && clean.startsWith("3") -> "92$clean"
            else -> clean
        }

        try {
            val url = "https://wa.me/$internationalNumber"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
}
