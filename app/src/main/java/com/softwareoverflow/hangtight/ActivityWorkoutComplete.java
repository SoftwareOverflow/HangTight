package com.softwareoverflow.hangtight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.softwareoverflow.hangtight.helper.MobileAdsHelper;
import com.softwareoverflow.hangtight.helper.StringHelper;
import com.softwareoverflow.hangtight.helper.UpgradeManager;
import com.softwareoverflow.hangtight.ui.dialog.SaveWorkoutDialog;
import com.softwareoverflow.hangtight.workout.Workout;

public class ActivityWorkoutComplete extends AppCompatActivity {

    private Workout workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_complete);


        // Try and load the Workout object first. If it doesn't exist we can't do the activity!
        Bundle data = getIntent().getExtras();
        if (data != null) {
            workout =  data.getParcelable("workout");
        }
        else {
            throw new IllegalArgumentException("Unable to load workout. No Workout object passed to activity.");
        }

        if(MobileAdsHelper.userHasUpgraded){
            findViewById(R.id.workoutComplete_button_upgrade).setVisibility(View.GONE);
        }

        logToFirebase();
    }

    private void logToFirebase(){
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString("workout_duration", StringHelper.minuteSecondTimeFormat(workout.getDuration()));
        firebaseAnalytics.logEvent("workout_completed", bundle);
    }

    public void saveWorkout(View v){
        new SaveWorkoutDialog(this, workout).show();
    }

    public void upgradeToPro(View v){
        UpgradeManager.upgrade(this);
    }

    public void goHome(View v){
        Intent i = new Intent(this, ActivityHomeScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        goHome(null);
    }
}
