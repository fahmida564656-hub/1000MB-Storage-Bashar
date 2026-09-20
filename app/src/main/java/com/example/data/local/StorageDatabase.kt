package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.AppNotification
import com.example.data.model.CloudFile
import com.example.data.model.FileCategory
import com.example.data.model.User

class Converters {
  @TypeConverter
  fun fromCategory(category: FileCategory): String = category.name

  @TypeConverter
  fun toCategory(value: String): FileCategory = try {
    FileCategory.valueOf(value)
  } catch (e: Exception) {
    FileCategory.OTHER
  }
}

@Database(
  entities = [User::class, CloudFile::class, AppNotification::class],
  version = 1,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StorageDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun fileDao(): FileDao
  abstract fun notificationDao(): NotificationDao

  companion object {
    @Volatile
    private var INSTANCE: StorageDatabase? = null

    fun getDatabase(context: Context): StorageDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          StorageDatabase::class.java,
          "storage_1000mb_db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
