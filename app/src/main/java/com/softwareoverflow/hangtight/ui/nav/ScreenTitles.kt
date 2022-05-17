package com.softwareoverflow.hangtight.ui.nav

import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.ui.screen.destinations.*

/**
 * A map of navigation routes to titles for their screens
 */
val screenTitles = hashMapOf(
    LoadWorkoutScreenDestination.route to R.string.screen_load,
    SaveWorkoutScreenDestination.route to R.string.screen_save,
    SettingsScreenDestination.route to R.string.screen_settings,
    WorkoutCreatorScreenDestination.route to R.string.screen_workout_creator,
    WorkoutScreenDestination.route to R.string.screen_workout
)
