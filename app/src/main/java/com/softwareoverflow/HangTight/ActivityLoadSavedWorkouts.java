package com.softwareoverflow.HangTight;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softwareoverflow.HangTight.workout.MyDBHandler;
import com.softwareoverflow.HangTight.workout.Workout;
import com.softwareoverflow.HangTight.ui.SavedWorkoutsListAdapter;

import java.util.List;

public class ActivityLoadSavedWorkouts extends Activity {


    private MyDBHandler dbHandler;
    private List<Workout> savedWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_saved_workouts);
        dbHandler = new MyDBHandler(getBaseContext(), null);
        createListView();
    }

    public void createListView() {
        RecyclerView loadScreenListView = findViewById(R.id.loadSavedWorkouts_recyclerView);

        savedWorkouts = dbHandler.loadAllWorkouts();

        RecyclerView.Adapter adapter = new SavedWorkoutsListAdapter(savedWorkouts, dbHandler);
        loadScreenListView.setAdapter(adapter);
        loadScreenListView.setLayoutManager(new LinearLayoutManager(this));

/*        registerForContextMenu(loadScreenListView);

        final SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);

        loadScreenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Workout workout = dbHandler.getWorkoutInfo(position);
                final Intent intent = new Intent(getApplicationContext(), ActivityWorkout.class);
                intent.putExtra("workout", workout);
                intent.putExtra("workoutSaved", true);

                final View checkBoxView = View.inflate(ActivityLoadSavedWorkouts.this, R.layout.check_box_alert_dialog, null);
                final CheckBox checkBox = checkBoxView.findViewById(R.id.checkbox);
                checkBox.setText("Do not show this warning again");

                boolean showWarning = settings.getBoolean("showWarmUp", true);

                if (showWarning){
                    new AlertDialog.Builder(ActivityLoadSavedWorkouts.this)
                            .setTitle("Have You Warmed Up?")
                            .setMessage("Ensure you are thoroughly warmed up before beginning any" +
                                    " workout. Failure to do so could result in injury.\n\nIf you feel" +
                                    " any pain during the workout, discontinue immediately.")
                            .setView(checkBoxView)
                            .setPositiveButton("Start ActivityWorkout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (checkBox.isChecked()) {
                                        settings.edit().putBoolean("showWarmUp", false).apply();
                                    }

                                    finish();
                                    startActivity(intent);

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                            .show();
                } else{
                    finish();

                    startActivity(intent);
                }

            }
        });*/
    }

/*    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.loadScreenListView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            String[] splitData = arrayAdapterStrings[info.position].split("\\|");
            menu.setHeaderTitle("Edit '" + splitData[0] + "' ActivityWorkout");
            String[] menuItems = new String[]{"Edit ActivityWorkout", "Delete ActivityWorkout", "Cancel"};
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
                Workout workout = dbHandler.getWorkoutInfo(position);

                Intent intent = new Intent(getApplicationContext(), EditSavedWorkout.class);
                intent.putExtra("workout", workout);
                intent.putExtra("position", position);
                intent.putExtra("words", dbHandler.getLoadScreenInfo(position));

                finish();

                startActivity(intent);
                break;
            case 1:
                new AlertDialog.Builder(ActivityLoadSavedWorkouts.this)
                        .setTitle("Confirm Delete")
                        .setMessage("You are about to delete the workout!\nThis action cannot be undone.\nDo you wish to proceed?")
                        .setPositiveButton("Delete ActivityWorkout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dbHandler.deleteWorkout(position)) Toast.makeText(ActivityLoadSavedWorkouts.this, "ActivityWorkout Deleted", Toast.LENGTH_SHORT).show();
                                else Toast.makeText(ActivityLoadSavedWorkouts.this, "Problem Deleting ActivityWorkout.\nPlease try again later", Toast.LENGTH_SHORT).show();

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

            default:
                closeContextMenu();
                break;
        }
        return true;
    }*/
}
