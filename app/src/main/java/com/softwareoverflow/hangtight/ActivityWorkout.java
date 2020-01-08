package com.softwareoverflow.hangtight;

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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.softwareoverflow.hangtight.helper.MobileAdsHelper;
import com.softwareoverflow.hangtight.helper.SharedPreferenceHelper;
import com.softwareoverflow.hangtight.helper.StringHelper;
import com.softwareoverflow.hangtight.helper.UpgradeManager;
import com.softwareoverflow.hangtight.helper.WorkoutHelper.WorkoutSection;
import com.softwareoverflow.hangtight.ui.CustomBannerAd;
import com.softwareoverflow.hangtight.workout.IWorkoutTimerObserver;
import com.softwareoverflow.hangtight.workout.Workout;
import com.softwareoverflow.hangtight.workout.WorkoutTimer;

import java.util.Locale;

public class ActivityWorkout extends AppCompatActivity implements IWorkoutTimerObserver {

    private Workout workout;
    private int prepTime = 0;

    private CustomBannerAd adView;
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
            throw new IllegalArgumentException("Unable to load workout. No Workout object passed to activity.");
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

        // Set the current rep and set to 1/<num>
        updateRep(1);
        updateSet(1);

        logToFirebase();
    }

    private void logToFirebase(){
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("workout_duration", StringHelper.minuteSecondTimeFormat(workout.getDuration()));
        analytics.logEvent("workout_started", bundle);
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

        adView = findViewById(R.id.admob_layout);
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
        title.setText(WorkoutSection.PREPARE.getNameResourceId());
    }

    @Override
    public void onHangStart() {
        updateWorkoutUI(R.color.Green, WorkoutSection.HANG.getNameResourceId(),
                workout.getHangTime(), true);
    }

    @Override
    public void onRestStart() {
        updateWorkoutUI(R.color.Orange, WorkoutSection.REST.getNameResourceId(),
                workout.getRestTime(), false);
    }

    @Override
    public void onRecoverStart() {
        updateWorkoutUI(R.color.Orange, WorkoutSection.RECOVER.getNameResourceId(),
                workout.getRecoverTime(), false);
    }

    private void updateWorkoutUI(int colorId, int workoutSectionResId, int sectionTime, boolean setProgressToZero){
        title.setTextColor(getResources().getColor(colorId, getTheme()));
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
        playSounds();

        Intent intent = new Intent(ActivityWorkout.this, ActivityWorkoutComplete.class);
        intent.putExtra("workout", workout);

        InterstitialAd ad = MobileAdsHelper.getInterstitialAd();
        if(!MobileAdsHelper.userHasUpgraded && ad != null && ad.isLoaded()){
            ad.setAdListener(new AdListener(){
                @Override
                public void onAdClosed() {
                    startActivity(intent);
                }
            });
            ad.show();
        } else {
            startActivity(intent);
        }
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

    private Snackbar snackbar;
    private long firstBackPress;
    private static final int TIME_INTERVAL = 2000;
    //press back twice within TIME_INTERVAL to avoid accidentally exiting workout
    @Override
    public void onBackPressed() {
        if(snackbar == null){
            View rootView = findViewById(R.id.activity_workout_root_view);
            snackbar = Snackbar.make(rootView, "Click BACK again to exit", Snackbar.LENGTH_SHORT);
        }

        if (firstBackPress + TIME_INTERVAL > System.currentTimeMillis()){
            snackbar.dismiss();
            timer.cancel();
            Intent i = new Intent(this, ActivityHomeScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

            finish();
            return;
        }
        else snackbar.show();

        firstBackPress = System.currentTimeMillis();
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
