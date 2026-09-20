# 1000MB Storage (১০০০ এমবি স্টোরেজ) - Production Architecture & Deployment Guide

> **Tagline:** “আপনার ফাইল, আপনার নিরাপদ স্থায়ী স্টোরেজ”  
> **Storage Quota:** Exactly 1000 MB (1,048,576,000 bytes) per account with permanent lifetime.

---

## 1. System Architecture Overview

- **Mobile Client:** Native Android App built with Kotlin & Jetpack Compose (Material 3). Supports Bengali (বাংলা) as primary language and English secondary.
- **Local Cache & Offline Shell:** Room Database (`StorageDatabase`) managing local offline cache, instant indexing, metadata synchronization, and background upload queue.
- **Cloud Backend:**
  - **Authentication:** Mobile Number + Secure Hash Authentication, Firebase Phone Auth / SMS Gateway integration.
  - **Database:** Cloud Firestore (`/users/{userId}`, `/files/{fileId}`).
  - **Object Storage:** Cloud Storage (`users/{userId}/files/{fileId}`, `users/{userId}/trash/{fileId}`).
  - **Functions:** Serverless Quota & Cleanup triggers (`prepareFileUpload`, `onFileDeleted`, `deleteUserAccount`).

---

## 2. Database Schema (Firestore & Room)

### `/users/{userId}`
| Field | Type | Description |
| :--- | :--- | :--- |
| `userId` | String | Unique user ID |
| `phoneNumber` | String | E.164 formatted unique mobile number |
| `displayName` | String | User name or formatted phone |
| `role` | String | `"user"` or `"admin"` |
| `status` | String | `"active"` or `"suspended"` |
| `storageQuotaBytes` | Long | Default `1048576000` (1000 MB) |
| `storageUsedBytes` | Long | Current total byte usage |
| `createdAt` | Timestamp | Account creation timestamp |
| `updatedAt` | Timestamp | Last activity timestamp |

### `/files/{fileId}`
| Field | Type | Description |
| :--- | :--- | :--- |
| `fileId` | String | UUID / Firestore Document ID |
| `ownerId` | String | User ID of file owner |
| `fileName` | String | User display name of file (with extension) |
| `originalName` | String | Original uploaded filename |
| `storagePath` | String | `users/{ownerId}/files/{fileId}` |
| `fileType` | String | Extension (`jpg`, `mp4`, `pdf`, etc.) |
| `mimeType` | String | e.g. `image/jpeg`, `video/mp4`, `application/pdf` |
| `sizeBytes` | Long | Exact file size in bytes |
| `category` | String | `IMAGES`, `VIDEOS`, `AUDIO`, `DOCUMENTS`, `PDF`, `ARCHIVES`, `OTHER` |
| `status` | String | `"active"`, `"trash"`, `"deleted"` |
| `createdAt` | Long | Upload timestamp |
| `updatedAt` | Long | Last modified timestamp |
| `deletedAt` | Long? | When moved to trash (null if active) |

---

## 3. Storage Quota Policy (Strict 1000 MB)

- **Hard Limit:** 1000 MB = `1000 * 1024 * 1024` = `1,048,576,000` bytes.
- **Rule:** Before any upload begins, `currentUsage + newFileSize` is validated. If `> 1,048,576,000`, the upload is rejected with Bengali notification:  
  *“এই ফাইল আপলোড করলে আপনার ১০০০ MB স্টোরেজ সীমা অতিক্রম করবে।”*
- **Visual Alert Thresholds:**
  - `0% - 79.9%`: Normal (Brand Blue/Teal)
  - `80.0% - 89.9%`: Warning (Amber) - *“আপনার স্টোরেজ ৮০% পূর্ণ।”*
  - `90.0% - 94.9%`: High (Orange) - *“আপনার স্টোরেজ ৯০% পূর্ণ।”*
  - `95.0% - 99.9%`: Critical (Deep Red) - *“আপনার স্টোরেজ প্রায় শেষ।”*
  - `100%`: Full (Crimson) - *“আপনার স্টোরেজ পূর্ণ হয়ে গেছে।”*

---

## 4. Production Deployment Steps

1. **Create Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with Package Name `com.aistudio.storage1000mb.kxqmtz`.
   - Download `google-services.json` and place it in the `app/` directory.

2. **Deploy Security Rules**:
   ```bash
   firebase deploy --only firestore:rules,storage:rules
   ```

3. **Deploy Backend Functions**:
   ```bash
   cd functions && npm install
   firebase deploy --only functions
   ```

4. **Configure SMS Gateway**:
   - In `.env` or AI Studio Secrets, configure `SMS_PROVIDER_API_KEY` and `SMS_PROVIDER_SENDER_ID`.
   - In development/sandbox mode, the SMS manager falls back to safe mock-OTP verification mode.

5. **Build Release APK**:
   ```bash
   gradle assembleRelease
   ```

---

## 5. Security & Isolation Guarantee

- **Multi-Tenant Isolation:** Storage rules enforce `request.auth.uid == userId`. User B can never read, enumerate, download, or delete User A's files.
- **Zero Public Buckets:** Storage objects are private and accessible solely via authenticated token streams or signed temporary URLs.
- **Safe Password Storage:** Passwords hashed with salted PBKDF2/SHA-256 before transit and backend verification.
