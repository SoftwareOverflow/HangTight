package com.softwareoverflow.HangTight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

        for (int i = 0; i < rows; i++) {
            if (dbHandler.getLoadScreenInfo(i) != null)
                arrayAdapterStrings[i] = dbHandler.getLoadScreenInfo(i);
        }

        ListAdapter adapter = new CustomAdapter(this, arrayAdapterStrings);
        loadScreenListView.setAdapter(adapter);
        registerForContextMenu(loadScreenListView);

        final SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);

        final View checkBoxView = View.inflate(this, R.layout.check_box_alert_dialog, null);
        final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    settings.edit().putBoolean("showWarmUp", false).apply();
                }
            }
        });
        checkBox.setText("Do not show this warning again");


        loadScreenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int[] intWorkoutData = dbHandler.getWorkoutInfo(position);
                final Intent intent = new Intent(getApplicationContext(), Workout.class);
                intent.putExtra("dataArray", intWorkoutData);
                intent.putExtra("workoutSaved", true);

                Boolean showWarning = settings.getBoolean("showWarmUp", true);

                if (showWarning){
                    new AlertDialog.Builder(LoadSavedWorkouts.this)
                            .setTitle("Have You Warmed Up?")
                            .setMessage("Ensure you are thoroughly warmed up before beginning any" +
                                    " workout. Failure to do so could result in injury.\n\nIf you feel" +
                                    " any pain during the workout, discontinue immediately.")
                            .setView(checkBoxView)
                            .setPositiveButton("Start Workout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    startActivity(intent);

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                            .show();
                } else{
                    finish();

                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.loadScreenListView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
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
                int workoutData[] = dbHandler.getWorkoutInfo(position);

                Intent intent = new Intent(getApplicationContext(), EditSavedWorkout.class);
                intent.putExtra("workoutData", workoutData);
                intent.putExtra("position", position);
                intent.putExtra("words", dbHandler.getLoadScreenInfo(position));

                finish();


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
                        .setIcon(R.drawable.ic_dialog_alert_holo_light)
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
