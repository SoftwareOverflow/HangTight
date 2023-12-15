package com.softwareoverflow.hangtight.billing

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.AdapterStatus
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.softwareoverflow.hangtight.BuildConfig
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.ui.screen.destinations.LoadWorkoutScreenDestination
import com.softwareoverflow.hangtight.ui.screen.destinations.WorkoutCreatorScreenDestination
import com.softwareoverflow.hangtight.ui.screen.destinations.WorkoutScreenDestination
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MobileAdsManager(val context: Context) : OnInitializationCompleteListener {

    private var isInitialized = false

    fun initialize() {
        if(isInitialized) return

        val conf = RequestConfiguration.Builder()
            .setTestDeviceIds(BuildConfig.DEV_DEVICES.asList())
            .build()
        MobileAds.setRequestConfiguration(conf)

        if (!UpgradeManager.isUserUpgraded())
            MobileAds.initialize(context.applicationContext, this)

        MainScope().launch {
            UpgradeManager.userUpgradedFlow.collect {
                interstitialAd = null
            }
        }

        isInitialized = true
    }

    override fun onInitializationComplete(p0: InitializationStatus) {
        Timber.i("onInitializationComplete: ${p0.adapterStatusMap.map { "${it.key} - ${it.value.initializationState}" }}")

        if (p0.adapterStatusMap.any { adapter -> adapter.value.initializationState == AdapterStatus.State.READY })
            loadInterstitial(context.applicationContext)
    }

    companion object {

        private const val retryDelay = 2000L
        private var adLoadAttempts = 0

        val bannerAdSize: AdSize = AdSize.BANNER

        private var interstitialAd: InterstitialAd? = null

        fun loadInterstitial(context: Context) {
            adLoadAttempts++

            val adRequest = AdRequest.Builder().build()
            val adUnitId = context.applicationContext.getString(R.string.adUnitId_interstitial)

            InterstitialAd.load(
                context.applicationContext,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        interstitialAd = null

                        if (adLoadAttempts <= 5 && !UpgradeManager.isUserUpgraded()) {
                            Timber.w("Failed to load interstitial advert on attempt $adLoadAttempts. Retrying in ${adLoadAttempts * retryDelay} milliseconds. '${adError.message}'")

                            MainScope().launch {
                                delay(adLoadAttempts * retryDelay)
                                loadInterstitial(context)
                            }
                        } else {
                            Timber.w("Failed to load interstitial advert. Retry attempts exhausted.")
                        }
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        MobileAdsManager.interstitialAd = interstitialAd
                        adLoadAttempts = 0
                    }
                })
        }

        fun setInterstitialShown(context: Context) {
            interstitialAd = null

            // Load the next advert
            loadInterstitial(context)
        }

        fun setFullScreenContentCallback(callback: FullScreenContentCallback) {
            interstitialAd?.fullScreenContentCallback = callback
        }

        /**
         * Show the interstitial advert.
         * Returns true if the advert is shown, false otherwise
         */
        fun showInterstitial(activity: Activity): Boolean {
            if (UpgradeManager.isUserUpgraded())
                return false

            if (interstitialAd?.fullScreenContentCallback == null)
                return false

            interstitialAd?.let {
                it.show(activity)
                return true
            }

            return false
        }

        val showAdsOnPages =
            listOf(
                WorkoutCreatorScreenDestination.route,
                LoadWorkoutScreenDestination.route,
                WorkoutScreenDestination.route
            )
    }
}