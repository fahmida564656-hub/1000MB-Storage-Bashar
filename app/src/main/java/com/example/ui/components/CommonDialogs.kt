package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppNotification
import com.example.data.model.CloudFile
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings

@Composable
fun RenameFileDialog(
  file: CloudFile,
  language: AppLanguage,
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit
) {
  val baseName = remember(file.fileName) {
    file.fileName.substringBeforeLast('.', file.fileName)
  }
  var newNameInput by remember { mutableStateOf(baseName) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = Strings.renameDialogTitle(language),
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column {
        Text(
          text = if (language == AppLanguage.BENGALI)
            "ফাইলের নতুন নাম লিখুন (.${file.fileType} স্বয়ংক্রিয়ভাবে যুক্ত থাকবে):"
          else
            "Enter new file name (.${file.fileType} extension will be preserved):",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
          value = newNameInput,
          onValueChange = { newNameInput = it },
          singleLine = true,
          label = { Text(if (language == AppLanguage.BENGALI) "নতুন নাম" else "New Name") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("rename_input_field"),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (newNameInput.isNotBlank()) {
            onConfirm(newNameInput.trim())
          }
        },
        enabled = newNameInput.isNotBlank(),
        modifier = Modifier.testTag("rename_confirm_button")
      ) {
        Text(Strings.rename(language))
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text(if (language == AppLanguage.BENGALI) "বাতিল" else "Cancel")
      }
    }
  )
}

@Composable
fun ConfirmationDialog(
  title: String,
  message: String,
  confirmText: String,
  dismissText: String,
  isDestructive: Boolean = false,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = title, fontWeight = FontWeight.Bold) },
    text = { Text(text = message) },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = if (isDestructive) {
          ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        } else {
          ButtonDefaults.buttonColors()
        }
      ) {
        Text(confirmText)
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text(dismissText)
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsBottomSheet(
  notifications: List<AppNotification>,
  language: AppLanguage,
  onDismiss: () -> Unit,
  onMarkAllRead: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (language == AppLanguage.BENGALI) "বিজ্ঞপ্তি সমূহ" else "Notifications",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
        }

        if (notifications.isNotEmpty()) {
          OutlinedButton(
            onClick = onMarkAllRead,
            shape = RoundedCornerShape(10.dp)
          ) {
            Text(if (language == AppLanguage.BENGALI) "সব পঠিত" else "Mark all read")
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (notifications.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("🔔", fontSize = 40.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = if (language == AppLanguage.BENGALI) "কোনো নতুন বিজ্ঞপ্তি নেই" else "No new notifications",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(notifications, key = { it.id }) { notif ->
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (notif.isRead) {
                  MaterialTheme.colorScheme.surface
                } else {
                  MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                }
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = if (language == AppLanguage.BENGALI) notif.titleBn else notif.titleEn,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = notif.getFormattedTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = if (language == AppLanguage.BENGALI) notif.messageBn else notif.messageEn,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
