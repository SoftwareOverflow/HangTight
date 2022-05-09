package com.softwareoverflow.hangtight

import android.app.Application
import com.softwareoverflow.hangtight.helper.BillingDataSource
import com.softwareoverflow.hangtight.helper.BillingRepo
import kotlinx.coroutines.MainScope

class HangTightApplication : Application() {

    lateinit var appContainer: AppContainer

    // Container of objects shared across the whole app
    inner class AppContainer {
        private val applicationScope = MainScope()

        private val billingDataSource = BillingDataSource.getInstance(
            this@HangTightApplication,
            applicationScope
        )
        val billingRepo = BillingRepo(billingDataSource, applicationScope)
    }

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer()


        // In Debug mode, first consume the users upgrade purchase
/*        if (BuildConfig.DEBUG) {
            appContainer.billingRepo.debugConsumePremium()
        }*/
    }
}