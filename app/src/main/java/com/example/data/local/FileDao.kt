package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CloudFile
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
  @Query("SELECT * FROM files WHERE ownerId = :ownerId AND status = 'active' ORDER BY createdAt DESC")
  fun getActiveFiles(ownerId: String): Flow<List<CloudFile>>

  @Query("SELECT * FROM files WHERE ownerId = :ownerId AND status = 'trash' ORDER BY deletedAt DESC")
  fun getTrashFiles(ownerId: String): Flow<List<CloudFile>>

  @Query("SELECT * FROM files WHERE fileId = :fileId AND ownerId = :ownerId LIMIT 1")
  suspend fun getFileById(fileId: String, ownerId: String): CloudFile?

  @Query("SELECT COALESCE(SUM(sizeBytes), 0) FROM files WHERE ownerId = :ownerId AND status != 'deleted'")
  fun getTotalStorageUsedBytes(ownerId: String): Flow<Long>

  @Query("SELECT COALESCE(SUM(sizeBytes), 0) FROM files WHERE ownerId = :ownerId AND status != 'deleted'")
  suspend fun getTotalStorageUsedBytesSync(ownerId: String): Long

  @Query("SELECT * FROM files WHERE ownerId = :ownerId AND status = 'active' AND (fileName LIKE '%' || :query || '%' OR fileType LIKE '%' || :query || '%')")
  fun searchFiles(ownerId: String, query: String): Flow<List<CloudFile>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFile(file: CloudFile)

  @Update
  suspend fun updateFile(file: CloudFile)

  @Query("UPDATE files SET fileName = :newName, updatedAt = :updatedAt WHERE fileId = :fileId AND ownerId = :ownerId")
  suspend fun renameFile(fileId: String, ownerId: String, newName: String, updatedAt: Long = System.currentTimeMillis())

  @Query("UPDATE files SET status = 'trash', deletedAt = :deletedAt, updatedAt = :updatedAt WHERE fileId = :fileId AND ownerId = :ownerId")
  suspend fun moveToTrash(fileId: String, ownerId: String, deletedAt: Long = System.currentTimeMillis(), updatedAt: Long = System.currentTimeMillis())

  @Query("UPDATE files SET status = 'active', deletedAt = NULL, updatedAt = :updatedAt WHERE fileId = :fileId AND ownerId = :ownerId")
  suspend fun restoreFromTrash(fileId: String, ownerId: String, updatedAt: Long = System.currentTimeMillis())

  @Query("DELETE FROM files WHERE fileId = :fileId AND ownerId = :ownerId")
  suspend fun permanentDelete(fileId: String, ownerId: String)

  @Query("DELETE FROM files WHERE ownerId = :ownerId AND status = 'trash'")
  suspend fun emptyTrash(ownerId: String)

  @Query("DELETE FROM files WHERE ownerId = :ownerId")
  suspend fun deleteAllUserFiles(ownerId: String)

  @Query("SELECT * FROM files WHERE ownerId = :ownerId")
  suspend fun getAllFilesForUserSync(ownerId: String): List<CloudFile>

  // Admin stats queries
  @Query("SELECT COUNT(*) FROM files WHERE status = 'active'")
  fun getTotalFilesCount(): Flow<Int>

  @Query("SELECT COALESCE(SUM(sizeBytes), 0) FROM files WHERE status != 'deleted'")
  fun getSystemTotalStorageUsed(): Flow<Long>
}
