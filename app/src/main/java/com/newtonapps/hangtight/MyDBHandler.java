package com.newtonapps.hangtight;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


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
        values.put(COLUMN_TITLE, "5 on 5 off");
        values.put(COLUMN_DESCRIPTION, "Hang/Rest for 5 seconds for a minute.\n3 minute rest between sets");
        values.put(COLUMN_HANG, 5);
        values.put(COLUMN_REST, 5);
        values.put(COLUMN_REPS, 6);
        values.put(COLUMN_SETS, 5);
        values.put(COLUMN_RECOVER, 180);
        values.put(COLUMN_TIME, 995);

        db.insert(TABLE_WORKOUTS, null, values);
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

    public boolean addWorkout(final String[] workoutData, boolean ignoreDupe){
        SQLiteDatabase db = getWritableDatabase();

        boolean isDupe = checkForDupe(db, workoutData[0]);

        if (!isDupe || ignoreDupe) return completeAddingWorkout(db, workoutData);
        else{
            new AlertDialog.Builder(context)
                    .setTitle("Duplicate Found!")
                    .setMessage("A workout with the same title has been found in the saved workouts.\nSave anyway?")
                    .setPositiveButton("Save anyway", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addWorkout(workoutData, true);
                            Toast.makeText(context, "Workout Saved!", Toast.LENGTH_SHORT);
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

    private boolean completeAddingWorkout(SQLiteDatabase db, String[] workoutData) {

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

        db.insert(TABLE_WORKOUTS, null, values);
        db.close();

        Log.d("db", workoutData[0] + " saved successfully");

        return true;
    }

    private boolean checkForDupe(SQLiteDatabase db, String title) {

        Cursor cursor = db.query(TABLE_WORKOUTS, new String[] {COLUMN_TITLE}, COLUMN_TITLE + "='" + title + "'", null, null, null, null);

        cursor.moveToFirst();
        int count = cursor.getCount();
        cursor.close();


        return (count>0);


    }

    public String getLoadScreenInfo(int position){
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
    }

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

    public int[] getWorkoutInfo(int position) {
        int[] workoutData = new int[6];

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_WORKOUTS, null);
        cursor.moveToPosition(position);

        workoutData[0] = cursor.getInt(cursor.getColumnIndex(COLUMN_HANG));
        workoutData[1] = cursor.getInt(cursor.getColumnIndex(COLUMN_REST));
        workoutData[2] = cursor.getInt(cursor.getColumnIndex(COLUMN_REPS));
        workoutData[3] = cursor.getInt(cursor.getColumnIndex(COLUMN_SETS));
        workoutData[4] = cursor.getInt(cursor.getColumnIndex(COLUMN_RECOVER));
        workoutData[5] = cursor.getInt(cursor.getColumnIndex(COLUMN_TIME));

        cursor.close();
        db.close();

        return workoutData;
    }
}