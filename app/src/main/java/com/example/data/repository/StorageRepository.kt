package com.example.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.data.local.StorageDatabase
import com.example.data.model.AppNotification
import com.example.data.model.CloudFile
import com.example.data.model.FileCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

sealed class UploadResult {
  data class Success(val file: CloudFile) : UploadResult()
  data class QuotaExceeded(val messageBn: String, val messageEn: String) : UploadResult()
  data class Error(val message: String) : UploadResult()
}

class StorageRepository(private val context: Context) {
  private val database = StorageDatabase.getDatabase(context)
  private val fileDao = database.fileDao()
  private val userDao = database.userDao()
  private val notificationDao = database.notificationDao()

  // Mutex to protect concurrent simultaneous uploads from racing quota checks
  private val uploadMutex = Mutex()

  private val MAX_QUOTA_BYTES = 1048576000L // Exactly 1000 MB (1000 * 1024 * 1024)

  fun getActiveFiles(ownerId: String): Flow<List<CloudFile>> = fileDao.getActiveFiles(ownerId)

  fun getTrashFiles(ownerId: String): Flow<List<CloudFile>> = fileDao.getTrashFiles(ownerId)

  fun getStorageUsed(ownerId: String): Flow<Long> = fileDao.getTotalStorageUsedBytes(ownerId)

  fun getNotifications(ownerId: String): Flow<List<AppNotification>> = notificationDao.getNotifications(ownerId)

  fun getUnreadNotificationsCount(ownerId: String): Flow<Int> = notificationDao.getUnreadCount(ownerId)

  suspend fun markAllNotificationsRead(ownerId: String) = withContext(Dispatchers.IO) {
    notificationDao.markAllAsRead(ownerId)
  }

  suspend fun uploadFile(
    ownerId: String,
    uri: Uri,
    onProgress: (progressPercent: Int, bytesUploaded: Long, totalBytes: Long) -> Unit
  ): UploadResult = withContext(Dispatchers.IO) {
    uploadMutex.withLock {
      try {
        // 1. Resolve file name and estimated size from ContentResolver
        val (originalName, resolvedSize) = resolveUriMetadata(uri)
        val fileExt = originalName.substringAfterLast('.', "").lowercase()
        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val category = FileCategory.fromMimeOrExtension(mimeType, fileExt)

        // 2. Strict Quota Check against 1000 MB
        val currentUsed = fileDao.getTotalStorageUsedBytesSync(ownerId)
        val estimatedSize = if (resolvedSize > 0) resolvedSize else 1024L

        if (currentUsed + estimatedSize > MAX_QUOTA_BYTES) {
          return@withLock UploadResult.QuotaExceeded(
            "এই ফাইল আপলোড করলে আপনার ১০০০ MB স্টোরেজ সীমা অতিক্রম করবে।",
            "Uploading this file will exceed your 1000 MB storage limit."
          )
        }

        // 3. Prepare target isolated user storage folder
        val fileId = UUID.randomUUID().toString()
        val userStorageDir = File(context.filesDir, "users/$ownerId/files")
        if (!userStorageDir.exists()) {
          userStorageDir.mkdirs()
        }

        val targetDiskFile = File(userStorageDir, "$fileId.$fileExt")
        var bytesWritten = 0L

        // 4. Stream and copy file data with real-time byte tracking
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
          FileOutputStream(targetDiskFile).use { outputStream ->
            val buffer = ByteArray(8192)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
              // Check quota on the fly in case streamed size exceeds declared size
              if (currentUsed + bytesWritten + read > MAX_QUOTA_BYTES) {
                targetDiskFile.delete()
                return@withLock UploadResult.QuotaExceeded(
                  "এই ফাইল আপলোড করলে আপনার ১০০০ MB স্টোরেজ সীমা অতিক্রম করবে।",
                  "Uploading this file will exceed your 1000 MB storage limit."
                )
              }
              outputStream.write(buffer, 0, read)
              bytesWritten += read

              val total = if (resolvedSize > 0) resolvedSize else bytesWritten
              val percent = ((bytesWritten.toDouble() / total.toDouble()) * 100).toInt().coerceIn(0, 100)
              onProgress(percent, bytesWritten, total)
            }
          }
        } ?: return@withLock UploadResult.Error("ফাইলটি পড়তে পারা যায়নি।")

        val actualSize = targetDiskFile.length()

        // 5. Final Quota Enforcement Check
        if (currentUsed + actualSize > MAX_QUOTA_BYTES) {
          targetDiskFile.delete()
          return@withLock UploadResult.QuotaExceeded(
            "এই ফাইল আপলোড করলে আপনার ১০০০ MB স্টোরেজ সীমা অতিক্রম করবে।",
            "Uploading this file will exceed your 1000 MB storage limit."
          )
        }

        // 6. Record metadata in database
        val cloudFile = CloudFile(
          fileId = fileId,
          ownerId = ownerId,
          fileName = originalName,
          originalName = originalName,
          storagePath = "users/$ownerId/files/$fileId.$fileExt",
          localFilePath = targetDiskFile.absolutePath,
          fileType = fileExt,
          mimeType = mimeType,
          sizeBytes = actualSize,
          category = category,
          status = "active"
        )

        fileDao.insertFile(cloudFile)

        // 7. Update user storageUsedBytes atomically
        val newTotalUsed = fileDao.getTotalStorageUsedBytesSync(ownerId)
        userDao.updateStorageUsage(ownerId, newTotalUsed)

        // 8. Generate notification
        notificationDao.insertNotification(
          AppNotification(
            id = UUID.randomUUID().toString(),
            userId = ownerId,
            titleBn = "ফাইল আপলোড হয়েছে",
            titleEn = "File Uploaded",
            messageBn = "$originalName (${CloudFile.formatStorageBytes(actualSize)}) সফলভাবে সংরক্ষিত হয়েছে।",
            messageEn = "$originalName (${CloudFile.formatStorageBytes(actualSize)}) was stored successfully.",
            type = "UPLOAD"
          )
        )

        UploadResult.Success(cloudFile)
      } catch (e: Exception) {
        UploadResult.Error(e.localizedMessage ?: "অপ্রত্যাশিত ত্রুটি ঘটেছে।")
      }
    }
  }

  suspend fun renameFile(fileId: String, ownerId: String, newDisplayName: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
    try {
      val file = fileDao.getFileById(fileId, ownerId) ?: return@withContext Pair(false, "ফাইল পাওয়া যায়নি।")
      val ext = file.fileType
      val finalName = if (newDisplayName.endsWith(".$ext", ignoreCase = true)) {
        newDisplayName
      } else {
        "$newDisplayName.$ext"
      }

      fileDao.renameFile(fileId, ownerId, finalName)
      Pair(true, "ফাইলের নাম সফলভাবে পরিবর্তন হয়েছে।")
    } catch (e: Exception) {
      Pair(false, e.localizedMessage ?: "নাম পরিবর্তন ব্যর্থ হয়েছে।")
    }
  }

  suspend fun moveToTrash(fileId: String, ownerId: String): Boolean = withContext(Dispatchers.IO) {
    try {
      fileDao.moveToTrash(fileId, ownerId)
      notificationDao.insertNotification(
        AppNotification(
          id = UUID.randomUUID().toString(),
          userId = ownerId,
          titleBn = "ফাইল ট্র্যাশে সরানো হয়েছে",
          titleEn = "File Moved to Trash",
          messageBn = "ফাইলটি ট্র্যাশে সরানো হয়েছে। কোটায় এখনো গণনা করা হচ্ছে।",
          messageEn = "The file has been moved to trash. It still counts toward quota until permanently deleted.",
          type = "DELETE"
        )
      )
      true
    } catch (e: Exception) {
      false
    }
  }

  suspend fun restoreFile(fileId: String, ownerId: String): Boolean = withContext(Dispatchers.IO) {
    try {
      fileDao.restoreFromTrash(fileId, ownerId)
      notificationDao.insertNotification(
        AppNotification(
          id = UUID.randomUUID().toString(),
          userId = ownerId,
          titleBn = "ফাইল পুনরুদ্ধার হয়েছে",
          titleEn = "File Restored",
          messageBn = "ফাইলটি সফলভাবে আমার ফাইলে পুনরুদ্ধার করা হয়েছে।",
          messageEn = "The file was successfully restored to My Files.",
          type = "RESTORE"
        )
      )
      true
    } catch (e: Exception) {
      false
    }
  }

  suspend fun permanentDelete(fileId: String, ownerId: String): Boolean = withContext(Dispatchers.IO) {
    try {
      val file = fileDao.getFileById(fileId, ownerId)
      if (file != null) {
        // Delete actual physical file from isolated disk storage
        val localDiskFile = File(file.localFilePath)
        if (localDiskFile.exists()) {
          localDiskFile.delete()
        }
        // Remove database record
        fileDao.permanentDelete(fileId, ownerId)

        // Update quota
        val newTotalUsed = fileDao.getTotalStorageUsedBytesSync(ownerId)
        userDao.updateStorageUsage(ownerId, newTotalUsed)
      }
      true
    } catch (e: Exception) {
      false
    }
  }

  suspend fun emptyTrash(ownerId: String): Boolean = withContext(Dispatchers.IO) {
    try {
      val trashFiles = fileDao.getAllFilesForUserSync(ownerId).filter { it.status == "trash" }
      trashFiles.forEach { file ->
        val localDiskFile = File(file.localFilePath)
        if (localDiskFile.exists()) {
          localDiskFile.delete()
        }
        fileDao.permanentDelete(file.fileId, ownerId)
      }
      val newTotalUsed = fileDao.getTotalStorageUsedBytesSync(ownerId)
      userDao.updateStorageUsage(ownerId, newTotalUsed)
      true
    } catch (e: Exception) {
      false
    }
  }

  // Admin global metrics
  fun getAdminTotalFiles(): Flow<Int> = fileDao.getTotalFilesCount()
  fun getAdminTotalStorageUsed(): Flow<Long> = fileDao.getSystemTotalStorageUsed()

  private fun resolveUriMetadata(uri: Uri): Pair<String, Long> {
    var name = "file_${System.currentTimeMillis()}"
    var size = 0L

    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
      val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
      val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

      if (cursor.moveToFirst()) {
        if (nameIndex != -1) {
          name = cursor.getString(nameIndex) ?: name
        }
        if (sizeIndex != -1) {
          size = cursor.getLong(sizeIndex)
        }
      }
    }
    return Pair(name, size)
  }
}
