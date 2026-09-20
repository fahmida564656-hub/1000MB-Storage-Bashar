package com.example.data.model

import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings

enum class FileCategory {
  IMAGES,
  VIDEOS,
  AUDIO,
  DOCUMENTS,
  PDF,
  ARCHIVES,
  OTHER;

  fun getDisplayName(lang: AppLanguage): String {
    return when (this) {
      IMAGES -> Strings.catImages(lang)
      VIDEOS -> Strings.catVideos(lang)
      AUDIO -> Strings.catAudio(lang)
      DOCUMENTS -> Strings.catDocuments(lang)
      PDF -> Strings.catPdf(lang)
      ARCHIVES -> Strings.catArchives(lang)
      OTHER -> Strings.catOther(lang)
    }
  }

  fun getEmoji(): String {
    return when (this) {
      IMAGES -> "📷"
      VIDEOS -> "🎬"
      AUDIO -> "🎵"
      DOCUMENTS -> "📄"
      PDF -> "📕"
      ARCHIVES -> "📦"
      OTHER -> "📁"
    }
  }

  companion object {
    fun fromMimeOrExtension(mimeType: String?, extension: String?): FileCategory {
      val ext = extension?.lowercase() ?: ""
      val mime = mimeType?.lowercase() ?: ""

      return when {
        mime.startsWith("image/") || ext in listOf("jpg", "jpeg", "png", "webp", "gif", "bmp", "svg", "heic") -> IMAGES
        mime.startsWith("video/") || ext in listOf("mp4", "webm", "mkv", "avi", "mov", "3gp", "flv") -> VIDEOS
        mime.startsWith("audio/") || ext in listOf("mp3", "wav", "m4a", "aac", "ogg", "flac", "opus") -> AUDIO
        ext == "pdf" || mime == "application/pdf" -> PDF
        mime.contains("zip") || mime.contains("tar") || mime.contains("compressed") ||
            ext in listOf("zip", "rar", "7z", "tar", "gz", "bz2", "xz") -> ARCHIVES
        mime.contains("document") || mime.contains("word") || mime.contains("sheet") ||
            mime.contains("presentation") || mime.contains("text/") ||
            ext in listOf("doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "rtf", "odt", "csv", "json") -> DOCUMENTS
        else -> OTHER
      }
    }
  }
}
