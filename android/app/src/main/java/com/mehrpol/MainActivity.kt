package com.mehrpol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mehrpol.theme.MehrpolTheme
import com.mehrpol.ui.main.AppThemeMode
import com.mehrpol.ui.main.AppUI
import com.mehrpol.ui.main.MainViewModel
import com.mehrpol.ui.main.MainViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(application))
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()
      MehrpolTheme(darkTheme = uiState.settings.themeMode == AppThemeMode.DARK) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          AppUI(viewModel = viewModel)
        }
      }
    }
  }
}
