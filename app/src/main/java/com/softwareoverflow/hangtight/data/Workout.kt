package com.softwareoverflow.hangtight.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Workout(
    val id: Int? = null,
    val name: String = "",
    val description: String = "",
    val hangTime: Int = 5,
    val restTime: Int = 5,
    val numReps: Int = 6,
    val numSets: Int = 5,
    val recoverTime: Int = 120,
) : Parcelable