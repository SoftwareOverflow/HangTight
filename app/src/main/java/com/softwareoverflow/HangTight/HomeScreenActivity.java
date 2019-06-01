package com.softwareoverflow.HangTight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        ImageView background = findViewById(R.id.background);


        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        if (settings.getInt("imageBackground", 1) == 1){
            background.setImageResource(R.drawable.bg_female);
        } else{
            background.setImageResource(R.drawable.bg_male);
        }
    }

    public void newBasicWorkout(View v){
        Intent i = new Intent(this, NewBasicWorkout.class);
        startActivity(i);
    }

    public void loadWorkouts(View v){
        Intent i = new Intent(this, LoadSavedWorkouts.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_bar_settings:
                Intent settingsIntent = new Intent(this, SettingsPage.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_bar_help:
                Intent helpIntent = new Intent(this, HelpPage.class);
                startActivity(helpIntent);
                return true;
        }
                return false;
    }

    @Override
    protected void onResume() {
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        ImageView background = findViewById(R.id.background);

        if (settings.getInt("backgroundImage", 1) == 1){
            background.setImageResource(R.drawable.bg_female);
        } else{
            background.setImageResource(R.drawable.bg_male);
        }
        super.onResume();
    }
}
