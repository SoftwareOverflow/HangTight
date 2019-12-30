package com.softwareoverflow.HangTight.database;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.core.util.Consumer;

import com.softwareoverflow.HangTight.R;
import com.softwareoverflow.HangTight.helper.WorkoutHelper;
import com.softwareoverflow.HangTight.workout.Workout;

import java.util.ArrayList;
import java.util.List;


public class MyDBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "saved_workouts.db", TABLE_WORKOUTS = "workouts",
            COLUMN_TITLE = "_title", COLUMN_DESCRIPTION = "_description", COLUMN_HANG = "_hang",
            COLUMN_REST = "_rest", COLUMN_REPS = "_reps", COLUMN_SETS = "_sets",
            COLUMN_RECOVER = "_recover", COLUMN_ID = "_id";

    // This column is no longer actively used, although as SQLite does not support DROP COLUMN commands
    // it is staying until a larger DB change is required
    private static final String COLUMN_TIME = "_time";

    private static final int DB_VERSION = 2;

    private Context context;

    public MyDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TABLE_WORKOUTS + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_TITLE + " text not null, " +
                COLUMN_DESCRIPTION + " text not null, " + COLUMN_HANG + " integer, " +
                COLUMN_REST + " integer, " + COLUMN_REPS + " integer, " + COLUMN_SETS + " integer, " +
                COLUMN_RECOVER + " integer, " + COLUMN_TIME + " integer);";

        db.execSQL(query);

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, "5 on 5 off - Beginner");
        values.put(COLUMN_DESCRIPTION, "6 sets. We recommend: jugs - jugs - 3 finger pocket - crimp - 3 finger pockets - 4 finger pockets\n" +
                "Change the holds to suit your board and ability");
        values.put(COLUMN_HANG, 5);
        values.put(COLUMN_REST, 5);
        values.put(COLUMN_REPS, 6);
        values.put(COLUMN_SETS, 6);
        values.put(COLUMN_RECOVER, 180);
        values.put(COLUMN_TIME, 995);


        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_TITLE, "7 on 3 off - Intermediate/Hard");
        values2.put(COLUMN_DESCRIPTION, "6 sets. We recommend: 4 finger pockets - 3 finger pockets - slopers - crimps - 2 finger pocket - 3 finger pocket" +
                "\nChange the holds to suit your board and ability.");
        values2.put(COLUMN_HANG, 7);
        values2.put(COLUMN_REST, 3);
        values2.put(COLUMN_REPS, 6);
        values2.put(COLUMN_SETS, 6);
        values2.put(COLUMN_RECOVER, 180);
        values2.put(COLUMN_TIME, 995);

        db.insert(TABLE_WORKOUTS, null, values);
        db.insert(TABLE_WORKOUTS, null, values2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                // Add future updates to this case statement
                // DO NOT add break statement. This will iteratively increment the updates if required
            case 2:
                // Add future updates to this case statement
                // DO NOT add break statement. This will iteratively increment the updates if required
        }
    }

    public void addWorkout(final Workout workout, boolean ignoreDupe, boolean overwriteExisting, Consumer<Boolean> delayedResultConsumer) {
        if (overwriteExisting && workout.getId() == null)
            throw new UnsupportedOperationException("Unable to overwrite existing workout with null Id");

        SQLiteDatabase db = getWritableDatabase();

        boolean isDupe = checkForDupe(db, workout.getWorkoutName(), overwriteExisting ? workout.getId() : null);

        if (!isDupe || ignoreDupe) {
            delayedResultConsumer.accept(completeAddingWorkout(db, workout, overwriteExisting));
        }
        else {
            new AlertDialog.Builder(context, R.style.CustomDialogTheme)
                    .setTitle("Duplicate Found!")
                    .setMessage("A workout with the same name has been found in the saved workouts.\nSave anyway?")
                    .setPositiveButton("Save anyway", (dialog, which) ->
                            addWorkout(workout, true, overwriteExisting, delayedResultConsumer))
                    .setNegativeButton(R.string.cancel, ((dialog, which) -> delayedResultConsumer.accept(false)))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        delayedResultConsumer.accept(false);
    }

    private boolean completeAddingWorkout(SQLiteDatabase db, Workout workout, boolean overwriteExisting) {

        int hang = workout.getHangTime(), rest = workout.getRestTime(),
                reps = workout.getNumReps(), sets = workout.getNumReps(),
                recover = workout.getRecoverTime();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, workout.getWorkoutName());
        values.put(COLUMN_DESCRIPTION, workout.getWorkoutDescription());
        values.put(COLUMN_HANG, hang);
        values.put(COLUMN_REST, rest);
        values.put(COLUMN_REPS, reps);
        values.put(COLUMN_SETS, sets);
        values.put(COLUMN_RECOVER, recover);

        if (overwriteExisting) {
            db.update(TABLE_WORKOUTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(workout.getId())});
        } else {
            int id = (int) db.insert(TABLE_WORKOUTS, null, values);
            if(id != -1)
                workout.setId(id);
        }
        db.close();

        return true;
    }

    private boolean checkForDupe(SQLiteDatabase db, String title, Integer currentId) {
        String query = COLUMN_TITLE + "='" + title + "' AND " + COLUMN_ID + " IS NOT " + currentId;
        Cursor cursor = db.query(TABLE_WORKOUTS, new String[]{COLUMN_TITLE}, query, null, null, null, null);

        cursor.moveToFirst();
        int count = cursor.getCount();
        cursor.close();

        return (count > 0);
    }

    public boolean deleteWorkout(final int position) {
        try {
            SQLiteDatabase db = getWritableDatabase();

            Cursor cursor = db.rawQuery("select * from " + TABLE_WORKOUTS, null);
            cursor.moveToPosition(position);
            int ID = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();

            db.delete(TABLE_WORKOUTS, COLUMN_ID + "=" + ID, null);
            db.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Workout> loadAllWorkouts() {
        ArrayList<Workout> workouts = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_WORKOUTS, null);
        while (cursor.moveToNext()) {
            Workout workout = new Workout();

            workout.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            workout.setWorkoutName(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            workout.setWorkoutDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            workout.setHangTime(cursor.getInt(cursor.getColumnIndex(COLUMN_HANG)));
            workout.setRestTime(cursor.getInt(cursor.getColumnIndex(COLUMN_REST)));
            workout.setNumReps(cursor.getInt(cursor.getColumnIndex(COLUMN_REPS)));
            workout.setNumSets(cursor.getInt(cursor.getColumnIndex(COLUMN_SETS)));
            workout.setRecoverTime(cursor.getInt(cursor.getColumnIndex(COLUMN_RECOVER)));
            workout.setWorkoutDuration(WorkoutHelper.getWorkoutDuration(workout));

            workouts.add(workout);
        }

        cursor.close();
        db.close();

        return workouts;
    }
}