package com.example.adressbook.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.adressbook.R;

public class TooltipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tooltip);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
}