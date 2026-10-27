package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AttendanceStatus
import com.example.data.model.Student
import com.example.data.model.Teacher
import com.example.data.repository.MadrasaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Madrasa Register", appName)
    }

    @Test
    fun `repository crud and attendance test`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = MadrasaRepository(context)

        // Clear existing to verify empty start
        repository.clearAllData()
        assertTrue(repository.teachers.value.isEmpty())
        assertTrue(repository.students.value.isEmpty())

        val newTeacher = Teacher(name = "Qari Abdullah", phone = "03009998877", address = "Room 5")
        repository.addTeacher(newTeacher)

        val newStudent = Student(
            name = "Bilal Ahmed",
            fatherName = "Ahmed Ali",
            teacherId = newTeacher.id,
            phone = "03001112233",
            address = "Street 1",
            age = "10"
        )
        repository.addStudent(newStudent)

        val teachers = repository.teachers.value
        val students = repository.students.value

        assertEquals(1, teachers.size)
        assertEquals(1, students.size)
        assertEquals("Qari Abdullah", teachers.first().name)
        assertEquals("Bilal Ahmed", students.first().name)

        // Test attendance
        val testDate = "2026-10-27"
        val record = com.example.data.model.AttendanceRecord(
            studentId = newStudent.id,
            date = testDate,
            dayName = "Tuesday",
            time = "10:00 AM",
            status = AttendanceStatus.PRESENT
        )
        repository.setAttendanceRecord(record)

        val attendance = repository.attendanceMap.value[testDate]
        assertEquals(AttendanceStatus.PRESENT, attendance?.get(newStudent.id)?.status)

        // Test Monthly Fee Tracking
        repository.updateStudentMonthlyFee(
            studentId = newStudent.id,
            monthKey = "2026-10",
            monthTitle = "October 2026",
            isPaid = true,
            paidDate = "2026-10-25",
            amount = "1200"
        )
        val updatedStudent = repository.students.value.first()
        val feeRecord = updatedStudent.monthlyFees["2026-10"]
        assertTrue(feeRecord != null)
        assertTrue(feeRecord!!.isPaid)
        assertEquals("2026-10-25", feeRecord.paidDate)
        assertEquals("1200", feeRecord.amount)

        // Test Teacher Attendance
        val teacherRecord = com.example.data.model.AttendanceRecord(
            studentId = newTeacher.id,
            date = testDate,
            dayName = "Tuesday",
            time = "09:30 AM",
            status = AttendanceStatus.PRESENT
        )
        repository.setTeacherAttendanceRecord(teacherRecord)
        val teacherDays = repository.getTeacherDaysPresentInMonth(newTeacher.id, "2026-10")
        assertEquals(1, teacherDays)

        // Test Report Card Generator
        val reportData = com.example.util.ReportCardGenerator.ReportData(
            student = updatedStudent,
            teacherName = newTeacher.name,
            monthTitle = "October 2026",
            monthKey = "2026-10",
            attendanceRecords = listOf(record),
            teacherDaysPresent = teacherDays,
            feeRecord = feeRecord
        )
        val bitmap = com.example.util.ReportCardGenerator.generateReportBitmap(reportData)
        assertTrue(bitmap.width > 0)
        assertTrue(bitmap.height > 0)
    }
}

