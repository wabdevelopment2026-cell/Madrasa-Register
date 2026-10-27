package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.viewmodel.DeleteTarget

@Composable
fun ConfirmationDialog(
    target: DeleteTarget,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val title: String
    val text: String

    when (target) {
        is DeleteTarget.TeacherTarget -> {
            title = "Delete Teacher?"
            text = if (target.studentCount > 0) {
                "Are you sure you want to delete ${target.teacher.name}?\n\nNote: They currently have ${target.studentCount} students assigned. The students will become unassigned."
            } else {
                "Are you sure you want to delete ${target.teacher.name}?"
            }
        }
        is DeleteTarget.StudentTarget -> {
            title = "Delete Student?"
            text = "Are you sure you want to delete ${target.student.name} (Father: ${target.student.fatherName})?"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.testTag("dialog_confirm_delete_button")
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_cancel_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
