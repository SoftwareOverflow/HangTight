package com.softwareoverflow.HangTight;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class SettingsPage extends AppCompatActivity {

    private Switch toggleSound, toggleVibrate, toggleWarmUp;
    private SeekBar countdownSeekBar;
    private Spinner soundSpinner, backgroundSpinner;
    private static int backgroundImage, countdownValue, sound;
    TextView countdownTV;
    private boolean playSound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        getSupportActionBar().hide();

        final LinearLayout chooseSoundLayout = (LinearLayout) findViewById(R.id.chooseSoundLayout);
        toggleSound = (Switch) findViewById(R.id.soundSettingSwitch);
        toggleVibrate = (Switch) findViewById(R.id.vibrateSettingSwitch);
        toggleWarmUp = (Switch) findViewById(R.id.warmUpWarning);
        countdownTV = (TextView) findViewById(R.id.countdownTV);
        countdownSeekBar = (SeekBar) findViewById(R.id.countdownSeekBar);
        countdownSeekBar.setMax(10);


        toggleSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) chooseSoundLayout.setVisibility(View.VISIBLE);
                else chooseSoundLayout.setVisibility(View.GONE);

            }
        });

        createSpinners();

        SharedPreferences settings = this.getSharedPreferences("settings", MODE_PRIVATE);
        Boolean isSoundOn = settings.getBoolean("sound", true);
        Boolean isVibrateOn = settings.getBoolean("vibrate", false);
        Boolean showWarmUpWarning = settings.getBoolean("showWarmUp", true);
        backgroundImage = settings.getInt("backgroundImage", 1);
        countdownValue = settings.getInt("timer", 5);
        sound = settings.getInt("beepTone", 0);

        countdownTV.setText("" + countdownValue);
        countdownSeekBar.setProgress(countdownValue);
        toggleSound.setChecked(isSoundOn);
        toggleVibrate.setChecked(isVibrateOn);
        toggleWarmUp.setChecked(showWarmUpWarning);
        soundSpinner.setSelection(sound);
        backgroundSpinner.setSelection(backgroundImage);

        if (!isSoundOn) chooseSoundLayout.setVisibility(View.GONE);

        countdownSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                countdownValue = seekBar.getProgress();
                countdownTV.setText("" + countdownValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    private void createSpinners() {

        //region BackgroundSpinner setup
        backgroundSpinner = (Spinner) findViewById(R.id.backgroundSpinner);
        final String[] backgroundChoices = {"Male", "Female"};
        ArrayAdapter<String> backgroundAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, backgroundChoices);
        backgroundSpinner.setAdapter(backgroundAdapter);
        backgroundSpinner.setSelection(backgroundImage);

        backgroundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                backgroundImage = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //endregion

        //region SoundSpinner setup
        final Map<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
        soundMap.put(0, R.raw.beep);
        soundMap.put(1, R.raw.blooper);
        soundMap.put(2, R.raw.censor);
        soundMap.put(3, R.raw.ding);
        soundMap.put(4,R.raw.ring);

        soundSpinner = (Spinner) findViewById(R.id.soundSpinner);
        final String[] soundChoices = {"Beep","Blooper", "Censor", "Ding", "Ring"};
        ArrayAdapter<String> soundAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, soundChoices);
        soundSpinner.setAdapter(soundAdapter);
        soundSpinner.setSelection(sound);

        soundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sound = position;
                if (playSound) MediaPlayer.create(SettingsPage.this, soundMap.get(position)).start();

                playSound = true;

            } // selects item and plays sound

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //endregion
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onStop();
        finish();
    }

    public void saveSettings(View v){

        SharedPreferences settings = getApplication().getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("backgroundImage", backgroundImage);
        editor.putBoolean("sound", toggleSound.isChecked());
        editor.putBoolean("vibrate", toggleVibrate.isChecked());
        editor.putInt("timer", countdownValue);
        editor.putInt("beepTone", sound);
        editor.putBoolean("showWarmUp", toggleWarmUp.isChecked());
        editor.apply();


        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();

        finish();
    }

    public void cancel(View v){
        finish();
    }
}