package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
  @PrimaryKey val userId: String,
  val phoneNumber: String,
  val displayName: String,
  val passwordHash: String,
  val salt: String,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val status: String = "active", // active, suspended
  val role: String = "user", // user, admin
  val storageQuotaBytes: Long = 1048576000L, // exactly 1000 MB (1000 * 1024 * 1024)
  val storageUsedBytes: Long = 0L
) {
  val remainingBytes: Long
    get() = (storageQuotaBytes - storageUsedBytes).coerceAtLeast(0L)

  val usagePercentage: Float
    get() = if (storageQuotaBytes > 0) {
      ((storageUsedBytes.toDouble() / storageQuotaBytes.toDouble()) * 100.0).toFloat().coerceIn(0f, 100f)
    } else 0f
}
