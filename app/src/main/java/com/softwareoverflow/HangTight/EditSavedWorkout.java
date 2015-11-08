package com.softwareoverflow.HangTight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditSavedWorkout extends AppCompatActivity {

    private String[] updatedData = new String[7];
    private EditText title, description, hang, rest, reps, sets, recover;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_saved_workout);

        title = (EditText) findViewById(R.id.editSavedWorkoutTitle);
        description = (EditText) findViewById(R.id.editSavedWorkoutDescription);
        hang = (EditText) findViewById(R.id.editSavedWorkoutHang);
        rest = (EditText) findViewById(R.id.editSavedWorkoutRest);
        reps = (EditText) findViewById(R.id.editSavedWorkoutReps);
        sets = (EditText) findViewById(R.id.editSavedWorkoutSets);
        recover = (EditText) findViewById(R.id.editSavedWorkoutRecover);


        Bundle data = getIntent().getExtras();
        int[] dataArray = data.getIntArray("workoutData");
        position = data.getInt("position");

        title.setText(data.getString("words").split("\\|")[0]);
        description.setText(data.getString("words").split("\\|")[1]);

        assert dataArray != null;
        hang.setText("" + dataArray[0]);
        rest.setText("" + dataArray[1]);
        reps.setText("" + dataArray[2]);
        sets.setText("" + dataArray[3]);
        recover.setText("" + dataArray[4]);
    }

    public void saveChanges(View v){
        MyDBHandler dbHandler = new MyDBHandler(this, null);

        if(!assignValues()){
            if (dbHandler.updateWorkout(position, updatedData)) Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "There was an error saving the updates", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "Title and Description cannot contain the pipe character '|'", Toast.LENGTH_SHORT).show();

        cancelSave(v);
    } //saves changes and goes back to screen for saved workouts

    public void cancelSave(View v) {
        Intent i = new Intent(this, LoadSavedWorkouts.class);
        finish();
        startActivity(i);
    } //goes back to load saved workout screen with no changes

    private boolean assignValues() {
        updatedData[0] = title.getText().toString();
        updatedData[1] = description.getText().toString();
        updatedData[2] = hang.getText().toString();
        updatedData[3] = rest.getText().toString();
        updatedData[4] = reps.getText().toString();
        updatedData[5] = sets.getText().toString();
        updatedData[6] = recover.getText().toString();

        return (updatedData[0].contains("|") || updatedData[1].contains("|"));

    } //returns true if title/desc contain invalid chars, false otherwise
}