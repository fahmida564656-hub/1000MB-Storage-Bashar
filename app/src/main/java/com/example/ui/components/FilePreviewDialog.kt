package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.data.model.CloudFile
import com.example.data.model.FileCategory
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import java.io.File

@Composable
fun FilePreviewDialog(
  file: CloudFile,
  language: AppLanguage,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val localFile = remember(file.localFilePath) { File(file.localFilePath) }
  val fileExists = localFile.exists()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .fillMaxHeight(0.85f)
        .testTag("file_preview_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = file.fileName,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "${file.getFormattedSize()} • ${file.category.getDisplayName(language)}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("preview_close_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Center Content Preview Body
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          contentAlignment = Alignment.Center
        ) {
          if (!fileExists) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(24.dp)
            ) {
              Text("⚠️", fontSize = 48.sp)
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "ফাইলটি স্টোরেজে পাওয়া যায়নি।",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
              )
            }
          } else {
            when (file.category) {
              FileCategory.IMAGES -> {
                AsyncImage(
                  model = localFile,
                  contentDescription = file.fileName,
                  contentScale = ContentScale.Fit,
                  modifier = Modifier.fillMaxSize()
                )
              }
              FileCategory.AUDIO -> {
                AudioPlaybackPreview(file = localFile, language = language)
              }
              FileCategory.VIDEOS -> {
                VideoActionPreview(file = localFile, context = context, language = language)
              }
              FileCategory.PDF, FileCategory.DOCUMENTS -> {
                DocumentActionPreview(file = file, localFile = localFile, context = context, language = language)
              }
              else -> {
                // Unsupported preview banner
                UnsupportedPreview(file = file, language = language)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Actions: Download & Share
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = { shareFile(context, localFile, file.mimeType) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (language == AppLanguage.BENGALI) "শেয়ার" else "Share")
          }

          Button(
            onClick = { exportFile(context, localFile, file.fileName) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("preview_download_button")
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(Strings.download(language))
          }
        }
      }
    }
  }
}

@Composable
fun AudioPlaybackPreview(file: File, language: AppLanguage) {
  var isPlaying by remember { mutableStateOf(false) }
  var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

  DisposableEffect(file) {
    val player = MediaPlayer().apply {
      try {
        setDataSource(file.absolutePath)
        prepare()
      } catch (e: Exception) {
        e.printStackTrace()
      }
      setOnCompletionListener {
        isPlaying = false
      }
    }
    mediaPlayer = player

    onDispose {
      player.stop()
      player.release()
    }
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(24.dp)
  ) {
    Text("🎵", fontSize = 64.sp)
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = file.name,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(20.dp))

    IconButton(
      onClick = {
        mediaPlayer?.let { player ->
          if (isPlaying) {
            player.pause()
            isPlaying = false
          } else {
            player.start()
            isPlaying = true
          }
        }
      },
      modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary)
    ) {
      Icon(
        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
        contentDescription = "Play/Pause",
        tint = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.size(36.dp)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))
    Text(
      text = if (isPlaying) {
        if (language == AppLanguage.BENGALI) "প্লে হচ্ছে..." else "Playing..."
      } else {
        if (language == AppLanguage.BENGALI) "বাজাতে চাপুন" else "Tap to Play"
      },
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
fun VideoActionPreview(file: File, context: Context, language: AppLanguage) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(24.dp)
  ) {
    Text("🎬", fontSize = 64.sp)
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = file.name,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(
      onClick = {
        try {
          val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
          val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "video/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
          }
          context.startActivity(intent)
        } catch (e: Exception) {
          // fallback
        }
      },
      shape = RoundedCornerShape(12.dp)
    ) {
      Icon(Icons.Default.PlayArrow, contentDescription = null)
      Spacer(modifier = Modifier.width(6.dp))
      Text(if (language == AppLanguage.BENGALI) "ভিডিও চালু করুন" else "Play Video")
    }
  }
}

@Composable
fun DocumentActionPreview(file: CloudFile, localFile: File, context: Context, language: AppLanguage) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(24.dp)
  ) {
    Text(file.category.getEmoji(), fontSize = 64.sp)
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = file.fileName,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = file.getFormattedSize(),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(
      onClick = {
        try {
          val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", localFile)
          val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, file.mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
          }
          context.startActivity(intent)
        } catch (e: Exception) {
          // fallback
        }
      },
      shape = RoundedCornerShape(12.dp)
    ) {
      Text(if (language == AppLanguage.BENGALI) "অ্যাপে খুলুন" else "Open in App")
    }
  }
}

@Composable
fun UnsupportedPreview(file: CloudFile, language: AppLanguage) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(24.dp)
  ) {
    Text(file.category.getEmoji(), fontSize = 64.sp)
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = file.fileName,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = Strings.previewNotSupported(language),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )
  }
}

private fun shareFile(context: Context, file: File, mimeType: String) {
  try {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
      type = mimeType
      putExtra(Intent.EXTRA_STREAM, uri)
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share File"))
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

private fun exportFile(context: Context, file: File, fileName: String) {
  // Save or share to system download
  shareFile(context, file, "*/*")
}
