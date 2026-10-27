package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
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
        phone: String,
        teacherId: String,
        feeAmount: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialStudent?.name ?: "") }
    var fatherName by remember { mutableStateOf(initialStudent?.fatherName ?: "") }
    var phone by remember { mutableStateOf(initialStudent?.phone ?: "") }
    var selectedTeacherId by remember { mutableStateOf(initialStudent?.teacherId ?: "") }
    var feeAmount by remember { mutableStateOf(initialStudent?.feeAmount ?: "1000") }

    var nameError by remember { mutableStateOf(false) }
    var fatherNameError by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val isEditing = initialStudent != null
    val selectedTeacher = teachersList.find { it.id == selectedTeacherId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "Edit Student (طالب علم میں ترمیم)" else "Add Student (نیا طالب علم)",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Bachay ka Naam (required)
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Bachay ka Naam (Student Name) *") },
                    placeholder = { Text("e.g. Muhammad Ahmed") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Student Name")
                    },
                    isError = nameError,
                    supportingText = {
                        if (nameError) {
                            Text("Name is required", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Walid ka Naam (required)
                OutlinedTextField(
                    value = fatherName,
                    onValueChange = {
                        fatherName = it
                        if (it.isNotBlank()) fatherNameError = false
                    },
                    label = { Text("Walid ka Naam (Father's Name) *") },
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

                // 3. Walid ka Mobile Number
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Walid ka Mobile Number (Phone)") },
                    placeholder = { Text("03001234567") },
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

                Spacer(modifier = Modifier.height(10.dp))

                // Optional Teacher Selector (if teachers exist)
                if (teachersList.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedTeacher?.name ?: "No Teacher Assigned (Optional)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Qari Sahab / Teacher (Optional)") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.School, contentDescription = null)
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )

                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("None (Unassigned)") },
                                onClick = {
                                    selectedTeacherId = ""
                                    isDropdownExpanded = false
                                }
                            )
                            teachersList.forEach { teacher ->
                                DropdownMenuItem(
                                    text = { Text(teacher.name) },
                                    onClick = {
                                        selectedTeacherId = teacher.id
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Optional Monthly Fee
                OutlinedTextField(
                    value = feeAmount,
                    onValueChange = { feeAmount = it },
                    label = { Text("Monthly Fee / Mahana Fees (Optional)") },
                    placeholder = { Text("e.g. 1000") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Payments, contentDescription = null)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_fee_input")
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

                    if (!hasError) {
                        onSave(name, fatherName, phone, selectedTeacherId, feeAmount)
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
