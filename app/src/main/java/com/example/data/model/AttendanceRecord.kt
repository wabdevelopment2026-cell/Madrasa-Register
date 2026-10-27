package com.example.data.model

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    LEAVE,
    UNMARKED
}

data class AttendanceRecord(
    val studentId: String,
    val date: String, // Format: YYYY-MM-DD
    val status: AttendanceStatus,
    val note: String = ""
)
