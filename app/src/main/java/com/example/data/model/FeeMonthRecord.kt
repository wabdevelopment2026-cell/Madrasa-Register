package com.example.data.model

data class FeeMonthRecord(
    val monthKey: String, // e.g. "2026-10"
    val monthTitle: String, // e.g. "October 2026"
    val isPaid: Boolean = false,
    val paidDate: String = "", // e.g. "2026-10-25"
    val amount: String = "" // e.g. "1000"
)
