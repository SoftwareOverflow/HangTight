package com.softwareoverflow.hangtight;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softwareoverflow.hangtight.database.MyDBHandler;
import com.softwareoverflow.hangtight.room.WorkoutDBRepo;
import com.softwareoverflow.hangtight.ui.SavedWorkoutsListAdapter;

public class ActivityLoadSavedWorkouts extends Activity {


    private MyDBHandler dbHandler;
    private WorkoutDBRepo db;
    private SavedWorkoutsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_saved_workouts);
        dbHandler = new MyDBHandler(getBaseContext(), null);
        createListView();
    }

    public void createListView() {
        RecyclerView loadScreenListView = findViewById(R.id.loadSavedWorkouts_recyclerView);

        adapter = new SavedWorkoutsListAdapter(dbHandler);
        loadScreenListView.setAdapter(adapter);
        loadScreenListView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if( adapter != null) {
            adapter.updateDataSet();
        }
    }
}
