package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloudFile
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.StorageCriticalRed
import com.example.ui.theme.StorageFullCrimson
import com.example.ui.theme.StorageHighOrange
import com.example.ui.theme.StorageNormalGreen
import com.example.ui.theme.StorageWarningAmber
import java.util.Locale

@Composable
fun StorageIndicatorCard(
  usedBytes: Long,
  quotaBytes: Long = 1048576000L, // 1000 MB
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val usedMB = usedBytes / (1024.0 * 1024.0)
  val totalMB = quotaBytes / (1024.0 * 1024.0)
  val remainingBytes = (quotaBytes - usedBytes).coerceAtLeast(0L)
  val remainingMB = remainingBytes / (1024.0 * 1024.0)
  val ratio = if (quotaBytes > 0) (usedBytes.toDouble() / quotaBytes.toDouble()).coerceIn(0.0, 1.0).toFloat() else 0f
  val percent = ratio * 100f

  val animatedProgress by animateFloatAsState(
    targetValue = ratio,
    animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
    label = "StorageProgress"
  )

  // Status visual color and Bengali warning text
  val (statusColor, warningText, statusIcon) = when {
    percent >= 100f -> Triple(StorageFullCrimson, Strings.warning100(language), Icons.Default.Warning)
    percent >= 95f -> Triple(StorageCriticalRed, Strings.warning95(language), Icons.Default.Warning)
    percent >= 90f -> Triple(StorageHighOrange, Strings.warning90(language), Icons.Default.Warning)
    percent >= 80f -> Triple(StorageWarningAmber, Strings.warning80(language), Icons.Default.Warning)
    percent >= 50f -> Triple(MaterialTheme.colorScheme.primary, Strings.warningNormal(language), Icons.Default.Cloud)
    else -> Triple(StorageNormalGreen, Strings.warningNormal(language), Icons.Default.Cloud)
  }

  val animatedCardColor by animateColorAsState(
    targetValue = statusColor,
    animationSpec = tween(durationMillis = 600),
    label = "StatusColor"
  )

  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("storage_indicator_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Top header with "স্থায়ী স্টোরেজ" permanent badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(animatedCardColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = statusIcon,
              contentDescription = null,
              tint = animatedCardColor,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = Strings.myStorage(language),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = Strings.permanentStorageShort(language),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // Permanent Badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (language == AppLanguage.BENGALI) "স্থায়ী" else "Permanent",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Usage Numbers Display
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Text(
            text = String.format(Locale.US, "%.1f MB", usedMB),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${Strings.totalQuota(language)}: 1000 MB",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = String.format(Locale.US, "%.1f%%", percent),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = animatedCardColor
          )
          Text(
            text = "${String.format(Locale.US, "%.1f MB", remainingMB)} ${Strings.remaining(language)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Animated Storage Progress Bar
      LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(12.dp)
          .clip(RoundedCornerShape(6.dp))
          .testTag("storage_progress_bar"),
        color = animatedCardColor,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Text Warning / Status Notice
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = animatedCardColor.copy(alpha = 0.1f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(animatedCardColor)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = warningText,
            style = MaterialTheme.typography.bodySmall,
            color = if (percent >= 80f) animatedCardColor else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (percent >= 80f) FontWeight.SemiBold else FontWeight.Normal
          )
        }
      }
    }
  }
}
