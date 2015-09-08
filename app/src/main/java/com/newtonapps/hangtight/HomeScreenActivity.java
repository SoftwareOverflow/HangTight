package com.newtonapps.hangtight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }


    public void newBasicWorkout(View v){
        Intent i = new Intent(this, NewBasicWorkout.class);
        startActivity(i);
    }

    public void loadWorkouts(View v){
        Intent i = new Intent(this, LoadSavedWorkouts.class);
        startActivity(i);
    }

}
