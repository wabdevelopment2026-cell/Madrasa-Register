package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.Student
import com.example.data.model.Teacher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFormDialog(
    initialStudent: Student?,
    teachersList: List<Teacher>,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        fatherName: String,
        teacherId: String,
        phone: String,
        address: String,
        age: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialStudent?.name ?: "") }
    var fatherName by remember { mutableStateOf(initialStudent?.fatherName ?: "") }
    var selectedTeacherId by remember {
        mutableStateOf(
            initialStudent?.teacherId ?: teachersList.firstOrNull()?.id.orEmpty()
        )
    }
    var phone by remember { mutableStateOf(initialStudent?.phone ?: "") }
    var address by remember { mutableStateOf(initialStudent?.address ?: "") }
    var age by remember { mutableStateOf(initialStudent?.age ?: "") }

    var nameError by remember { mutableStateOf(false) }
    var fatherNameError by remember { mutableStateOf(false) }
    var teacherError by remember { mutableStateOf(false) }

    var isDropdownExpanded by remember { mutableStateOf(false) }

    val isEditing = initialStudent != null
    val selectedTeacher = teachersList.find { it.id == selectedTeacherId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "Edit Student (Bachay ki Maloomat)" else "Add Student (Naya Bacha)",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Bachay ka Naam (required)
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Student Name (Bachay ka Naam) *") },
                    placeholder = { Text("e.g. Muhammad Ahmed") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Student Name")
                    },
                    isError = nameError,
                    supportingText = {
                        if (nameError) {
                            Text("Student name is required", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Walid ka Naam (required)
                OutlinedTextField(
                    value = fatherName,
                    onValueChange = {
                        fatherName = it
                        if (it.isNotBlank()) fatherNameError = false
                    },
                    label = { Text("Father's Name (Walid ka Naam) *") },
                    placeholder = { Text("e.g. Muhammad Bilal") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Father Name")
                    },
                    isError = fatherNameError,
                    supportingText = {
                        if (fatherNameError) {
                            Text("Father's name is required", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_father_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Qari Sahab Dropdown (required)
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedTeacher?.name ?: if (teachersList.isEmpty()) "No teacher available (Add teacher first)" else "Select Qari Sahab",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Qari Sahab (Teacher) *") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.School, contentDescription = "Qari Sahab")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                        },
                        isError = teacherError,
                        supportingText = {
                            if (teacherError) {
                                Text("Selecting a teacher is required", color = MaterialTheme.colorScheme.error)
                            } else if (teachersList.isEmpty()) {
                                Text("Please add a teacher first in Teachers tab", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("student_teacher_dropdown")
                    )

                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        teachersList.forEach { teacher ->
                            DropdownMenuItem(
                                text = { Text(teacher.name) },
                                onClick = {
                                    selectedTeacherId = teacher.id
                                    teacherError = false
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Walid ka Number
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Father's Phone (Walid ka Number)") },
                    placeholder = { Text("03011112233") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Father Phone")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_phone_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Umar (Age)
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age in Years (Umar)") },
                    placeholder = { Text("e.g. 12") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Age")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_age_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Address
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address / Mohalla") },
                    placeholder = { Text("House / Street / City") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Home, contentDescription = "Address")
                    },
                    maxLines = 2,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_address_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false
                    if (name.isBlank()) {
                        nameError = true
                        hasError = true
                    }
                    if (fatherName.isBlank()) {
                        fatherNameError = true
                        hasError = true
                    }
                    if (selectedTeacherId.isBlank()) {
                        teacherError = true
                        hasError = true
                    }

                    if (!hasError) {
                        onSave(name, fatherName, selectedTeacherId, phone, address, age)
                    }
                },
                modifier = Modifier.testTag("student_save_button")
            ) {
                Text(if (isEditing) "Save Changes" else "Add Student")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("student_cancel_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
