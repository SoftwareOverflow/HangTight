package com.softwareoverflow.hangtight.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.consent.ConsentManagerGoogle
import com.softwareoverflow.hangtight.ui.theme.AppTheme
import com.softwareoverflow.hangtight.ui.util.SnackbarManager
import com.softwareoverflow.hangtight.ui.util.findActivity
import com.softwareoverflow.hangtight.ui.viewmodel.SettingsViewModel

@Composable
@Destination
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    val consentManager = ConsentManagerGoogle.getInstance(LocalContext.current)

    SettingsScreenContent(uiState,
        onPrepTimeChange = { viewModel.setPrepTime(it) },
        onSound321Change = { viewModel.setSound321(it) },
        onVibrateChange = { viewModel.setVibrate(it) },
        onWarmUpChange = { viewModel.setWarmUp(it) },
        onAnalyticsChange = { viewModel.setAnalyticsEnabled(it) },
        onSaveChanges = { viewModel.saveSettings(context) },
        showPrivacyOption = consentManager.isPrivacyOptionsRequired,
        openPrivacyOptions = {
            val activity = context.findActivity()
            var privacyOptionError = activity == null
            activity?.let {
                consentManager.showPrivacyOptionsForm(activity) {
                    it?.let {
                        privacyOptionError = true
                    }
                }
            }

            if (privacyOptionError) {
                SnackbarManager.showMessage(context.getString(R.string.unepected_problem_try_again))
            }
        })
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsViewModel.UiState,
    onPrepTimeChange: (Int) -> Unit,
    onSound321Change: (Boolean) -> Unit,
    onVibrateChange: (Boolean) -> Unit,
    onWarmUpChange: (Boolean) -> Unit,
    onAnalyticsChange: (Boolean) -> Unit,
    onSaveChanges: () -> Unit,
    showPrivacyOption: Boolean,
    openPrivacyOptions: () -> Unit,
) {
    val activity = LocalContext.current.findActivity()
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
        ) {
            item {
                PrepTimeRow(uiState.prepTime) {
                    onPrepTimeChange(it)
                }
            }

            item {
                Sound321Row(uiState.sound321) {
                    onSound321Change(it)
                }
            }

            item {
                VibrateRow(value = uiState.vibrate) {
                    onVibrateChange(it)
                }
            }

            item {
                WarmUpRow(value = uiState.warmUp, onValueChange = { onWarmUpChange(it) })
            }

            item {
                AnalyticsRow(value = uiState.analyticsEnabled) {
                    onAnalyticsChange(it)
                }
            }

            if (showPrivacyOption) {
                activity?.let {
                    item {

                        Text(
                            stringResource(R.string.show_privacy_options),
                            Modifier
                                .padding(4.dp)
                                .clickable {
                                    openPrivacyOptions()
                                },
                            style = typography.body1
                        )
                    }

                }
            }
        }

        FloatingActionButton(onClick = { onSaveChanges() }, Modifier.align(Alignment.BottomEnd)) {
            Icon(Icons.Filled.Save, stringResource(R.string.content_desc_save_settings))
        }
    }
}

@Composable
private fun SettingsRow(
    name: String, subtitle: String = "", content: @Composable () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(Modifier.weight(7f)) {
            Text(name, Modifier.padding(4.dp), style = typography.body1)
            Text(subtitle, Modifier.padding(4.dp), style = typography.caption)
        }

        Box(Modifier.weight(3f), contentAlignment = Alignment.CenterEnd) {
            content()
        }
    }
}

@Composable
private fun PrepTimeRow(value: Int, onValueChange: (Int) -> Unit) {
    SettingsRow(
        name = stringResource(R.string.prep_time_name),
        subtitle = stringResource(R.string.prep_time_desc),
    ) {
        var expanded by remember { mutableStateOf(false) }

        val icon =
            if (expanded) Icons.Filled.ArrowDropUp //it requires androidx.compose.material:material-icons-extended
            else Icons.Filled.ArrowDropDown


        Column {
            OutlinedTextField(value = value.toString(),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(icon,
                        stringResource(R.string.content_desc_expand_collapse),
                        Modifier.clickable { expanded = !expanded })
                })
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.height(300.dp)
            ) {
                for (i in 0..15) {
                    DropdownMenuItem(onClick = {
                        onValueChange(i)
                        expanded = false
                    }) {
                        Text(text = i.toString())
                    }
                }
            }
        }
    }
}

@Composable
private fun Sound321Row(value: Boolean, onValueChange: (Boolean) -> Unit) {
    SettingsRow(
        name = stringResource(R.string.sounds_321_name),
        subtitle = stringResource(R.string.sounds_321_desc)
    ) {
        Switch(checked = value, onCheckedChange = onValueChange)
    }
}

@Composable
private fun VibrateRow(value: Boolean, onValueChange: (Boolean) -> Unit) {
    SettingsRow(
        name = stringResource(R.string.vibrate_name),
        subtitle = stringResource(R.string.vibrate_desc)
    ) {
        Switch(checked = value, onCheckedChange = onValueChange)
    }
}

@Composable
private fun WarmUpRow(value: Boolean, onValueChange: (Boolean) -> Unit) {
    SettingsRow(
        name = stringResource(R.string.show_warm_up_warning),
        subtitle = stringResource(R.string.settings_warm_up_desc)
    ) {
        Switch(checked = value, onCheckedChange = onValueChange)
    }
}

@Composable
private fun AnalyticsRow(value: Boolean, onValueChange: (Boolean) -> Unit) {
    SettingsRow(
        name = stringResource(R.string.analytics_name),
        subtitle = stringResource(R.string.analytics_desc)
    ) {
        Switch(checked = value, onCheckedChange = onValueChange)
    }
}

@Composable
@Preview
private fun Preview_SettingsScreen() {
    AppTheme(darkTheme = false) {
        SettingsScreenContent(uiState = SettingsViewModel.UiState(
            12, sound321 = false, vibrate = true, warmUp = true, analyticsEnabled = false
        ),
            onPrepTimeChange = {},
            onSound321Change = {},
            onWarmUpChange = {},
            onAnalyticsChange = {},
            onVibrateChange = {},
            onSaveChanges = {},
            showPrivacyOption = true,
            openPrivacyOptions = {})
    }
}

@Composable
@Preview
private fun Preview_SettingsScreen_Dark() {
    AppTheme(darkTheme = true) {
        SettingsScreenContent(uiState = SettingsViewModel.UiState(
            12, sound321 = false, vibrate = true, warmUp = false, analyticsEnabled = false
        ),
            onPrepTimeChange = {},
            onSound321Change = {},
            onWarmUpChange = {},
            onAnalyticsChange = {},
            onVibrateChange = {},
            onSaveChanges = {},
            showPrivacyOption = true,
            openPrivacyOptions = {})
    }
}