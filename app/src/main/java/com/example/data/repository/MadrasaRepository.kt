package com.example.data.repository

import android.content.Context
import com.example.data.model.AppLanguage
import com.example.data.model.AppThemeMode
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.Student
import com.example.data.model.Teacher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class MadrasaRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("madrasa_storage_v3", Context.MODE_PRIVATE)

    private val _teachers = MutableStateFlow<List<Teacher>>(emptyList())
    val teachers: StateFlow<List<Teacher>> = _teachers.asStateFlow()

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    // Date (YYYY-MM-DD) -> (StudentId -> AttendanceRecord)
    private val _attendanceMap = MutableStateFlow<Map<String, Map<String, AttendanceRecord>>>(emptyMap())
    val attendanceMap: StateFlow<Map<String, Map<String, AttendanceRecord>>> = _attendanceMap.asStateFlow()

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    init {
        loadSettings()
        loadData()
        loadAttendance()
    }

    private fun loadSettings() {
        val themeStr = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
        _themeMode.value = try {
            AppThemeMode.valueOf(themeStr ?: AppThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }

        val langStr = prefs.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.name)
        _language.value = try {
            AppLanguage.valueOf(langStr ?: AppLanguage.ENGLISH.name)
        } catch (e: Exception) {
            AppLanguage.ENGLISH
        }
    }

    private fun loadData() {
        val teachersJson = prefs.getString(KEY_TEACHERS, null)
        val studentsJson = prefs.getString(KEY_STUDENTS, null)

        if (!teachersJson.isNullOrBlank()) {
            try {
                _teachers.value = parseTeachers(teachersJson)
            } catch (e: Exception) {
                _teachers.value = emptyList()
            }
        } else {
            _teachers.value = emptyList()
        }

        if (!studentsJson.isNullOrBlank()) {
            try {
                _students.value = parseStudents(studentsJson)
            } catch (e: Exception) {
                _students.value = emptyList()
            }
        } else {
            _students.value = emptyList()
        }
    }

    private fun loadAttendance() {
        val json = prefs.getString(KEY_ATTENDANCE, null) ?: return
        try {
            val root = JSONObject(json)
            val map = mutableMapOf<String, Map<String, AttendanceRecord>>()
            val keys = root.keys()
            while (keys.hasNext()) {
                val date = keys.next()
                val dateObj = root.getJSONObject(date)
                val innerMap = mutableMapOf<String, AttendanceRecord>()
                val studentKeys = dateObj.keys()
                while (studentKeys.hasNext()) {
                    val sId = studentKeys.next()
                    val recObj = dateObj.getJSONObject(sId)
                    val status = try {
                        AttendanceStatus.valueOf(recObj.optString("status", AttendanceStatus.UNMARKED.name))
                    } catch (e: Exception) {
                        AttendanceStatus.UNMARKED
                    }
                    innerMap[sId] = AttendanceRecord(
                        studentId = sId,
                        date = date,
                        dayName = recObj.optString("dayName", ""),
                        time = recObj.optString("time", ""),
                        status = status,
                        leaveReason = recObj.optString("leaveReason", "")
                    )
                }
                map[date] = innerMap
            }
            _attendanceMap.value = map
        } catch (e: Exception) {
            _attendanceMap.value = emptyMap()
        }
    }

    private fun saveData() {
        val tArray = JSONArray()
        for (t in _teachers.value) {
            val obj = JSONObject().apply {
                put("id", t.id)
                put("name", t.name)
                put("phone", t.phone)
                put("address", t.address)
            }
            tArray.put(obj)
        }

        val sArray = JSONArray()
        for (s in _students.value) {
            val obj = JSONObject().apply {
                put("id", s.id)
                put("name", s.name)
                put("fatherName", s.fatherName)
                put("phone", s.phone)
                put("teacherId", s.teacherId)
                put("address", s.address)
                put("age", s.age)
                put("feeAmount", s.feeAmount)
                put("feePaidDate", s.feePaidDate)
                put("isFeePaid", s.isFeePaid)
            }
            sArray.put(obj)
        }

        prefs.edit()
            .putString(KEY_TEACHERS, tArray.toString())
            .putString(KEY_STUDENTS, sArray.toString())
            .apply()
    }

    private fun saveAttendanceData() {
        val root = JSONObject()
        for ((date, studentMap) in _attendanceMap.value) {
            val dateObj = JSONObject()
            for ((studentId, record) in studentMap) {
                val rObj = JSONObject().apply {
                    put("status", record.status.name)
                    put("dayName", record.dayName)
                    put("time", record.time)
                    put("leaveReason", record.leaveReason)
                }
                dateObj.put(studentId, rObj)
            }
            root.put(date, dateObj)
        }
        prefs.edit().putString(KEY_ATTENDANCE, root.toString()).apply()
    }

    private fun parseTeachers(json: String): List<Teacher> {
        val list = mutableListOf<Teacher>()
        val arr = JSONArray(json)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                Teacher(
                    id = obj.optString("id", ""),
                    name = obj.optString("name", ""),
                    phone = obj.optString("phone", ""),
                    address = obj.optString("address", "")
                )
            )
        }
        return list
    }

    private fun parseStudents(json: String): List<Student> {
        val list = mutableListOf<Student>()
        val arr = JSONArray(json)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                Student(
                    id = obj.optString("id", ""),
                    name = obj.optString("name", ""),
                    fatherName = obj.optString("fatherName", ""),
                    phone = obj.optString("phone", ""),
                    teacherId = obj.optString("teacherId", ""),
                    address = obj.optString("address", ""),
                    age = obj.optString("age", ""),
                    feeAmount = obj.optString("feeAmount", ""),
                    feePaidDate = obj.optString("feePaidDate", ""),
                    isFeePaid = obj.optBoolean("isFeePaid", false)
                )
            )
        }
        return list
    }

    // Teacher CRUD
    fun addTeacher(teacher: Teacher) {
        _teachers.value = _teachers.value + teacher
        saveData()
    }

    fun updateTeacher(teacher: Teacher) {
        _teachers.value = _teachers.value.map { if (it.id == teacher.id) teacher else it }
        saveData()
    }

    fun deleteTeacher(teacherId: String) {
        _teachers.value = _teachers.value.filter { it.id != teacherId }
        _students.value = _students.value.map {
            if (it.teacherId == teacherId) it.copy(teacherId = "") else it
        }
        saveData()
    }

    // Student CRUD
    fun addStudent(student: Student) {
        _students.value = _students.value + student
        saveData()
    }

    fun updateStudent(student: Student) {
        _students.value = _students.value.map { if (it.id == student.id) student else it }
        saveData()
    }

    fun deleteStudent(studentId: String) {
        _students.value = _students.value.filter { it.id != studentId }
        saveData()
    }

    fun updateStudentFee(studentId: String, isPaid: Boolean, paidDate: String, amount: String) {
        _students.value = _students.value.map {
            if (it.id == studentId) {
                it.copy(
                    isFeePaid = isPaid,
                    feePaidDate = if (isPaid) paidDate else "",
                    feeAmount = if (amount.isNotBlank()) amount else it.feeAmount
                )
            } else it
        }
        saveData()
    }

    fun getStudentsForTeacher(teacherId: String): List<Student> {
        return _students.value.filter { it.teacherId == teacherId }
    }

    // Attendance Operations with Auto-Save
    fun setAttendanceRecord(record: AttendanceRecord) {
        val currentAll = _attendanceMap.value.toMutableMap()
        val currentForDate = currentAll[record.date]?.toMutableMap() ?: mutableMapOf()
        currentForDate[record.studentId] = record
        currentAll[record.date] = currentForDate
        _attendanceMap.value = currentAll
        saveAttendanceData()
    }

    fun markAllPresentForDate(date: String, dayName: String, time: String, studentIds: List<String>) {
        val currentAll = _attendanceMap.value.toMutableMap()
        val currentForDate = currentAll[date]?.toMutableMap() ?: mutableMapOf()
        studentIds.forEach { id ->
            currentForDate[id] = AttendanceRecord(
                studentId = id,
                date = date,
                dayName = dayName,
                time = time,
                status = AttendanceStatus.PRESENT
            )
        }
        currentAll[date] = currentForDate
        _attendanceMap.value = currentAll
        saveAttendanceData()
    }

    // Student Attendance History (Timeline)
    fun getStudentAttendanceHistory(studentId: String): List<AttendanceRecord> {
        val list = mutableListOf<AttendanceRecord>()
        for ((_, studentMap) in _attendanceMap.value) {
            studentMap[studentId]?.let { list.add(it) }
        }
        // sort by date descending
        return list.sortedByDescending { it.date }
    }

    // Settings
    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun setLanguage(language: AppLanguage) {
        _language.value = language
        prefs.edit().putString(KEY_LANGUAGE, language.name).apply()
    }

    fun clearAllData() {
        _teachers.value = emptyList()
        _students.value = emptyList()
        _attendanceMap.value = emptyMap()
        saveData()
        saveAttendanceData()
    }

    companion object {
        private const val KEY_TEACHERS = "key_teachers_data_v3"
        private const val KEY_STUDENTS = "key_students_data_v3"
        private const val KEY_ATTENDANCE = "key_attendance_data_v3"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_LANGUAGE = "key_language"
    }
}
