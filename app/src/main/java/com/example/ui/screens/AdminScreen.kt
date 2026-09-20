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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloudFile
import com.example.data.model.User
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.StorageCriticalRed
import com.example.ui.theme.StorageNormalGreen
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.StorageViewModel

@Composable
fun AdminScreen(
  viewModel: StorageViewModel,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val users by viewModel.adminUsers.collectAsState()
  val totalFiles by viewModel.adminTotalFiles.collectAsState()
  val totalStorageUsed by viewModel.adminTotalStorage.collectAsState()
  val searchQuery by viewModel.adminSearchQuery.collectAsState()

  val totalUsersCount = users.size
  val activeUsersCount = users.count { it.status == "active" }
  val suspendedUsersCount = users.count { it.status == "suspended" }
  val totalAllocatedMB = totalUsersCount * 1000L

  val avgUsedBytes = if (totalUsersCount > 0) totalStorageUsed / totalUsersCount else 0L

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { viewModel.navigateTo(ScreenDestination.PROFILE) },
        modifier = Modifier.testTag("admin_back_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = Strings.navAdmin(language),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = if (language == AppLanguage.BENGALI) "সিস্টেম সার্বিক পরিসংখ্যান ও ব্যবহারকারী নিয়ন্ত্রণ" else "System Analytics & User Control",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      // Metric Cards Grid
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            AdminMetricCard(
              title = if (language == AppLanguage.BENGALI) "মোট ব্যবহারকারী" else "Total Users",
              value = "$totalUsersCount",
              subtitle = if (language == AppLanguage.BENGALI) "সক্রিয়: $activeUsersCount | স্থগিত: $suspendedUsersCount" else "Active: $activeUsersCount | Suspended: $suspendedUsersCount",
              icon = Icons.Default.Group,
              modifier = Modifier.weight(1f)
            )

            AdminMetricCard(
              title = if (language == AppLanguage.BENGALI) "মোট আপলোডকৃত ফাইল" else "Total Files",
              value = "$totalFiles",
              subtitle = if (language == AppLanguage.BENGALI) "সক্রিয় ফাইল সংখ্যা" else "Active stored files",
              icon = Icons.Default.Folder,
              modifier = Modifier.weight(1f)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            AdminMetricCard(
              title = if (language == AppLanguage.BENGALI) "মোট বরাদ্দ স্টোরেজ" else "Total Quota",
              value = "$totalAllocatedMB MB",
              subtitle = "1000 MB × $totalUsersCount",
              icon = Icons.Default.Storage,
              modifier = Modifier.weight(1f)
            )

            AdminMetricCard(
              title = if (language == AppLanguage.BENGALI) "ব্যবহৃত মোট ক্লাউড স্পেস" else "Total Used Space",
              value = CloudFile.formatMBOnly(totalStorageUsed),
              subtitle = if (language == AppLanguage.BENGALI) "গড়: ${CloudFile.formatMBOnly(avgUsedBytes)}" else "Avg: ${CloudFile.formatMBOnly(avgUsedBytes)}",
              icon = Icons.Default.CloudDone,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // Search Users Bar
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setAdminSearchQuery(it) },
          placeholder = { Text(if (language == AppLanguage.BENGALI) "মোবাইল নম্বর দিয়ে ব্যবহারকারী খুঁজুন..." else "Search user by phone number...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_search_input")
        )
      }

      item {
        Text(
          text = if (language == AppLanguage.BENGALI) "নিবন্ধিত ব্যবহারকারী তালিকা" else "Registered Users List",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      // User List
      items(users, key = { it.userId }) { userItem ->
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
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = userItem.phoneNumber,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = if (userItem.status == "active") StorageNormalGreen.copy(alpha = 0.15f) else StorageCriticalRed.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = if (userItem.status == "active") "Active" else "Suspended",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (userItem.status == "active") StorageNormalGreen else StorageCriticalRed,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "${CloudFile.formatMBOnly(userItem.storageUsedBytes)} / 1000 MB • ${String.format(java.util.Locale.US, "%.1f%%", userItem.usagePercentage)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
              )
            }

            // Suspend / Activate Button
            Button(
              onClick = { viewModel.toggleSuspendUser(userItem.userId, userItem.status) },
              colors = if (userItem.status == "active") {
                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error)
              } else {
                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.primary)
              },
              shape = RoundedCornerShape(10.dp)
            ) {
              Text(
                text = if (userItem.status == "active") {
                  if (language == AppLanguage.BENGALI) "স্থগিত করুন" else "Suspend"
                } else {
                  if (language == AppLanguage.BENGALI) "সক্রিয় করুন" else "Reactivate"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }
}

@Composable
fun AdminMetricCard(
  title: String,
  value: String,
  subtitle: String,
  icon: ImageVector,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
