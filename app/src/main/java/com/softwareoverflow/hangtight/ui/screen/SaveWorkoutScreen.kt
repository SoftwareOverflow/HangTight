package com.softwareoverflow.hangtight.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.billing.UpgradeManager
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.ui.theme.AppTheme
import com.softwareoverflow.hangtight.ui.util.ErrorIconWarning
import com.softwareoverflow.hangtight.ui.util.SnackbarManager
import com.softwareoverflow.hangtight.ui.util.workout.SaveType
import com.softwareoverflow.hangtight.ui.viewmodel.SaveWorkoutViewModel

@Composable
@Destination
fun SaveWorkoutScreen(
    resultNavigator: ResultBackNavigator<Workout>,
    workout: Workout,
    saveViewModel: SaveWorkoutViewModel = hiltViewModel()
) {
    val allSavedWorkouts by saveViewModel.savedWorkouts.collectAsState()
    val isLoadingSavedWorkouts by saveViewModel.isLoading.collectAsState()

    if (isLoadingSavedWorkouts)
        CircularProgressIndicator()
    else {
        val context = LocalContext.current

        SaveWorkoutScreenContent(
            workout = workout,
            allSavedWorkouts = allSavedWorkouts,
            onUpgrade = { saveViewModel.launchUpgrade(context) },
            onCancel = {
                resultNavigator.navigateBack()
            }) { workoutToSave, name, desc, idToOverwrite ->
            saveViewModel.saveWorkout(workoutToSave, name, desc, idToOverwrite)
        }
    }

    val savedWorkout by saveViewModel.savedWorkout.collectAsState()
    savedWorkout?.let {
        LaunchedEffect(it) { // LaunchedEffect to ensure we only trigger the navigateBack call once
            resultNavigator.navigateBack(it)
        }
    }
}

@Composable
private fun SaveWorkoutScreenContent(
    workout: Workout,
    allSavedWorkouts: List<Workout>,
    onUpgrade: () -> Unit,
    onCancel: () -> Unit,
    onSave: (Workout, String, String, Int?) -> Unit
) {
    val context = LocalContext.current

    var workoutName by remember { mutableStateOf(workout.name) }
    var workoutDesc by remember { mutableStateOf(workout.description) }

    val initialSaveType = if (workout.id == null) SaveType.CreateNew else SaveType.OverwriteExisting
    var saveType by remember { mutableStateOf(initialSaveType) }
    var selectedOverwriteId by remember { mutableStateOf(workout.id ?: -1) }


    val nameLengthLimit = 50
    val descriptionLengthLimit = 200

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            val isWorkoutNameError = workoutName.isBlank() || workoutName.length > nameLengthLimit


            OutlinedTextField(
                value = workoutName, onValueChange = { workoutName = it },
                label = {
                    Text(stringResource(R.string.workout_name))
                },
                isError = isWorkoutNameError,
                trailingIcon = {
                    if (isWorkoutNameError)
                        Icon(Icons.Filled.Error, stringResource(R.string.content_desc_error))
                },
                singleLine = true,
                modifier = Modifier.weight(8f),
            )

            Text(
                "${workoutName.length}/$nameLengthLimit",
                Modifier.weight(2f),
                style = typography.overline,
                color = if (isWorkoutNameError) colors.error else colors.secondary,
                textAlign = TextAlign.Center,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            val isDescriptionError = workoutDesc.length > descriptionLengthLimit

            OutlinedTextField(
                value = workoutDesc, onValueChange = { workoutDesc = it },
                label = {
                    Text(stringResource(R.string.workout_desc))
                },
                modifier = Modifier.weight(8f), isError = isDescriptionError,
                trailingIcon = {
                    if (isDescriptionError)
                        Icon(Icons.Filled.Error, stringResource(R.string.content_desc_error))
                },
                maxLines = 5
            )

            Text(
                "${workoutDesc.length}/$descriptionLengthLimit",
                Modifier.weight(2f),
                style = typography.overline,
                color = if (isDescriptionError) colors.error else colors.secondary,
                textAlign = TextAlign.Center
            )

        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (saveType == SaveType.CreateNew),
                onClick = { saveType = SaveType.CreateNew })

            Text(stringResource(R.string.save_new))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (saveType == SaveType.OverwriteExisting),
                onClick = {
                    when {
                        allSavedWorkouts.isEmpty() -> {
                            showNoSavedWorkoutsSnackbar(context)
                        }
                        else -> {
                            saveType = SaveType.OverwriteExisting
                        }
                    }
                }
            )

            Text(stringResource(R.string.save_overwrite))
        }

        AnimatedVisibility(
            visible = saveType == SaveType.OverwriteExisting,
            Modifier
                .weight(1f)
                .padding(start = 32.dp, bottom = 64.dp)
        ) {
            if (allSavedWorkouts.isEmpty()) {
                showNoSavedWorkoutsSnackbar(context)
                saveType = SaveType.CreateNew
            }

            Column {
                ErrorIconWarning(
                    message = stringResource(R.string.save_overwrite_warning),
                    Modifier.padding(vertical = 4.dp)
                )

                LazyColumn {
                    items(allSavedWorkouts) { savedWorkout ->
                        Row(
                            Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (savedWorkout.id!! == selectedOverwriteId),
                                onClick = { selectedOverwriteId = savedWorkout.id })

                            Text(savedWorkout.name)
                        }
                    }
                }
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            Button(onClick = { onCancel() }) {
                Text(stringResource(R.string.cancel))
            }

            val canSave = workoutName.isNotBlank() &&
                    !(saveType == SaveType.OverwriteExisting && selectedOverwriteId == -1)

            Button(onClick = {
                if (
                    saveType == SaveType.CreateNew &&
                    !UpgradeManager.isUserUpgraded() &&
                    allSavedWorkouts.size >= UpgradeManager.numFreeWorkouts
                ) {
                    SnackbarManager.showMessage(
                        context.getString(
                            R.string.free_version_save_limit,
                            UpgradeManager.numFreeWorkouts
                        ),
                        actionText = context.getString(R.string.upgrade),
                        onAction = { onUpgrade() })
                } else {
                    when (saveType) {
                        SaveType.OverwriteExisting ->
                            onSave(
                                workout,
                                workoutName,
                                workoutDesc,
                                selectedOverwriteId
                            )
                        SaveType.CreateNew ->
                            onSave(
                                workout,
                                workoutName,
                                workoutDesc,
                                null
                            )
                    }
                }
            }, enabled = canSave) {
                Text(stringResource(R.string.save_workout))
            }
        }
    }
}

private fun showNoSavedWorkoutsSnackbar(context: Context) {
    SnackbarManager.showMessage(context.getString(R.string.no_saved_workouts))
}

@Preview
@Composable
private fun Preview_SaveScreen() {
    AppTheme(darkTheme = false) {
        SaveWorkoutScreenContent(
            Workout().copy(id = 1, name = "This name is just far too long, lets be honest here..."),
            listOf(
                Workout().copy(id = 1, name = "This is the workout name"),
                Workout().copy(id = 2, name = "Hello Workout", description = "Hello World")
            ),
            {},
            {},
            { _, _, _, _ -> })
    }
}

@Preview
@Composable
private fun Preview_SaveScreen_Dark() {
    AppTheme(darkTheme = true) {
        SaveWorkoutScreenContent(
            Workout().copy(id = 1, name = "This name is an acceptable length"),
            listOf(
                Workout().copy(id = 1, name = "This is the workout name"),
                Workout().copy(id = 2, name = "Hello Workout", description = "Hello World")
            ),
            {},
            {},
            { _, _, _, _ -> })
    }
}