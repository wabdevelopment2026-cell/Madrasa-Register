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
    }
}
