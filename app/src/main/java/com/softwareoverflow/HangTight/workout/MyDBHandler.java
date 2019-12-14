package com.softwareoverflow.HangTight.workout;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MyDBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "saved_workouts.db", TABLE_WORKOUTS = "workouts",
            COLUMN_TITLE = "_title", COLUMN_DESCRIPTION = "_description", COLUMN_HANG = "_hang",
            COLUMN_REST = "_rest", COLUMN_REPS = "_reps", COLUMN_SETS = "_sets",
            COLUMN_RECOVER = "_recover", COLUMN_ID = "_id", COLUMN_TIME = "_time";

    private static final String[] ALL_COLUMNS = new String[] {COLUMN_TITLE, COLUMN_DESCRIPTION, COLUMN_HANG, COLUMN_REST, COLUMN_REPS, COLUMN_SETS, COLUMN_RECOVER};

    private static final int DB_VERSION = 1;

    private Context context;

    public MyDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TABLE_WORKOUTS + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_TITLE +" text not null, " +
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
        db.execSQL("drop table if exists " + TABLE_WORKOUTS);
        onCreate(db);

    }

    public boolean updateWorkout(int position, String[] workoutData){
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_WORKOUTS, null);
        cursor.moveToPosition(position);
        final int ID = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        cursor.close();

        int hang = Integer.parseInt(workoutData[2]), rest = Integer.parseInt(workoutData[3]),
                reps = Integer.parseInt(workoutData[4]), sets = Integer.parseInt(workoutData[5]),
                recover = Integer.parseInt(workoutData[6]);

        int totalTime = (hang + rest) * reps;
        totalTime += recover - rest;
        totalTime *= sets;
        totalTime -= recover;

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, workoutData[0]);
        values.put(COLUMN_DESCRIPTION, workoutData[1]);
        values.put(COLUMN_HANG, hang);
        values.put(COLUMN_REST, rest);
        values.put(COLUMN_REPS, reps);
        values.put(COLUMN_SETS, sets);
        values.put(COLUMN_RECOVER, recover);
        values.put(COLUMN_TIME, totalTime);


        boolean saved =  db.update(TABLE_WORKOUTS, values, COLUMN_ID + "=" + ID + "", null) !=0 ;
        db.close();
        return saved;
    }

    public boolean addWorkout(final Workout workout, boolean ignoreDupe){
        SQLiteDatabase db = getWritableDatabase();

        boolean isDupe = checkForDupe(db, workout.getWorkoutName());

        if (!isDupe || ignoreDupe) return completeAddingWorkout(db, workout);
        else{
            new AlertDialog.Builder(context)
                    .setTitle("Duplicate Found!")
                    .setMessage("A workout with the same title has been found in the saved workouts.\nSave anyway?")
                    .setPositiveButton("Save anyway", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addWorkout(workout, true);
                            Toast.makeText(context, "ActivityWorkout Saved!", Toast.LENGTH_SHORT);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }) //Does nothing
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        return false;
    }

    private boolean completeAddingWorkout(SQLiteDatabase db, Workout workout) {

        int hang = workout.getHangTime(), rest = workout.getRestTime(),
                reps = workout.getNumReps(), sets = workout.getNumReps(),
                recover = workout.getRecoverTime();

        int totalTime = (hang + rest) * reps;
        totalTime += recover - rest;
        totalTime *= sets;
        totalTime -= recover;

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, workout.getWorkoutName());
        values.put(COLUMN_DESCRIPTION, workout.getWorkoutDescription());
        values.put(COLUMN_HANG, hang);
        values.put(COLUMN_REST, rest);
        values.put(COLUMN_REPS, reps);
        values.put(COLUMN_SETS, sets);
        values.put(COLUMN_RECOVER, recover);
        values.put(COLUMN_TIME, totalTime);

        db.insert(TABLE_WORKOUTS, null, values);
        db.close();

        return true;
    }

    private boolean checkForDupe(SQLiteDatabase db, String title) {

        Cursor cursor = db.query(TABLE_WORKOUTS, new String[] {COLUMN_TITLE}, COLUMN_TITLE + "='" + title + "'", null, null, null, null);

        cursor.moveToFirst();
        int count = cursor.getCount();
        cursor.close();


        return (count>0);


    }

/*    public String getLoadScreenInfo(int position){
        String workoutString;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_WORKOUTS, null);
        cursor.moveToPosition(position);

        workoutString = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)) + "|" +
                    cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)) + "|" +
                    cursor.getString(cursor.getColumnIndex(COLUMN_TIME));

        cursor.close();
        db.close();
        return workoutString;
    }*/

    public int getRows() {
        SQLiteDatabase db = getReadableDatabase();
        int rows = (int) DatabaseUtils.queryNumEntries(db, TABLE_WORKOUTS);
        db.close();

        return rows;
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

    public Workout getWorkoutInfo(int position) {
        Workout workout = new Workout();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_WORKOUTS, null);
        cursor.moveToPosition(position);

        workout.setHangTime(cursor.getInt(cursor.getColumnIndex(COLUMN_HANG)));
        workout.setRestTime(cursor.getInt(cursor.getColumnIndex(COLUMN_REST)));
        workout.setNumReps(cursor.getInt(cursor.getColumnIndex(COLUMN_REPS)));
        workout.setNumSets(cursor.getInt(cursor.getColumnIndex(COLUMN_SETS)));
        workout.setRecoverTime(cursor.getInt(cursor.getColumnIndex(COLUMN_RECOVER)));
        workout.setWorkoutDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_TIME)));

        cursor.close();
        db.close();

        return workout;
    }

    public List<Workout> loadAllWorkouts(){
        ArrayList<Workout> workouts = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_WORKOUTS, null);
        while (cursor.moveToNext()){
            Workout workout = new Workout();

            workout.setWorkoutName(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            workout.setWorkoutDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            workout.setHangTime(cursor.getInt(cursor.getColumnIndex(COLUMN_HANG)));
            workout.setRestTime(cursor.getInt(cursor.getColumnIndex(COLUMN_REST)));
            workout.setNumReps(cursor.getInt(cursor.getColumnIndex(COLUMN_REPS)));
            workout.setNumSets(cursor.getInt(cursor.getColumnIndex(COLUMN_SETS)));
            workout.setRecoverTime(cursor.getInt(cursor.getColumnIndex(COLUMN_RECOVER)));
            workout.setWorkoutDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_TIME)));

            workouts.add(workout);
        }

        cursor.close();
        db.close();

        return workouts;
    }
}