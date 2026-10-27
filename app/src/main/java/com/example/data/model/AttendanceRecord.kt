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
    val dayName: String = "", // e.g. "Tuesday"
    val time: String = "", // e.g. "10:30 AM"
    val status: AttendanceStatus,
    val leaveReason: String = "" // Reason if status == LEAVE, e.g. "Bimari (Sick)"
)
