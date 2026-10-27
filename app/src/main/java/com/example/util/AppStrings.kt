package com.example.util

import com.example.data.model.AppLanguage

object AppStrings {

    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Madrasa App"
        AppLanguage.URDU -> "مدرسہ ایپ"
    }

    fun home(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Home"
        AppLanguage.URDU -> "ہوم"
    }

    fun teachers(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Teachers"
        AppLanguage.URDU -> "اساتذہ"
    }

    fun students(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Students"
        AppLanguage.URDU -> "طلباء"
    }

    fun attendance(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Attendance"
        AppLanguage.URDU -> "حاضری"
    }

    fun settings(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Settings"
        AppLanguage.URDU -> "سیٹنگز"
    }

    fun totalStudents(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Total Students"
        AppLanguage.URDU -> "کل طلباء"
    }

    fun totalTeachers(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Total Teachers"
        AppLanguage.URDU -> "کل اساتذہ"
    }

    fun addTeacher(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Add Teacher"
        AppLanguage.URDU -> "نیا قاری صاحب"
    }

    fun addStudent(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Add Student"
        AppLanguage.URDU -> "نیا بچہ"
    }

    fun search(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Search..."
        AppLanguage.URDU -> "تلاش کریں..."
    }

    fun present(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Present"
        AppLanguage.URDU -> "حاضر"
    }

    fun absent(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Absent"
        AppLanguage.URDU -> "غیر حاضر"
    }

    fun leave(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Leave"
        AppLanguage.URDU -> "رخصت"
    }

    fun unmarked(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Unmarked"
        AppLanguage.URDU -> "غیر درج"
    }

    fun markAllPresent(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Mark All Present"
        AppLanguage.URDU -> "سب حاضر کریں"
    }

    fun today(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Today"
        AppLanguage.URDU -> "آج"
    }

    fun date(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Date"
        AppLanguage.URDU -> "تاریخ"
    }

    fun teacherName(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Teacher Name (Qari Sahab) *"
        AppLanguage.URDU -> "قاری صاحب کا نام *"
    }

    fun phone(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Phone Number"
        AppLanguage.URDU -> "فون نمبر"
    }

    fun address(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Address"
        AppLanguage.URDU -> "پتہ"
    }

    fun studentName(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Student Name (Bachay ka Naam) *"
        AppLanguage.URDU -> "بچے کا نام *"
    }

    fun fatherName(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Father's Name (Walid ka Naam) *"
        AppLanguage.URDU -> "والد کا نام *"
    }

    fun fatherPhone(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Father's Phone (Walid ka Number)"
        AppLanguage.URDU -> "والد کا نمبر"
    }

    fun age(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Age (Umar)"
        AppLanguage.URDU -> "عمر"
    }

    fun assignedTeacher(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Assigned Teacher (Qari Sahab) *"
        AppLanguage.URDU -> "متعلقہ قاری صاحب *"
    }

    fun call(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Call"
        AppLanguage.URDU -> "کال"
    }

    fun whatsapp(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "WhatsApp"
        AppLanguage.URDU -> "واٹس ایپ"
    }

    fun edit(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Edit"
        AppLanguage.URDU -> "ترمیم"
    }

    fun delete(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Delete"
        AppLanguage.URDU -> "ڈیلیٹ"
    }

    fun save(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Save"
        AppLanguage.URDU -> "محفوظ کریں"
    }

    fun cancel(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Cancel"
        AppLanguage.URDU -> "منسوخ"
    }

    fun theme(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Theme"
        AppLanguage.URDU -> "تھیم"
    }

    fun language(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Language"
        AppLanguage.URDU -> "زبان"
    }
}
