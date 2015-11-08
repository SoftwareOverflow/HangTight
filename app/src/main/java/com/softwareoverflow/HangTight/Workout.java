package com.softwareoverflow.HangTight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class Workout extends AppCompatActivity {

    private int[] dataArray = new int[5];
    private CountDownTimer hangTimer, restTimer, recoveryTimer, readyTimer, resumeTimer;
    private int currentRep = 1, currentSet = 1, totalTime, timeLeft;
    private TextView timeTextView, title, remainingTimeTV;
    private int totalSets, totalReps, progress = 0;
    private Boolean mute = false, vibrate = true;
    private Boolean pause = false, resumeTimerRunning = false;
    private int beepTone;
    private String whichTimer;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        timeTextView = (TextView) findViewById(R.id.timerTextView);
        title = (TextView) findViewById(R.id.titleTextView);

        setupWorkout(); //unpacks all relevant data and starts the workout
    }

    private void setupWorkout() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //region Getting Workout Data (durations)
        Bundle data = getIntent().getExtras();
        dataArray = data.getIntArray("dataArray"); //{hang, rest, reps, sets, recover}

        assert dataArray != null;
        totalSets = dataArray[3];
        totalReps = dataArray[2];

        if (dataArray.length == 6)
            totalTime = dataArray[5]; //Total time saved in DB - sent with pre-saved workouts
        else { //If workout not saved manually calculate total time of workout
            totalTime = dataArray[0] * dataArray[2];
            totalTime += dataArray[1] * (dataArray[2] - 1);
            totalTime += dataArray[4];
            totalTime *= dataArray[3];
            totalTime -= dataArray[4];
        }

        TextView totalTimeTV = (TextView) findViewById(R.id.totalTimeTV);
        totalTimeTV.setText(String.format("%02d", totalTime / 60) + ":" + String.format("%02d", totalTime % 60));
        timeLeft = totalTime * 1000; //Time left in millis

        for (int i = 0; i < dataArray.length; i++) dataArray[i] *= 1000; //converting to millis (works fine)
        //endregion

        //region setting up progressBar and getting sharedPreferences
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setMax(dataArray[0]);
        progressBar.setProgress(progress);
        Animation rot = new RotateAnimation(0.0f, 90.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rot.setFillAfter(true);
        progressBar.startAnimation(rot);

        SharedPreferences settings = this.getSharedPreferences("settings", MODE_PRIVATE);
        mute = !settings.getBoolean("sound", false);
        if (mute){
            ImageButton muteButton = (ImageButton) findViewById(R.id.muteButton);
            muteButton.setImageResource(R.drawable.muted);
        }
        beepTone = settings.getInt("beepTone", 0);
        vibrate = settings.getBoolean("vibrate", true);
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        Timers(settings.getInt("timer", 5) * 1000, v); //creates the timers
        updateRepAndSet();

        title.setText("Get Ready!");
        title.setTextColor(getResources().getColor(R.color.DarkOrange));
        whichTimer = "ready";
        //endregion

        readyTimer.start(); //starts chain reaction of timers
    }

    private void workoutComplete() {
        findViewById(R.id.workoutLayout).setVisibility(View.GONE);
        findViewById(R.id.endWorkoutLayout).setVisibility(View.VISIBLE);
        TextView tv = (TextView) findViewById(R.id.endWorkoutTV);
        tv.setTextColor(getResources().getColor(R.color.Green));

        Button homeButton = (Button) findViewById(R.id.homeButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
                finish();
                whichTimer().cancel();
                startActivity(i);
            }
        });

    }

    private void Timers(int countdownTime, final Vibrator v) {
        Map<Integer, Integer> soundMap = new HashMap<>();
        soundMap.put(0, R.raw.beep);
        soundMap.put(1, R.raw.blooper);
        soundMap.put(2, R.raw.censor);
        soundMap.put(3, R.raw.ding);
        soundMap.put(4,R.raw.ring);

        final MediaPlayer beep = MediaPlayer.create(getApplicationContext(), soundMap.get(beepTone));
        remainingTimeTV = (TextView) findViewById(R.id.remainingTimeTV);
        remainingTimeTV.setText(String.format("%02d", totalTime / 60) + ":" + String.format("%02d", totalTime % 60));


        hangTimer = new CountDownTimer(dataArray[0], 100) {

            @Override
            public void onTick(long millisUntilFinished) {updateScreen(millisUntilFinished, true); }

            @Override
            public void onFinish() {
                progress = dataArray[0];
                progressBar.setProgress(progress);

                timeLeft = Math.round(timeLeft/1000f)*1000; // round to nearest second
                remainingTimeTV.setText(String.format("%02d:%02d", Math.round((float) timeLeft / 1000) / 60, Math.round((float) timeLeft / 1000) % 60));

                currentRep +=1;

                if (!mute) beep.start();
                if (vibrate) v.vibrate(200);

                if (currentRep <= totalReps && currentSet <= totalSets){
                    title.setText("Rest");
                    title.setTextColor(getResources().getColor(R.color.DarkOrange));
                    whichTimer = "rest";
                    timeTextView.setText("" + dataArray[1] / 1000);

                    progressBar.setMax(dataArray[1]);
                    progress = dataArray[1];

                    restTimer.start();
                }
                else if (currentSet < totalSets){
                    currentRep = 1;
                    currentSet += 1;
                    title.setTextColor(getResources().getColor(R.color.DarkOrange));
                    whichTimer = "recover";
                    title.setText("Recover");
                    timeTextView.setText("" + dataArray[4] / 1000);

                    progressBar.setMax(dataArray[4]);
                    progress = dataArray[4];

                    recoveryTimer.start();
                }

                else{
                    progressBar.setVisibility(View.GONE);
                    workoutComplete();
                }
            }
        };
        restTimer = new CountDownTimer(dataArray[1], 100) {

            @Override
            public void onTick(long millisUntilFinished) { updateScreen(millisUntilFinished, false); }

            @Override
            public void onFinish() {
                if (!mute) beep.start();
                if (vibrate) v.vibrate(200);

                timeLeft = Math.round(timeLeft/1000f) * 1000; //round to nearest second
                remainingTimeTV.setText(String.format("%02d:%02d", Math.round((float) timeLeft / 1000) / 60, Math.round((float) timeLeft / 1000) % 60));

                title.setTextColor(getResources().getColor(R.color.Green));
                title.setText("Hang");
                timeTextView.setText("0");
                whichTimer="hang";
                timeTextView.setText("" + dataArray[0] / 1000);

                progress = 0;
                progressBar.setProgress(progress);
                progressBar.setMax(dataArray[0]);

                updateRepAndSet();
                hangTimer.start();
            }
        };
        recoveryTimer = new CountDownTimer(dataArray[4], 100) {
            @Override
            public void onTick(long millisUntilFinished) { updateScreen(millisUntilFinished, false);}

            @Override
            public void onFinish() {
                if (!mute) beep.start();
                if (vibrate) v.vibrate(200);

                progress = 0;
                progressBar.setProgress(progress);
                progressBar.setMax(dataArray[0]);

                timeLeft = Math.round(timeLeft / 1000f) * 1000;
                remainingTimeTV.setText(String.format("%02d:%02d", Math.round((float)timeLeft/1000)/60, Math.round((float)timeLeft/1000)%60));

                timeTextView.setText("0");
                title.setText("Hang");
                title.setTextColor(getResources().getColor(R.color.Green));
                whichTimer = "hang";
                timeTextView.setText("" + dataArray[0] / 1000);

                updateRepAndSet();
                hangTimer.start();
            }
        };
        readyTimer = new CountDownTimer(countdownTime, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeTextView.setText(Integer.toString(safeLongToInt(millisUntilFinished + 1000) / 1000));
            }

            @Override
            public void onFinish() {
                if (!mute) beep.start();
                if (vibrate) v.vibrate(200);

                title.setTextColor(getResources().getColor(R.color.Green));
                timeTextView.setText("0");
                title.setText("Hang");
                whichTimer = "hang";
                timeTextView.setText("" + dataArray[0]/1000);

                hangTimer.start();
            }
        };
    } //creates all timers

    private void updateRepAndSet(){
        TextView repNum = (TextView) findViewById(R.id.repsCounterTextView);
        TextView setNum = (TextView) findViewById(R.id.setCounterTextView);

        setNum.setText(String.valueOf(currentSet) + "/" + String.valueOf(totalSets));
        repNum.setText(String.valueOf(currentRep) + "/" + String.valueOf(totalReps));
    }

    //region settings button methods
    public void setMute(View v){
        ImageButton muteButton = (ImageButton) findViewById(R.id.muteButton);
        mute = (!mute);
        if (mute) { muteButton.setImageResource(R.drawable.muted); }
        else{ muteButton.setImageResource(R.drawable.unmuted); }
    } //deals with mute/un mute button pressed
    public void setPause(View v){
        pause = (!pause);
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);

        if (pause) {
            pauseButton.setImageResource(R.drawable.resume);
            if (resumeTimerRunning){
                resumeTimer.cancel();
                resumeTimerRunning = false;
            }
            else whichTimer().cancel();
        }
        else{
            pauseButton.setImageResource(R.drawable.pause);
            int timeToFinish = Integer.parseInt(timeTextView.getText().toString());

            resumeTimer = new CountDownTimer(timeToFinish*1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (whichTimer.equals("hang")){updateScreen(millisUntilFinished, true);}
                    else if (!whichTimer.equals("ready")){updateScreen(millisUntilFinished, false);}
                    else{updateScreen(millisUntilFinished, false);}
                }

                @Override
                public void onFinish() {
                    resumeTimerRunning = false;
                    whichTimer().onFinish();
                    timeLeft = Math.round(timeLeft / 1000f) * 1000; //round to nearest second
                    remainingTimeTV.setText(String.format("%02d:%02d", Math.round((float) timeLeft / 1000) / 60, Math.round((float) timeLeft / 1000) % 60));
                }
            }.start();
            resumeTimerRunning = true;
        }
    } // pause/resume button and timer/progressbar
    public void skip(View v){
        if (!whichTimer.equals("ready")) {
            int timeToFinish = Integer.parseInt(timeTextView.getText().toString());
            timeLeft = Math.round(timeLeft/1000f) - timeToFinish;
            timeLeft*=1000;
        }

        if (resumeTimerRunning){
            resumeTimer.cancel();
            resumeTimer.onFinish();
        }
        else{
            whichTimer().cancel();
            whichTimer().onFinish();
        }

        if (pause) whichTimer().cancel();
    } //skips current exercise and goes onto next one
    //endregion settings button methods

    public void updateScreen(long millisUntilFinished, boolean incProgress){
        if (!whichTimer.equals("ready")) {
            if (incProgress) progress += 100;
            else progress -= 100;
            progressBar.setProgress(progress);

            timeLeft -= 100;
            remainingTimeTV.setText(String.format("%02d:%02d", Math.round((float) timeLeft / 1000) / 60, Math.round((float) timeLeft / 1000) % 60));
        }
        timeTextView.setText(Integer.toString(Math.round(safeLongToInt(millisUntilFinished+500) / 1000)));
    }

    public CountDownTimer whichTimer() {

            switch (whichTimer) {
                case "hang":
                    return hangTimer;
                case "rest":
                    return restTimer;
                case "recover":
                    return recoveryTimer;
                case "ready":
                    return readyTimer;
                default:
                    return null;
            }
    } //returns current active timer or null if none active

    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    } //safely convert long to int


    private long firstBackPress;
    private static final int TIME_INTERVAL = 2000;
    @Override
    public void onBackPressed() {
        Toast toast = Toast.makeText(this, "Click BACK again to exit", Toast.LENGTH_SHORT);

        if (firstBackPress + TIME_INTERVAL > System.currentTimeMillis()){
            toast.cancel();
            whichTimer().cancel();
            Intent i = new Intent(this, HomeScreenActivity.class);
            finish();
            startActivity(i);

            return;
        }
        else toast.show();

        firstBackPress = System.currentTimeMillis();
    } //press back twice within TIME_INTERVAL to avoid accidental exiting workout


}
