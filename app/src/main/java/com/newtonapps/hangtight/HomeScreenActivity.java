package com.newtonapps.hangtight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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


}
