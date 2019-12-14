package com.softwareoverflow.HangTight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.softwareoverflow.HangTight.helper.SharedPreferenceHelper;
import com.softwareoverflow.HangTight.helper.StringHelper;
import com.softwareoverflow.HangTight.helper.WorkoutHelper.WorkoutSection;
import com.softwareoverflow.HangTight.workout.IWorkoutTimerObserver;
import com.softwareoverflow.HangTight.workout.Workout;
import com.softwareoverflow.HangTight.workout.WorkoutTimer;

import java.util.Locale;

public class ActivityWorkout extends AppCompatActivity implements IWorkoutTimerObserver {

    private Workout workout;
    private int prepTime = 0;

    private WorkoutTimer timer;
    private TextView timeTextView, title, remainingTimeTV, currentRepTV, currentSetTV;
    private ImageButton pauseButton, soundButton;

    private boolean isPaused, isMuted, isVibrateOn;

    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Try and load the Workout object first. If it doesn't exist we can't do the activity!
        Bundle data = getIntent().getExtras();
        if (data != null) {
            workout =  data.getParcelable("workout");
        }
        else {
            throw new IllegalArgumentException("Unable to load workout. No Extras passed to activity.");
        }

        setupViews();
        loadSharedPrefs();

        if(workout != null) {
            String workoutDuration = StringHelper.minuteSecondTimeFormat(workout.getDuration());
            ((TextView) findViewById(R.id.totalTimeTV)).setText(workoutDuration);
            remainingTimeTV.setText(workoutDuration);

            timer = new WorkoutTimer(workout, prepTime);
            timer.setObserver(this);
            timer.start();
        } else {
            throw new IllegalArgumentException("Unable to load workout");
        }
    }

    private void setupViews(){
        timeTextView = findViewById(R.id.timerTextView);
        title = findViewById(R.id.titleTextView);
        remainingTimeTV = findViewById(R.id.remainingTimeTV);
        currentRepTV = findViewById(R.id.repsCounterTextView);
        currentSetTV = findViewById(R.id.setCounterTextView);

        pauseButton = findViewById(R.id.pauseButton);
        soundButton = findViewById(R.id.muteButton);

        progressBar = findViewById(R.id.progressbar);
        Animation rot = new RotateAnimation(0.0f, 90.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rot.setFillAfter(true);
        progressBar.startAnimation(rot);
    }

    private void loadSharedPrefs(){
        SharedPreferences settings = this.getSharedPreferences("settings", MODE_PRIVATE);

        Integer soundId = SharedPreferenceHelper.getSavedSound(settings);
        if(soundId != null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(), soundId);
        } else {
            isMuted = true;
            ImageButton muteButton = findViewById(R.id.muteButton);
            muteButton.setImageResource(R.drawable.muted);
        }

        isVibrateOn = settings.getBoolean("vibrate", true);
        if(isVibrateOn)
            vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        prepTime = settings.getInt("timer", 10);
    }

    @Override
    public void onTick(long millisUntilFinished, long millisLeftBeforeChange, int tickInterval) {
        WorkoutSection currentSection = timer.getCurrentSection();
        if(currentSection == WorkoutSection.HANG){
            progressBar.setProgress(progressBar.getMax() - (int) millisLeftBeforeChange);
        } else if (currentSection == WorkoutSection.REST || currentSection == WorkoutSection.RECOVER){
            progressBar.setProgress((int) millisLeftBeforeChange);
        }

        if(currentSection != WorkoutSection.PREPARE){
            int secondsRemaining = (int) Math.ceil(millisUntilFinished / 1000.0);
            remainingTimeTV.setText(StringHelper.minuteSecondTimeFormat(secondsRemaining));
        }
        timeTextView.setText(String.format(Locale.getDefault(), "%d", (int) Math.ceil(millisLeftBeforeChange / 1000.0)));
    }

    @Override
    public void onPrepareStart() {
        // TODO - did it use the progress bar for prepare?
        title.setText(WorkoutSection.PREPARE.getNameResourceId());
    }

    @Override
    public void onHangStart() {
        updateWorkoutUI(R.color.Green, WorkoutSection.HANG.getNameResourceId(),
                workout.getHangTime(), true);
    }

    @Override
    public void onRestStart() {
        updateWorkoutUI(R.color.DarkOrange, WorkoutSection.REST.getNameResourceId(),
                workout.getRestTime(), false);
    }

    @Override
    public void onRecoverStart() {
        updateWorkoutUI(R.color.DarkOrange, WorkoutSection.RECOVER.getNameResourceId(),
                workout.getRecoverTime(), false);
    }

    private void updateWorkoutUI(int colorId, int workoutSectionResId, int sectionTime, boolean setProgressToZero){
        title.setTextColor(getResources().getColor(colorId));
        title.setText(workoutSectionResId);

        setProgress(sectionTime, setProgressToZero);
        playSounds();
    }

    private void setProgress(int numSeconds, boolean setToZero){
        progressBar.setMax(numSeconds * 1000);
        progressBar.setProgress(setToZero ? 0 : progressBar.getMax());
    }

    @Override
    public void onRepComplete(int nextRep) {
        if (nextRep > workout.getNumReps())
            nextRep = workout.getNumReps();

        updateRep(nextRep);
    }

    @Override
    public void onSetComplete(int nextSet) {
        updateRep(1);
        updateSet(nextSet);
    }

    @Override
    public void onTimerComplete() {
        // There is not rest time on the final rep, so update to max on completion
        updateRep(workout.getNumReps());
    }

    private void updateRep(int repNum){
        currentRepTV.setText(String.format(Locale.getDefault(), "%d/%d", repNum, workout.getNumReps()));
    }

    private void updateSet(int setNum){
        currentSetTV.setText(String.format(Locale.getDefault(), "%d/%d", setNum, workout.getNumSets()));
    }

    public void skip(View v){
        timer.skip();
    }

    public void togglePause(View v){
        isPaused = (!isPaused);

        if (isPaused) {
            pauseButton.setImageResource(R.drawable.resume);
            timer.pause();
        } else {
            pauseButton.setImageResource(R.drawable.pause);
            timer.resume();
        }
    }

    public void toggleMute(View v){
        isMuted = !isMuted;

        if(isMuted){
            soundButton.setImageResource(R.drawable.muted);
        } else {
            soundButton.setImageResource(R.drawable.unmuted);
        }
    }

    private void playSounds(){
        if(isPaused)
            return;

        if(!isMuted && mediaPlayer != null)
            mediaPlayer.start();

        if(isVibrateOn && vibrator != null)
            vibrator.vibrate(200);
    }

    private long firstBackPress;
    private static final int TIME_INTERVAL = 2000;
    //press back twice within TIME_INTERVAL to avoid accidentally exiting workout
    @Override
    public void onBackPressed() {
        Toast toast = Toast.makeText(this, "Click BACK again to exit", Toast.LENGTH_SHORT);

        if (firstBackPress + TIME_INTERVAL > System.currentTimeMillis()){
            toast.cancel();
            timer.cancel();
            Intent i = new Intent(this, ActivityHomeScreen.class);
            finish();
            startActivity(i);

            return;
        }
        else toast.show();

        firstBackPress = System.currentTimeMillis();
    }
}
