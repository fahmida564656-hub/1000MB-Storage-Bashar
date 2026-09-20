package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AuthResult
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.StorageViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
  viewModel: StorageViewModel,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  var phone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  val scope = rememberCoroutineScope()
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(24.dp)
  ) {
    IconButton(
      onClick = { viewModel.navigateTo(ScreenDestination.LANDING) },
      modifier = Modifier.testTag("login_back_button")
    ) {
      Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = Strings.login(language),
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = if (language == AppLanguage.BENGALI)
        "আপনার নিবন্ধিত মোবাইল নম্বর ও পাসওয়ার্ড দিয়ে প্রবেশ করুন।"
      else
        "Enter your registered mobile number and password to log in.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(28.dp))

    // Error Box
    if (errorMessage != null) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = errorMessage!!,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer
          )
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Phone Field
    OutlinedTextField(
      value = phone,
      onValueChange = { phone = it; errorMessage = null },
      label = { Text(Strings.mobileNumber(language)) },
      placeholder = { Text(Strings.mobilePlaceholder(language)) },
      leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("login_phone_field"),
      shape = RoundedCornerShape(14.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Password Field
    OutlinedTextField(
      value = password,
      onValueChange = { password = it; errorMessage = null },
      label = { Text(Strings.password(language)) },
      leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
      trailingIcon = {
        IconButton(onClick = { passwordVisible = !passwordVisible }) {
          Icon(
            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
            contentDescription = null
          )
        }
      },
      visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("login_password_field"),
      shape = RoundedCornerShape(14.dp)
    )

    // Forgot Password Link
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      TextButton(
        onClick = { viewModel.navigateTo(ScreenDestination.FORGOT_PASSWORD) },
        modifier = Modifier.testTag("login_forgot_password_button")
      ) {
        Text(
          text = Strings.forgotPassword(language),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Login Action Button
    Button(
      onClick = {
        if (phone.isBlank() || password.isBlank()) {
          errorMessage = Strings.errWrongPassword(language)
          return@Button
        }
        isLoading = true
        scope.launch {
          val result = viewModel.authRepository.login(phone, password)
          isLoading = false
          when (result) {
            is AuthResult.Success -> {
              viewModel.navigateTo(ScreenDestination.DASHBOARD)
            }
            is AuthResult.Error -> {
              errorMessage = if (language == AppLanguage.BENGALI) result.messageBn else result.messageEn
            }
          }
        }
      },
      shape = RoundedCornerShape(16.dp),
      enabled = !isLoading,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("login_submit_button")
    ) {
      if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
      } else {
        Text(
          text = Strings.login(language),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Navigate to Register
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (language == AppLanguage.BENGALI) "অ্যাকাউন্ট নেই?" else "Don't have an account?",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      TextButton(onClick = { viewModel.navigateTo(ScreenDestination.REGISTER) }) {
        Text(
          text = Strings.register(language),
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

@Composable
fun RegisterScreen(
  viewModel: StorageViewModel,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  var phone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var acceptedTerms by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  val scope = rememberCoroutineScope()
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(24.dp)
  ) {
    IconButton(
      onClick = { viewModel.navigateTo(ScreenDestination.LANDING) },
      modifier = Modifier.testTag("register_back_button")
    ) {
      Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = Strings.register(language),
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = if (language == AppLanguage.BENGALI)
        "অ্যাকাউন্ট খুলে তাৎক্ষণিকভাবে ১০০০ MB স্থায়ী স্টোরেজ পান।"
      else
        "Create an account and immediately receive 1000 MB permanent cloud storage.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (errorMessage != null) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = errorMessage!!,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer
          )
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Phone
    OutlinedTextField(
      value = phone,
      onValueChange = { phone = it; errorMessage = null },
      label = { Text(Strings.mobileNumber(language)) },
      placeholder = { Text(Strings.mobilePlaceholder(language)) },
      leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("register_phone_field"),
      shape = RoundedCornerShape(14.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Password
    OutlinedTextField(
      value = password,
      onValueChange = { password = it; errorMessage = null },
      label = { Text(Strings.password(language)) },
      leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
      trailingIcon = {
        IconButton(onClick = { passwordVisible = !passwordVisible }) {
          Icon(
            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
            contentDescription = null
          )
        }
      },
      visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("register_password_field"),
      shape = RoundedCornerShape(14.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Confirm Password
    OutlinedTextField(
      value = confirmPassword,
      onValueChange = { confirmPassword = it; errorMessage = null },
      label = { Text(Strings.confirmPassword(language)) },
      leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
      visualTransformation = PasswordVisualTransformation(),
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("register_confirm_password_field"),
      shape = RoundedCornerShape(14.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Terms and Conditions checkbox
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Checkbox(
        checked = acceptedTerms,
        onCheckedChange = { acceptedTerms = it; errorMessage = null },
        modifier = Modifier.testTag("register_terms_checkbox")
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = Strings.acceptTerms(language),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.clickable { viewModel.navigateTo(ScreenDestination.TERMS) }
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
      onClick = {
        if (!acceptedTerms) {
          errorMessage = Strings.errAcceptTerms(language)
          return@Button
        }
        if (password.length < 8) {
          errorMessage = Strings.errPasswordShort(language)
          return@Button
        }
        if (password != confirmPassword) {
          errorMessage = Strings.errPasswordMismatch(language)
          return@Button
        }
        isLoading = true
        scope.launch {
          val result = viewModel.authRepository.register(phone, password, confirmPassword, acceptedTerms)
          isLoading = false
          when (result) {
            is AuthResult.Success -> {
              viewModel.navigateTo(ScreenDestination.DASHBOARD)
            }
            is AuthResult.Error -> {
              errorMessage = if (language == AppLanguage.BENGALI) result.messageBn else result.messageEn
            }
          }
        }
      },
      shape = RoundedCornerShape(16.dp),
      enabled = !isLoading,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("register_submit_button")
    ) {
      if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
      } else {
        Text(
          text = Strings.register(language),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (language == AppLanguage.BENGALI) "ইতিমধ্যে অ্যাকাউন্ট আছে?" else "Already have an account?",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      TextButton(onClick = { viewModel.navigateTo(ScreenDestination.LOGIN) }) {
        Text(
          text = Strings.login(language),
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

@Composable
fun ForgotPasswordScreen(
  viewModel: StorageViewModel,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  var step by remember { mutableStateOf(1) } // 1: phone, 2: otp + new password
  var phone by remember { mutableStateOf("") }
  var otpCode by remember { mutableStateOf("") }
  var newPassword by remember { mutableStateOf("") }
  var confirmNewPassword by remember { mutableStateOf("") }
  var testVerificationHint by remember { mutableStateOf<String?>(null) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  val scope = rememberCoroutineScope()
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(24.dp)
  ) {
    IconButton(
      onClick = { viewModel.navigateTo(ScreenDestination.LOGIN) },
      modifier = Modifier.testTag("forgot_back_button")
    ) {
      Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = Strings.resetPassword(language),
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = if (step == 1) {
        if (language == AppLanguage.BENGALI)
          "আপনার অ্যাকাউন্টের মোবাইল নম্বর লিখুন। আমরা একটি ওটিপি (OTP) কোড পাঠাব।"
        else
          "Enter your account mobile number. We will send an OTP verification code."
      } else {
        if (language == AppLanguage.BENGALI)
          "আপনার মোবাইলে পাঠানো ৬ সংখ্যার ওটিপি কোড এবং নতুন পাসওয়ার্ড লিখুন।"
        else
          "Enter the 6-digit OTP code sent to your phone and your new password."
      },
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (errorMessage != null) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = errorMessage!!,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer
          )
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    if (testVerificationHint != null) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = if (language == AppLanguage.BENGALI)
              "যাচাইকরণ কোড: $testVerificationHint (SMS সার্ভিস সংযোগের বিকল্পে অ্যাপে প্রদর্শিত)"
            else
              "Verification code: $testVerificationHint (Displayed for instant testing)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
          )
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    if (step == 1) {
      OutlinedTextField(
        value = phone,
        onValueChange = { phone = it; errorMessage = null },
        label = { Text(Strings.mobileNumber(language)) },
        placeholder = { Text(Strings.mobilePlaceholder(language)) },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("forgot_phone_field"),
        shape = RoundedCornerShape(14.dp)
      )

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = {
          if (phone.isBlank()) {
            errorMessage = Strings.errInvalidMobile(language)
            return@Button
          }
          isLoading = true
          scope.launch {
            val (success, otpOrError) = viewModel.authRepository.requestPasswordResetOtp(phone)
            isLoading = false
            if (success) {
              testVerificationHint = otpOrError
              step = 2
            } else {
              errorMessage = otpOrError
            }
          }
        },
        shape = RoundedCornerShape(16.dp),
        enabled = !isLoading,
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("forgot_request_otp_button")
      ) {
        if (isLoading) {
          CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
        } else {
          Text(
            text = if (language == AppLanguage.BENGALI) "ওটিপি পাঠান" else "Send OTP",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }
      }
    } else {
      // Step 2
      OutlinedTextField(
        value = otpCode,
        onValueChange = { otpCode = it; errorMessage = null },
        label = { Text(Strings.otpCode(language)) },
        placeholder = { Text("123456") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("forgot_otp_field"),
        shape = RoundedCornerShape(14.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = newPassword,
        onValueChange = { newPassword = it; errorMessage = null },
        label = { Text(if (language == AppLanguage.BENGALI) "নতুন পাসওয়ার্ড" else "New Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("forgot_new_password_field"),
        shape = RoundedCornerShape(14.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = confirmNewPassword,
        onValueChange = { confirmNewPassword = it; errorMessage = null },
        label = { Text(Strings.confirmPassword(language)) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("forgot_confirm_new_password_field"),
        shape = RoundedCornerShape(14.dp)
      )

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = {
          if (newPassword.length < 8) {
            errorMessage = Strings.errPasswordShort(language)
            return@Button
          }
          if (newPassword != confirmNewPassword) {
            errorMessage = Strings.errPasswordMismatch(language)
            return@Button
          }
          isLoading = true
          scope.launch {
            val result = viewModel.authRepository.verifyOtpAndResetPassword(
              phone,
              otpCode,
              newPassword,
              confirmNewPassword
            )
            isLoading = false
            when (result) {
              is AuthResult.Success -> {
                viewModel.navigateTo(ScreenDestination.DASHBOARD)
              }
              is AuthResult.Error -> {
                errorMessage = if (language == AppLanguage.BENGALI) result.messageBn else result.messageEn
              }
            }
          }
        },
        shape = RoundedCornerShape(16.dp),
        enabled = !isLoading,
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("forgot_verify_submit_button")
      ) {
        if (isLoading) {
          CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
        } else {
          Text(
            text = Strings.verifyAndChange(language),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun TermsScreen(
  language: AppLanguage,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(24.dp)
  ) {
    IconButton(onClick = onBack) {
      Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = if (language == AppLanguage.BENGALI) "শর্তাবলী ও গোপনীয়তা নীতি" else "Terms of Service & Privacy Policy",
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(16.dp))

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Text(
          text = if (language == AppLanguage.BENGALI) "১. ১০০০ MB স্থায়ী স্টোরেজ নীতি" else "1. 1000 MB Permanent Storage Policy",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (language == AppLanguage.BENGALI)
            "প্রতিটি নিবন্ধিত ব্যবহারকারী সর্বোচ্চ ১০০০ MB (১ GB) স্থায়ী ক্লাউড স্টোরেজ পাবেন। এই স্টোরেজ কোনো নির্দিষ্ট মেয়াদের পর স্বয়ংক্রিয়ভাবে মুছে যাবে না।"
          else
            "Each registered user receives exactly 1000 MB (1 GB) of permanent cloud storage. Storage files do not automatically expire or get deleted after 30 days.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = if (language == AppLanguage.BENGALI) "২. তথ্য নিরাপত্তা ও গোপনীয়তা" else "2. Data Privacy & Isolation",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (language == AppLanguage.BENGALI)
            "আপনার পাসওয়ার্ড এবং সমস্ত ফাইল কঠোরভাবে সুরক্ষিত এবং এনক্রিপ্ট করা হয়। অন্য কোনো ব্যবহারকারী আপনার ফাইল অ্যাক্সেস করতে পারবে না।"
          else
            "Your password and all files are strictly isolated and protected. No other user can access or view your uploaded assets.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = if (language == AppLanguage.BENGALI) "৩. নিষিদ্ধ ফাইল ও কন্টেন্ট" else "3. Prohibited Content",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (language == AppLanguage.BENGALI)
            "ম্যালওয়্যার, ভাইরাস বা ক্ষতিকর সফটওয়্যার আপলোড করা কঠোরভাবে নিষিদ্ধ। লঙ্ঘনে অ্যাকাউন্ট স্থগিত করা হতে পারে।"
          else
            "Uploading malware, viruses, or illegal files is strictly prohibited and subject to account suspension.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
