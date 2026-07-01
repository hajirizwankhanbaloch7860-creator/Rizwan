package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.IslamicViewModel
import com.example.ui.screens.IslamicAppUi
import com.example.ui.theme.MyApplicationTheme
import com.example.utils.IslamicNotificationHelper

class MainActivity : ComponentActivity() {

  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    // Permission response handled if needed
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Create Notification Channel
    IslamicNotificationHelper.createNotificationChannel(this)

    // Request notification permission for Android 13+
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
      if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
      }
    }

    // Schedule default daily reminder at 8:00 AM if never set
    val prefs = getSharedPreferences("islamic_notification_settings", MODE_PRIVATE)
    if (!prefs.contains("enabled")) {
      IslamicNotificationHelper.scheduleDailyReminder(this, 8, 0)
    }

    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          val viewModel: IslamicViewModel = viewModel()
          IslamicAppUi(viewModel = viewModel)
        }
      }
    }
  }
}
