package com.softwareoverflow.hangtight.helper;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.softwareoverflow.hangtight.BuildConfig;
import com.softwareoverflow.hangtight.R;

import java.util.Arrays;

public class MobileAdsHelper {

    private static boolean isInitialized;

    public static boolean userHasUpgraded = false;

    private static InterstitialAd interstitialAd;

    public static void initialize(Context context){

        if (isInitialized || userHasUpgraded) return;

        RequestConfiguration conf= new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList(BuildConfig.DEV_DEVICES))
                .build();
        MobileAds.setRequestConfiguration(conf);
        MobileAds.initialize(context, initializationStatus -> isInitialized = true);

        loadAd(context);
    }

    public static void onInterstitialShown(Context context){
        interstitialAd = null;

        loadAd(context);
    }

    public static void loadAd(Context context){
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context.getApplicationContext(), context.getString(R.string.adUnitId_interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                MobileAdsHelper.interstitialAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);

                MobileAdsHelper.interstitialAd = interstitialAd;
            }
        });
    }

    public static InterstitialAd getInterstitialAd(){
        return interstitialAd;
    }

    static void notifyUpgradePurchased(){
        userHasUpgraded = true;
        interstitialAd = null;
    }
}
