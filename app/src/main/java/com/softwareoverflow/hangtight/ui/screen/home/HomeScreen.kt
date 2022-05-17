package com.softwareoverflow.hangtight.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.ui.screen.destinations.LoadWorkoutScreenDestination
import com.softwareoverflow.hangtight.ui.screen.destinations.WorkoutCreatorScreenDestination
import com.softwareoverflow.hangtight.ui.theme.AppTheme

@Destination
@RootNavGraph(start = true)
@Composable
fun HomeScreen(navigator: DestinationsNavigator) {

    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = Modifier
        .fillMaxSize()
        .onGloballyPositioned {
            size = it.size
        }) {

        val viewWidth = with(LocalDensity.current) { size.width.dp / this.density }
        val viewHeight = with(LocalDensity.current) { size.height.dp / this.density }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(0.dp, (viewHeight - viewWidth - 45.dp)), Arrangement.End
        ) {

            Button(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    navigator.navigate(WorkoutCreatorScreenDestination(Workout()))
                }) {
                Text(stringResource(R.string.create_new_workout))
            }
        }

        Button(
            modifier = Modifier
                .offset(0.dp, viewWidth - 16.dp)
                .padding(start = 16.dp),
            onClick = { navigator.navigate(LoadWorkoutScreenDestination()) }) {
            Text(stringResource(R.string.load_saved_workout))
        }

        Image(
            painter = painterResource(id = R.drawable.bg_male), contentDescription = null,
            Modifier
                .width(IntrinsicSize.Max)
                .aspectRatio(1f)
        )

        Image(
            painter = painterResource(id = R.drawable.bg_female), contentDescription = null,
            Modifier
                .width(IntrinsicSize.Max)
                .aspectRatio(1f)
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
private fun Preview_homeScreen_Dark() {
    AppTheme(darkTheme = true) {
        HomeScreen(EmptyDestinationsNavigator)
    }
}

@Preview
@Composable
private fun Preview_homeScreen() {
    AppTheme(darkTheme = false) {
        HomeScreen(EmptyDestinationsNavigator)
    }
}