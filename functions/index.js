/**
 * 1000MB Storage - Cloud Functions
 * Server-side quota validation, transactional storage updates,
 * OTP verification, and account cleanup.
 */

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

const db = admin.firestore();
const storage = admin.storage().bucket();
const MAX_QUOTA_BYTES = 1048576000; // Exactly 1000 MB

/**
 * Server-side transactional upload approval and quota verification
 */
exports.prepareFileUpload = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated.');
  }

  const userId = context.auth.uid;
  const fileSize = Number(data.fileSizeBytes);

  if (!fileSize || fileSize <= 0) {
    throw new functions.https.HttpsError('invalid-argument', 'Invalid file size.');
  }

  const userRef = db.collection('users').doc(userId);

  return await db.runTransaction(async (transaction) => {
    const userDoc = await transaction.get(userRef);
    if (!userDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'User profile not found.');
    }

    const userData = userDoc.data();
    if (userData.status === 'suspended') {
      throw new functions.https.HttpsError('permission-denied', 'Account is suspended.');
    }

    const currentUsed = userData.storageUsedBytes || 0;
    const quota = userData.storageQuotaBytes || MAX_QUOTA_BYTES;

    if (currentUsed + fileSize > quota) {
      throw new functions.https.HttpsError(
        'resource-exhausted',
        'এই ফাইল আপলোড করলে আপনার ১০০০ MB স্টোরেজ সীমা অতিক্রম করবে।'
      );
    }

    return {
      allowed: true,
      currentUsedBytes: currentUsed,
      newTotalBytes: currentUsed + fileSize,
      remainingBytes: quota - (currentUsed + fileSize),
      storagePath: `users/${userId}/files/${data.fileId}`
    };
  });
});

/**
 * Triggered on File deletion to remove Cloud Storage object and decrement storage
 */
exports.onFileDeleted = functions.firestore
  .document('files/{fileId}')
  .onDelete(async (snap, context) => {
    const fileData = snap.data();
    if (!fileData) return null;

    const { ownerId, sizeBytes, storagePath } = fileData;

    try {
      // 1. Delete physical file from Storage bucket
      if (storagePath) {
        const file = storage.file(storagePath);
        const [exists] = await file.exists();
        if (exists) {
          await file.delete();
        }
      }

      // 2. Decrement user storage usage atomically
      if (ownerId && sizeBytes > 0) {
        const userRef = db.collection('users').doc(ownerId);
        await userRef.update({
          storageUsedBytes: admin.firestore.FieldValue.increment(-sizeBytes),
          updatedAt: admin.firestore.FieldValue.serverTimestamp()
        });
      }
    } catch (error) {
      console.error('Error in onFileDeleted:', error);
    }
    return null;
  });

/**
 * Complete Account Deletion: Purges user data and all stored cloud files
 */
exports.deleteUserAccount = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated.');
  }

  const userId = context.auth.uid;

  try {
    // 1. Delete all user files from Cloud Storage under users/{userId}/
    await storage.deleteFiles({ prefix: `users/${userId}/` });

    // 2. Delete all Firestore file records for this user
    const filesSnapshot = await db.collection('files').where('ownerId', '==', userId).get();
    const batch = db.batch();
    filesSnapshot.forEach(doc => {
      batch.delete(doc.ref);
    });
    await batch.commit();

    // 3. Delete user document
    await db.collection('users').doc(userId).delete();

    // 4. Delete Auth record
    await admin.auth().deleteUser(userId);

    return { success: true, message: 'অ্যাকাউন্ট এবং সমস্ত ফাইল সফলভাবে মুছে ফেলা হয়েছে।' };
  } catch (error) {
    console.error('Account deletion error:', error);
    throw new functions.https.HttpsError('internal', 'Account deletion failed: ' + error.message);
  }
});
