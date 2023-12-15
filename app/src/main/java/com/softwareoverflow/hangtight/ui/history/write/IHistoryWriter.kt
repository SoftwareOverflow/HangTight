package com.softwareoverflow.hangtight.ui.history.write

import com.softwareoverflow.hangtight.data.WorkoutHistory

interface IHistoryWriter {

    fun writeHistory(obj: WorkoutHistory)

}