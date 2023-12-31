package com.softwareoverflow.hangtight.ui.history.write

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.softwareoverflow.hangtight.data.WorkoutHistory
import com.softwareoverflow.hangtight.ui.util.workout.WorkoutSection
import java.time.LocalDate
import javax.inject.Inject

class HistorySaverLocal @Inject constructor(private val historyWriter: IHistoryWriter) :
    IHistorySaver {

    var history = WorkoutHistory(0, 0, 0, LocalDate.now())

    override fun addHistory(seconds: Int, section: WorkoutSection) {
        try {
            val dateNow = LocalDate.now()

            if (history.date != dateNow)
                write()

            when (section) {
                WorkoutSection.Hang -> history.hangTime += seconds
                WorkoutSection.Rest -> history.restTime += seconds
                WorkoutSection.Recover -> history.recoverTime += seconds
                else -> { /* Do Nothing */
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun write() {
        try {
            historyWriter.writeHistory(history)

            // Reset the history
            history = WorkoutHistory(0, 0, 0, LocalDate.now())
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}