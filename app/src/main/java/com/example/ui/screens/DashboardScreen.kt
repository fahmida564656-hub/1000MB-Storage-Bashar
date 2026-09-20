package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FileCategory
import com.example.ui.components.CategoryCard
import com.example.ui.components.FileItemCard
import com.example.ui.components.NotificationsBottomSheet
import com.example.ui.components.StorageIndicatorCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.StorageViewModel
import com.example.ui.viewmodel.ViewMode

@Composable
fun DashboardScreen(
  viewModel: StorageViewModel,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val user by viewModel.currentUser.collectAsState()
  val usedBytes by viewModel.storageUsedBytes.collectAsState()
  val activeFiles by viewModel.rawActiveFiles.collectAsState()
  val notifications by viewModel.notifications.collectAsState()
  val unreadNotifications by viewModel.storageRepository.getUnreadNotificationsCount(user?.userId ?: "").collectAsState(initial = 0)

  var showNotificationsSheet by remember { mutableStateOf(false) }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // Top Bar
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (language == AppLanguage.BENGALI) "স্বাগতম," else "Welcome,",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = user?.displayName ?: user?.phoneNumber ?: "User",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Language switcher
            IconButton(
              onClick = {
                viewModel.setLanguage(if (language == AppLanguage.BENGALI) AppLanguage.ENGLISH else AppLanguage.BENGALI)
              },
              modifier = Modifier.testTag("dashboard_language_toggle")
            ) {
              Icon(Icons.Default.Translate, contentDescription = "Switch Language")
            }

            // Notification Bell with Badge
            IconButton(
              onClick = { showNotificationsSheet = true },
              modifier = Modifier.testTag("dashboard_notifications_button")
            ) {
              BadgedBox(
                badge = {
                  if (unreadNotifications > 0) {
                    Badge { Text(unreadNotifications.toString()) }
                  }
                }
              ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
              }
            }
          }
        }
      }

      // Storage Quota Card (Animated, strict 1000 MB, Bengali warnings)
      item {
        StorageIndicatorCard(
          usedBytes = usedBytes,
          quotaBytes = 1048576000L,
          language = language
        )
      }

      // Quick Upload Callout
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (language == AppLanguage.BENGALI) "নতুন ফাইল ব্যাকআপ করুন" else "Backup New Files",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = if (language == AppLanguage.BENGALI) "যেকোনো ফাইল আপলোড করে নিরাপদে রাখুন" else "Upload any file to keep it permanently safe",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Button(
              onClick = { viewModel.navigateTo(ScreenDestination.UPLOAD) },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("dashboard_quick_upload_button")
            ) {
              Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(Strings.navUpload(language))
            }
          }
        }
      }

      // Categories Section Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (language == AppLanguage.BENGALI) "ক্যাটাগরি সমূহ" else "Categories",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          TextButton(
            onClick = {
              viewModel.setSelectedCategory(null)
              viewModel.navigateTo(ScreenDestination.FILES)
            }
          ) {
            Text(if (language == AppLanguage.BENGALI) "সব দেখুন" else "View All")
          }
        }
      }

      // Categories Grid 2x2
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          val categories = FileCategory.values()
          for (i in categories.indices step 2) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              val cat1 = categories[i]
              val (count1, bytes1) = viewModel.getCategoryUsage(cat1)
              CategoryCard(
                category = cat1,
                fileCount = count1,
                bytesUsed = bytes1,
                language = language,
                onClick = {
                  viewModel.setSelectedCategory(cat1)
                  viewModel.navigateTo(ScreenDestination.FILES)
                },
                modifier = Modifier.weight(1f)
              )

              if (i + 1 < categories.size) {
                val cat2 = categories[i + 1]
                val (count2, bytes2) = viewModel.getCategoryUsage(cat2)
                CategoryCard(
                  category = cat2,
                  fileCount = count2,
                  bytesUsed = bytes2,
                  language = language,
                  onClick = {
                    viewModel.setSelectedCategory(cat2)
                    viewModel.navigateTo(ScreenDestination.FILES)
                  },
                  modifier = Modifier.weight(1f)
                )
              } else {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
      }

      // Recent Files Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (language == AppLanguage.BENGALI) "সাম্প্রতিক আপলোডকৃত ফাইল" else "Recent Uploads",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          if (activeFiles.isNotEmpty()) {
            TextButton(onClick = { viewModel.navigateTo(ScreenDestination.FILES) }) {
              Text(if (language == AppLanguage.BENGALI) "ফাইল ম্যানেজার" else "File Manager")
            }
          }
        }
      }

      // Recent Files List (up to 5)
      val recentFiles = activeFiles.take(5)
      if (recentFiles.isEmpty()) {
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp)
          ) {
            Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("📂", fontSize = 40.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = if (language == AppLanguage.BENGALI) "এখনো কোনো ফাইল আপলোড করা হয়নি" else "No files uploaded yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = { viewModel.navigateTo(ScreenDestination.UPLOAD) },
                shape = RoundedCornerShape(12.dp)
              ) {
                Text(Strings.uploadTitle(language))
              }
            }
          }
        }
      } else {
        items(recentFiles, key = { it.fileId }) { file ->
          FileItemCard(
            file = file,
            language = language,
            isGrid = false,
            onPreview = { viewModel.setPreviewFile(file) },
            onDownload = { viewModel.setPreviewFile(file) },
            onRename = { viewModel.setRenameTarget(file) },
            onDelete = { viewModel.moveToTrash(file.fileId) }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }

    // Floating Action Upload Button
    FloatingActionButton(
      onClick = { viewModel.navigateTo(ScreenDestination.UPLOAD) },
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(24.dp)
        .testTag("dashboard_fab_upload")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Upload File", modifier = Modifier.size(28.dp))
    }
  }

  // Notifications Bottom Sheet
  if (showNotificationsSheet) {
    NotificationsBottomSheet(
      notifications = notifications,
      language = language,
      onDismiss = { showNotificationsSheet = false },
      onMarkAllRead = {
        viewModel.markAllNotificationsRead()
      }
    )
  }
}
