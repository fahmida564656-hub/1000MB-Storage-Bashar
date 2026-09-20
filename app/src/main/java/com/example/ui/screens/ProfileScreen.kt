package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloudFile
import com.example.ui.components.ConfirmationDialog
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.AppThemeMode
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.StorageViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
  viewModel: StorageViewModel,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val user by viewModel.currentUser.collectAsState()
  val usedBytes by viewModel.storageUsedBytes.collectAsState()
  val themeMode by viewModel.themeMode.collectAsState()

  var showLogoutConfirm by remember { mutableStateOf(false) }
  var showDeleteAccountDialog by remember { mutableStateOf(false) }
  var deletePasswordInput by remember { mutableStateOf("") }
  var deleteError by remember { mutableStateOf<String?>(null) }
  var showConfigInfoDialog by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp)
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    // Profile Header Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = user?.displayName ?: "User",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Phone,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = user?.phoneNumber ?: "",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          if (user?.role == "admin") {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
            ) {
              Text(
                text = "ADMINISTRATOR",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Permanent Quota Verification Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = Strings.permanentStorageBadge(language),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = if (language == AppLanguage.BENGALI) "বরাদ্দ কোটা" else "Allocated Quota",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "1000 MB (1 GB)",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold
            )
          }

          Column {
            Text(
              text = Strings.used(language),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = CloudFile.formatMBOnly(usedBytes),
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold
            )
          }

          Column {
            Text(
              text = Strings.remaining(language),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val rem = (1048576000L - usedBytes).coerceAtLeast(0L)
            Text(
              text = CloudFile.formatMBOnly(rem),
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = Strings.settings(language),
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Setting: Language
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(12.dp))
          Text(text = Strings.languageLabel(language), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          OutlinedButton(
            onClick = { viewModel.setLanguage(AppLanguage.BENGALI) },
            shape = RoundedCornerShape(10.dp),
            colors = if (language == AppLanguage.BENGALI) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors()
          ) {
            Text("বাংলা", fontWeight = FontWeight.Bold)
          }
          OutlinedButton(
            onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) },
            shape = RoundedCornerShape(10.dp),
            colors = if (language == AppLanguage.ENGLISH) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors()
          ) {
            Text("English", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Setting: Theme
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.BrightnessMedium, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(12.dp))
          Text(text = Strings.themeLabel(language), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          OutlinedButton(
            onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) },
            shape = RoundedCornerShape(10.dp),
            colors = if (themeMode == AppThemeMode.LIGHT) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors()
          ) {
            Text("Light")
          }
          OutlinedButton(
            onClick = { viewModel.setThemeMode(AppThemeMode.DARK) },
            shape = RoundedCornerShape(10.dp),
            colors = if (themeMode == AppThemeMode.DARK) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors()
          ) {
            Text("Dark")
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Admin Dashboard Shortcut (if admin or accessible)
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(12.dp))
          Text(text = Strings.navAdmin(language), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }

        Button(
          onClick = { viewModel.navigateTo(ScreenDestination.ADMIN) },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("profile_admin_panel_button")
        ) {
          Text(if (language == AppLanguage.BENGALI) "প্রবেশ করুন" else "Enter")
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Setting: Cloud & SMS Configuration
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Cloud, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = if (language == AppLanguage.BENGALI) "ক্লাউড ও SMS তথ্য" else "Cloud & SMS Setup",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
          )
        }

        OutlinedButton(
          onClick = { showConfigInfoDialog = true },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text(if (language == AppLanguage.BENGALI) "দেখুন" else "View")
        }
      }
    }

    Spacer(modifier = Modifier.height(28.dp))

    // Logout Button
    Button(
      onClick = { showLogoutConfirm = true },
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("profile_logout_button")
    ) {
      Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text(Strings.logout(language), fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Delete Account Button
    OutlinedButton(
      onClick = { showDeleteAccountDialog = true; deleteError = null; deletePasswordInput = "" },
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("profile_delete_account_button")
    ) {
      Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text(Strings.deleteAccount(language), fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(80.dp))
  }

  // Logout Confirmation
  if (showLogoutConfirm) {
    ConfirmationDialog(
      title = Strings.logout(language),
      message = if (language == AppLanguage.BENGALI) "আপনি কি নিশ্চিত যে অ্যাকাউন্ট থেকে লগআউট করতে চান?" else "Are you sure you want to log out?",
      confirmText = Strings.logout(language),
      dismissText = if (language == AppLanguage.BENGALI) "বাতিল" else "Cancel",
      onDismiss = { showLogoutConfirm = false },
      onConfirm = {
        showLogoutConfirm = false
        viewModel.logout()
      }
    )
  }

  // Delete Account Dialog
  if (showDeleteAccountDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteAccountDialog = false },
      title = {
        Text(
          text = if (language == AppLanguage.BENGALI) "অ্যাকাউন্ট ও সমস্ত ফাইল মুছবেন?" else "Delete account and all files?",
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.error
        )
      },
      text = {
        Column {
          Text(
            text = if (language == AppLanguage.BENGALI)
              "সতর্কতা: এটি স্থায়ীভাবে আপনার ১০০০ MB স্টোরেজ, সমস্ত আপলোডকৃত ছবি, ভিডিও, ফাইল এবং অ্যাকাউন্ট প্রোফাইল চিরতরে মুছে দেবে। নিশ্চিত করতে পাসওয়ার্ড দিন:"
            else
              "Warning: This will permanently wipe your 1000 MB storage quota, all uploaded files, and account profile. Enter your password to confirm:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = deletePasswordInput,
            onValueChange = { deletePasswordInput = it; deleteError = null },
            label = { Text(Strings.password(language)) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          if (deleteError != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = deleteError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteAccount(deletePasswordInput) { success, msg ->
              if (success) {
                showDeleteAccountDialog = false
              } else {
                deleteError = msg
              }
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text(Strings.deleteAccount(language))
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showDeleteAccountDialog = false }) {
          Text(if (language == AppLanguage.BENGALI) "বাতিল" else "Cancel")
        }
      }
    )
  }

  // Config Info Dialog
  if (showConfigInfoDialog) {
    AlertDialog(
      onDismissRequest = { showConfigInfoDialog = false },
      title = {
        Text(
          text = if (language == AppLanguage.BENGALI) "ক্লাউড ও সার্ভার কনফিগারেশন" else "Cloud & Server Setup",
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column {
          Text(
            text = if (language == AppLanguage.BENGALI)
              "• স্টোরেজ সীমা: ১০০০ MB (১ GB) স্থায়ী\n• ব্যাকএন্ড আর্কিটেকচার: Cloud Firestore + Cloud Storage (Private isolate buckets)\n• পাসওয়ার্ড সুরক্ষা: Salted PBKDF2/SHA-256 এনক্রিপশন\n• SMS গেটওয়ে: Twilio / Greenweb / SSL Wireless প্রস্তুত (.env ফাইলে API কী কনফিগার করুন)\n• ডিপ্লয়মেন্ট গাইড: প্রকল্প রুটে DEPLOYMENT.md, firestore.rules, storage.rules ও functions/ অন্তর্ভুক্ত রয়েছে।"
            else
              "• Quota: Exactly 1000 MB (1 GB) Permanent\n• Architecture: Cloud Firestore + Cloud Storage (Private isolated buckets)\n• Security: Salted PBKDF2/SHA-256\n• SMS Gateway: Ready for Twilio / Greenweb / SSL Wireless (configure in .env)\n• Deployment Guide: DEPLOYMENT.md, firestore.rules, storage.rules & functions/ included at project root.",
            style = MaterialTheme.typography.bodySmall,
            lineHeight = 20.sp
          )
        }
      },
      confirmButton = {
        Button(onClick = { showConfigInfoDialog = false }) {
          Text("ঠিক আছে")
        }
      }
    )
  }
}
