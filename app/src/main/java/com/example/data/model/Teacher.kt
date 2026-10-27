package com.example.data.model

import java.util.UUID

data class Teacher(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String = "",
    val address: String = ""
)
