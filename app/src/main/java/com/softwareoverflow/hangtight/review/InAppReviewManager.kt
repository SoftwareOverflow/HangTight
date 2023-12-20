package com.softwareoverflow.hangtight.review

import android.app.Activity
import android.content.Context
import androidx.preference.PreferenceManager
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.review.InAppReviewManager.askForReview
import com.softwareoverflow.hangtight.review.InAppReviewManager.createReviewManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Singleton object for managing InAppReviews for the application.
 * [createReviewManager] must be called before [askForReview] with enough time to allow the API to respond
 */
object InAppReviewManager {

    private lateinit var reviewManager: ReviewManager

    private var reviewInfo: ReviewInfo? = null

    /**
     * Boolean to indicate if the user will be asked for a review.
     * Property instantiated once only in [createReviewManager].
     * Defaults to true until properly instantiated
     */
    var willAskForReview: Boolean = false
        private set

    /**
     * Create the review manager in a coroutine
     */
    suspend fun createReviewManager(context: Context) {
        if (!this::reviewManager.isInitialized) {

            // Launch a coroutine as this method is likely to be called early on in the app lifecycle
            withContext(Dispatchers.Default) {
                reviewManager = ReviewManagerFactory.create(context.applicationContext)

                // Request the flow information (cache in advance)
                val request = reviewManager.requestReviewFlow()
                request.addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        reviewInfo = result.result

                        // Update the value for later use
                        willAskForReview = shouldAskForReview(context.applicationContext)
                    } else {
                        reviewInfo = null

                        // We can't ask for a review if this step fails
                        willAskForReview = false
                    }
                }
                request.addOnFailureListener {
                    Timber.w(it, "InAppReview manager request failed")

                    willAskForReview = false
                }
            }
        }
    }

    /**
     * Call this to ask the user for a review.
     * The request flow will only be called occasionally based on chance and duration since last ask.
     * Even in cases where the request flow is called, the API may not show the review dialog based on
     * Google's implementation details regarding individual quotas
     */
    fun askForReview(context: Context, activity: Activity, onFailure: () -> Unit) {
        if (willAskForReview && reviewInfo != null) {

            // Update last ask time in case the user doesn't complete the review for any reason
            updateLastAskTime(context)

            reviewManager.launchReviewFlow(activity, reviewInfo!!)
                .addOnFailureListener {
                    Timber.w(it, "ReviewManager failed to complete review flow")
                    onFailure()
                }.addOnCompleteListener {
                    Timber.i("ReviewManager completed review flow")
                }.addOnSuccessListener {
                    Timber.i("ReviewManager success review flow")
                }
        }
    }

    /**
     * Update the lastAskTime in sharedPreferences to the current system time
     */
    private fun updateLastAskTime(context: Context) {
        // Update the last ask time
        with(PreferenceManager.getDefaultSharedPreferences(context).edit()) {
            putLong(context.getString(R.string.pref_review_last_ask), System.currentTimeMillis())
            apply()
        }
    }

    /**
     * Used to check if a user is eligible to be asked for a review
     */
    private fun shouldAskForReview(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val lastAskMillis = prefs.getLong(context.getString(R.string.pref_review_last_ask), 0)

        // This is the first request, don't show on the first time but make sure to save the pref for future
        if (lastAskMillis == 0L) {
            updateLastAskTime(context)
            return false
        }

        val minWaitMillis = 3 * 24 * 60 * 60 * 1000 // 3 days
        val timeElapsed = (lastAskMillis + minWaitMillis) <= System.currentTimeMillis()

        val chance = 5
        val randomChance = (1..chance).random() == chance // Only ask 1 in every {chance} times

        return timeElapsed && randomChance
    }
}