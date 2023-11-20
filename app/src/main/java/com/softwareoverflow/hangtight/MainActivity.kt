package com.softwareoverflow.hangtight

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import com.softwareoverflow.hangtight.billing.AdvertView
import com.softwareoverflow.hangtight.billing.BillingRepo
import com.softwareoverflow.hangtight.billing.MobileAdsManager
import com.softwareoverflow.hangtight.billing.UpgradeManager
import com.softwareoverflow.hangtight.logging.ConsentDialog
import com.softwareoverflow.hangtight.logging.EmailFeedback
import com.softwareoverflow.hangtight.logging.FirebaseManager
import com.softwareoverflow.hangtight.ui.nav.screenTitles
import com.softwareoverflow.hangtight.ui.screen.NavGraphs
import com.softwareoverflow.hangtight.ui.screen.destinations.HomeScreenDestination
import com.softwareoverflow.hangtight.ui.screen.destinations.SettingsScreenDestination
import com.softwareoverflow.hangtight.ui.screen.home.AppDrawer
import com.softwareoverflow.hangtight.ui.screen.home.HangTightTopAppBar
import com.softwareoverflow.hangtight.ui.screen.home.HangTightTopScreenBar
import com.softwareoverflow.hangtight.ui.theme.AppTheme
import com.softwareoverflow.hangtight.ui.util.LockScreenOrientation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var billingRepo: BillingRepo

    @Inject
    lateinit var adsManager: MobileAdsManager

    @Inject
    lateinit var firebaseManager: FirebaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            AppTheme {

                val appState = rememberAppState()
                val drawerState = appState.scaffoldState.drawerState

                val toggleDrawer: () -> Unit =
                    {
                        appState.coroutineScope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }

                BackHandler(enabled = drawerState.isOpen) {
                    toggleDrawer()
                }

                val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
                var showTopAppBar by remember { mutableStateOf(true) }
                val currentScreen = navBackStackEntry?.destination?.route

                showTopAppBar = when (currentScreen) {
                    HomeScreenDestination.route -> true
                    else -> false
                }

                val isUpgraded by UpgradeManager.userUpgradedFlow.collectAsState()

                Scaffold(
                    scaffoldState = appState.scaffoldState,
                    topBar = {
                        AnimatedVisibility(visible = showTopAppBar) {
                            HangTightTopAppBar(toggleDrawer)
                        }
                        if (!showTopAppBar) {
                            screenTitles.getOrDefault(currentScreen, R.string.__empty).let {
                                if (it != R.string.__empty)
                                    HangTightTopScreenBar(currentScreen = stringResource(it))
                            }
                        }
                    },
                    drawerContent = {
                        AppDrawer(
                            openSettings = {
                                toggleDrawer()
                                appState.navController.navigate(SettingsScreenDestination())
                            },
                            sendFeedback = {
                                toggleDrawer()
                                EmailFeedback.launch(this@MainActivity)
                            }, onUpgrade =
                            {
                                billingRepo.launchProUpgradeFlow(this@MainActivity)
                            })
                    },
                    drawerGesturesEnabled = false,
                    content = { paddingValues ->

                        var mod = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)

                        if (!isUpgraded && (currentScreen in MobileAdsManager.showAdsOnPages)) {
                            Box {
                                AdvertView()
                            }

                            mod = mod.padding(top = MobileAdsManager.bannerAdSize.height.dp)
                        }

                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = appState.navController,
                            modifier = mod
                        )

                        if (FirebaseManager.showConsentDialog)
                            ConsentDialog {
                                firebaseManager.onConsentGiven()
                                adsManager.initialize()
                            } // Ensure we obtain the users consent for analytics etc
                        else
                            adsManager.initialize()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (this::billingRepo.isInitialized)
            billingRepo.onResume()
    }
}