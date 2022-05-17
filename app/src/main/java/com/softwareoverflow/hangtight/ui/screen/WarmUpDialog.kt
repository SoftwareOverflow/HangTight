package com.softwareoverflow.hangtight.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.ui.theme.AppTheme

/**
 * Shows a dialog warning the user to ensure they are correctly warmed up.
 * Callback includes boolean for if the user has checked the box to not show again.
 */
@Composable
fun WarmUpDialog(onDismiss: () -> Unit, onAccept: (Boolean) -> Unit) {
    var isCheckboxChecked by remember{ mutableStateOf(false)}

    Dialog(onDismissRequest = onDismiss) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    Modifier.height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.WarningAmber, null,
                        Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                    )
                    Text(
                        stringResource(R.string.dialog_warm_up_title),
                        Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.h6
                    )
                }

                Text(stringResource(R.string.dialog_warm_up_message))

                Row (
                    Modifier
                        .fillMaxWidth()
                        .align(CenterHorizontally), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCheckboxChecked, onCheckedChange = { isCheckboxChecked = it })
                    Text(stringResource(R.string.dont_show_again))
                }
                
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
                    Button(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(onClick = { onAccept(isCheckboxChecked) }) {
                        Text(stringResource(R.string.start))
                    }
                }

            }
        }
    }
}

@Preview
@Composable
private fun Preview_WarmUpDialog(){
    AppTheme(darkTheme = false) {
        WarmUpDialog(onDismiss = {}, onAccept = {})
    }
}

@Preview
@Composable
private fun Preview_WarmUpDialog_Dark(){
    AppTheme(darkTheme = true) {
        WarmUpDialog(onDismiss = {}, onAccept = {})
    }
}