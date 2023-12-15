package com.softwareoverflow.hangtight.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.billing.MobileAdsManager
import com.softwareoverflow.hangtight.billing.UpgradeManager
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.ui.screen.destinations.HomeScreenDestination
import com.softwareoverflow.hangtight.ui.screen.destinations.SaveWorkoutScreenDestination
import com.softwareoverflow.hangtight.ui.theme.AppTheme
import com.softwareoverflow.hangtight.ui.util.ErrorIconWarning
import com.softwareoverflow.hangtight.ui.util.SnackbarManager
import com.softwareoverflow.hangtight.ui.util.findActivity
import com.softwareoverflow.hangtight.ui.viewmodel.WorkoutCompleteViewModel
import timber.log.Timber

@Composable
@Destination
fun WorkoutCompleteScreen(
    navigator: DestinationsNavigator,
    savedWorkoutResult: ResultRecipient<SaveWorkoutScreenDestination, Workout>,
    workout: Workout,
    viewModel: WorkoutCompleteViewModel = hiltViewModel()
) {

    LaunchedEffect(workout) {
        viewModel.initialize(workout)
    }

    val context = LocalContext.current

    BackHandler(enabled = true) {
        navigator.popBackStack(HomeScreenDestination, inclusive = false)
        navigator.clearBackStack(HomeScreenDestination)
    }

    savedWorkoutResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                SnackbarManager.showMessage(context.getString(R.string.workout_saved))
            }
        }
    }

    WorkoutCompleteScreenContent(showSaveWarning = workout.id == null,
        onUpgrade = { viewModel.launchUpgrade(context) },
        onSave = { navigator.navigate(SaveWorkoutScreenDestination(workout)) })

    var showAd by rememberSaveable { mutableStateOf(!UpgradeManager.isUserUpgraded()) }
    val launchInAppReview by viewModel.launchReviewFlow.collectAsState()

    if (launchInAppReview && !showAd) // Don't ask for review over the top of interstitial advert
        viewModel.launchReviewFlow(context)

    if (showAd) {
        InterstitialAdContent {
            showAd = false
        }
    }
}

@Composable
private fun InterstitialAdContent(toggleShowAd: () -> Unit) {
    val context = LocalContext.current

    MobileAdsManager.setFullScreenContentCallback(object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            toggleShowAd()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            Timber.d("Ad failed to load: ${adError.message}")
            toggleShowAd()
        }

        override fun onAdShowedFullScreenContent() {
            MobileAdsManager.setInterstitialShown(context)
        }
    })

    val activity = context.findActivity()
    if (activity == null || !MobileAdsManager.showInterstitial(activity)) {
        toggleShowAd()
    }
}

@Composable
private fun WorkoutCompleteScreenContent(
    showSaveWarning: Boolean, onUpgrade: () -> Unit, onSave: () -> Unit
) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.EmojiEvents,
            contentDescription = stringResource(R.string.content_desc_trophy),
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            tint = colors.primary
        )

        Text(
            text = stringResource(R.string.workout_complete),
            style = typography.h2,
            textAlign = TextAlign.Center
        )

        Text(
            stringResource(R.string.great_job),
            style = typography.body2,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(2f))

        if (!UpgradeManager.isUserUpgraded()) {
            Button({
                onUpgrade()
            }) {
                Text(stringResource(R.string.upgrade_ad_free))
            }
        }

        Spacer(Modifier.weight(1f))

        Row(Modifier.align(End), verticalAlignment = Alignment.CenterVertically) {
            if (showSaveWarning) ErrorIconWarning(
                message = stringResource(R.string.save_workout_use_again),
                Modifier.padding(end = 8.dp)
            )

            FloatingActionButton(onClick = { onSave() }) {
                Icon(Icons.Filled.Save, stringResource(R.string.content_desc_save_workout))
            }
        }
    }
}

@Preview
@Composable
private fun Preview_WorkoutCompleteScreen() {
    AppTheme(darkTheme = false) {
        WorkoutCompleteScreenContent(true, {}, {})
    }
}

@Preview
@Composable
private fun Preview_WorkoutCompleteScreen_Dark() {
    AppTheme(darkTheme = true) {
        WorkoutCompleteScreenContent(true, {}, {})
    }
}