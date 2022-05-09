package com.softwareoverflow.hangtight;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.softwareoverflow.hangtight.helper.BillingRepo;
import com.softwareoverflow.hangtight.helper.MobileAdsHelper;

public class ActivityHomeScreen extends AppCompatActivity {

    BillingRepo billingRepo;

    //TempViewModel viewModel;

    //Observer<Boolean> observer = value -> MobileAdsHelper.userHasUpgraded = value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        billingRepo = ((HangTightApplication) this.getApplication()).getAppContainer().getBillingRepo();

        MobileAdsHelper.initialize(this);

        //viewModel = new TempViewModel(getApplicationContext());
    }

    public void newBasicWorkout(View v) {
        //List<WorkoutEntity> es = viewModel.getWorkouts();

        Intent i = new Intent(this, ActivityWorkoutCreator.class);
        startActivity(i);
    }

    public void loadWorkouts(View v) {
        Intent i = new Intent(this, ActivityLoadSavedWorkouts.class);
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
                Intent settingsIntent = new Intent(this, ActivitySettings.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_bar_help:
                Intent helpIntent = new Intent(this, HelpPage.class);
                startActivity(helpIntent);
                return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        billingRepo.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        //billingRepo.isProVersionPurchased().removeObserver(observer);
        super.onDestroy();
    }
}
