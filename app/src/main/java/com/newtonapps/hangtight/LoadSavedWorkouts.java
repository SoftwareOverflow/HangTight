package com.newtonapps.hangtight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

public class LoadSavedWorkouts extends Activity {

    private MyDBHandler dbHandler;
    private String[] arrayAdapterStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_saved_workouts);

        dbHandler = new MyDBHandler(getBaseContext(), null);
        createListView();
    }

    public void createListView() {
        setContentView(R.layout.activity_load_saved_workouts);
        ListView loadScreenListView = (ListView) findViewById(R.id.loadScreenListView);
        int rows = dbHandler.getRows();
        arrayAdapterStrings = new String[rows];

        for(int i=0; i<rows; i++) {
            if (dbHandler.getLoadScreenInfo(i) != null) arrayAdapterStrings[i] = dbHandler.getLoadScreenInfo(i);
        }

        ListAdapter adapter = new CustomAdapter(this, arrayAdapterStrings);
        loadScreenListView.setAdapter(adapter);
        registerForContextMenu(loadScreenListView);


        loadScreenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] intWorkoutData = dbHandler.getWorkoutInfo(position);

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
            String[] menuItems = new String[]{"Edit Workout", "Delete Workout", "Cancel"};
            for (int i=0; i< menuItems.length; i++){
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        final int position = info.position;

        switch (menuItemIndex){
            case 0:
                String dataFromDB = dbHandler.getLoadScreenInfo(position);
                int[] workoutData = dbHandler.getWorkoutInfo(position);

                for (int i = 0; i< workoutData.length; i++) dataFromDB += "|" + workoutData[i];


                Intent intent = new Intent(getApplicationContext(), EditSavedWorkout.class);
                intent.putExtra("workoutData", dataFromDB.split("\\|"));
                intent.putExtra("position", position);
                startActivity(intent);
                break;
            case 1:
                new AlertDialog.Builder(LoadSavedWorkouts.this)
                        .setTitle("Confirm Delete")
                        .setMessage("You are about to delete the workout!\nThis action cannot be undone.\nDo you wish to proceed?")
                        .setPositiveButton("Delete Workout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dbHandler.deleteWorkout(position)) Toast.makeText(LoadSavedWorkouts.this, "Workout Deleted", Toast.LENGTH_SHORT).show();
                                else Toast.makeText(LoadSavedWorkouts.this, "Problem Deleting Workout.\nPlease try again later", Toast.LENGTH_SHORT).show();

                                createListView();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }) //Does nothing
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                break;
            case 2:
                closeContextMenu();
                break;

            default:
                closeContextMenu();
                break;
        }
        return true;
    }
}
