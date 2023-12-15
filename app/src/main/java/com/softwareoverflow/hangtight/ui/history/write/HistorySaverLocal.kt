package com.softwareoverflow.hangtight.ui.history.write

import com.softwareoverflow.hangtight.data.WorkoutHistory
import com.softwareoverflow.hangtight.ui.util.workout.WorkoutSection
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class HistorySaverLocal @Inject constructor (private val historyWriter: IHistoryWriter) : IHistorySaver {

    var history = WorkoutHistory(0, 0, 0, LocalDate.now())

    override fun addHistory(seconds: Int, section: WorkoutSection) {
        Timber.d("AddHistory cache: $seconds, $section")

        val dateNow = LocalDate.now()

        if(history.date != dateNow)
            write()

        when (section) {
            WorkoutSection.Hang -> history.hangTime += seconds
            WorkoutSection.Rest -> history.restTime += seconds
            WorkoutSection.Recover -> history.recoverTime += seconds
            else -> { /* Do Nothing */ }
        }
    }

    override fun write() {
        Timber.d("Cached history writing to database... $history")
        historyWriter.writeHistory(history)

        // Reset the history
        history = WorkoutHistory(0, 0, 0, LocalDate.now())
    }
}