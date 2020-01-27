package com.softwareoverflow.hangtight.helper;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.softwareoverflow.hangtight.R;

public class MobileAdsHelper {

    private static boolean isInitialized;

    public static boolean userHasUpgraded = false;

    private static InterstitialAd interstitialAd;

    public static void initialize(Context context){

        if (isInitialized || userHasUpgraded) return;

        RequestConfiguration conf= new RequestConfiguration.Builder().build();
        MobileAds.setRequestConfiguration(conf);
        MobileAds.initialize(context, initializationStatus -> isInitialized = true);

        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.adUnitId_interstitial));
        loadAd();
    }

    public static void loadAd(){
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public static InterstitialAd getInterstitialAd(){
        return interstitialAd;
    }

    static void notifyUpgradePurchased(){
        userHasUpgraded = true;
        interstitialAd = null;
    }
}
