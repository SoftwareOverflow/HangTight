package com.softwareoverflow.hangtight.workout;

import android.os.Parcel;
import android.os.Parcelable;

import com.softwareoverflow.hangtight.helper.WorkoutHelper;

/**
 * A class to hold a basic workout
 */
public class Workout implements Parcelable {

    private Integer id;
    private String workoutName, workoutDescription;
    private int hangTime, restTime, numReps, numSets, recoverTime;

    // Empty constructor to allow instantiation. Parcelable constructor also exists.
    public Workout() {
    }

    //region getters/setters
    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
    //endregion getters/setters

    // region Parcelable
    public Workout(Parcel in) {
        this.id = (Integer) in.readSerializable();
        this.workoutName = in.readString();
        this.workoutDescription = in.readString();
        this.hangTime = in.readInt();
        this.restTime = in.readInt();
        this.numReps = in.readInt();
        this.numSets = in.readInt();
        this.recoverTime = in.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(id);
        dest.writeString(workoutName);
        dest.writeString(workoutDescription);
        dest.writeInt(hangTime);
        dest.writeInt(restTime);
        dest.writeInt(numReps);
        dest.writeInt(numSets);
        dest.writeInt(recoverTime);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new Workout[size];
        }
    };
    //endregion
}
