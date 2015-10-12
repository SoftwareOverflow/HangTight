package com.newtonapps.hangtight;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsPage extends AppCompatActivity {

    //TODO - finish settings page & help page
    //TODO - settings page: sound default ON/OFF, vibrate ON/OFF, male/female home screen background, readyTimer length?
    //TODO - help page: how to use the app, about section


    Switch toggleSound, toggleVibrate;
    private String backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        getSupportActionBar().hide();

        toggleSound = (Switch) findViewById(R.id.soundSettingSwitch);
        toggleVibrate = (Switch) findViewById(R.id.vibrateSettingSwitch);


        final String[] backgroundChoices = {"Male", "Female"};

        Spinner backgroundSpinner = (Spinner) findViewById(R.id.backgroundSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, backgroundChoices);
        backgroundSpinner.setAdapter(adapter);

        backgroundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                backgroundImage = backgroundChoices[position];
                //TODO -- create variable to use in save class
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        onStop();
        finish();
    }

    public void saveSettings(View v){

        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("backgroundImage", backgroundImage);
        editor.putBoolean("sound", toggleSound.isChecked());
        editor.putBoolean("vibrate", toggleVibrate.isChecked());
        editor.apply();
        


        //TODO -- assign imageView for home screen background
        if (backgroundImage.equals("Male")){
            //TODO -- change background to Male
        } else {} //TODO -- change background to Female


        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();

        finish();
    }

    public void cancel(View v){
        finish();
    }
}