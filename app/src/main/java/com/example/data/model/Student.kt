package com.example.data.model

import java.util.UUID

data class Student(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val fatherName: String,
    val teacherId: String,
    val phone: String = "",
    val address: String = "",
    val age: String = ""
)
