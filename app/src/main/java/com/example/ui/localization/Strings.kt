package com.example.ui.localization

enum class AppLanguage {
  BENGALI, ENGLISH
}

object Strings {
  fun appName(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "1000MB স্টোরেজ" else "1000MB Storage"
  fun tagline(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "আপনার ফাইল, আপনার নিরাপদ স্থায়ী স্টোরেজ" else "Your files, your secure permanent storage"
  fun subheadline(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "সহজে সংরক্ষণ করুন আপনার গুরুত্বপূর্ণ ছবি, ভিডিও, ডকুমেন্ট ও অন্যান্য ফাইল।" else "Easily store your important photos, videos, documents, and other files."

  fun permanentStorageBadge(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "স্থায়ী স্টোরেজ (কোনো মেয়াদ শেষ নেই)" else "Permanent Storage (No Expiration)"
  fun permanentStorageShort(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "স্থায়ী স্টোরেজ" else "Permanent Storage"

  // Nav
  fun navHome(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ড্যাশবোর্ড" else "Dashboard"
  fun navFiles(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "আমার ফাইল" else "My Files"
  fun navUpload(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "আপলোড" else "Upload"
  fun navTrash(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ট্র্যাশ" else "Trash"
  fun navProfile(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "প্রোফাইল" else "Profile"
  fun navAdmin(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "অ্যাডমিন প্যানেল" else "Admin Panel"

  // Dashboard & Storage
  fun myStorage(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "আমার স্টোরেজ" else "My Storage"
  fun used(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ব্যবহৃত" else "Used"
  fun remaining(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "অবশিষ্ট" else "Remaining"
  fun totalQuota(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "১০০০ MB বরাদ্দ" else "1000 MB Quota"

  // Warnings
  fun warningNormal(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "স্টোরেজ স্বাস্থ্যকর অবস্থায় আছে" else "Storage health is good"
  fun warning80(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "আপনার স্টোরেজ ৮০% পূর্ণ।" else "Your storage is 80% full."
  fun warning90(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "আপনার স্টোরেজ ৯০% পূর্ণ।" else "Your storage is 90% full."
  fun warning95(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "আপনার স্টোরেজ প্রায় শেষ।" else "Your storage is almost depleted."
  fun warning100(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "আপনার স্টোরেজ পূর্ণ হয়ে গেছে।" else "Your storage is completely full."
  fun quotaExceededError(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "এই ফাইল আপলোড করলে আপনার ১০০০ MB স্টোরেজ সীমা অতিক্রম করবে।" else "Uploading this file will exceed your 1000 MB storage limit."

  // Categories
  fun catImages(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ছবি" else "Images"
  fun catVideos(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ভিডিও" else "Videos"
  fun catAudio(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "অডিও" else "Audio"
  fun catDocuments(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ডকুমেন্ট" else "Documents"
  fun catPdf(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "পিডিএফ" else "PDF"
  fun catArchives(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "আর্কাইভ" else "Archives"
  fun catOther(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "অন্যান্য" else "Other"
  fun catAll(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "সব ফাইল" else "All Files"

  // File Actions
  fun uploadTitle(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ফাইল আপলোড করুন" else "Upload Files"
  fun uploadSuccess(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ফাইল সফলভাবে আপলোড হয়েছে" else "File uploaded successfully"
  fun uploadFailure(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ফাইল আপলোড করা যায়নি। আবার চেষ্টা করুন।" else "Failed to upload file. Please try again."
  fun open(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "খুলুন" else "Open"
  fun preview(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "প্রিভিউ" else "Preview"
  fun download(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ডাউনলোড" else "Download"
  fun rename(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "নাম পরিবর্তন" else "Rename"
  fun delete(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "মুছে ফেলুন" else "Delete"
  fun restore(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "পুনরুদ্ধার" else "Restore"
  fun permanentDelete(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "স্থায়ীভাবে মুছুন" else "Delete Permanently"
  fun renameDialogTitle(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ফাইলের নাম পরিবর্তন করবেন?" else "Rename this file?"
  fun renameSuccess(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ফাইলের নাম সফলভাবে পরিবর্তন হয়েছে।" else "File renamed successfully."
  fun previewNotSupported(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "এই ফাইলটি সরাসরি Preview করা যাচ্ছে না।" else "This file cannot be previewed directly."

  // Trash
  fun trashTitle(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "রিসাইকেল বিন / ট্র্যাশ" else "Trash / Recycle Bin"
  fun trashEmpty(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ট্র্যাশে কোনো ফাইল নেই" else "Trash is empty"
  fun trashSubtitle(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "মুছে ফেলা ফাইলগুলো এখানে সংরক্ষিত থাকে। স্থায়ীভাবে মুছে না ফেলা পর্যন্ত কোটায় গণ্য হয়।" else "Deleted files are kept here. Restore or permanently delete them."

  // Auth & Account
  fun login(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "লগইন করুন" else "Login"
  fun register(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "অ্যাকাউন্ট খুলুন" else "Register"
  fun mobileNumber(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "মোবাইল নম্বর" else "Mobile Number"
  fun mobilePlaceholder(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "০১XXXXXXXXX বা +৮৮০..." else "01XXXXXXXXX or +880..."
  fun password(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "পাসওয়ার্ড" else "Password"
  fun confirmPassword(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "পাসওয়ার্ড নিশ্চিত করুন" else "Confirm Password"
  fun acceptTerms(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "আমি শর্তাবলী ও গোপনীয়তা নীতি মেনে নিচ্ছি" else "I accept the Terms and Privacy Policy"
  fun forgotPassword(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "পাসওয়ার্ড ভুলে গেছেন?" else "Forgot Password?"
  fun resetPassword(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "পাসওয়ার্ড রিসেট করুন" else "Reset Password"
  fun otpCode(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "৬ সংখ্যার ওটিপি কোড" else "6-digit OTP Code"
  fun verifyAndChange(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "যাচাই ও পাসওয়ার্ড পরিবর্তন" else "Verify & Change Password"
  fun logout(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "লগআউট" else "Logout"
  fun deleteAccount(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "অ্যাকাউন্ট মুছে ফেলুন" else "Delete My Account"
  fun settings(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "সেটিংস" else "Settings"
  fun languageLabel(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ভাষা (Language)" else "Language"
  fun themeLabel(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "থিম (Theme)" else "Theme"

  // Errors & Validations
  fun errWrongPassword(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "মোবাইল নম্বর অথবা পাসওয়ার্ড সঠিক নয়।" else "Incorrect mobile number or password."
  fun errDuplicateNumber(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "এই মোবাইল নম্বর দিয়ে ইতিমধ্যে একটি অ্যাকাউন্ট রয়েছে।" else "An account already exists with this mobile number."
  fun errPasswordShort(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "পাসওয়ার্ড কমপক্ষে ৮ অক্ষরের হতে হবে।" else "Password must be at least 8 characters."
  fun errPasswordMismatch(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "পাসওয়ার্ড দুটি মিলছে না।" else "Passwords do not match."
  fun errAcceptTerms(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "অনুগ্রহ করে শর্তাবলী মেনে নিন।" else "Please accept the Terms & Conditions."
  fun errInvalidMobile(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "সঠিক মোবাইল নম্বর প্রদান করুন।" else "Please enter a valid mobile number."
  fun errStorageFull(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "আপনার ১০০০ MB স্টোরেজ সীমা পূর্ণ হয়ে গেছে।" else "Your 1000 MB storage limit is full."
  fun errNetwork(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "ইন্টারনেট সংযোগ পরীক্ষা করে আবার চেষ্টা করুন।" else "Please check your internet connection."
  fun errUnauthorized(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "এই ফাইলটি দেখার অনুমতি আপনার নেই।" else "You do not have permission to view this file."
  fun errInvalidFile(lang: AppLanguage) = if (lang == AppLanguage.BENGALI) "এই ধরনের ফাইল আপলোড করা যাবে না।" else "This file type cannot be uploaded."
}
