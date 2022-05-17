package com.softwareoverflow.hangtight.billing

import android.app.Activity
import com.softwareoverflow.hangtight.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BillingRepo(
    private val billingDataSource: BillingDataSource,
    defaultScope: CoroutineScope
) {

    fun launchProUpgradeFlow(activity: Activity) {
        billingDataSource.launchBillingFlow(activity, BillingDataSource.upgradeSKU)
    }

    fun onResume() {
        billingDataSource.onResume()
    }

    /**
     * ONLY to be used in debug. Will consume the pro version upgrade.
     */
    fun debugConsumePremium() {
        if (BuildConfig.DEBUG) {
            CoroutineScope(Dispatchers.Main).launch {
                billingDataSource.consumeInappPurchase(BillingDataSource.upgradeSKU)
            }
        }
    }


    init {
        // Since both are tied to application lifecycle, we can launch this scope to collect
        // consumed purchases from the billing data source while the app process is alive.
        defaultScope.launch {
            billingDataSource.getNewPurchases().collect {
                if (it.contains(BillingDataSource.upgradeSKU)) {
                    UpgradeManager.setUserUpgraded()
                }
            }
        }

        defaultScope.launch {
            billingDataSource.isPurchased(BillingDataSource.upgradeSKU).collect {
                if (it) {
                    UpgradeManager.setUserUpgraded()
                }
            }
        }

        if (!UpgradeManager.isUserUpgraded()) {
            defaultScope.launch {
                billingDataSource.getSkuPrice(BillingDataSource.upgradeSKU).collect {
                    UpgradeManager.setUpgradePrice(it)
                }
            }
        }
    }
}
