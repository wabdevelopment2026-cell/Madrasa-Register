package com.example.data.model

import java.util.UUID

data class Student(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val fatherName: String,
    val phone: String = "",
    val teacherId: String = "",
    val address: String = "",
    val age: String = "",
    val feeAmount: String = "",
    val feePaidDate: String = "", // e.g. "2026-10-15"
    val isFeePaid: Boolean = false
)
