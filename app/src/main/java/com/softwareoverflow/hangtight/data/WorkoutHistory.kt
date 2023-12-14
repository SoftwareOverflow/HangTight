package com.softwareoverflow.hangtight.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class WorkoutHistory(
    var hangTime: Int, var restTime: Int, var recoverTime: Int, val date: LocalDate
) : Parcelable