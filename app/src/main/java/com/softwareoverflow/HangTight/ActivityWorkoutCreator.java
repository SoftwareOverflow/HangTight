package com.softwareoverflow.HangTight;

import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.softwareoverflow.HangTight.ui.NumberPickerPlusMinus;
import com.softwareoverflow.HangTight.ui.WarmUpWarningDialog;
import com.softwareoverflow.HangTight.workout.MyDBHandler;
import com.softwareoverflow.HangTight.workout.Workout;

public class ActivityWorkoutCreator extends AppCompatActivity {

    private View rootView, saveWorkoutView;
    private EditText workoutName, workoutDescription;
    private NumberPickerPlusMinus hangTimePicker, restTimePicker, repsPicker, setsPicker, recoverPicker;
    private Workout workout = new Workout();
    private boolean workoutSaved = false; // TODO - might need to move this, as we need to know after the workout to offer the option to save

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_basic_workout);
        getSupportActionBar().hide();

        initViews();

        Bundle data = getIntent().getExtras();
        if (data != null) {
            workout = data.getParcelable("workout");
            workoutSaved = true;

            loadWorkoutValues();
        }
    }

    private void loadWorkoutValues(){
        hangTimePicker.setValue(workout.getHangTime());
        restTimePicker.setValue(workout.getRestTime());
        repsPicker.setValue(workout.getNumReps());
        recoverPicker.setValue(workout.getRecoverTime());
        setsPicker.setValue(workout.getNumSets());

        workoutName.setText(workout.getWorkoutName());
        workoutDescription.setText(workout.getWorkoutDescription());
    }

    public void initViews(){
        rootView = findViewById(R.id.root_new_basic_workout);

        hangTimePicker = findViewById(R.id.hangTimeNumberPicker);
        restTimePicker = findViewById(R.id.restTimeNumberPicker);
        repsPicker = findViewById(R.id.repsNumberPicker);
        recoverPicker = findViewById(R.id.recoverTimeNumberPicker);
        setsPicker = findViewById(R.id.setsNumberPicker);

        saveWorkoutView = findViewById(R.id.workout_creator_save_workout_view);
        workoutName = saveWorkoutView.findViewById(R.id.editText_saveWorkout_workoutName);
        workoutDescription = saveWorkoutView.findViewById(R.id.editText_saveWorkout_workoutDescription);

        int color = getResources().getColor(R.color.Charcoal, getTheme());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveWorkoutView.getBackground().setColorFilter(new BlendModeColorFilter(R.color.Charcoal, BlendMode.DST_OVER));
        } else {
            saveWorkoutView.getBackground().setColorFilter(color, PorterDuff.Mode.DST_OVER);
        }

        saveWorkoutView.findViewById(R.id.save_workout_button).setOnClickListener((View v) -> {
            String name = workoutName.getText().toString();
            String description = workoutDescription.getText().toString();

            if(name.matches("")){
                Snackbar.make(saveWorkoutView, "Please enter a name.", Snackbar.LENGTH_SHORT);
                return;
            }

            workout.setWorkoutName(name);
            workout.setWorkoutDescription(description);

            MyDBHandler dbHandler = new MyDBHandler(ActivityWorkoutCreator.this, null);

            if (dbHandler.addWorkout(workout, false)) Snackbar.make(rootView, "Workout Saved!", Snackbar.LENGTH_SHORT).show();
            hideSaveScreen();
        });
        saveWorkoutView.findViewById(R.id.cancel_saving_workout_button).setOnClickListener((View v) -> hideSaveScreen());
    }

    /**
     * Checks the user inputted values and assigns them to the Workout/
     * @return true if the values are valid, false otherwise
     */
    private boolean assignValues(){
        try {
            workout.setHangTime(hangTimePicker.getValue());
            workout.setRestTime(restTimePicker.getValue());
            workout.setNumReps(repsPicker.getValue());
            workout.setRecoverTime(recoverPicker.getValue());
            workout.setNumSets(setsPicker.getValue());

            return true;
        } catch(NumberFormatException e){
            // At least one of the values is not parsable. Inform the user
            Snackbar.make(rootView, R.string.invalid_numbers, Snackbar.LENGTH_SHORT).show();

            return false;
        }
    }

    public void startWorkout(View v){
        // Stop if we can't assign the values correctly
        if(!assignValues()){
            return;
        }

        final Intent i = new Intent(ActivityWorkoutCreator.this, ActivityWorkout.class);
        i.putExtra("workout", workout);
        i.putExtra("workoutSaved", workoutSaved);

        new WarmUpWarningDialog(this, i).show(); // Creates and shows the warm up warning (if required)
    }

    public void saveWorkout(View v){
        // Stop if we can't assign the values correctly
        if(!assignValues()){
            return;
        }

        // TODO - load title and description from workout object into the fields.
        // TODO - If the workout has been saved previously, add a toggle for overwriting existing workout

        saveWorkoutView.setVisibility(View.VISIBLE);
        workoutName.setFocusableInTouchMode(true);
        workoutName.requestFocus();
        rootView.setFocusable(false);
        rootView.setFocusableInTouchMode(false);
        rootView.setAlpha(0.4f);
        saveWorkoutView.setAlpha(1f);



        /*
        final LinearLayout workoutExtraInfoScreen = findViewById(R.id.overlayScreen);
        final LinearLayout workoutScreen = findViewById(R.id.basicMainScreen);

        workoutScreen.setVisibility(View.GONE);
        workoutExtraInfoScreen.setVisibility(View.VISIBLE);

        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workoutSaved = true;

                assignValues();

                String titleString = title.getText().toString();
                String descriptionString = description.getText().toString();


                if (titleString.matches("") || descriptionString.matches("")) Toast.makeText(getApplicationContext(), "Please enter a title and a description", Toast.LENGTH_SHORT).show();
                else if (titleString.contains("|") || descriptionString.contains("|")){
                    Toast.makeText(getApplicationContext(), "Title and Description cannot contain the pipe character '|'", Toast.LENGTH_SHORT).show();
                }
                else{
                    MyDBHandler dbHandler = new MyDBHandler(ActivityWorkoutCreator.this, null);

                    workout.setWorkoutName(titleString);
                    workout.setWorkoutDescription(descriptionString);

                    if (dbHandler.addWorkout(workout, false)) Toast.makeText(getApplicationContext(), "ActivityWorkout Saved!", Toast.LENGTH_SHORT).show();
                    workoutExtraInfoScreen.setVisibility(View.GONE);
                    workoutScreen.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workoutExtraInfoScreen.setVisibility(View.GONE);
                workoutScreen.setVisibility(View.VISIBLE);
            }
        });*/
    }

    private void hideSaveScreen(){
        rootView.setFocusable(true);
        rootView.setFocusableInTouchMode(true);
        rootView.setAlpha(1f);
        //reset the entered values and hide the view
        workoutName.setText("");
        workoutDescription.setText("");
        saveWorkoutView.setVisibility(View.GONE);
        rootView.requestFocus();
    }

    @Override
    public void onBackPressed() {
        if(saveWorkoutView.getVisibility() == View.VISIBLE)
            hideSaveScreen();
        else
            super.onBackPressed();
    }
}
