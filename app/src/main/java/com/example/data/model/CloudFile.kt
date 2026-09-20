package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "files")
data class CloudFile(
  @PrimaryKey val fileId: String,
  val ownerId: String,
  val fileName: String,
  val originalName: String,
  val storagePath: String, // users/{ownerId}/files/{fileId}
  val localFilePath: String, // actual local private file on device
  val fileType: String, // extension without dot
  val mimeType: String,
  val sizeBytes: Long,
  val category: FileCategory,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val deletedAt: Long? = null,
  val status: String = "active" // active, trash, deleted
) {
  fun getFormattedSize(): String {
    return formatStorageBytes(sizeBytes)
  }

  fun getFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(createdAt))
  }

  fun getFormattedDeletedDate(): String {
    if (deletedAt == null) return ""
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(deletedAt))
  }

  companion object {
    fun formatStorageBytes(bytes: Long): String {
      if (bytes <= 0) return "0 B"
      val kb = bytes / 1024.0
      val mb = kb / 1024.0
      val gb = mb / 1024.0

      return when {
        gb >= 1.0 -> String.format(Locale.US, "%.2f GB", gb)
        mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
        kb >= 1.0 -> String.format(Locale.US, "%.1f KB", kb)
        else -> "$bytes B"
      }
    }

    fun formatMBOnly(bytes: Long): String {
      val mb = bytes / (1024.0 * 1024.0)
      return String.format(Locale.US, "%.1f MB", mb)
    }
  }
}
