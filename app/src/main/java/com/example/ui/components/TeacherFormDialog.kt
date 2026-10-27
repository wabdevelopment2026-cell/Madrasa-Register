package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.Teacher

@Composable
fun TeacherFormDialog(
    initialTeacher: Teacher?,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, address: String) -> Unit
) {
    var name by remember { mutableStateOf(initialTeacher?.name ?: "") }
    var phone by remember { mutableStateOf(initialTeacher?.phone ?: "") }
    var address by remember { mutableStateOf(initialTeacher?.address ?: "") }
    var nameError by remember { mutableStateOf(false) }

    val isEditing = initialTeacher != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "Edit Teacher (Qari Sahab)" else "Add Teacher (Qari Sahab)",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Teacher Name (Qari Sahab) *") },
                    placeholder = { Text("e.g. Qari Abdul Rehman") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Name")
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
                        .testTag("teacher_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("03001234567") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teacher_phone_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address / Location") },
                    placeholder = { Text("Room / Area / Street") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Home, contentDescription = "Address")
                    },
                    maxLines = 2,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teacher_address_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        onSave(name, phone, address)
                    }
                },
                modifier = Modifier.testTag("teacher_save_button")
            ) {
                Text(if (isEditing) "Save Changes" else "Add Teacher")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("teacher_cancel_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
