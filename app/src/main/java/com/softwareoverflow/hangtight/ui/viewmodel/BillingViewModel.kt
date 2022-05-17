package com.softwareoverflow.hangtight.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.softwareoverflow.hangtight.billing.BillingRepo
import com.softwareoverflow.hangtight.ui.util.findActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class BillingViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var billingRepo: BillingRepo

    fun launchUpgrade(context: Context) {
        billingRepo.launchProUpgradeFlow(context.findActivity()!!)
    }
}