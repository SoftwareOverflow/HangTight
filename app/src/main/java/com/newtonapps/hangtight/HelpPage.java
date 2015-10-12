package com.newtonapps.hangtight;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class HelpPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);
        getSupportActionBar().hide();
    }

}
