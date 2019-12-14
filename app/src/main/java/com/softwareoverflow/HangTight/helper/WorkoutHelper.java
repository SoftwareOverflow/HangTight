package com.softwareoverflow.HangTight.helper;

import android.util.Pair;

import com.softwareoverflow.HangTight.R;
import com.softwareoverflow.HangTight.workout.Workout;

public class WorkoutHelper {

    public enum WorkoutSection {
        PREPARE(R.string.timer_prepare),
        HANG(R.string.timer_hang),
        REST(R.string.timer_rest),
        RECOVER(R.string.timer_recover);

        private int nameResId;

        WorkoutSection(int nameResId) {
            this.nameResId = nameResId;
        }

        public int getNameResourceId() {
            return nameResId;
        }
    }

    public static int getWorkoutDuration(Workout workout) {
        int totalTime = workout.getHangTime() * workout.getNumReps();
        totalTime += workout.getRestTime() * (workout.getNumReps() - 1);
        totalTime += workout.getRecoverTime();
        totalTime *= workout.getNumSets();
        totalTime -= workout.getRecoverTime();

        return totalTime;
    }

    /**
     * @param workout             The Workout object to get the current section for
     * @param millisUntilFinished The number of milliseconds until the Workout is completed
     * @return The current WorkoutSection and the number of milliseconds
     * left in this WorkoutSection.
     */
    public static Pair<WorkoutSection, Long> getCurrentSection(Workout workout, long millisUntilFinished) {
        WorkoutSection currentSection;
        long timeRemainingInSection;

        long timeCompleted = workout.getDuration() * 1000 - millisUntilFinished;

        if(timeCompleted < 0)
            return new Pair<>(WorkoutSection.PREPARE, Math.abs(timeCompleted));

        int hangTime = workout.getHangTime() * 1000;
        int restTime = workout.getRestTime() * 1000;
        int repTime = hangTime + restTime;
        int recoverTime = workout.getRecoverTime() * 1000;

        // Account for last rep of the set not having a rest period
        int setTime = repTime * workout.getNumReps() - restTime + recoverTime;

        long progressInSet = timeCompleted % setTime;

        if (progressInSet >= setTime - recoverTime) {
            currentSection = WorkoutSection.RECOVER;
            timeRemainingInSection = setTime - progressInSet;
        } else {
            long progressInRep = progressInSet % repTime;

            if (progressInRep < hangTime) {
                currentSection = WorkoutSection.HANG;
                timeRemainingInSection = hangTime - progressInRep;
            } else if (progressInRep <= repTime) {
                currentSection = WorkoutSection.REST;
                timeRemainingInSection = repTime - progressInRep;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        return new Pair<>(currentSection, timeRemainingInSection);
    }
}
