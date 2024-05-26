package com.softwareoverflow.hangtight.ui.util.workout

import com.softwareoverflow.hangtight.data.Workout
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutKtTest {

    @Test
    fun workout_getDuration_extraSimple(){
        val workout = Workout(
            hangTime = 3,
            restTime = 2,
            numSets = 1,
            numReps = 1,
            recoverTime = 23,
        )

        // For this we expect to hang only

        assertEquals(3000L,workout.getDurationMillis())
    }

    @Test
    fun workout_getDuration_addsRecoverEndOfSet(){
        val workout = Workout(
            hangTime = 3,
            restTime = 2,
            numSets = 2,
            numReps = 2,
            recoverTime = 23,
        )

        // H, r, H, R, H, r, H
        // 3+ 2+ 3+23 +3+ 2 +3
        //39

        assertEquals(39000L,workout.getDurationMillis())
    }

    @Test
    fun workout_getDuration_complex(){
        val workout = Workout(
            hangTime = 7,
            restTime = 3,
            numSets = 6,
            numReps = 5,
            recoverTime = 120,
        )

        // Expected Hang/Rest 4 times, then Hang/Recover
        // Repeat that numSets
        // Take off recoverTime

        var expected = (7+3)*4 +(7 + 120)
        expected *= 6
        expected -= 120

        assertEquals(expected * 1000L, workout.getDurationMillis())
    }
}