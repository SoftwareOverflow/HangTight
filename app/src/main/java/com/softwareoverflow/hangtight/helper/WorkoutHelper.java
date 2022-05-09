package com.softwareoverflow.hangtight.helper;

import com.softwareoverflow.hangtight.R;
import com.softwareoverflow.hangtight.workout.Workout;

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
}
