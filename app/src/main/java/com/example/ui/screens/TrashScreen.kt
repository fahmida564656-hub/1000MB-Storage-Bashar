package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloudFile
import com.example.ui.components.ConfirmationDialog
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.viewmodel.StorageViewModel

@Composable
fun TrashScreen(
  viewModel: StorageViewModel,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val trashFiles by viewModel.trashFiles.collectAsState()
  var fileToPermanentlyDelete by remember { mutableStateOf<CloudFile?>(null) }
  var showEmptyTrashDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = Strings.trashTitle(language),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = if (language == AppLanguage.BENGALI) "${trashFiles.size} টি ফাইল ট্র্যাশে আছে" else "${trashFiles.size} files in trash",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      if (trashFiles.isNotEmpty()) {
        OutlinedButton(
          onClick = { showEmptyTrashDialog = true },
          colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("trash_empty_button")
        ) {
          Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (language == AppLanguage.BENGALI) "খালি করুন" else "Empty")
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Information Banner
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = Strings.trashSubtitle(language),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(12.dp)
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (trashFiles.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("🗑️", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = Strings.trashEmpty(language),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (language == AppLanguage.BENGALI)
              "কোনো ফাইল মুছে ফেলা হলে তা এখানে দেখা যাবে।"
            else
              "Files you delete will be held here before permanent removal.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(trashFiles, key = { it.fileId }) { file ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = file.category.getEmoji(), fontSize = 28.sp)
              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = file.fileName,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.SemiBold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "${file.getFormattedSize()} • ${file.getFormattedDeletedDate()}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              // Restore Button
              IconButton(
                onClick = { viewModel.restoreFile(file.fileId) },
                modifier = Modifier.testTag("trash_restore_${file.fileId}")
              ) {
                Icon(
                  imageVector = Icons.Default.Restore,
                  contentDescription = "Restore",
                  tint = MaterialTheme.colorScheme.primary
                )
              }

              // Permanent Delete Button
              IconButton(
                onClick = { fileToPermanentlyDelete = file },
                modifier = Modifier.testTag("trash_delete_perm_${file.fileId}")
              ) {
                Icon(
                  imageVector = Icons.Default.DeleteForever,
                  contentDescription = "Permanent Delete",
                  tint = MaterialTheme.colorScheme.error
                )
              }
            }
          }
        }
      }
    }
  }

  // Confirmation dialog for single file permanent delete
  fileToPermanentlyDelete?.let { file ->
    ConfirmationDialog(
      title = if (language == AppLanguage.BENGALI) "স্থায়ীভাবে মুছে ফেলবেন?" else "Permanently delete file?",
      message = if (language == AppLanguage.BENGALI)
        "\"${file.fileName}\" ফাইলটি ডিভাইস ও ক্লাউড স্টোরেজ থেকে চিরতরে মুছে যাবে। এটি আর পুনরুদ্ধার করা যাবে না।"
      else
        "\"${file.fileName}\" will be permanently deleted from cloud storage. This cannot be undone.",
      confirmText = Strings.permanentDelete(language),
      dismissText = if (language == AppLanguage.BENGALI) "বাতিল" else "Cancel",
      isDestructive = true,
      onDismiss = { fileToPermanentlyDelete = null },
      onConfirm = {
        viewModel.permanentDelete(file.fileId)
        fileToPermanentlyDelete = null
      }
    )
  }

  // Confirmation dialog for Empty Trash
  if (showEmptyTrashDialog) {
    ConfirmationDialog(
      title = if (language == AppLanguage.BENGALI) "সম্পূর্ণ ট্র্যাশ খালি করবেন?" else "Empty entire trash?",
      message = if (language == AppLanguage.BENGALI)
        "ট্র্যাশের সমস্ত ফাইল স্থায়ীভাবে মুছে ফেলা হবে এবং কোটায় জায়গা খালি হবে।"
      else
        "All files currently in trash will be permanently wiped and storage quota will be freed.",
      confirmText = if (language == AppLanguage.BENGALI) "ট্র্যাশ খালি করুন" else "Empty Trash",
      dismissText = if (language == AppLanguage.BENGALI) "বাতিল" else "Cancel",
      isDestructive = true,
      onDismiss = { showEmptyTrashDialog = false },
      onConfirm = {
        viewModel.emptyTrash()
        showEmptyTrashDialog = false
      }
    )
  }
}
