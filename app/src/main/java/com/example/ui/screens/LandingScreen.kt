package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.BrandSkyDark
import com.example.ui.theme.BrandSkyPrimary
import com.example.ui.theme.BrandTeal

@Composable
fun LandingScreen(
  language: AppLanguage,
  onLanguageToggle: () -> Unit,
  onNavigateLogin: () -> Unit,
  onNavigateRegister: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Language Selector Pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.testTag("landing_language_toggle")
        ) {
          OutlinedButton(
            onClick = onLanguageToggle,
            shape = RoundedCornerShape(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Text(
              text = if (language == AppLanguage.BENGALI) "English" else "বাংলা",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Brand Hero Graphic
      Box(
        modifier = Modifier
          .size(100.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(
              colors = listOf(BrandSkyPrimary, BrandTeal)
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Cloud,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(56.dp)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // App Title
      Text(
        text = Strings.appName(language),
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Tagline
      Text(
        text = Strings.tagline(language),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Subheadline
      Text(
        text = Strings.subheadline(language),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp,
        modifier = Modifier.padding(horizontal = 12.dp)
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Highlight Quota Banner: 1000 MB Permanent
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "1GB",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Text(
              text = if (language == AppLanguage.BENGALI) "১০০০ MB স্থায়ী ক্লাউড স্পেস" else "1000 MB Permanent Cloud Space",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = if (language == AppLanguage.BENGALI)
                "কোনো লুকানো খরচ নেই, কোনো মেয়াদ শেষ নেই"
              else
                "No hidden costs, no expiration date",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Core Features Grid
      Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        FeatureItem(
          icon = Icons.Default.PhoneAndroid,
          title = if (language == AppLanguage.BENGALI) "মোবাইল নম্বর দিয়ে সহজ লগইন" else "Easy Mobile Number Login",
          desc = if (language == AppLanguage.BENGALI)
            "আপনার নিজস্ব মোবাইল নম্বর ও পাসওয়ার্ড দিয়ে সুরক্ষিত প্রবেশ।"
          else
            "Secure access using your personal phone number and password."
        )

        FeatureItem(
          icon = Icons.Default.Lock,
          title = if (language == AppLanguage.BENGALI) "নিরাপদ এনক্রিপশন ও আইসোলেশন" else "Safe Encryption & Isolation",
          desc = if (language == AppLanguage.BENGALI)
            "প্রতিটি ব্যবহারকারীর নিজস্ব সুরক্ষিত ফোল্ডার; অন্য কেউ অ্যাক্সেস করতে পারবে না।"
          else
            "Isolated private directories; zero unauthorized cross-user access."
        )

        FeatureItem(
          icon = Icons.Default.Speed,
          title = if (language == AppLanguage.BENGALI) "দ্রুত আপলোড ও সরাসরি প্রিভিউ" else "Fast Upload & Direct Preview",
          desc = if (language == AppLanguage.BENGALI)
            "ছবি, অডিও, ভিডিও ও ডকুমেন্ট সরাসরি অ্যাপে দেখার সুবিধা।"
          else
            "In-app image, audio playback, video viewing, and document preview."
        )
      }

      Spacer(modifier = Modifier.height(36.dp))

      // Action Buttons: Login & Register
      Button(
        onClick = onNavigateLogin,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("landing_login_button")
      ) {
        Text(
          text = Strings.login(language),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedButton(
        onClick = onNavigateRegister,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("landing_register_button")
      ) {
        Text(
          text = Strings.register(language),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun FeatureItem(icon: ImageVector, title: String, desc: String) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = desc,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
