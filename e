[1mdiff --git a/app/src/main/java/com/newtonapps/hangtight/Workout.java b/app/src/main/java/com/newtonapps/hangtight/Workout.java[m
[1mindex b10f10c..00434c7 100644[m
[1m--- a/app/src/main/java/com/newtonapps/hangtight/Workout.java[m
[1m+++ b/app/src/main/java/com/newtonapps/hangtight/Workout.java[m
[36m@@ -101,9 +101,8 @@[m [mpublic class Workout extends AppCompatActivity {[m
                 progress = dataArray[0];[m
                 progressBar.setProgress(progress);[m
 [m
[31m-                timeLeft = Math.round(timeLeft/1000);[m
[31m-                remainingTimeTV.setText(String.format("%02d:%02d", timeLeft/60, timeLeft%60));[m
[31m-                timeLeft*= 1000;[m
[32m+[m[32m                timeLeft = Math.round(timeLeft/1000f)*1000; // round to nearest second[m
[32m+[m[32m                remainingTimeTV.setText(String.format("%02d:%02d", Math.round((float)timeLeft/1000)/60, Math.round((float)timeLeft/1000)%60));[m
 [m
                 currentRep +=1;[m
 [m
[36m@@ -149,9 +148,8 @@[m [mpublic class Workout extends AppCompatActivity {[m
             public void onFinish() {[m
                 if (! mute) {beep.start();}[m
 [m
[31m-                timeLeft = Math.round(timeLeft/1000);[m
[31m-                remainingTimeTV.setText(String.format("%02d:%02d", timeLeft/60, timeLeft%60));[m
[31m-                timeLeft*=1000;[m
[32m+[m[32m                timeLeft = Math.round(timeLeft/1000f) * 1000; //round to nearest second[m
[32m+[m[32m                remainingTimeTV.setText(String.format("%02d:%02d", Math.round((float)timeLeft/1000)/60, Math.round((float)timeLeft/1000)%60));[m
 [m
                 title.setTextColor(getResources().getColor(R.color.Green));[m
                 title.setText("Hang");[m
[36m@@ -175,9 +173,8 @@[m [mpublic class Workout extends AppCompatActivity {[m
             public void onFinish() {[m
                 if (!mute) {beep.start();}[m
 [m
[31m-                timeLeft = Math.round(timeLeft/1000);[m
[31m-                remainingTimeTV.setText(String.format("%02d:%02d", timeLeft/60, timeLeft%60));[m
[31m-                timeLeft*=1000;[m
[32m+[m[32m                timeLeft = Math.round(timeLeft/1000f)*1000;[m
[32m+[m[32m                remainingTimeTV.setText(String.format("%02d:%02d", Math.round((float)timeLeft/1000)/60, Math.round((float)timeLeft/1000)%60));[m
 [m
                 timeTextView.setText("0");[m
                 title.setText("Hang");[m
[36m@@ -246,9 +243,8 @@[m [mpublic class Workout extends AppCompatActivity {[m
             @Override[m
             public void onFinish() {[m
                 whichTimer().onFinish();[m
[31m-                timeLeft = Math.round(timeLeft/1000); //round to nearest second[m
[31m-                remainingTimeTV.setText(String.format("%02d:%02d", timeLeft/60, timeLeft%60));[m
[31m-                timeLeft*=1000; //convert back to millis[m
[32m+[m[32m                timeLeft = Math.round(timeLeft/1000f) * 1000; //round to nearest second[m
[32m+[m[32m                remainingTimeTV.setText(String.format("%02d:%02d", Math.round((float)timeLeft/1000)/60, Math.round((float)timeLeft/1000)%60));[m
             }[m
         };[m
 [m
[36m@@ -272,7 +268,8 @@[m [mpublic class Workout extends AppCompatActivity {[m
 [m
         if (!whichTimer.matches("ready")) {[m
             int timeToFinish = Integer.parseInt(timeTextView.getText().toString());[m
[31m-            timeLeft = Math.round(timeLeft / 1000) - timeToFinish;[m
[32m+[m[32m            timeLeft = Math.round(timeLeft/1000f) - timeToFinish;[m
[32m+[m[32m            timeLeft*=1000;[m
         }[m
 [m
         whichTimer().cancel();[m
[36m@@ -291,11 +288,9 @@[m [mpublic class Workout extends AppCompatActivity {[m
         progressBar.setProgress(progress);[m
 [m
         timeLeft -= 100;[m
[31m-        timeLeft = Math.round(timeLeft/1000); //convert to seconds[m
[31m-        remainingTimeTV.setText(String.format("%02d:%02d", timeLeft/60,timeLeft%60));[m
[31m-        timeTextView.setText(Integer.toString(safeLongToInt(millisUntilFinished + 1000) / 1000));[m
[31m-[m
[31m-        timeLeft*=1000;//convert back to millis[m
[32m+[m[32m        remainingTimeTV.setText(String.format("%02d:%02d", Math.round((float)timeLeft/1000)/60, Math.round((float)timeLeft/1000)%60));[m
[32m+[m[32m        Log.d("time", "-100ms: " + timeLeft % 60);[m
[32m+[m[32m        timeTextView.setText(Integer.toString(Math.round(safeLongToInt(millisUntilFinished+1000) / 1000)));[m
     }[m
 [m
     public CountDownTimer whichTimer() {[m
