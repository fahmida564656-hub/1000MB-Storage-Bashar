package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloudFile
import com.example.ui.components.FileUploadSection
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.StorageViewModel

@Composable
fun UploadScreen(
  viewModel: StorageViewModel,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val uploadState by viewModel.uploadState.collectAsState()
  val usedBytes by viewModel.storageUsedBytes.collectAsState()
  val scrollState = rememberScrollState()

  val remainingBytes = (1048576000L - usedBytes).coerceAtLeast(0L)

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { viewModel.navigateTo(ScreenDestination.DASHBOARD) },
        modifier = Modifier.testTag("upload_screen_back_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
      }
      Spacer(modifier = Modifier.padding(start = 8.dp))
      Column {
        Text(
          text = Strings.uploadTitle(language),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "${Strings.remaining(language)}: ${CloudFile.formatMBOnly(remainingBytes)} / 1000 MB",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Main Upload Interactive Component
    FileUploadSection(
      uploadState = uploadState,
      language = language,
      onPickFiles = { uris -> viewModel.uploadFiles(uris) },
      onCancelUpload = { viewModel.cancelUpload() }
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Supported formats and permanent storage info
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.padding(start = 8.dp))
          Text(
            text = if (language == AppLanguage.BENGALI) "স্টোরেজ ও ফাইল নীতি" else "Storage & Upload Policy",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = if (language == AppLanguage.BENGALI)
            "• সমর্থনযোগ্য ফাইল: JPG, PNG, WEBP, GIF, MP4, MKV, MP3, WAV, PDF, DOCX, XLSX, ZIP, ইত্যাদি।\n" +
            "• স্থায়ী সংরক্ষণ: আপনার আপলোডকৃত ফাইল চিরতরে নিরাপদ থাকে।\n" +
            "• এনক্রিপশন: সম্পূর্ণ ক্লাউড আইসোলেশন ও ব্যক্তিগত স্টোরেজ সুরক্ষিত থাকে।\n" +
            "• কোটা সীমা: সর্বোচ্চ ১০০০ MB পর্যন্ত সীমাহীন সংখ্যক ফাইল আপলোড করতে পারবেন।"
          else
            "• Supported Formats: JPG, PNG, WEBP, GIF, MP4, MKV, MP3, WAV, PDF, DOCX, XLSX, ZIP, etc.\n" +
            "• Permanent Retention: Your uploaded files stay safe forever.\n" +
            "• Encryption: Multi-tenant private isolation; strictly no unauthorized public sharing.\n" +
            "• Quota: Upload unlimited files up to your exact 1000 MB limit.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 22.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(80.dp))
  }
}
