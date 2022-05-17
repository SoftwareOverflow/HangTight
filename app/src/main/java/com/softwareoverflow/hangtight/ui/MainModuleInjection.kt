package com.softwareoverflow.hangtight.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.softwareoverflow.hangtight.billing.BillingDataSource
import com.softwareoverflow.hangtight.billing.BillingRepo
import com.softwareoverflow.hangtight.billing.MobileAdsManager
import com.softwareoverflow.hangtight.logging.FirebaseManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope

@Module
@InstallIn(SingletonComponent::class)
object MainModuleInjection {

    @Provides
    fun providesBillingDataStore(application: Application): BillingDataSource {
        return BillingDataSource.getInstance(application, MainScope())
    }

    @Provides
    fun providesBillingRepo(billingDataSource: BillingDataSource): BillingRepo {
        return BillingRepo(billingDataSource, MainScope())
    }

    @Provides
    fun providesMobileAdsManager(@ApplicationContext context: Context) : MobileAdsManager {
        return MobileAdsManager(context)
    }

    @Provides
    fun providesSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        return (context as Application).getSharedPreferences(
            SharedPreferencesManager.prefsFile,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    fun providesFirebaseManager(@ApplicationContext context: Context, sharedPreferences: SharedPreferences) : FirebaseManager{
        return FirebaseManager(context, sharedPreferences)
    }
}