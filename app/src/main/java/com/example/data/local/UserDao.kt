package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
  fun getUserById(userId: String): Flow<User?>

  @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
  suspend fun getUserByIdSync(userId: String): User?

  @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
  suspend fun getUserByPhone(phoneNumber: String): User?

  @Query("SELECT * FROM users ORDER BY createdAt DESC")
  fun getAllUsers(): Flow<List<User>>

  @Query("SELECT * FROM users WHERE phoneNumber LIKE '%' || :query || '%' ORDER BY createdAt DESC")
  fun searchUsersByPhone(query: String): Flow<List<User>>

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insertUser(user: User)

  @Update
  suspend fun updateUser(user: User)

  @Query("UPDATE users SET storageUsedBytes = :usedBytes, updatedAt = :updatedAt WHERE userId = :userId")
  suspend fun updateStorageUsage(userId: String, usedBytes: Long, updatedAt: Long = System.currentTimeMillis())

  @Query("UPDATE users SET passwordHash = :newHash, salt = :newSalt, updatedAt = :updatedAt WHERE userId = :userId")
  suspend fun updatePassword(userId: String, newHash: String, newSalt: String, updatedAt: Long = System.currentTimeMillis())

  @Query("UPDATE users SET status = :status, updatedAt = :updatedAt WHERE userId = :userId")
  suspend fun updateUserStatus(userId: String, status: String, updatedAt: Long = System.currentTimeMillis())

  @Query("DELETE FROM users WHERE userId = :userId")
  suspend fun deleteUser(userId: String)
}
