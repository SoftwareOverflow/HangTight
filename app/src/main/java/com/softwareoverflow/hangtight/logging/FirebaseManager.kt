package com.softwareoverflow.hangtight.logging

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.ui.SharedPreferencesManager
import com.softwareoverflow.hangtight.ui.util.workout.getDurationMillis

class FirebaseManager(
    context: Context,
    private val sharedPreferences: SharedPreferences
) {

    init {
        firebase = FirebaseAnalytics.getInstance(context)

        analyticsEnabled =
            sharedPreferences.getBoolean(SharedPreferencesManager.analyticsEnabled, false)
    }

    /**
     * Enable Firebase analytics collection
     */
    fun onConsentGiven() {
        sharedPreferences.edit().apply {
            putBoolean(SharedPreferencesManager.analyticsEnabled, true)
            putBoolean(SharedPreferencesManager.isFirstOpen, false)
            apply()
        }

        firebase?.setAnalyticsCollectionEnabled(true)
    }

    /**
     * Disable Firebase analytics collection
     */
    fun onConsentWithdrawn() {
        sharedPreferences.edit().putBoolean(SharedPreferencesManager.analyticsEnabled, false)
            .apply()

        firebase?.setAnalyticsCollectionEnabled(false)
    }

    fun logNumWorkoutsCompleted(value: Int){
        logEvent("CompletedMultipleWorkout") {
            param(FirebaseAnalytics.Param.QUANTITY, value.toLong())
        }
    }

    fun logWorkoutCompletion(workout: Workout){
        logEvent("CompletedWorkout"){
            param("DurationMillis", workout.getDurationMillis())
            param("isSaved", (workout.id != null).toString())
        }
    }

    fun logWorkoutSaved(id: Long, durationMillis: Long){
        logEvent("SavedWorkout"){
            param(FirebaseAnalytics.Param.ITEM_ID, id)
            param("DurationMillis", durationMillis)
        }
    }

    private fun logEvent(name: String, block: com.google.firebase.analytics.ktx.ParametersBuilder.() -> kotlin.Unit){
        if(analyticsEnabled)
            firebase?.logEvent(name, block)
    }

    companion object {
        private var firebase: FirebaseAnalytics? = null

        private var analyticsEnabled = false
    }
}