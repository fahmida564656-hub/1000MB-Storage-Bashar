package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = BrandSkyAccent,
  onPrimary = Color(0xFF003355),
  primaryContainer = Color(0xFF004C74),
  onPrimaryContainer = Color(0xFFC2E8FF),
  secondary = BrandTealAccent,
  onSecondary = Color(0xFF003731),
  secondaryContainer = Color(0xFF005047),
  onSecondaryContainer = Color(0xFF70F7E5),
  tertiary = Color(0xFFA5B4FC),
  background = SurfaceDark,
  surface = SurfaceDarkCard,
  onBackground = TextDarkPrimary,
  onSurface = TextDarkPrimary,
  surfaceVariant = Color(0xFF1E293B),
  onSurfaceVariant = TextDarkSecondary,
  outline = SurfaceDarkBorder
)

private val LightColorScheme = lightColorScheme(
  primary = BrandSkyPrimary,
  onPrimary = Color.White,
  primaryContainer = BrandSkyLight,
  onPrimaryContainer = BrandSkyDark,
  secondary = BrandTeal,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFCCFBF1),
  onSecondaryContainer = Color(0xFF115E59),
  tertiary = BrandIndigo,
  background = SurfaceLight,
  surface = SurfaceLightCard,
  onBackground = TextLightPrimary,
  onSurface = TextLightPrimary,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextLightSecondary,
  outline = SurfaceLightBorder
)

enum class AppThemeMode {
  SYSTEM, LIGHT, DARK
}

@Composable
fun StorageTheme(
  themeMode: AppThemeMode = AppThemeMode.SYSTEM,
  dynamicColor: Boolean = false, // Preserve brand sky identity
  content: @Composable () -> Unit
) {
  val isDark = when (themeMode) {
    AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    AppThemeMode.DARK -> true
    AppThemeMode.LIGHT -> false
  }

  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    isDark -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
