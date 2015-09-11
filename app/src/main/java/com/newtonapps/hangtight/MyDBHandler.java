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


public class MyDBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "saved_workouts.db", TABLE_WORKOUTS = "workouts",
            COLUMN_TITLE = "_title", COLUMN_DESCRIPTION = "_description", COLUMN_HANG = "_hang",
            COLUMN_REST = "_rest", COLUMN_REPS = "_reps", COLUMN_SETS = "_sets",
            COLUMN_RECOVER = "_recover", COLUMN_ID = "_id";

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
                COLUMN_RECOVER + " integer);";

        db.execSQL(query);

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, "5 on 5 off");
        values.put(COLUMN_DESCRIPTION, "Hang/Rest for 5 seconds for a minute.\n3 minute rest between sets");
        values.put(COLUMN_HANG, 5);
        values.put(COLUMN_REST, 5);
        values.put(COLUMN_REPS, 6);
        values.put(COLUMN_SETS, 5);
        values.put(COLUMN_RECOVER, 180);

        db.insert(TABLE_WORKOUTS, null, values);

        Cursor cursor = db.query(TABLE_WORKOUTS, ALL_COLUMNS, COLUMN_REST + " = 5", null, null, null, null );
        cursor.moveToFirst();

        Log.d("db", "db created");
        Log.d("db", Integer.toString(cursor.getCount()));

        cursor.close();
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


        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, workoutData[0]);
        values.put(COLUMN_DESCRIPTION, workoutData[1]);
        values.put(COLUMN_HANG, Integer.parseInt(workoutData[2]));
        values.put(COLUMN_REST, Integer.parseInt(workoutData[3]));
        values.put(COLUMN_REPS, Integer.parseInt(workoutData[4]));
        values.put(COLUMN_SETS, Integer.parseInt(workoutData[5]));
        values.put(COLUMN_RECOVER, Integer.parseInt(workoutData[6]));

        int saveSuccess = db.update(TABLE_WORKOUTS, values, COLUMN_ID + "=" + ID + "", null);
        db.close();

        return ( saveSuccess > 0);
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
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show(); //TODO -- fix crash caused by alert dialog
        }
        return false;
    }

    private boolean completeAddingWorkout(SQLiteDatabase db, String[] workoutData) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, workoutData[0]);
        values.put(COLUMN_DESCRIPTION, workoutData[1]);
        values.put(COLUMN_HANG, Integer.parseInt(workoutData[2]));
        values.put(COLUMN_REST, Integer.parseInt(workoutData[3]));
        values.put(COLUMN_REPS, Integer.parseInt(workoutData[4]));
        values.put(COLUMN_SETS, Integer.parseInt(workoutData[5]));
        values.put(COLUMN_RECOVER, Integer.parseInt(workoutData[6]));

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

    public String getInfo(int position){
        String workoutString;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_WORKOUTS, null);

        cursor.moveToFirst();
        Log.d("db", "position before moveToPosition: " + cursor.getPosition());
        cursor.moveToPosition(position);

        Log.d("db", "position after moveToPosition: " + cursor.getPosition());

        workoutString = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)) + "|" +
                    cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)) + "|" +
                    cursor.getString(cursor.getColumnIndex(COLUMN_HANG)) + "|" +
                    cursor.getString(cursor.getColumnIndex(COLUMN_REST)) + "|" +
                    cursor.getString(cursor.getColumnIndex(COLUMN_REPS)) + "|" +
                    cursor.getString(cursor.getColumnIndex(COLUMN_SETS)) + "|" +
                    cursor.getString(cursor.getColumnIndex(COLUMN_RECOVER));


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

    public void deleteWorkout(int position) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_WORKOUTS, null);
        cursor.moveToPosition(position);
        int ID = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        cursor.close();

        db.delete(TABLE_WORKOUTS, COLUMN_ID + "=" + ID, null);

        db.close();
    }
}