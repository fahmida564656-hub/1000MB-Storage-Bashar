package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.FilePreviewDialog
import com.example.ui.components.RenameFileDialog
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FileManagerScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.TermsScreen
import com.example.ui.screens.TrashScreen
import com.example.ui.screens.UploadScreen
import com.example.ui.theme.StorageTheme
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.StorageViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: StorageViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val themeMode by viewModel.themeMode.collectAsState()
      val language by viewModel.language.collectAsState()
      val currentScreen by viewModel.currentScreen.collectAsState()
      val previewFile by viewModel.previewFile.collectAsState()
      val renameTarget by viewModel.renameFileTarget.collectAsState()
      val bannerMessage by viewModel.bannerMessage.collectAsState()

      val snackbarHostState = remember { SnackbarHostState() }

      LaunchedEffect(bannerMessage) {
        bannerMessage?.let { (msg, _) ->
          snackbarHostState.showSnackbar(
            message = msg,
            duration = SnackbarDuration.Short
          )
          viewModel.clearBanner()
        }
      }

      StorageTheme(themeMode = themeMode) {
        val showBottomBar = currentScreen in listOf(
          ScreenDestination.DASHBOARD,
          ScreenDestination.FILES,
          ScreenDestination.UPLOAD,
          ScreenDestination.TRASH,
          ScreenDestination.PROFILE
        )

        // Android System Back Button Handler
        BackHandler(enabled = currentScreen != ScreenDestination.DASHBOARD && currentScreen != ScreenDestination.LANDING) {
          when (currentScreen) {
            ScreenDestination.FILES,
            ScreenDestination.UPLOAD,
            ScreenDestination.TRASH,
            ScreenDestination.PROFILE -> viewModel.navigateTo(ScreenDestination.DASHBOARD)
            ScreenDestination.ADMIN -> viewModel.navigateTo(ScreenDestination.PROFILE)
            ScreenDestination.LOGIN,
            ScreenDestination.REGISTER -> viewModel.navigateTo(ScreenDestination.LANDING)
            ScreenDestination.FORGOT_PASSWORD -> viewModel.navigateTo(ScreenDestination.LOGIN)
            ScreenDestination.TERMS -> viewModel.navigateTo(ScreenDestination.REGISTER)
            else -> {}
          }
        }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          snackbarHost = { SnackbarHost(snackbarHostState) },
          bottomBar = {
            if (showBottomBar) {
              AppBottomNavigationBar(
                currentScreen = currentScreen,
                language = language,
                onNavigate = { dest -> viewModel.navigateTo(dest) }
              )
            }
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            when (currentScreen) {
              ScreenDestination.LANDING -> {
                LandingScreen(
                  language = language,
                  onLanguageToggle = {
                    viewModel.setLanguage(if (language == AppLanguage.BENGALI) AppLanguage.ENGLISH else AppLanguage.BENGALI)
                  },
                  onNavigateLogin = { viewModel.navigateTo(ScreenDestination.LOGIN) },
                  onNavigateRegister = { viewModel.navigateTo(ScreenDestination.REGISTER) }
                )
              }
              ScreenDestination.LOGIN -> {
                LoginScreen(viewModel = viewModel, language = language)
              }
              ScreenDestination.REGISTER -> {
                RegisterScreen(viewModel = viewModel, language = language)
              }
              ScreenDestination.FORGOT_PASSWORD -> {
                ForgotPasswordScreen(viewModel = viewModel, language = language)
              }
              ScreenDestination.TERMS -> {
                TermsScreen(
                  language = language,
                  onBack = { viewModel.navigateTo(ScreenDestination.REGISTER) }
                )
              }
              ScreenDestination.DASHBOARD -> {
                DashboardScreen(viewModel = viewModel, language = language)
              }
              ScreenDestination.FILES -> {
                FileManagerScreen(viewModel = viewModel, language = language)
              }
              ScreenDestination.UPLOAD -> {
                UploadScreen(viewModel = viewModel, language = language)
              }
              ScreenDestination.TRASH -> {
                TrashScreen(viewModel = viewModel, language = language)
              }
              ScreenDestination.PROFILE -> {
                ProfileScreen(viewModel = viewModel, language = language)
              }
              ScreenDestination.ADMIN -> {
                AdminScreen(viewModel = viewModel, language = language)
              }
            }

            // Preview Dialog Overlay
            previewFile?.let { file ->
              FilePreviewDialog(
                file = file,
                language = language,
                onDismiss = { viewModel.setPreviewFile(null) }
              )
            }

            // Rename Dialog Overlay
            renameTarget?.let { file ->
              RenameFileDialog(
                file = file,
                language = language,
                onDismiss = { viewModel.setRenameTarget(null) },
                onConfirm = { newName -> viewModel.renameFile(file.fileId, newName) }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun AppBottomNavigationBar(
  currentScreen: ScreenDestination,
  language: AppLanguage,
  onNavigate: (ScreenDestination) -> Unit
) {
  NavigationBar(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("app_bottom_navigation"),
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    NavigationBarItem(
      selected = currentScreen == ScreenDestination.DASHBOARD,
      onClick = { onNavigate(ScreenDestination.DASHBOARD) },
      icon = { Icon(Icons.Default.Home, contentDescription = Strings.navHome(language)) },
      label = { Text(Strings.navHome(language)) },
      modifier = Modifier.testTag("nav_item_dashboard")
    )

    NavigationBarItem(
      selected = currentScreen == ScreenDestination.FILES,
      onClick = { onNavigate(ScreenDestination.FILES) },
      icon = { Icon(Icons.Default.Folder, contentDescription = Strings.navFiles(language)) },
      label = { Text(Strings.navFiles(language)) },
      modifier = Modifier.testTag("nav_item_files")
    )

    NavigationBarItem(
      selected = currentScreen == ScreenDestination.UPLOAD,
      onClick = { onNavigate(ScreenDestination.UPLOAD) },
      icon = { Icon(Icons.Default.CloudUpload, contentDescription = Strings.navUpload(language)) },
      label = { Text(Strings.navUpload(language)) },
      modifier = Modifier.testTag("nav_item_upload")
    )

    NavigationBarItem(
      selected = currentScreen == ScreenDestination.TRASH,
      onClick = { onNavigate(ScreenDestination.TRASH) },
      icon = { Icon(Icons.Default.Delete, contentDescription = Strings.navTrash(language)) },
      label = { Text(Strings.navTrash(language)) },
      modifier = Modifier.testTag("nav_item_trash")
    )

    NavigationBarItem(
      selected = currentScreen == ScreenDestination.PROFILE,
      onClick = { onNavigate(ScreenDestination.PROFILE) },
      icon = { Icon(Icons.Default.Person, contentDescription = Strings.navProfile(language)) },
      label = { Text(Strings.navProfile(language)) },
      modifier = Modifier.testTag("nav_item_profile")
    )
  }
}
