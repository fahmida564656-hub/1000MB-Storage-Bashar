package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppNotification
import com.example.data.model.CloudFile
import com.example.data.model.FileCategory
import com.example.data.model.User
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthResult
import com.example.data.repository.StorageRepository
import com.example.data.repository.UploadResult
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenDestination {
  LANDING,
  LOGIN,
  REGISTER,
  FORGOT_PASSWORD,
  TERMS,
  DASHBOARD,
  FILES,
  UPLOAD,
  TRASH,
  PROFILE,
  ADMIN
}

enum class SortMode {
  NEWEST, OLDEST, NAME_AZ, NAME_ZA, LARGEST, SMALLEST
}

enum class ViewMode {
  GRID, LIST
}

data class UploadProgressState(
  val isUploading: Boolean = false,
  val fileName: String = "",
  val fileType: String = "",
  val totalSizeBytes: Long = 0L,
  val progressPercent: Int = 0,
  val speedText: String = "",
  val statusText: String = "",
  val errorMessage: String? = null,
  val successMessage: String? = null
)

class StorageViewModel(application: Application) : AndroidViewModel(application) {
  val authRepository = AuthRepository(application)
  val storageRepository = StorageRepository(application)

  val currentUser: StateFlow<User?> = authRepository.currentUser

  private val _language = MutableStateFlow(AppLanguage.BENGALI)
  val language = _language.asStateFlow()

  private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
  val themeMode = _themeMode.asStateFlow()

  private val _currentScreen = MutableStateFlow(ScreenDestination.LANDING)
  val currentScreen = _currentScreen.asStateFlow()

  // File Manager States
  private val _searchQuery = MutableStateFlow("")
  val searchQuery = _searchQuery.asStateFlow()

  private val _selectedCategory = MutableStateFlow<FileCategory?>(null)
  val selectedCategory = _selectedCategory.asStateFlow()

  private val _sortMode = MutableStateFlow(SortMode.NEWEST)
  val sortMode = _sortMode.asStateFlow()

  private val _viewMode = MutableStateFlow(ViewMode.GRID)
  val viewMode = _viewMode.asStateFlow()

  // Upload Progress
  private val _uploadState = MutableStateFlow(UploadProgressState())
  val uploadState = _uploadState.asStateFlow()

  // Selected file for preview / rename
  private val _previewFile = MutableStateFlow<CloudFile?>(null)
  val previewFile = _previewFile.asStateFlow()

  private val _renameFileTarget = MutableStateFlow<CloudFile?>(null)
  val renameFileTarget = _renameFileTarget.asStateFlow()

  private val _bannerMessage = MutableStateFlow<Pair<String, Boolean>?>(null) // (Message, isError)
  val bannerMessage = _bannerMessage.asStateFlow()

  // Admin Search
  private val _adminSearchQuery = MutableStateFlow("")
  val adminSearchQuery = _adminSearchQuery.asStateFlow()

  init {
    // Check if a user is logged in
    viewModelScope.launch {
      currentUser.collect { user ->
        if (user != null && _currentScreen.value in listOf(
            ScreenDestination.LANDING,
            ScreenDestination.LOGIN,
            ScreenDestination.REGISTER,
            ScreenDestination.FORGOT_PASSWORD
          )
        ) {
          _currentScreen.value = ScreenDestination.DASHBOARD
        } else if (user == null && _currentScreen.value !in listOf(
            ScreenDestination.LANDING,
            ScreenDestination.LOGIN,
            ScreenDestination.REGISTER,
            ScreenDestination.FORGOT_PASSWORD,
            ScreenDestination.TERMS
          )
        ) {
          _currentScreen.value = ScreenDestination.LANDING
        }
      }
    }
  }

  // Active files flow for current user
  @OptIn(ExperimentalCoroutinesApi::class)
  val rawActiveFiles: StateFlow<List<CloudFile>> = currentUser.flatMapLatest { user ->
    if (user != null) storageRepository.getActiveFiles(user.userId) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Filtered and sorted files
  val displayedFiles: StateFlow<List<CloudFile>> = combine(
    rawActiveFiles,
    searchQuery,
    selectedCategory,
    sortMode
  ) { files, query, cat, sort ->
    var filtered = files
    if (query.isNotBlank()) {
      filtered = filtered.filter {
        it.fileName.contains(query, ignoreCase = true) ||
            it.fileType.contains(query, ignoreCase = true)
      }
    }
    if (cat != null) {
      filtered = filtered.filter { it.category == cat }
    }
    when (sort) {
      SortMode.NEWEST -> filtered.sortedByDescending { it.createdAt }
      SortMode.OLDEST -> filtered.sortedBy { it.createdAt }
      SortMode.NAME_AZ -> filtered.sortedBy { it.fileName.lowercase() }
      SortMode.NAME_ZA -> filtered.sortedByDescending { it.fileName.lowercase() }
      SortMode.LARGEST -> filtered.sortedByDescending { it.sizeBytes }
      SortMode.SMALLEST -> filtered.sortedBy { it.sizeBytes }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Trash files flow
  @OptIn(ExperimentalCoroutinesApi::class)
  val trashFiles: StateFlow<List<CloudFile>> = currentUser.flatMapLatest { user ->
    if (user != null) storageRepository.getTrashFiles(user.userId) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Storage usage flow
  @OptIn(ExperimentalCoroutinesApi::class)
  val storageUsedBytes: StateFlow<Long> = currentUser.flatMapLatest { user ->
    if (user != null) storageRepository.getStorageUsed(user.userId) else flowOf(0L)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

  // Notifications flow
  @OptIn(ExperimentalCoroutinesApi::class)
  val notifications: StateFlow<List<AppNotification>> = currentUser.flatMapLatest { user ->
    if (user != null) storageRepository.getNotifications(user.userId) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Admin users list
  @OptIn(ExperimentalCoroutinesApi::class)
  val adminUsers: StateFlow<List<User>> = adminSearchQuery.flatMapLatest { query ->
    if (query.isBlank()) authRepository.getAllUsers() else authRepository.searchUsers(query)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val adminTotalFiles: StateFlow<Int> = storageRepository.getAdminTotalFiles()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val adminTotalStorage: StateFlow<Long> = storageRepository.getAdminTotalStorageUsed()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

  // Category summary helper
  fun getCategoryUsage(category: FileCategory): Pair<Int, Long> {
    val files = rawActiveFiles.value.filter { it.category == category }
    val count = files.size
    val bytes = files.sumOf { it.sizeBytes }
    return Pair(count, bytes)
  }

  fun setLanguage(newLang: AppLanguage) {
    _language.value = newLang
  }

  fun setThemeMode(newMode: AppThemeMode) {
    _themeMode.value = newMode
  }

  fun navigateTo(destination: ScreenDestination) {
    _currentScreen.value = destination
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSelectedCategory(category: FileCategory?) {
    _selectedCategory.value = category
  }

  fun setSortMode(mode: SortMode) {
    _sortMode.value = mode
  }

  fun toggleViewMode() {
    _viewMode.value = if (_viewMode.value == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID
  }

  fun setPreviewFile(file: CloudFile?) {
    _previewFile.value = file
  }

  fun setRenameTarget(file: CloudFile?) {
    _renameFileTarget.value = file
  }

  fun setAdminSearchQuery(query: String) {
    _adminSearchQuery.value = query
  }

  fun clearBanner() {
    _bannerMessage.value = null
  }

  // File Upload
  fun uploadFiles(uris: List<Uri>) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      for (uri in uris) {
        _uploadState.value = UploadProgressState(
          isUploading = true,
          statusText = if (_language.value == AppLanguage.BENGALI) "ফাইল প্রস্তুত হচ্ছে..." else "Preparing file..."
        )

        val startTime = System.currentTimeMillis()
        val result = storageRepository.uploadFile(user.userId, uri) { percent, uploaded, total ->
          val elapsedSec = ((System.currentTimeMillis() - startTime) / 1000.0).coerceAtLeast(0.1)
          val speedBytesPerSec = uploaded / elapsedSec
          val speedText = CloudFile.formatStorageBytes(speedBytesPerSec.toLong()) + "/s"

          _uploadState.value = _uploadState.value.copy(
            progressPercent = percent,
            totalSizeBytes = total,
            speedText = speedText,
            statusText = if (_language.value == AppLanguage.BENGALI) "আপলোড হচ্ছে $percent%" else "Uploading $percent%"
          )
        }

        when (result) {
          is UploadResult.Success -> {
            _uploadState.value = UploadProgressState(
              isUploading = false,
              successMessage = if (_language.value == AppLanguage.BENGALI)
                "ফাইল সফলভাবে আপলোড হয়েছে"
              else
                "File uploaded successfully"
            )
            _bannerMessage.value = Pair(
              if (_language.value == AppLanguage.BENGALI) "ফাইল সফলভাবে আপলোড হয়েছে" else "File uploaded successfully",
              false
            )
          }
          is UploadResult.QuotaExceeded -> {
            val msg = if (_language.value == AppLanguage.BENGALI) result.messageBn else result.messageEn
            _uploadState.value = UploadProgressState(
              isUploading = false,
              errorMessage = msg
            )
            _bannerMessage.value = Pair(msg, true)
            break
          }
          is UploadResult.Error -> {
            _uploadState.value = UploadProgressState(
              isUploading = false,
              errorMessage = result.message
            )
            _bannerMessage.value = Pair(result.message, true)
            break
          }
        }
      }
    }
  }

  fun cancelUpload() {
    _uploadState.value = UploadProgressState(isUploading = false)
  }

  fun renameFile(fileId: String, newName: String) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val (success, msg) = storageRepository.renameFile(fileId, user.userId, newName)
      _bannerMessage.value = Pair(msg, !success)
      _renameFileTarget.value = null
    }
  }

  fun moveToTrash(fileId: String) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val success = storageRepository.moveToTrash(fileId, user.userId)
      if (success) {
        val msg = if (_language.value == AppLanguage.BENGALI) "ফাইল ট্র্যাশে সরানো হয়েছে" else "File moved to trash"
        _bannerMessage.value = Pair(msg, false)
      }
    }
  }

  fun restoreFile(fileId: String) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val success = storageRepository.restoreFile(fileId, user.userId)
      if (success) {
        val msg = if (_language.value == AppLanguage.BENGALI) "ফাইল পুনরুদ্ধার হয়েছে" else "File restored"
        _bannerMessage.value = Pair(msg, false)
      }
    }
  }

  fun permanentDelete(fileId: String) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val success = storageRepository.permanentDelete(fileId, user.userId)
      if (success) {
        val msg = if (_language.value == AppLanguage.BENGALI) "ফাইল স্থায়ীভাবে মুছে ফেলা হয়েছে" else "File permanently deleted"
        _bannerMessage.value = Pair(msg, false)
      }
    }
  }

  fun emptyTrash() {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      storageRepository.emptyTrash(user.userId)
      val msg = if (_language.value == AppLanguage.BENGALI) "ট্র্যাশ সম্পূর্ণ খালি করা হয়েছে" else "Trash completely emptied"
      _bannerMessage.value = Pair(msg, false)
    }
  }

  fun toggleSuspendUser(targetUserId: String, currentStatus: String) {
    viewModelScope.launch {
      authRepository.toggleUserSuspension(targetUserId, currentStatus)
    }
  }

  fun markAllNotificationsRead() {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      storageRepository.markAllNotificationsRead(user.userId)
    }
  }

  fun logout() {
    authRepository.logout()
    _currentScreen.value = ScreenDestination.LANDING
  }

  fun deleteAccount(password: String, onComplete: (Boolean, String) -> Unit) {
    viewModelScope.launch {
      val (success, msg) = authRepository.deleteAccount(password)
      if (success) {
        _currentScreen.value = ScreenDestination.LANDING
      }
      onComplete(success, msg)
    }
  }
}
