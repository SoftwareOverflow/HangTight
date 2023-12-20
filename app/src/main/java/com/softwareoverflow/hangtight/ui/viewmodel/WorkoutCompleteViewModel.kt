package com.softwareoverflow.hangtight.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwareoverflow.hangtight.billing.MobileAdsManager
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.logging.FirebaseManager
import com.softwareoverflow.hangtight.review.InAppReviewManager
import com.softwareoverflow.hangtight.ui.SharedPreferencesManager
import com.softwareoverflow.hangtight.ui.util.findActivity
import com.softwareoverflow.hangtight.ui.util.workout.media.WorkoutCompleteMediaManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutCompleteViewModel @Inject constructor(
    sharedPreferences: SharedPreferences,
    private val firebaseManager: FirebaseManager,
    private val billingViewModel: BillingViewModel,
    private val workoutCompleteMediaManager: WorkoutCompleteMediaManager,
) : ViewModel() {

    private var isInitialized = false

    private var tryShowAdvert = true

    init {
        val workoutsCompleted =
            sharedPreferences.getInt(SharedPreferencesManager.workoutsCompleted, 0)
        sharedPreferences.edit()
            .putInt(SharedPreferencesManager.workoutsCompleted, workoutsCompleted + 1).apply()


        if (workoutsCompleted % 10 == 0) {
            firebaseManager.logNumWorkoutsCompleted(workoutsCompleted % 10)
        }
    }

    fun initialize(workout: Workout, context: Context, activity: Activity?) {
        if (!isInitialized) {
            firebaseManager.logWorkoutCompletion(workout)
            isInitialized = true

            playWorkoutCompleteSound(context, activity)
        }
    }

    private fun playWorkoutCompleteSound(context: Context, activity: Activity?) {
        viewModelScope.launch {
            workoutCompleteMediaManager.playWorkoutCompleteSound(onSoundPlayed = {
                activity?.let {
                    showAdvert(context, activity)
                }
            })
        }
    }

    fun launchUpgrade(context: Context) {
        context.findActivity()?.let {
            billingViewModel.purchasePro(it)
        }
    }

    private fun showAdvert(context: Context, activity: Activity?) {
        if (InAppReviewManager.willAskForReview) {
            tryShowAdvert = false
            activity?.let {
                InAppReviewManager.askForReview(context, activity, onFailure = {
                    tryShowAdvert = true // Try and show the advert if we failed to ask for a review
                })
            }
        }

        if (tryShowAdvert) {
            MobileAdsManager.showAdAfterWorkout(activity, onAdClosedCallback = {
                tryShowAdvert = false
            })
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        workoutCompleteMediaManager.cancel()
        super.onCleared()
    }
}