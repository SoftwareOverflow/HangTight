package com.softwareoverflow.hangtight.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.logging.FirebaseManager
import com.softwareoverflow.hangtight.ui.SharedPreferencesManager
import com.softwareoverflow.hangtight.ui.util.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val firebaseManager: FirebaseManager
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            prepTime = sharedPrefs.getInt(SharedPreferencesManager.prepTime, 10),
            sound321 = sharedPrefs.getBoolean(SharedPreferencesManager.sound321, true),
            vibrate = sharedPrefs.getBoolean(SharedPreferencesManager.vibrate, true),
            warmUp = sharedPrefs.getBoolean(SharedPreferencesManager.showWarmUpWarning, true),
            analyticsEnabled = sharedPrefs.getBoolean(
                SharedPreferencesManager.analyticsEnabled,
                false
            )
        )
    )
    val uiState: StateFlow<UiState> get() = _uiState

    fun setPrepTime(value: Int) {
        _uiState.value = _uiState.value.copy(prepTime = value)
    }

    fun setSound321(value: Boolean) {
        _uiState.value = _uiState.value.copy(sound321 = value)
    }

    fun setVibrate(value: Boolean) {
        _uiState.value = _uiState.value.copy(vibrate = value)
    }

    fun setAnalyticsEnabled(value: Boolean) {
        _uiState.value = _uiState.value.copy(analyticsEnabled = value)
    }

    fun setWarmUp(value: Boolean){
        _uiState.value = _uiState.value.copy(warmUp = value)
    }


    fun saveSettings(context: Context) {
        sharedPrefs.edit().apply {
            putInt(SharedPreferencesManager.prepTime, _uiState.value.prepTime)
            putBoolean(SharedPreferencesManager.sound321, _uiState.value.sound321)
            putBoolean(SharedPreferencesManager.vibrate, _uiState.value.vibrate)
            putBoolean(SharedPreferencesManager.showWarmUpWarning, _uiState.value.warmUp)

            apply()
        }

        if (uiState.value.analyticsEnabled)
            firebaseManager.onConsentGiven()
        else
            firebaseManager.onConsentWithdrawn()

        SnackbarManager.showMessage(context.getString(R.string.settings_saved))
    }

    data class UiState(
        val prepTime: Int,
        val sound321: Boolean,
        val vibrate: Boolean,
        val warmUp: Boolean,
        val analyticsEnabled: Boolean
    )
}