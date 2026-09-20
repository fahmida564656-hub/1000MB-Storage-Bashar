package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FileCategory
import com.example.ui.components.FileItemCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SortMode
import com.example.ui.viewmodel.StorageViewModel
import com.example.ui.viewmodel.ViewMode

@Composable
fun FileManagerScreen(
  viewModel: StorageViewModel,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val files by viewModel.displayedFiles.collectAsState()
  val rawFiles by viewModel.rawActiveFiles.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val sortMode by viewModel.sortMode.collectAsState()
  val viewMode by viewModel.viewMode.collectAsState()

  var sortMenuExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    // Top Header & Controls
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = Strings.navFiles(language),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = if (language == AppLanguage.BENGALI) "${files.size} টি ফাইল পাওয়া গেছে" else "${files.size} files found",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Sort Menu Button
        Box {
          IconButton(
            onClick = { sortMenuExpanded = true },
            modifier = Modifier.testTag("file_manager_sort_button")
          ) {
            Icon(Icons.Default.FilterList, contentDescription = "Sort")
          }

          DropdownMenu(
            expanded = sortMenuExpanded,
            onDismissRequest = { sortMenuExpanded = false }
          ) {
            DropdownMenuItem(
              text = { Text(if (language == AppLanguage.BENGALI) "নতুন ফাইল আগে" else "Newest first") },
              onClick = { viewModel.setSortMode(SortMode.NEWEST); sortMenuExpanded = false }
            )
            DropdownMenuItem(
              text = { Text(if (language == AppLanguage.BENGALI) "পুরাতন ফাইল আগে" else "Oldest first") },
              onClick = { viewModel.setSortMode(SortMode.OLDEST); sortMenuExpanded = false }
            )
            DropdownMenuItem(
              text = { Text(if (language == AppLanguage.BENGALI) "নাম (A থেকে Z)" else "Name (A to Z)") },
              onClick = { viewModel.setSortMode(SortMode.NAME_AZ); sortMenuExpanded = false }
            )
            DropdownMenuItem(
              text = { Text(if (language == AppLanguage.BENGALI) "নাম (Z থেকে A)" else "Name (Z to A)") },
              onClick = { viewModel.setSortMode(SortMode.NAME_ZA); sortMenuExpanded = false }
            )
            DropdownMenuItem(
              text = { Text(if (language == AppLanguage.BENGALI) "বড় ফাইল আগে" else "Largest first") },
              onClick = { viewModel.setSortMode(SortMode.LARGEST); sortMenuExpanded = false }
            )
            DropdownMenuItem(
              text = { Text(if (language == AppLanguage.BENGALI) "ছোট ফাইল আগে" else "Smallest first") },
              onClick = { viewModel.setSortMode(SortMode.SMALLEST); sortMenuExpanded = false }
            )
          }
        }

        // View Mode Switcher (Grid / List)
        IconButton(
          onClick = { viewModel.toggleViewMode() },
          modifier = Modifier.testTag("file_manager_view_mode_toggle")
        ) {
          Icon(
            imageVector = if (viewMode == ViewMode.GRID) Icons.Default.ViewList else Icons.Default.GridView,
            contentDescription = "Toggle View Mode"
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { viewModel.setSearchQuery(it) },
      placeholder = { Text(if (language == AppLanguage.BENGALI) "ফাইল খুঁজুন..." else "Search files...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { viewModel.setSearchQuery("") }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear")
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("file_manager_search_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Filter Chips (All, Images, Videos, Audio, Documents, PDF, Archives, Other)
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(vertical = 4.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      item {
        FilterChip(
          selected = selectedCategory == null,
          onClick = { viewModel.setSelectedCategory(null) },
          label = { Text(Strings.catAll(language)) },
          shape = RoundedCornerShape(12.dp)
        )
      }

      items(FileCategory.values()) { category ->
        FilterChip(
          selected = selectedCategory == category,
          onClick = {
            viewModel.setSelectedCategory(if (selectedCategory == category) null else category)
          },
          label = { Text("${category.getEmoji()} ${category.getDisplayName(language)}") },
          shape = RoundedCornerShape(12.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Files List / Grid / Empty State
    if (files.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("🔍", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = if (rawFiles.isEmpty()) {
              if (language == AppLanguage.BENGALI) "এখনো কোনো ফাইল আপলোড করা হয়নি।" else "No files have been uploaded yet."
            } else {
              if (language == AppLanguage.BENGALI) "কোনো ফাইল পাওয়া যায়নি।" else "No matching files found."
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = if (rawFiles.isEmpty()) {
              if (language == AppLanguage.BENGALI)
                "১০০০ MB ফ্রি স্টোরেজে ছবি, ভিডিও বা ডকুমেন্ট ব্যাকআপ নিন।"
              else
                "Back up photos, videos, or documents to your 1000 MB storage."
            } else {
              if (language == AppLanguage.BENGALI) "অন্য কোনো নাম বা ক্যাটাগরি দিয়ে চেষ্টা করুন।" else "Try searching with another name or filter."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          if (rawFiles.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = { viewModel.navigateTo(ScreenDestination.UPLOAD) },
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.CloudUpload, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(Strings.uploadTitle(language))
            }
          }
        }
      }
    } else {
      if (viewMode == ViewMode.GRID) {
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(bottom = 80.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          items(files, key = { it.fileId }) { file ->
            FileItemCard(
              file = file,
              language = language,
              isGrid = true,
              onPreview = { viewModel.setPreviewFile(file) },
              onDownload = { viewModel.setPreviewFile(file) },
              onRename = { viewModel.setRenameTarget(file) },
              onDelete = { viewModel.moveToTrash(file.fileId) }
            )
          }
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 80.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          items(files, key = { it.fileId }) { file ->
            FileItemCard(
              file = file,
              language = language,
              isGrid = false,
              onPreview = { viewModel.setPreviewFile(file) },
              onDownload = { viewModel.setPreviewFile(file) },
              onRename = { viewModel.setRenameTarget(file) },
              onDelete = { viewModel.moveToTrash(file.fileId) }
            )
          }
        }
      }
    }
  }
}
