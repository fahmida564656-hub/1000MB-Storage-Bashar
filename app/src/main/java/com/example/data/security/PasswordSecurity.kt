package com.example.data.security

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordSecurity {
  private const val ITERATIONS = 10000
  private const val KEY_LENGTH = 256
  private const val ALGORITHM = "PBKDF2WithHmacSHA256"

  fun generateSalt(): String {
    val random = SecureRandom()
    val salt = ByteArray(16)
    random.nextBytes(salt)
    return Base64.getEncoder().encodeToString(salt)
  }

  fun hashPassword(password: String, saltBase64: String): String {
    val salt = Base64.getDecoder().decode(saltBase64)
    val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
    val factory = SecretKeyFactory.getInstance(ALGORITHM)
    val hash = factory.generateSecret(spec).encoded
    return Base64.getEncoder().encodeToString(hash)
  }

  fun verifyPassword(password: String, saltBase64: String, expectedHashBase64: String): Boolean {
    val computedHash = hashPassword(password, saltBase64)
    return computedHash == expectedHashBase64
  }
}
