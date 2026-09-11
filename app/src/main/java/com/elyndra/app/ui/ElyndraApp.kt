package com.elyndra.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elyndra.app.domain.model.UserPreferences
import com.elyndra.app.domain.repository.UserPreferencesRepository
import com.elyndra.app.ui.navigation.ElyndraNavHost
import com.elyndra.app.ui.theme.ElyndraTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@Composable
fun ElyndraApp(viewModel: AppViewModel = hiltViewModel()) {
    val preferences by viewModel.preferences.collectAsState()

    AppLocaleProvider(language = preferences.language) {
        ElyndraTheme(preferences = preferences) {
            ElyndraNavHost()
        }
    }
}

@HiltViewModel
class AppViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val preferences: StateFlow<UserPreferences> = userPreferencesRepository.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences())
}
