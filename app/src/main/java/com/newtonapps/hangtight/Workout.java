package com.newtonapps.hangtight;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Workout extends AppCompatActivity {

    private int[] dataArray = new int[5];
    private CountDownTimer hangTimer, restTimer, recoveryTimer, readyTimer, resumeTimer;
    private int currentRep = 1, currentSet = 1, totalTime, timeLeft;
    private TextView timeTextView, title, remainingTimeTV;
    private int totalSets, totalReps, progress = 0;
    private Boolean mute = false;
    private Boolean pause = false;
    private String whichTimer;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        updateRepAndSet(); // sets current/total rep and sets
        timeTextView = (TextView) findViewById(R.id.timerTextView);
        title = (TextView) findViewById(R.id.titleTextView);

        Bundle data = getIntent().getExtras();
        dataArray = data.getIntArray("dataArray"); //{hang, rest, reps, sets, recover}

        assert dataArray != null;
        totalSets = dataArray[3];
        totalReps = dataArray[2];

        totalTime = dataArray[0] * dataArray[2];
        totalTime += dataArray[1] * (dataArray[2] - 1);
        totalTime += dataArray[4];
        totalTime *= dataArray[3];
        totalTime -= dataArray[4];

        TextView totalTimeTV = (TextView) findViewById(R.id.totalTimeTV);
        totalTimeTV.setText(String.format("%02d", totalTime / 60) + ":" + String.format("%02d", totalTime % 60));
        timeLeft = totalTime * 1000; //Time left in millis


        try {
            for (int i = 0; i < dataArray.length; i++) {
                dataArray[i] *= 1000; //converting to millis (works fine)
            }
        } catch (NullPointerException e){
            e.printStackTrace();
            Log.d("convert2milli", "Error converting to millis");
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setMax(dataArray[0]);
        progressBar.setProgress(progress);
        Animation rot = new RotateAnimation(0.0f, 90.0f, 100.0f, 100.0f);
        rot.setFillAfter(true);
        progressBar.startAnimation(rot);

        Timers(); //creates the workout
        updateRepAndSet();

        title.setText("Get Ready!");
        title.setTextColor(getResources().getColor(R.color.DarkOrange));
        whichTimer = "ready";
        readyTimer.start(); //starts chain reaction of timers!
    }

    private void workoutComplete() {
        timeTextView.setText("0");
        title.setText("YOU'RE DONE!");
    } //TODO -- add ending screen for end of Workout (+ sound?) (option to save workout/go to home screen)

    private void Timers() {
        final MediaPlayer beep = MediaPlayer.create(getApplicationContext(), R.raw.beep);
        remainingTimeTV = (TextView) findViewById(R.id.remainingTimeTV);
        remainingTimeTV.setText(String.format("%02d", totalTime / 60) + ":" + String.format("%02d", totalTime % 60));

        hangTimer = new CountDownTimer(dataArray[0], 100) {

            @Override
            public void onTick(long millisUntilFinished) { updateScreen(millisUntilFinished, true); }

            @Override
            public void onFinish() {
                progress = dataArray[0];
                progressBar.setProgress(progress);

                timeLeft = Math.round(timeLeft/1000);
                remainingTimeTV.setText(String.format("%02d:%02d", timeLeft/60, timeLeft%60));
                timeLeft*= 1000;

                currentRep +=1;

                if (!mute) {beep.start();}

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
                if (! mute) {beep.start();}

                timeLeft = Math.round(timeLeft/1000);
                remainingTimeTV.setText(String.format("%02d:%02d", timeLeft/60, timeLeft%60));
                timeLeft*=1000;

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
                if (!mute) {beep.start();}

                timeLeft = Math.round(timeLeft/1000);
                remainingTimeTV.setText(String.format("%02d:%02d", timeLeft/60, timeLeft%60));
                timeLeft*=1000;

                timeTextView.setText("0");
                title.setText("Hang");
                title.setTextColor(getResources().getColor(R.color.Green));
                whichTimer = "hang";
                timeTextView.setText("" + dataArray[0] / 1000);

                progress = 0;
                progressBar.setProgress(progress);
                progressBar.setMax(dataArray[0]);

                updateRepAndSet();
                hangTimer.start();
            }
        };
        readyTimer = new CountDownTimer(5000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeTextView.setText(Integer.toString(safeLongToInt(millisUntilFinished + 1000)/1000));
            }

            @Override
            public void onFinish() {
                if (!mute) beep.start();

                title.setTextColor(getResources().getColor(R.color.Green));
                timeTextView.setText("0");
                title.setText("Hang");
                whichTimer = "hang";
                timeTextView.setText("" + dataArray[0]/1000);

                hangTimer.start();
            }
        };
    } //creates all timers for use

    private void updateRepAndSet(){
        TextView repNum = (TextView) findViewById(R.id.repsCounterTextView);
        TextView setNum = (TextView) findViewById(R.id.setCounterTextView);

        setNum.setText(String.valueOf(currentSet) + "/" + String.valueOf(totalSets));
        repNum.setText(String.valueOf(currentRep) + "/" + String.valueOf(totalReps));
    }

    //begin settings button methods
    public void setMute(View v){
        ImageButton muteButton = (ImageButton) findViewById(R.id.muteButton);
        mute = (!mute);
        if (mute) { muteButton.setImageResource(R.drawable.muted); }
        else{ muteButton.setImageResource(R.drawable.unmuted); }
    } //deals with mute/un mute button pressed
    public void setPause(View v){
        pause = (!pause);
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);

        int timeToFinish = Integer.parseInt(timeTextView.getText().toString());

        resumeTimer = new CountDownTimer(timeToFinish*1000, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (whichTimer.equals("hang")){updateScreen(millisUntilFinished, true);}
                else if (!whichTimer.equals("ready")){updateScreen(millisUntilFinished, false); }
        }

            @Override
            public void onFinish() {
                whichTimer().onFinish();
                timeLeft = Math.round(timeLeft/1000); //round to nearest second
                remainingTimeTV.setText(String.format("%02d:%02d", timeLeft/60, timeLeft%60));
                timeLeft*=1000; //convert back to millis
            }
        };

        if (pause) {
            pauseButton.setImageResource(R.drawable.resume);
            whichTimer().cancel();
            resumeTimer.cancel();

        }
        else{
            pauseButton.setImageResource(R.drawable.pause);
            whichTimer().cancel();
            resumeTimer.cancel();
            resumeTimer.start();
        }



    } // pause/resume button and timer/progressbar
    public void skip(View v){

        if (!whichTimer.matches("ready")) {
            int timeToFinish = Integer.parseInt(timeTextView.getText().toString());
            timeLeft = Math.round(timeLeft / 1000) - timeToFinish;
        }

        whichTimer().cancel();
        whichTimer().onFinish();

        if (pause){
            whichTimer().cancel();
            resumeTimer.cancel();
        }
    } //skips current exercise and goes onto next one
    // end settings button methods

    public void updateScreen(long millisUntilFinished, boolean incProgress){
        if (incProgress) progress += 100;
        else progress -= 100;
        progressBar.setProgress(progress);

        timeLeft -= 100;
        timeLeft = Math.round(timeLeft/1000); //convert to seconds
        remainingTimeTV.setText(String.format("%02d:%02d", timeLeft/60,timeLeft%60));
        timeTextView.setText(Integer.toString(safeLongToInt(millisUntilFinished + 1000) / 1000));

        timeLeft*=1000;//convert back to millis
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
                    Log.d("whichTimer()", "No timers found in switch!");
                    return null;
            }
    } //returns current active timer or null if none active (Logs in this case)

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
            Intent i = new Intent(this, HomeScreenActivity.class);
            startActivity(i);
            finish();
            return;
        }
        else toast.show();

        firstBackPress = System.currentTimeMillis();
    }
}
