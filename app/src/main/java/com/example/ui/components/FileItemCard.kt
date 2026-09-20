package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CloudFile
import com.example.data.model.FileCategory
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import java.io.File

@Composable
fun FileItemCard(
  file: CloudFile,
  language: AppLanguage,
  isGrid: Boolean,
  onPreview: () -> Unit,
  onDownload: () -> Unit,
  onRename: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }

  if (isGrid) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
      modifier = modifier
        .fillMaxWidth()
        .clickable(onClick = onPreview)
        .testTag("file_grid_item_${file.fileId}")
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Thumbnail or category art
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
          contentAlignment = Alignment.Center
        ) {
          if (file.category == FileCategory.IMAGES && File(file.localFilePath).exists()) {
            AsyncImage(
              model = File(file.localFilePath),
              contentDescription = file.fileName,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          } else {
            Surface(
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.fillMaxSize()
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = file.category.getEmoji(),
                  fontSize = 38.sp
                )
              }
            }
          }

          // Format badge
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(8.dp)
          ) {
            Text(
              text = file.fileType.uppercase(),
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        // Details
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = file.fileName,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f)
            )

            Box {
              IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.MoreVert,
                  contentDescription = "Options",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
              }
              FileActionMenu(
                expanded = menuExpanded,
                language = language,
                onDismiss = { menuExpanded = false },
                onPreview = onPreview,
                onDownload = onDownload,
                onRename = onRename,
                onDelete = onDelete
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = file.getFormattedSize(),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = file.category.getDisplayName(language),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  } else {
    // List View
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
      modifier = modifier
        .fillMaxWidth()
        .clickable(onClick = onPreview)
        .testTag("file_list_item_${file.fileId}")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Thumbnail
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          if (file.category == FileCategory.IMAGES && File(file.localFilePath).exists()) {
            AsyncImage(
              model = File(file.localFilePath),
              contentDescription = file.fileName,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          } else {
            Surface(
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.fillMaxSize()
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = file.category.getEmoji(), fontSize = 22.sp)
              }
            }
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name & Meta
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = file.fileName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = file.getFormattedSize(),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = " • ",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = file.getFormattedDate(),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Box {
          IconButton(onClick = { menuExpanded = true }) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "Options",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          FileActionMenu(
            expanded = menuExpanded,
            language = language,
            onDismiss = { menuExpanded = false },
            onPreview = onPreview,
            onDownload = onDownload,
            onRename = onRename,
            onDelete = onDelete
          )
        }
      }
    }
  }
}

@Composable
fun FileActionMenu(
  expanded: Boolean,
  language: AppLanguage,
  onDismiss: () -> Unit,
  onPreview: () -> Unit,
  onDownload: () -> Unit,
  onRename: () -> Unit,
  onDelete: () -> Unit
) {
  DropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismiss
  ) {
    DropdownMenuItem(
      text = { Text(Strings.preview(language)) },
      leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null) },
      onClick = { onDismiss(); onPreview() }
    )
    DropdownMenuItem(
      text = { Text(Strings.download(language)) },
      leadingIcon = { Icon(Icons.Default.Download, contentDescription = null) },
      onClick = { onDismiss(); onDownload() }
    )
    DropdownMenuItem(
      text = { Text(Strings.rename(language)) },
      leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
      onClick = { onDismiss(); onRename() }
    )
    DropdownMenuItem(
      text = { Text(Strings.delete(language), color = MaterialTheme.colorScheme.error) },
      leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
      onClick = { onDismiss(); onDelete() }
    )
  }
}
