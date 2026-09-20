package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "notifications")
data class AppNotification(
  @PrimaryKey val id: String,
  val userId: String,
  val titleBn: String,
  val titleEn: String,
  val messageBn: String,
  val messageEn: String,
  val type: String, // UPLOAD, DELETE, RESTORE, QUOTA, SECURITY
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false
) {
  fun getFormattedTime(): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
  }
}
