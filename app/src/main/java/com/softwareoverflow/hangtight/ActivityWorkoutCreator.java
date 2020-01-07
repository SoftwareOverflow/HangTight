package com.softwareoverflow.hangtight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.softwareoverflow.hangtight.helper.MobileAdsHelper;
import com.softwareoverflow.hangtight.helper.UpgradeManager;
import com.softwareoverflow.hangtight.ui.CustomBannerAd;
import com.softwareoverflow.hangtight.ui.NumberPickerPlusMinus;
import com.softwareoverflow.hangtight.ui.dialog.SaveWorkoutDialog;
import com.softwareoverflow.hangtight.ui.dialog.WarmUpWarningDialog;
import com.softwareoverflow.hangtight.workout.Workout;

public class ActivityWorkoutCreator extends AppCompatActivity {

    private View rootView;
    private CustomBannerAd adView;
    private NumberPickerPlusMinus hangTimePicker, restTimePicker, repsPicker, setsPicker, recoverPicker;
    private Workout workout = new Workout();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_creator);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initViews();

        Bundle data = getIntent().getExtras();
        if (data != null) {
            workout = data.getParcelable("workout");
            loadWorkoutValues();
        }
    }

    private void loadWorkoutValues(){
        hangTimePicker.setValue(workout.getHangTime());
        restTimePicker.setValue(workout.getRestTime());
        repsPicker.setValue(workout.getNumReps());
        recoverPicker.setValue(workout.getRecoverTime());
        setsPicker.setValue(workout.getNumSets());
    }

    public void initViews(){
        rootView = findViewById(R.id.root_new_basic_workout);

        adView = findViewById(R.id.admob_layout);
        hangTimePicker = findViewById(R.id.hangTimeNumberPicker);
        restTimePicker = findViewById(R.id.restTimeNumberPicker);
        repsPicker = findViewById(R.id.repsNumberPicker);
        recoverPicker = findViewById(R.id.recoverTimeNumberPicker);
        setsPicker = findViewById(R.id.setsNumberPicker);
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

        new WarmUpWarningDialog(this, i).show(); // Creates and shows the warm up warning (if required)
    }

    public void saveWorkout(View v) {
        // Stop if we can't assign the values correctly
        if (!assignValues()) {
            return;
        }

        new SaveWorkoutDialog(this, workout).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpgradeManager.checkUserPurchases(this);
        if(MobileAdsHelper.userHasUpgraded && adView != null) {
            adView.hide();
        }
    }
}
