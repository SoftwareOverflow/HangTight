package com.softwareoverflow.HangTight.workout;

import android.os.CountDownTimer;
import android.util.Pair;

import com.softwareoverflow.HangTight.helper.WorkoutHelper;

public class WorkoutTimer {

    private CountDownTimer timer;
    private static final int TICK_INTERVAL = 100;
    private long millisecondsRemaining;

    private IWorkoutTimerObserver observer;

    private Workout workout;
    private int prepTimeMillis;

    private int currentRep = 1, currentSet = 1;

    private WorkoutHelper.WorkoutSection currentSection = null;

    private boolean isPaused;

    /**
     * @param workout         The Workout object to construct the timer for.
     * @param prepTimeSeconds The number of seconds to run the prepare countdown for
     */
    public WorkoutTimer(Workout workout, int prepTimeSeconds) {
        // TODO - retrofit preparation time into this
        this.workout = workout;
        this.prepTimeMillis = prepTimeSeconds * 1000;

        createTimer((workout.getDuration() + prepTimeSeconds) * 1000);
    }

    public void start() {
        timer.start();
    }

    public void cancel() {
        timer.cancel();
    }

    /**
     * Skip the current timer section execution
     */
    public void skip() {
        Pair workoutSectionTimePair = WorkoutHelper.getCurrentSection(workout, millisecondsRemaining);
        millisecondsRemaining -= (long) workoutSectionTimePair.second;

        if(isPaused){
            // Allow the UI to be updated if skip is pressed whilst paused
            notifyObservers();
        } else {
            // Cancel and recreate the timer
            timer.cancel();
            createTimer(millisecondsRemaining);
            timer.start();
        }
    }

    /**
     * Pause the current timer execution
     */
    public void pause() {
        isPaused = true;
        timer.cancel();
    }

    /**
     * Resume the current execution
     */
    public void resume() {
        isPaused = false;
        createTimer(millisecondsRemaining);
        timer.start();
    }

    public WorkoutHelper.WorkoutSection getCurrentSection() {
        return currentSection;
    }

    private void createTimer(long durationMillis) {
        timer = new CountDownTimer(durationMillis, TICK_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisecondsRemaining = millisUntilFinished;
                notifyObservers();
                /*if (observer == null)
                    return;

                millisecondsRemaining = millisUntilFinished;

                Pair workoutSectionTimePair = WorkoutHelper.getCurrentSection(workout, millisUntilFinished);

                if (skipFlag) {
                    millisUntilFinished -= (long) workoutSectionTimePair.second;

                    workoutSectionTimePair = WorkoutHelper.getCurrentSection(workout, millisUntilFinished);
                }

                WorkoutHelper.WorkoutSection workoutSection = (WorkoutHelper.WorkoutSection) workoutSectionTimePair.first;
                if (workoutSection != currentSection) {
                    if (workoutSection == WorkoutHelper.WorkoutSection.PREPARE) {
                        observer.onPrepareStart();
                    } else if (workoutSection == WorkoutHelper.WorkoutSection.HANG) {
                        observer.onHangStart();

                        if (currentSection == WorkoutHelper.WorkoutSection.REST) {
                            observer.onRepComplete(++currentRep);
                        } else if (currentSection == WorkoutHelper.WorkoutSection.RECOVER) {
                            observer.onSetComplete(++currentSet);
                            currentRep = 1;
                        }
                    } else if (workoutSection == WorkoutHelper.WorkoutSection.REST) {
                        observer.onRestStart();
                    } else if (workoutSection == WorkoutHelper.WorkoutSection.RECOVER) {
                        observer.onRecoverStart();
                    }
                }

                currentSection = workoutSection;

                if (skipFlag) {
                    // Cancel and recreate the timer to simulate skipping the section
                    this.cancel();
                    createTimer(millisUntilFinished);
                    timer.start();
                    skipFlag = false;
                } else {
                    observer.onTick(millisUntilFinished, (long) workoutSectionTimePair.second, TICK_INTERVAL);
                }*/
            }

            @Override
            public void onFinish() {
                if (observer != null)
                    observer.onTimerComplete();
            }
        };
    }

    private void notifyObservers(){
        if (observer == null)
            return;

        Pair workoutSectionTimePair = WorkoutHelper.getCurrentSection(workout, millisecondsRemaining);

        WorkoutHelper.WorkoutSection workoutSection = (WorkoutHelper.WorkoutSection) workoutSectionTimePair.first;
        if (workoutSection != currentSection) {
            if (workoutSection == WorkoutHelper.WorkoutSection.PREPARE) {
                observer.onPrepareStart();
            } else if (workoutSection == WorkoutHelper.WorkoutSection.HANG) {
                observer.onHangStart();

                if (currentSection == WorkoutHelper.WorkoutSection.REST) {
                    observer.onRepComplete(++currentRep);
                } else if (currentSection == WorkoutHelper.WorkoutSection.RECOVER) {
                    observer.onSetComplete(++currentSet);
                    currentRep = 1;
                }
            } else if (workoutSection == WorkoutHelper.WorkoutSection.REST) {
                observer.onRestStart();
            } else if (workoutSection == WorkoutHelper.WorkoutSection.RECOVER) {
                observer.onRecoverStart();
            }
        }

        currentSection = workoutSection;
        observer.onTick(millisecondsRemaining, (long) workoutSectionTimePair.second, TICK_INTERVAL);
    }

    public void setObserver(IWorkoutTimerObserver observer) {
        this.observer = observer;
    }
}
