package com.softwareoverflow.hangtight.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.ui.util.ScreenTitle

@Composable
fun HangTightTopAppBar(toggleDrawer: () -> Unit) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
            )
        },

        navigationIcon = {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = stringResource(R.string.content_desc_app_icon)
            )
        },

        actions = {
            IconButton(onClick = {
                toggleDrawer()
            }) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = Icons.Filled.Settings.name
                )
            }
        },

        elevation = AppBarDefaults.TopAppBarElevation
    )
}

@Composable
fun HangTightTopScreenBar(currentScreen: String) {
    TopAppBar(backgroundColor = MaterialTheme.colors.primary,
        title = { ScreenTitle(title = currentScreen) }
    )
}