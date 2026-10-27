package com.example.data.repository

import android.content.Context
import com.example.data.model.AppLanguage
import com.example.data.model.AppThemeMode
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.FeeMonthRecord
import com.example.data.model.Student
import com.example.data.model.Teacher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class MadrasaRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("madrasa_storage_v4", Context.MODE_PRIVATE)

    private val _teachers = MutableStateFlow<List<Teacher>>(emptyList())
    val teachers: StateFlow<List<Teacher>> = _teachers.asStateFlow()

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    // Date (YYYY-MM-DD) -> (StudentId -> AttendanceRecord)
    private val _attendanceMap = MutableStateFlow<Map<String, Map<String, AttendanceRecord>>>(emptyMap())
    val attendanceMap: StateFlow<Map<String, Map<String, AttendanceRecord>>> = _attendanceMap.asStateFlow()

    // Date (YYYY-MM-DD) -> (TeacherId -> AttendanceRecord)
    private val _teacherAttendanceMap = MutableStateFlow<Map<String, Map<String, AttendanceRecord>>>(emptyMap())
    val teacherAttendanceMap: StateFlow<Map<String, Map<String, AttendanceRecord>>> = _teacherAttendanceMap.asStateFlow()

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    init {
        loadSettings()
        loadData()
        loadAttendance()
        loadTeacherAttendance()
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
            _attendanceMap.value = parseAttendanceJson(json)
        } catch (e: Exception) {
            _attendanceMap.value = emptyMap()
        }
    }

    private fun loadTeacherAttendance() {
        val json = prefs.getString(KEY_TEACHER_ATTENDANCE, null) ?: return
        try {
            _teacherAttendanceMap.value = parseAttendanceJson(json)
        } catch (e: Exception) {
            _teacherAttendanceMap.value = emptyMap()
        }
    }

    private fun parseAttendanceJson(json: String): Map<String, Map<String, AttendanceRecord>> {
        val root = JSONObject(json)
        val map = mutableMapOf<String, Map<String, AttendanceRecord>>()
        val keys = root.keys()
        while (keys.hasNext()) {
            val date = keys.next()
            val dateObj = root.getJSONObject(date)
            val innerMap = mutableMapOf<String, AttendanceRecord>()
            val itemKeys = dateObj.keys()
            while (itemKeys.hasNext()) {
                val id = itemKeys.next()
                val recObj = dateObj.getJSONObject(id)
                val status = try {
                    AttendanceStatus.valueOf(recObj.optString("status", AttendanceStatus.UNMARKED.name))
                } catch (e: Exception) {
                    AttendanceStatus.UNMARKED
                }
                innerMap[id] = AttendanceRecord(
                    studentId = id,
                    date = date,
                    dayName = recObj.optString("dayName", ""),
                    time = recObj.optString("time", ""),
                    status = status,
                    leaveReason = recObj.optString("leaveReason", "")
                )
            }
            map[date] = innerMap
        }
        return map
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

                // Serialize monthlyFees map
                val monthlyObj = JSONObject()
                for ((mKey, mRec) in s.monthlyFees) {
                    val mItem = JSONObject().apply {
                        put("monthKey", mRec.monthKey)
                        put("monthTitle", mRec.monthTitle)
                        put("isPaid", mRec.isPaid)
                        put("paidDate", mRec.paidDate)
                        put("amount", mRec.amount)
                    }
                    monthlyObj.put(mKey, mItem)
                }
                put("monthlyFees", monthlyObj)
            }
            sArray.put(obj)
        }

        prefs.edit()
            .putString(KEY_TEACHERS, tArray.toString())
            .putString(KEY_STUDENTS, sArray.toString())
            .apply()
    }

    private fun saveAttendanceData() {
        val root = serializeAttendanceMap(_attendanceMap.value)
        prefs.edit().putString(KEY_ATTENDANCE, root.toString()).apply()
    }

    private fun saveTeacherAttendanceData() {
        val root = serializeAttendanceMap(_teacherAttendanceMap.value)
        prefs.edit().putString(KEY_TEACHER_ATTENDANCE, root.toString()).apply()
    }

    private fun serializeAttendanceMap(map: Map<String, Map<String, AttendanceRecord>>): JSONObject {
        val root = JSONObject()
        for ((date, subMap) in map) {
            val dateObj = JSONObject()
            for ((id, record) in subMap) {
                val rObj = JSONObject().apply {
                    put("status", record.status.name)
                    put("dayName", record.dayName)
                    put("time", record.time)
                    put("leaveReason", record.leaveReason)
                }
                dateObj.put(id, rObj)
            }
            root.put(date, dateObj)
        }
        return root
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
            val monthlyFees = mutableMapOf<String, FeeMonthRecord>()
            val mObj = obj.optJSONObject("monthlyFees")
            if (mObj != null) {
                val keys = mObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    val item = mObj.getJSONObject(k)
                    monthlyFees[k] = FeeMonthRecord(
                        monthKey = item.optString("monthKey", k),
                        monthTitle = item.optString("monthTitle", k),
                        isPaid = item.optBoolean("isPaid", false),
                        paidDate = item.optString("paidDate", ""),
                        amount = item.optString("amount", "")
                    )
                }
            }

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
                    isFeePaid = obj.optBoolean("isFeePaid", false),
                    monthlyFees = monthlyFees
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

    fun updateStudentMonthlyFee(
        studentId: String,
        monthKey: String,
        monthTitle: String,
        isPaid: Boolean,
        paidDate: String,
        amount: String
    ) {
        _students.value = _students.value.map { student ->
            if (student.id == studentId) {
                val updatedMap = student.monthlyFees.toMutableMap()
                updatedMap[monthKey] = FeeMonthRecord(
                    monthKey = monthKey,
                    monthTitle = monthTitle,
                    isPaid = isPaid,
                    paidDate = if (isPaid) paidDate else "",
                    amount = amount.ifBlank { student.feeAmount }
                )
                student.copy(
                    monthlyFees = updatedMap,
                    isFeePaid = isPaid, // sync current status
                    feePaidDate = if (isPaid) paidDate else "",
                    feeAmount = amount.ifBlank { student.feeAmount }
                )
            } else student
        }
        saveData()
    }

    fun getStudentsForTeacher(teacherId: String): List<Student> {
        return _students.value.filter { it.teacherId == teacherId }
    }

    // Student Attendance Operations with Auto-Save
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

    fun getStudentAttendanceHistory(studentId: String): List<AttendanceRecord> {
        val list = mutableListOf<AttendanceRecord>()
        for ((_, studentMap) in _attendanceMap.value) {
            studentMap[studentId]?.let { list.add(it) }
        }
        return list.sortedByDescending { it.date }
    }

    // Teacher Attendance Operations with Auto-Save
    fun setTeacherAttendanceRecord(record: AttendanceRecord) {
        val currentAll = _teacherAttendanceMap.value.toMutableMap()
        val currentForDate = currentAll[record.date]?.toMutableMap() ?: mutableMapOf()
        currentForDate[record.studentId] = record // using studentId field for teacherId
        currentAll[record.date] = currentForDate
        _teacherAttendanceMap.value = currentAll
        saveTeacherAttendanceData()
    }

    fun markAllTeachersPresentForDate(date: String, dayName: String, time: String, teacherIds: List<String>) {
        val currentAll = _teacherAttendanceMap.value.toMutableMap()
        val currentForDate = currentAll[date]?.toMutableMap() ?: mutableMapOf()
        teacherIds.forEach { id ->
            currentForDate[id] = AttendanceRecord(
                studentId = id,
                date = date,
                dayName = dayName,
                time = time,
                status = AttendanceStatus.PRESENT
            )
        }
        currentAll[date] = currentForDate
        _teacherAttendanceMap.value = currentAll
        saveTeacherAttendanceData()
    }

    fun getTeacherAttendanceHistory(teacherId: String): List<AttendanceRecord> {
        val list = mutableListOf<AttendanceRecord>()
        for ((_, teacherMap) in _teacherAttendanceMap.value) {
            teacherMap[teacherId]?.let { list.add(it) }
        }
        return list.sortedByDescending { it.date }
    }

    fun getTeacherDaysPresentInMonth(teacherId: String, monthKey: String): Int {
        var count = 0
        for ((date, teacherMap) in _teacherAttendanceMap.value) {
            if (date.startsWith(monthKey)) {
                if (teacherMap[teacherId]?.status == AttendanceStatus.PRESENT) {
                    count++
                }
            }
        }
        return count
    }

    fun getStudentDaysPresentInMonth(studentId: String, monthKey: String): Int {
        var count = 0
        for ((date, studentMap) in _attendanceMap.value) {
            if (date.startsWith(monthKey)) {
                if (studentMap[studentId]?.status == AttendanceStatus.PRESENT) {
                    count++
                }
            }
        }
        return count
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
        _teacherAttendanceMap.value = emptyMap()
        saveData()
        saveAttendanceData()
        saveTeacherAttendanceData()
    }

    companion object {
        private const val KEY_TEACHERS = "key_teachers_data_v4"
        private const val KEY_STUDENTS = "key_students_data_v4"
        private const val KEY_ATTENDANCE = "key_attendance_data_v4"
        private const val KEY_TEACHER_ATTENDANCE = "key_teacher_attendance_v4"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_LANGUAGE = "key_language"
    }
}
