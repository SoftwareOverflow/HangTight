package com.softwareoverflow.hangtight.consent

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.softwareoverflow.hangtight.BuildConfig
import timber.log.Timber

class ConsentManagerGoogle private constructor(context: Context) {

    private val consentInformation = UserMessagingPlatform.getConsentInformation(context)

    /**
     * Opens & shows the GDPR message to relevant users when we do not have consent from them.
     * Utilizes the Admob recommended Google CMP / UMP
     */
    fun handleConsent(
        activity: Activity, onConsentReceived: () -> Unit
    ) {
        val debugSettings = ConsentDebugSettings.Builder(activity)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA)
            .addTestDeviceHashedId("B3EEABB8EE11C2BE770B684D95219ECB")
            .build()

        // Create a ConsentRequestParameters object.
        val params = ConsentRequestParameters.Builder()
            .setConsentDebugSettings(debugSettings)
            .build()

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

    /** Indicates if the extra privacy option is required **/
    val isPrivacyOptionsRequired: Boolean
        get() =
            consentInformation.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    /** Helper method to call the UMP SDK method to show the privacy options form. */
    fun showPrivacyOptionsForm(
        activity: Activity,
        onConsentFormDismissedListener: ConsentForm.OnConsentFormDismissedListener
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
    }

    /**
     * Resets the consent information - ONLY TO BE USED IN DEBUG
     */
    fun resetConsent() {
        if(BuildConfig.DEBUG){
            consentInformation.reset()
        }
    }

    companion object {
        @Volatile private var instance: ConsentManagerGoogle? = null

        fun getInstance(context: Context) =
            instance
                ?: synchronized(this) {
                    instance ?: ConsentManagerGoogle(context).also { instance = it }
                }
    }
}