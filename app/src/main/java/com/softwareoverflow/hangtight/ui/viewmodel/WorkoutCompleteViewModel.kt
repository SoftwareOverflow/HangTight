package com.softwareoverflow.hangtight.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.logging.FirebaseManager
import com.softwareoverflow.hangtight.ui.SharedPreferencesManager
import com.softwareoverflow.hangtight.ui.util.findActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class WorkoutCompleteViewModel @Inject constructor(
    @ApplicationContext context: Context,
    sharedPreferences: SharedPreferences,
    private val firebaseManager: FirebaseManager,
    private val billingViewModel: BillingViewModel
) : ViewModel() {

    private var isInitialized = false

    private val reviewManager = ReviewManagerFactory.create(context)
    private var reviewInfo: ReviewInfo? = null

    private var _launchReviewFlow = MutableStateFlow(false)
    val launchReviewFlow: StateFlow<Boolean> get() = _launchReviewFlow

    private val workoutsCompleted: Int

    init {
        workoutsCompleted =
            sharedPreferences.getInt(SharedPreferencesManager.workoutsCompleted, 0)
        sharedPreferences.edit()
            .putInt(SharedPreferencesManager.workoutsCompleted, workoutsCompleted + 1).apply()


        if (workoutsCompleted % 10 == 0) {
            firebaseManager.logNumWorkoutsCompleted(workoutsCompleted % 10)
        }

        // Wait until the user has completed a few workouts, and then only ask sometimes
        if (workoutsCompleted > 5 && Random.nextInt(10) >= 8)
            reviewManager.requestReviewFlow().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reviewInfo = task.result
                    _launchReviewFlow.value = true
                }
            }
    }

    fun initialize(workout: Workout) {
        if(!isInitialized) {
            firebaseManager.logWorkoutCompletion(workout)
            isInitialized = true
        }
    }

    fun launchReviewFlow(context: Context) {
        context.findActivity()?.let { activity ->
            reviewInfo?.let { reviewInfo ->
                reviewManager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }

    fun launchUpgrade(context: Context){
        context.findActivity()?.let {
            billingViewModel.purchasePro(it)
        }
    }

}