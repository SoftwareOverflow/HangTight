package com.softwareoverflow.hangtight.workout;

public interface IWorkoutTimerObserver {

    void onTick(long millisUntilFinished, long millisLeftBeforeChange, int tickInterval);
    void onPrepareStart();
    void onHangStart();
    void onRestStart();
    void onRepComplete(int nextRep);
    void onRecoverStart();
    void onSetComplete(int nextSet);
    void onTimerComplete();
}
