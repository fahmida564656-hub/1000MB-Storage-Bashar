package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AppNotification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
  @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
  fun getNotifications(userId: String): Flow<List<AppNotification>>

  @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
  fun getUnreadCount(userId: String): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: AppNotification)

  @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
  suspend fun markAllAsRead(userId: String)

  @Query("DELETE FROM notifications WHERE userId = :userId")
  suspend fun clearUserNotifications(userId: String)
}
