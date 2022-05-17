package com.softwareoverflow.hangtight.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.ui.screen.destinations.WorkoutCreatorScreenDestination
import com.softwareoverflow.hangtight.ui.screen.destinations.WorkoutScreenDestination
import com.softwareoverflow.hangtight.ui.theme.AppTheme
import com.softwareoverflow.hangtight.ui.util.IconAboveText
import com.softwareoverflow.hangtight.ui.util.workout.getFormattedDuration
import com.softwareoverflow.hangtight.ui.viewmodel.LoadWorkoutViewModel

@Composable
@Destination(route = "Load Saved Workout")
fun LoadWorkoutScreen(
    navigator: DestinationsNavigator,
    viewModel: LoadWorkoutViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val workouts by viewModel.allWorkouts.collectAsState()


    if (isLoading)
        CircularProgressIndicator()
    else if (workouts.isEmpty())
        Text("No Saved Workouts...")
    else
        LoadWorkoutScreenContent(
            workouts,
            onDelete = { viewModel.deleteWorkout(it) },
            onEdit = { navigator.navigate(WorkoutCreatorScreenDestination(it)) },
            onStart = { navigator.navigate(WorkoutScreenDestination(it)) })


}

@Composable
private fun LoadWorkoutScreenContent(
    savedWorkouts: List<Workout>,
    onDelete: (Workout) -> Unit,
    onEdit: (Workout) -> Unit,
    onStart: (Workout) -> Unit
) {

    var selectedWorkoutId: Int? by remember { mutableStateOf(null) }

    var workoutToDelete: Workout? by remember { mutableStateOf(null) }

    workoutToDelete?.let {
        ConfirmDeleteDialog(workout = it, onConfirm = {
            onDelete(it)
            workoutToDelete = null
        }, onDismiss = {
            workoutToDelete = null
        })
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        LazyColumn(Modifier.padding(16.dp)) {
            items(savedWorkouts) { workout ->
                SavedWorkoutRow(
                    workout,
                    workout.id == selectedWorkoutId,
                    onSelected = {
                        selectedWorkoutId = if (selectedWorkoutId != it.id)
                            it.id
                        else
                            null
                    },
                    onDelete = {
                        workoutToDelete = it
                    },
                    onEdit = { onEdit(it) },
                    onStart = { onStart(it) },
                    Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SavedWorkoutRow(
    workout: Workout,
    isSelected: Boolean,
    onSelected: (Workout) -> Unit,
    onDelete: (Workout) -> Unit,
    onEdit: (Workout) -> Unit,
    onStart: (Workout) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                shape = shapes.medium,
                color = MaterialTheme.colors.primary
            )
            .animateContentSize()
            .clickable {
                onSelected(workout)
            },
        shape = shapes.medium
    ) {
        Column(
            Modifier
                .padding(start = 16.dp, end = 8.dp)
                .padding(vertical = 4.dp)
        ) {
            Row {
                Text(workout.name, Modifier.weight(1f), style = typography.body1)
                Text(workout.getFormattedDuration())

                if (isSelected)
                    Icon(Icons.Filled.ExpandLess, stringResource(R.string.content_desc_expand_collapse), Modifier.clickable {
                        onSelected(workout)
                    })
                else
                    Icon(Icons.Filled.ExpandMore, stringResource(R.string.content_desc_expand_collapse), Modifier.clickable {
                        onSelected(workout)
                    })
            }

            AnimatedVisibility(visible = isSelected) {
                Column {
                    Row {
                        Text(workout.description, style = typography.caption)
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        IconAboveText(
                            imageVector = Icons.Filled.Delete,
                            text =  stringResource(R.string.delete),
                            Modifier.clickable {
                                onDelete(workout)
                            })

                        IconAboveText(
                            imageVector = Icons.Filled.Edit,
                            text =  stringResource(R.string.edit),
                            Modifier.clickable {
                                onEdit(workout)
                            })
                        IconAboveText(
                            imageVector = Icons.Filled.PlayArrow,
                            text = stringResource(R.string.start),
                            Modifier.clickable {
                                onStart(workout)
                            })
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmDeleteDialog(workout: Workout, onConfirm: () -> Unit, onDismiss: () -> Unit) {
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
                    Text(stringResource(R.string.are_you_sure), Modifier.padding(start = 8.dp), style = typography.h6)
                }


                Text(
                    "${
                        stringResource(
                            R.string.are_you_sure_delete_workout,
                            workout.name
                        )
                    }\n${stringResource(R.string.action_unrecoverable)}",
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(onClick = onConfirm) {
                        Text(stringResource(R.string.yes_delete))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview_LoadScreen() {
    val savedWorkouts =
        listOf(Workout(id = 1, name = "Example workout 1"), Workout(name = "Example Workout 2"))

    AppTheme(darkTheme = false) {
        LoadWorkoutScreenContent(savedWorkouts, {}, {}, {})
    }
}

@Preview
@Composable
private fun Preview_LoadScreen_Dark() {
    val savedWorkouts =
        listOf(Workout(id = 1, name = "Example workout 1"), Workout(name = "Example Workout 2"))

    AppTheme(darkTheme = true) {
        LoadWorkoutScreenContent(savedWorkouts, {}, {}, {})
    }
}

@Preview
@Composable
private fun Preview_DeleteDialog(){
    AppTheme(darkTheme = false) {
        ConfirmDeleteDialog(workout = Workout(name = "Example Workout 1"), {}, {})
    }
}

@Preview
@Composable
private fun Preview_DeleteDialog_Dark(){
    AppTheme(darkTheme = true) {
        ConfirmDeleteDialog(workout = Workout(name = "Example Workout 1"), {}, {})
    }
}