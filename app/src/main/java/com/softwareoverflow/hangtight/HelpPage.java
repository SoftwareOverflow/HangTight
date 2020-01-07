package com.softwareoverflow.hangtight;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HelpPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);
        getSupportActionBar().hide();
    }

}
