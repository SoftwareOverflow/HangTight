package com.softwareoverflow.hangtight.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softwareoverflow.hangtight.BuildConfig
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.billing.UpgradeManager
import com.softwareoverflow.hangtight.ui.theme.AppTheme
import com.softwareoverflow.hangtight.ui.util.IconTextRow

@Composable
fun AppDrawer(
    openSettings: () -> Unit,
    sendFeedback: () -> Unit,
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
    ) {
        Box(
            Modifier
                .width(IntrinsicSize.Min)
                .aspectRatio(1f)
                .background(MaterialTheme.colors.primary.copy(alpha = 0.5f))
        ) {
            Text(
                "Hang",
                Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp), style = MaterialTheme.typography.h3
            )

            Image(
                painterResource(id = R.mipmap.ic_launcher_foreground), null,
                Modifier.fillMaxSize(),
                alignment = Alignment.Center
            )

            Text(
                "Tight",
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp), style = MaterialTheme.typography.h3
            )

            Text(
                "${stringResource(R.string.version)} ${BuildConfig.VERSION_NAME}",
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
            )
        }
        Spacer(Modifier.weight(1f))

        Column(Modifier.padding(16.dp)) {

            IconTextRow(
                message = stringResource(R.string.settings),
                icon = Icons.Filled.Settings,
                iconSize = 30.dp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { openSettings() })

            IconTextRow(message = stringResource(R.string.feedback),
                icon = Icons.Filled.Email,
                iconSize = 30.dp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { sendFeedback() })


            if (UpgradeManager.isUserUpgraded()) {
                Text(stringResource(R.string.upgrade_state_premium_thanks))
            } else {
                Text(stringResource(R.string.upgrade_state_free))

                var upgradeText = stringResource(R.string.upgrade_ad_free)

                if (UpgradeManager.proPrice != null) {
                    upgradeText += " ${stringResource(R.string.for_)} ${UpgradeManager.proPrice}"
                }

                Text(
                    upgradeText,
                    Modifier.clickable { onUpgrade() },
                    style = TextStyle(
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun Preview_Drawer() {
    AppTheme(darkTheme = false) {
        AppDrawer({}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
fun Preview_Drawer_Dark() {
    AppTheme(darkTheme = true) {
        AppDrawer({}, {}, {})
    }
}