package com.softwareoverflow.HangTight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NewBasicWorkout extends AppCompatActivity {

    private EditText hangTimeEditText, restTimeEditText, repsEditText, setsEditText, recoveryEditText, title, description;
    private int valuesArray[] = new int[5]; //[hang, rest, reps, sets, recovery]
    private boolean workoutSaved = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_basic_workout);

        initViews();

        Bundle data = getIntent().getExtras();
        if (data!=null) valuesArray = data.getIntArray("dataArray"); //{hang, rest, reps, sets, recover}

        assignValues();
    }

    public void initViews(){
        LinearLayout rootView = (LinearLayout) findViewById(R.id.basicMainScreen);

        hangTimeEditText = (EditText) rootView.findViewById(R.id.hangTimeTextView);
        restTimeEditText = (EditText) rootView.findViewById(R.id.restTimeTextView);
        repsEditText = (EditText) rootView.findViewById(R.id.repsTimeTextView);
        setsEditText = (EditText) rootView.findViewById(R.id.setsTimeTextView);
        recoveryEditText = (EditText) rootView.findViewById(R.id.recoveryTimeTextView);

        title = (EditText) findViewById(R.id.titleEditText);
        description = (EditText) findViewById(R.id.descriptionEditText);

    } //creates all views for referencing later

    private void assignValues(){
        valuesArray[0] = Integer.parseInt(hangTimeEditText.getText().toString());
        valuesArray[1] = Integer.parseInt(restTimeEditText.getText().toString());
        valuesArray[2] = Integer.parseInt(repsEditText.getText().toString());
        valuesArray[3] = Integer.parseInt(setsEditText.getText().toString());
        valuesArray[4] = Integer.parseInt(recoveryEditText.getText().toString());

    } // gets values from textViews and saves to valuesArray[]

    // START +/- BUTTON METHODS
    public void hangIncrement(View v){
        valuesArray[0] = Integer.parseInt(hangTimeEditText.getText().toString());

        valuesArray[0] += 1;
        hangTimeEditText.setText(Integer.toString(valuesArray[0]));
    }
    public void hangDecrement(View v){
        valuesArray[0] = Integer.parseInt(hangTimeEditText.getText().toString());
        if (isNumValid(valuesArray[0] -1)) valuesArray[0] -= 1;
        hangTimeEditText.setText(Integer.toString(valuesArray[0]));

    }
    public void restIncrement(View v){
        valuesArray[1] = Integer.parseInt(restTimeEditText.getText().toString());

        valuesArray[1] += 1;
        restTimeEditText.setText(Integer.toString(valuesArray[1]));
    }
    public void restDecrement(View v){
        valuesArray[1] = Integer.parseInt(restTimeEditText.getText().toString());
        if (isNumValid(valuesArray[1] -1)) valuesArray[1] -= 1;
        restTimeEditText.setText(Integer.toString(valuesArray[1]));

    }
    public void repsIncrement(View v){
        valuesArray[2] = Integer.parseInt(repsEditText.getText().toString());

        valuesArray[2] += 1;
        repsEditText.setText(Integer.toString(valuesArray[2]));
    }
    public void repsDecrement(View v){
        valuesArray[2] = Integer.parseInt(repsEditText.getText().toString());
        if (isNumValid(valuesArray[2] -1)) valuesArray[2] -= 1;
        repsEditText.setText(Integer.toString(valuesArray[2]));
    }
    public void setsIncrement(View v){
        valuesArray[3] = Integer.parseInt(setsEditText.getText().toString());

        valuesArray[3] += 1;
        setsEditText.setText(Integer.toString(valuesArray[3]));
    }
    public void setsDecrement(View v){
        valuesArray[3] = Integer.parseInt(setsEditText.getText().toString());
        if (isNumValid(valuesArray[3] -1)) valuesArray[3] -= 1;
        setsEditText.setText(Integer.toString(valuesArray[3]));
    }
    public void recoveryIncrement(View v){
        valuesArray[4] = Integer.parseInt(recoveryEditText.getText().toString());

        valuesArray[4] += 1;
        recoveryEditText.setText(Integer.toString(valuesArray[4]));
    }
    public void recoveryDecrement(View v){
        valuesArray[4] = Integer.parseInt(recoveryEditText.getText().toString());
        if (isNumValid(valuesArray[4] -1)) valuesArray[4] -= 1;
        recoveryEditText.setText(Integer.toString(valuesArray[4]));
    }

    private boolean isNumValid(int i) {return i>0;}
    //END +/- BUTTON METHODS

    public void startWorkout(View v){
        assignValues();

        final Intent i = new Intent(NewBasicWorkout.this, Workout.class);
        i.putExtra("dataArray", valuesArray);
        i.putExtra("workoutSaved", workoutSaved);

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

        Boolean showWarning = settings.getBoolean("showWarmUp", true);

        if (showWarning){
            new AlertDialog.Builder(NewBasicWorkout.this)
                    .setTitle("Have You Warmed Up?")
                    .setMessage("Ensure you are thoroughly warmed up before beginning any" +
                            " workout. Failure to do so could result in injury.\n\nIf you feel" +
                            " any pain during the workout, discontinue immediately.")
                    .setView(checkBoxView)
                    .setPositiveButton("Start Workout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(i);

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
            startActivity(i);
        }

        title.setVisibility(View.GONE);
        description.setVisibility(View.GONE);

    } //method called when start button pressed

    public void saveWorkout(View v){
        assignValues();

        final LinearLayout workoutExtraInfoScreen = (LinearLayout) findViewById(R.id.overlayScreen);
        final LinearLayout workoutScreen = (LinearLayout) findViewById(R.id.basicMainScreen);

        workoutScreen.setVisibility(View.GONE);
        workoutExtraInfoScreen.setVisibility(View.VISIBLE);

        final String workoutData[] = new String[7];


        for (int i=0; i<valuesArray.length; i++){
            workoutData[i+2] = Integer.toString(valuesArray[i]);
        }

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
                    MyDBHandler dbHandler = new MyDBHandler(NewBasicWorkout.this, null);

                    workoutData[0] = titleString;
                    workoutData[1] = descriptionString;

                    if (dbHandler.addWorkout(workoutData, false)) Toast.makeText(getApplicationContext(), "Workout Saved!", Toast.LENGTH_SHORT).show();
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
        });



    }

}
