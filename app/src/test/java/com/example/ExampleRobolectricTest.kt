package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CloudFile
import com.example.data.security.PasswordSecurity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("1000MB Storage", appName)
  }

  @Test
  fun `test password hashing and verification`() {
    val password = "SecretPassword123!"
    val salt = PasswordSecurity.generateSalt()
    val hash = PasswordSecurity.hashPassword(password, salt)

    assertTrue(PasswordSecurity.verifyPassword(password, salt, hash))
    assertFalse(PasswordSecurity.verifyPassword("WrongPassword", salt, hash))
  }

  @Test
  fun `test 1000MB quota formatting`() {
    val quotaBytes = 1048576000L // 1000 MB in bytes
    val formatted = CloudFile.formatMBOnly(quotaBytes)
    assertEquals("1000.0 MB", formatted)
  }
}
