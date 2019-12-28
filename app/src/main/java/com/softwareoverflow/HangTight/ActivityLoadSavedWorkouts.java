package com.softwareoverflow.HangTight;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softwareoverflow.HangTight.database.MyDBHandler;
import com.softwareoverflow.HangTight.workout.Workout;
import com.softwareoverflow.HangTight.ui.SavedWorkoutsListAdapter;

import java.util.List;

public class ActivityLoadSavedWorkouts extends Activity {


    private MyDBHandler dbHandler;
    private List<Workout> savedWorkouts;
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
