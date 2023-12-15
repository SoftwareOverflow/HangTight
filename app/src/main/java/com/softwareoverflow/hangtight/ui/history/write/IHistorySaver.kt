package com.softwareoverflow.hangtight.ui.history.write

import com.softwareoverflow.hangtight.ui.util.workout.WorkoutSection

interface IHistorySaver {

    fun addHistory(seconds: Int, section: WorkoutSection)

    fun write()
}