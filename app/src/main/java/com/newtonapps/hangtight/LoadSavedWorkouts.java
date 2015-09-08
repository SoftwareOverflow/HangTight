package com.newtonapps.hangtight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Arrays;

public class LoadSavedWorkouts extends AppCompatActivity {

    private MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_saved_workouts);

        dbHandler = new MyDBHandler(getBaseContext(), null);
        createListView();
    }


    private void createListView() {
        ListView loadScreenListView = (ListView) findViewById(R.id.loadScreenListView);
        int rows = dbHandler.getRows();
        String[] arrayAdapterStrings = new String[rows];


        for(int i=0; i<rows; i++) {
            if (dbHandler.getInfo(i) != null) arrayAdapterStrings[i] = dbHandler.getInfo(i);
            Log.d("db", "stringFromDB: " + arrayAdapterStrings[i]);
        }


        ListAdapter adapter = new CustomAdapter(this, arrayAdapterStrings);
        loadScreenListView.setAdapter(adapter);


        loadScreenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dataFromDB = dbHandler.getInfo(position);
                String[] workoutData = dataFromDB.split("\\|");
                Log.d("db", workoutData[3]);

                int[] intWorkoutData = new int[] {Integer.parseInt(workoutData[2]), Integer.parseInt(workoutData[3]),
                        Integer.parseInt(workoutData[4]), Integer.parseInt(workoutData[5]), Integer.parseInt(workoutData[6])};

                Intent intent = new Intent(getApplicationContext(), Workout.class);
                intent.putExtra("dataArray", intWorkoutData);
                startActivity(intent);
                finish();
            }
        });

        loadScreenListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String dataFromDB = dbHandler.getInfo(position);
                String[] workoutData = dataFromDB.split("\\|");

                Intent intent = new Intent(getApplicationContext(), EditSavedWorkout.class);
                intent.putExtra("workoutData", Arrays.copyOfRange(workoutData, 3, workoutData.length));
                startActivity(intent);
                return true;
            }
        });
    }
}
