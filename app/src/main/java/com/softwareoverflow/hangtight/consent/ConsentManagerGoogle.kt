package com.softwareoverflow.hangtight.consent

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.softwareoverflow.hangtight.BuildConfig
import timber.log.Timber

class ConsentManagerGoogle {

    private lateinit var consentInformation: ConsentInformation

    /**
     * Opens & shows the GDPR message to relevant users when we do not have consent from them.
     * Utilizes the Admob recommended Google CMP / UMP
     */
    fun handleConsent(
        context: Context, activity: Activity, onConsentReceived: () -> Unit
    ) {
        // Create a ConsentRequestParameters object.
        val params = ConsentRequestParameters.Builder().build()

        consentInformation = UserMessagingPlatform.getConsentInformation(context)
        consentInformation.requestConsentInfoUpdate(activity,
            params,
            { // OnConsentInfoUpdateSuccessListener
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    activity
                ) { loadAndShowError ->
                    // Consent gathering failed.
                    Timber.w("${loadAndShowError?.errorCode}:${loadAndShowError?.message}")

                    // Consent has been gathered.
                    if (consentInformation.canRequestAds()) {
                        onConsentReceived()
                    }
                }
            },
            { // OnConsentInfoUpdateFailureListener
                    requestConsentError ->
                // Consent gathering failed.
                Timber.w("${requestConsentError.errorCode}:${requestConsentError.message}")
            })
    }

    /**
     * Resets the consent information - ONLY TO BE USED IN DEBUG
     */
    fun resetConsent() {
        if(BuildConfig.DEBUG){
            consentInformation.reset()
        }
    }
}