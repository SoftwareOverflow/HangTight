package com.softwareoverflow.HangTight.workout;

import android.os.Parcel;
import android.os.Parcelable;

import com.softwareoverflow.HangTight.helper.WorkoutHelper;

/**
 * A POJO to hold a basic workout
 */
public class Workout implements Parcelable {

    // TODO - sort out workoutDuration. Should NOT exist in the parcel as it can be derived. Database SHOULD NOT STORE IT EITHER
    private String workoutName, workoutDescription;
    private int hangTime, restTime, numReps, numSets, recoverTime, workoutDuration;

    // Empty constructor to allow instantiation. Parcelable constructor also exists.
    public Workout(){}

    //region getters/setters
    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getWorkoutDescription() {
        return workoutDescription;
    }

    public void setWorkoutDescription(String workoutDescription) {
        this.workoutDescription = workoutDescription;
    }

    public int getHangTime() {
        return hangTime;
    }

    public void setHangTime(int hangTime) {
        this.hangTime = hangTime;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public int getNumReps() {
        return numReps;
    }

    public void setNumReps(int numReps) {
        this.numReps = numReps;
    }

    public int getRecoverTime() {
        return recoverTime;
    }

    public void setRecoverTime(int recoverTime) {
        this.recoverTime = recoverTime;
    }

    public int getNumSets() {
        return numSets;
    }

    public void setNumSets(int numSets) {
        this.numSets = numSets;
    }

    public int getDuration() {
        return WorkoutHelper.getWorkoutDuration(this);
    }

    public void setWorkoutDuration(int workoutDuration) {
        this.workoutDuration = workoutDuration;
    }
    //endregion getters/setters

// region Parcelable
    public Workout(Parcel in){
        this.workoutName = in.readString();
        this.workoutDescription = in.readString();
        this.hangTime = in.readInt();
        this.restTime = in.readInt();
        this.numReps = in.readInt();
        this.numSets = in.readInt();
        this.recoverTime = in.readInt();
        this.workoutDuration = in.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(workoutName);
        dest.writeString(workoutDescription);
        dest.writeInt(hangTime);
        dest.writeInt(restTime);
        dest.writeInt(numReps);
        dest.writeInt(numSets);
        dest.writeInt(recoverTime);
        dest.writeInt(workoutDuration);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Workout createFromParcel(Parcel in){
            return new Workout(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new Workout[size];
        }
    };
    //endregion
}
