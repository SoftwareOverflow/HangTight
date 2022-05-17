package com.softwareoverflow.hangtight.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.ui.screen.destinations.WorkoutCompleteScreenDestination
import com.softwareoverflow.hangtight.ui.theme.AppTheme
import com.softwareoverflow.hangtight.ui.util.KeepScreenOn
import com.softwareoverflow.hangtight.ui.util.SnackbarManager
import com.softwareoverflow.hangtight.ui.util.workout.WorkoutSection
import com.softwareoverflow.hangtight.ui.util.workout.WorkoutSectionCounter
import com.softwareoverflow.hangtight.ui.util.workout.WorkoutSectionWithTime
import com.softwareoverflow.hangtight.ui.util.workout.getFormattedDuration
import com.softwareoverflow.hangtight.ui.viewmodel.WorkoutUiState
import com.softwareoverflow.hangtight.ui.viewmodel.WorkoutViewModel

@Composable
@Destination
fun WorkoutScreen(
    navigator: DestinationsNavigator,
    workout: Workout,
    viewModel: WorkoutViewModel = hiltViewModel()
) {

    viewModel.initialize(workout)

    KeepScreenOn()

    val context = LocalContext.current
    BackHandler(enabled = true) {
        SnackbarManager.showMessage(context.getString(R.string.are_you_sure),
            actionText = context.getString(
                R.string.exit
            ),
            onAction = {
                navigator.navigateUp()
            })
    }

    val isFinished by viewModel.isWorkoutFinished.collectAsState()
    if (isFinished) {
        viewModel.cancel()
        navigator.navigate(WorkoutCompleteScreenDestination(workout))
    }

    val uiState by viewModel.uiState.collectAsState()

    WorkoutScreenContent(
        uiState = uiState,
        workoutDuration = workout.getFormattedDuration(),
        onRewind = { viewModel.rewind() },
        onSkip = { viewModel.skipSection() },
        onToggleMute = { viewModel.toggleMute() },
        onTogglePause = { viewModel.togglePause() })

    val showWarmUpWarningDialog by viewModel.showWarmUpWarning.collectAsState()
    if(showWarmUpWarningDialog)
        WarmUpDialog(onDismiss = { navigator.navigateUp() }, onAccept = { viewModel.closeWarmUpDialog(it) })

}

@Composable
private fun WorkoutScreenContent(
    uiState: WorkoutUiState,
    workoutDuration: String,
    onRewind: () -> Unit,
    onSkip: () -> Unit,
    onToggleMute: () -> Unit,
    onTogglePause: () -> Unit,
) {
    Column(
        Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val currentSection = uiState.currentSection

        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
                Text("Set:")
                Text(
                    "%d/%d".format(
                        currentSection.set.index + 1,
                        currentSection.set.total
                    )
                )
                Spacer(Modifier.weight(1f))
                Text("Rep:")
                Text(
                    "%d/%d".format(
                        currentSection.rep.index + 1,
                        currentSection.rep.total
                    )
                )
            }
        }
        Divider(Modifier.fillMaxWidth())

        Spacer(Modifier.weight(1f))

        Text(currentSection.section.name, style = MaterialTheme.typography.h2)

        Box(
            Modifier
                .padding(16.dp)
                .weight(1.5f)
                .fillMaxWidth()
        ) {

            Text(
                uiState.timeLeftInSection.toString(),
                Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.h1
            )

            CircularProgressIndicator(
                progress = uiState.currentSectionProgress,
                Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f, true)
                    .align(
                        Alignment.Center
                    )
            )
        }

        Spacer(Modifier.weight(1f))

        Divider(Modifier.fillMaxWidth())

        Row(
            Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                Icons.Filled.SkipPrevious, stringResource(R.string.content_desc_rewind),
                Modifier.clickable {
                    onRewind()
                })

            if (uiState.isMuted) {
                Icon(
                    Icons.Filled.VolumeOff, stringResource(R.string.content_desc_unmute),
                    Modifier.clickable {
                        onToggleMute()
                    })
            } else {
                Icon(
                    Icons.Filled.VolumeUp, stringResource(R.string.content_desc_mute),
                    Modifier.clickable {
                        onToggleMute()
                    })
            }

            if (uiState.isPaused) {
                Icon(
                    Icons.Filled.PlayArrow, stringResource(R.string.content_desc_unpause),
                    Modifier.clickable { onTogglePause() })
            } else {
                Icon(
                    Icons.Filled.Pause, stringResource(R.string.content_desc_pause),
                    Modifier.clickable {
                        onTogglePause()
                    })
            }

            Icon(
                Icons.Filled.SkipNext, stringResource(R.string.content_desc_skip),
                Modifier.clickable { onSkip() })
        }

        Divider(Modifier.fillMaxWidth())

        Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {

            ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
                Text(stringResource(R.string.remaining))
                Text(uiState.timeLeftInWorkout)

                Spacer(Modifier.weight(1f))

                Text(stringResource(R.string.total))
                Text(workoutDuration)
            }
        }
    }
}

@Preview
@Composable
private fun Preview_WorkoutScreen() {
    AppTheme(darkTheme = false) {
        WorkoutScreenContent(
            uiState = WorkoutUiState(
                currentSection = WorkoutSectionWithTime(
                    WorkoutSection.Rest,
                    12,
                    WorkoutSectionCounter(3, 6),
                    WorkoutSectionCounter(2, 4)
                ),
                timeLeftInSection = 5,
                currentSectionProgress = 0.75f,
                isMuted = true,
                isPaused = false,
                timeLeftInWorkout = "12:35",
            ),
            workoutDuration = "20:45",
            onRewind = { },
            onSkip = { },
            onToggleMute = { },
            onTogglePause = { })
    }
}

@Preview
@Composable
private fun Preview_WorkoutScreen_Dark() {
    AppTheme(darkTheme = true) {
        WorkoutScreenContent(
            uiState = WorkoutUiState(
                currentSection = WorkoutSectionWithTime(
                    WorkoutSection.Hang,
                    12,
                    WorkoutSectionCounter(3, 6),
                    WorkoutSectionCounter(2, 4)
                ),
                timeLeftInSection = 5,
                currentSectionProgress = 0.75f,
                isMuted = true,
                isPaused = false,
                timeLeftInWorkout = "12:35",
            ),
            workoutDuration = "20:45",
            onRewind = { },
            onSkip = { },
            onToggleMute = { },
            onTogglePause = { })
    }
}