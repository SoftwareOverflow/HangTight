package com.softwareoverflow.hangtight.billing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object UpgradeManager {

    /**
     * The number of workouts allowed to be saved on the free version of the app
     */
    const val numFreeWorkouts = 3

    private val userUpgraded = MutableStateFlow(false)
    val userUpgradedFlow : StateFlow<Boolean>  get() = userUpgraded

    fun isUserUpgraded() : Boolean {
        return userUpgraded.value
    }

    var proPrice: String? = null
    private set

    fun setUserUpgraded(){
        userUpgraded.value = true
    }

    fun setUpgradePrice(price: String) {
        proPrice = price
    }
}