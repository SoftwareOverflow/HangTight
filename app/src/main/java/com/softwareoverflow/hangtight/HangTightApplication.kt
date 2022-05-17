package com.softwareoverflow.hangtight

import android.app.Application
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.softwareoverflow.hangtight.ui.util.SnackbarManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltAndroidApp
class HangTightApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}

/**
 * Responsible for holding state related to the application and containing UI-related logic.
 */
@Stable
class AppState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    private val snackbarManager: SnackbarManager,
    val coroutineScope: CoroutineScope,
) {
    // Process snackbar messages coming from SnackbarManager
    init {
        coroutineScope.launch {
            snackbarManager.messages.collect { currentMessages ->
                if (currentMessages.isNotEmpty()) {
                    val message = currentMessages[0]

                    // Display the snackbar on the screen. `showSnackbar` is a function
                    // that suspends until the snackbar disappears from the screen
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message.text,
                        message.actionText,
                        message.duration
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> message.onDismiss?.invoke()
                        SnackbarResult.ActionPerformed -> message.onAction?.invoke()
                    }

                    // Once the snackbar is gone or dismissed, notify the SnackbarManager
                    snackbarManager.setMessageShown(message.id)
                }
            }
        }
    }
}

/**
 * Remembers and creates an instance of [AppState]
 */
@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed)),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) =
    remember(scaffoldState, navController, snackbarManager, coroutineScope) {
        AppState(scaffoldState, navController, snackbarManager, coroutineScope)
    }