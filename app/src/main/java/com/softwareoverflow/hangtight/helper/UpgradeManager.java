package com.softwareoverflow.hangtight.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.softwareoverflow.hangtight.R;

import java.util.ArrayList;
import java.util.List;

public class UpgradeManager implements PurchasesUpdatedListener, BillingClientStateListener {

    private static FirebaseAnalytics firebaseAnalytics;

    private static UpgradeManager upgradeManager;

    private static BillingClient billingClient;
    private static final String UPGRADE_SKU = "upgrade_to_pro";

    private int connectionAttempt = 1; // Number of connection attempts. Used to restrict reties

    public static void setup(Context context) {
        if (upgradeManager == null) {
            upgradeManager = new UpgradeManager(context.getApplicationContext());
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
    }

    private UpgradeManager(Context context) {
        billingClient = BillingClient.newBuilder(context).setListener(this).build();
        billingClient.startConnection(this);
    }

    @Override
    public void onBillingSetupFinished(@BillingResponse int billingResponseCode) {
        Bundle bundle = new Bundle();
        bundle.putInt("billingSetupFinishedResponseCode", billingResponseCode);

        if (billingResponseCode == BillingResponse.OK) {
            List<String> skuList = new ArrayList<>();
            skuList.add(UPGRADE_SKU);
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
            billingClient.querySkuDetailsAsync(params.build(),
                    (responseCode, skuDetailsList) ->
                            bundle.putInt("querySkuDetailsResponseCode", responseCode));
        }

        if (firebaseAnalytics != null) {
            firebaseAnalytics.logEvent("billing_setup", bundle);
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        if (firebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("connectionAttempt", connectionAttempt);
            firebaseAnalytics.logEvent("billingServiceDisconnected", bundle);
        }

        int MAX_RETRY_ATTEMPTS = 3;
        if (connectionAttempt <= MAX_RETRY_ATTEMPTS) {
            connectionAttempt++;

            if (billingClient != null) {
                billingClient.startConnection(this);
            }
        }
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if (responseCode == BillingResponse.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getSku().equals(UPGRADE_SKU)) {
                    MobileAdsHelper.notifyUpgradePurchased();
                    break;
                }
            }
        }
    }

    public static void upgrade(Context context, View v) {
        Bundle bundle = new Bundle();

        if (billingClient == null) {
            setup(context);
            Snackbar.make(v, "Billing client is unavailable. Please try again later", Snackbar.LENGTH_LONG).show();
            bundle.putInt("upgrade_response_code", -999);
            if (firebaseAnalytics != null) {
                firebaseAnalytics.logEvent("view_upgrade", bundle);
            }
            return;
        }

        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(UPGRADE_SKU)
                .setType(BillingClient.SkuType.INAPP)
                .build();
        int billingResponse = billingClient.launchBillingFlow((Activity) context, flowParams);
        BillingResponseCode responseCode = getInternalBillingResponse(billingResponse);

        if (responseCode == BillingResponseCode.CONNECTION_FAILED) {
            setup(context);
        }

        // Handle any error response codes
        if(responseCode != BillingResponseCode.BILLING_SUCCESS){
            Snackbar snackbar = Snackbar.make(v,
                    context.getString(R.string.billing_error, billingResponse),
                    Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            TextView snackTextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);

            snackTextView.setMaxLines(6);
            snackbar.show();
        }

        bundle.putInt("upgrade_response_code", billingResponse);
        if (firebaseAnalytics != null) {
            firebaseAnalytics.logEvent("view_upgrade", bundle);
        }
    }

    public static void checkUserPurchases(Context context) {
        if (billingClient == null || !billingClient.isReady()) {
            upgradeManager = new UpgradeManager(context);
            return;
        }

        Purchase.PurchasesResult purchasesResult =
                billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        if (purchasesResult.getResponseCode() == BillingResponse.OK) {
            for (Purchase purchase : purchasesResult.getPurchasesList()) {
                if (purchase.getSku().equals(UPGRADE_SKU)) {
                    MobileAdsHelper.notifyUpgradePurchased();
                    break;
                }
            }
        }
    }

    public enum BillingResponseCode {
        BILLING_SUCCESS,
        CONNECTION_FAILED,
        GOOGLE_PLAY_VERSION_ISSUE,
        FEATURE_NOT_SUPPORTED,
        UNHANDLED_ERROR
    }

    private static BillingResponseCode getInternalBillingResponse(int billingResponse) {
        switch (billingResponse) {
            case BillingResponse.OK: // Fall through
            case BillingResponse.USER_CANCELED:
                return BillingResponseCode.BILLING_SUCCESS;
            case BillingResponse.SERVICE_DISCONNECTED: // Fall through
            case BillingResponse.SERVICE_UNAVAILABLE:
                return BillingResponseCode.CONNECTION_FAILED;
            case BillingResponse.FEATURE_NOT_SUPPORTED:
                return BillingResponseCode.FEATURE_NOT_SUPPORTED;
            case BillingResponse.BILLING_UNAVAILABLE:
                return BillingResponseCode.GOOGLE_PLAY_VERSION_ISSUE;
            default:
                return BillingResponseCode.UNHANDLED_ERROR;
        }
    }
}
