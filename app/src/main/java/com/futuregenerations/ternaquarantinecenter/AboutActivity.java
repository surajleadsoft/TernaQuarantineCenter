package com.futuregenerations.ternaquarantinecenter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }
    }
}