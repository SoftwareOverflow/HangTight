package com.softwareoverflow.hangtight.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultRecipient
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.ui.screen.destinations.SaveWorkoutScreenDestination
import com.softwareoverflow.hangtight.ui.screen.destinations.WorkoutScreenDestination
import com.softwareoverflow.hangtight.ui.theme.AppTheme
import com.softwareoverflow.hangtight.ui.util.DisableableFloatingActionButton
import com.softwareoverflow.hangtight.ui.util.IntNumberConverter
import com.softwareoverflow.hangtight.ui.util.NumberFieldPlusMinus
import com.softwareoverflow.hangtight.ui.viewmodel.WorkoutCreatorViewModel

@Composable
@Destination
fun WorkoutCreatorScreen(
    navigator: DestinationsNavigator,
    savedWorkoutResult: ResultRecipient<SaveWorkoutScreenDestination, Workout>,
    workoutToEdit: Workout,
    viewModel: WorkoutCreatorViewModel = hiltViewModel()
) {
    viewModel.initialize(workoutToEdit)

    val workout by viewModel.workout.collectAsState()
    val anyInputErrors by viewModel.anyInputErrors.collectAsState(initial = false)

    savedWorkoutResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                viewModel.onWorkoutSaved(result.value)
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        NumberFieldPlusMinus(
            value = workout.hangTime,
            numberConverter = IntNumberConverter(),
            label = stringResource(R.string.hang),
            Modifier
                .weight(1f)
                .padding(16.dp),
            onInputError = {
                viewModel.setHangError()
            }
        ) {
            viewModel.setHangTime(it)
        }

        NumberFieldPlusMinus(
            value = workout.restTime,
            numberConverter = IntNumberConverter(),
            label = stringResource(R.string.rest),
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            allowZero = true,
            onInputError = {
                viewModel.setRestError()
            }
        ) {
            viewModel.setRestTime(it)
        }

        NumberFieldPlusMinus(
            value = workout.numReps,
            numberConverter = IntNumberConverter(),
            label = "Num Reps",
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp),
            onInputError = {
                viewModel.setNumRepsError()
            }
        ) {
            viewModel.setNumReps(it)
        }

        NumberFieldPlusMinus(
            value = workout.numSets,
            numberConverter = IntNumberConverter(),
            label = "Num Sets",
            Modifier
                .weight(1f)
                .padding(16.dp),
            onInputError = {
                viewModel.setNumSetsError()
            }
        ) {
            viewModel.setNumSets(it)
        }

        NumberFieldPlusMinus(
            value = workout.recoverTime,
            numberConverter = IntNumberConverter(),
            label = stringResource(R.string.recover),
            Modifier
                .weight(1f)
                .padding(16.dp),
            allowZero = true,
            onInputError = {
                viewModel.setRecoverError()
            }
        ) {
            viewModel.setRecoverTime(it)
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), Arrangement.SpaceBetween
        ) {
            DisableableFloatingActionButton(onClick = {
                navigator.navigate(SaveWorkoutScreenDestination(workout))
            }, enabled = { !anyInputErrors }) {
                Icon(Icons.Filled.Save, stringResource(R.string.content_desc_save_workout))
            }

            DisableableFloatingActionButton(onClick = {
                navigator.navigate(
                    WorkoutScreenDestination(
                        workout
                    )
                )
            }, enabled = { !anyInputErrors }) {
                Icon(Icons.Filled.PlayArrow, stringResource(R.string.content_desc_start))
            }
        }
    }
}

@Preview
@Composable
private fun Preview_WorkoutCreatorScreen() {
    AppTheme(darkTheme = false) {
        WorkoutCreatorScreen(
            EmptyDestinationsNavigator,
            EmptyResultRecipient(),
            Workout(),
            WorkoutCreatorViewModel()
        )
    }
}

@Preview
@Composable
private fun Preview_WorkoutCreatorScreen_Dark() {
    AppTheme(darkTheme = true) {
        WorkoutCreatorScreen(
            EmptyDestinationsNavigator,
            EmptyResultRecipient(),
            Workout(),
            WorkoutCreatorViewModel()
        )
    }
}