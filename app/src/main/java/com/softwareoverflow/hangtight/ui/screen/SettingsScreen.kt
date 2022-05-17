package com.softwareoverflow.hangtight.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.ui.theme.AppTheme
import com.softwareoverflow.hangtight.ui.viewmodel.SettingsViewModel

@Composable
@Destination
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    SettingsScreenContent(
        uiState,
        onPrepTimeChange = { viewModel.setPrepTime(it) },
        onSound321Change = { viewModel.setSound321(it) },
        onVibrateChange = { viewModel.setVibrate(it) },
        onWarmUpChange = { viewModel.setWarmUp(it) },
        onAnalyticsChange = { viewModel.setAnalyticsEnabled(it) },
        onSaveChanges = { viewModel.saveSettings(context) }
    )
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
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PrepTimeRow(uiState.prepTime) {
            onPrepTimeChange(it)
        }

        Sound321Row(uiState.sound321) {
            onSound321Change(it)
        }

        VibrateRow(value = uiState.vibrate) {
            onVibrateChange(it)
        }

        WarmUpRow(value = uiState.warmUp, onValueChange = { onWarmUpChange(it) })

        AnalyticsRow(value = uiState.analyticsEnabled) {
            onAnalyticsChange(it)
        }

        Spacer(Modifier.weight(1f))

        FloatingActionButton(onClick = { onSaveChanges() }, Modifier.align(End)) {
            Icon(Icons.Filled.Save, stringResource(R.string.content_desc_save_settings))
        }
    }
}

@Composable
private fun SettingsRow(
    name: String,
    subtitle: String = "",
    content: @Composable () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(Modifier.weight(3f)) {
            Text(name, Modifier.padding(4.dp), style = typography.body1)
            Text(subtitle, Modifier.padding(4.dp), style = typography.caption)
        }

        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
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

        val icon = if (expanded)
            Icons.Filled.ArrowDropUp //it requires androidx.compose.material:material-icons-extended
        else
            Icons.Filled.ArrowDropDown


        Column {
            OutlinedTextField(
                value = value.toString(),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(icon, stringResource(R.string.content_desc_expand_collapse),
                        Modifier.clickable { expanded = !expanded })
                }
            )
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
        SettingsScreenContent(
            uiState = SettingsViewModel.UiState(
                12,
                sound321 = false,
                vibrate = true,
                warmUp = true,
                analyticsEnabled = false
            ),
            onPrepTimeChange = {},
            onSound321Change = {},
            onWarmUpChange = {},
            onAnalyticsChange = {},
            onVibrateChange = {}
        ) { }
    }
}

@Composable
@Preview
private fun Preview_SettingsScreen_Dark() {
    AppTheme(darkTheme = true) {
        SettingsScreenContent(
            uiState = SettingsViewModel.UiState(
                12,
                sound321 = false,
                vibrate = true,
                warmUp = false,
                analyticsEnabled = false
            ),
            onPrepTimeChange = {},
            onSound321Change = {},
            onWarmUpChange = {},
            onAnalyticsChange = {},
            onVibrateChange = {}
        ) { }
    }
}