package com.softwareoverflow.hangtight.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.softwareoverflow.hangtight.billing.MobileAdsManager
import com.softwareoverflow.hangtight.logging.FirebaseManager
import com.softwareoverflow.hangtight.repository.billing.BillingRepository
import com.softwareoverflow.hangtight.ui.viewmodel.BillingViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MainModuleInjection {


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

    @Provides
    fun providesBillingRepo(@ApplicationContext context: Context): BillingRepository {
        return BillingRepository(context)
    }

    @Provides
    fun providesBillingViewModel(repository: BillingRepository) : BillingViewModel {
        return BillingViewModel(repository)
    }
}