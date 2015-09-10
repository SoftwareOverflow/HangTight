package com.newtonapps.hangtight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class LoadSavedWorkouts extends Activity {

    private MyDBHandler dbHandler;
    private ListView loadScreenListView;
    private String[] menuItems, arrayAdapterStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_saved_workouts);

        dbHandler = new MyDBHandler(getBaseContext(), null);
        createListView();
    }


    private void createListView() {
       loadScreenListView = (ListView) findViewById(R.id.loadScreenListView);
        int rows = dbHandler.getRows();
        arrayAdapterStrings = new String[rows];

        for(int i=0; i<rows; i++) {
            if (dbHandler.getInfo(i) != null) arrayAdapterStrings[i] = dbHandler.getInfo(i);
            Log.d("db", "stringFromDB: " + arrayAdapterStrings[i]);
        }



        ListAdapter adapter = new CustomAdapter(this, arrayAdapterStrings);
        loadScreenListView.setAdapter(adapter);
        registerForContextMenu(loadScreenListView);


        loadScreenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dataFromDB = dbHandler.getInfo(position);
                String[] workoutData = dataFromDB.split("\\|");

                int[] intWorkoutData = new int[]{Integer.parseInt(workoutData[2]), Integer.parseInt(workoutData[3]),
                        Integer.parseInt(workoutData[4]), Integer.parseInt(workoutData[5]), Integer.parseInt(workoutData[6])};

                Intent intent = new Intent(getApplicationContext(), Workout.class);
                intent.putExtra("dataArray", intWorkoutData);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.loadScreenListView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Log.d("menu", arrayAdapterStrings[info.position]);
            String[] splitData = arrayAdapterStrings[info.position].split("\\|");
            menu.setHeaderTitle("Edit '" + splitData[0] + "' Workout");
            menuItems = new String[] {"Edit Workout", "Delete Workout", "Cancel"};
            for (int i=0; i<menuItems.length; i++){
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        switch (menuItemIndex){
            case 0:
                String dataFromDB = dbHandler.getInfo(info.position);
                String[] workoutData = dataFromDB.split("\\|");

                Intent intent = new Intent(getApplicationContext(), EditSavedWorkout.class);
                intent.putExtra("workoutData", workoutData);
                intent.putExtra("position", info.position);

                break;


            case 1:
                dbHandler.deleteWorkout(info.position);
                createListView();
                break;

            case 2:
                closeContextMenu();
                break;

        }


        return true;
    }
}
