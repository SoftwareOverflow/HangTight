/**
 * Copyright (C) 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.softwareoverflow.hangtight.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ConsumeParams
import com.softwareoverflow.hangtight.BuildConfig
import com.softwareoverflow.hangtight.billing.UpgradeManager
import com.softwareoverflow.hangtight.repository.billing.BillingRepository
import com.softwareoverflow.hangtight.repository.billing.BillingRepository.Companion.PRO_UPGRADE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
open class BillingViewModel @Inject constructor(val repository: BillingRepository) : ViewModel() {

    init {
        viewModelScope.launch {

            repository.oneTimeProductPurchases.collect{ list ->
                if(list.any { it.products.contains(PRO_UPGRADE) })
                    UpgradeManager.setUserUpgraded()
            }
        }
    }

    fun purchasePro(activity: Activity?) {

        activity?.let {
            // First, the ProductDetails of the product being purchased.
            val productDetails =
                repository.oneTimeProductWithProductDetails.value ?: run {
                    Timber.e("Could not find ProductDetails to make purchase.")
                    return
                }

            // Use [billingFlowParamsBuilder] to build the Params that describe the product to be
            // purchased and the offer to purchase with.
            val billingParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            ).build()

            repository.launchBillingFlow(it, billingParams)
        }
    }

    fun getMaxWorkoutSlots(): Int = if(UpgradeManager.isUserUpgraded()) Int.MAX_VALUE else 3

    /**
     * ONLY to be used in debug. Will consume the pro version upgrade.
     */
    fun debugConsumePremium() {
        if (BuildConfig.DEBUG) {
            CoroutineScope(Dispatchers.Main).launch {

                repository.oneTimeProductPurchases.collect{ list ->
                    list.firstOrNull { it.products.contains(PRO_UPGRADE) }?.let {
                        val consumeParams =
                            ConsumeParams.newBuilder()
                                .setPurchaseToken(it.purchaseToken)
                                .build()

                        withContext(Dispatchers.IO) {
                            val consumeResult = repository.consumePurchase(consumeParams)

                            Timber.d("CONSUMED PURCHASE: ${consumeResult?.billingResult?.debugMessage}")
                        }
                    }

                    }
            }
        }
    }

}