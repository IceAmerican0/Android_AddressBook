package com.example.adressbook.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.adressbook.R;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    Handler handler=new Handler();

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        textView = findViewById(R.id.start_loading);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText("Loading . .");
            }
        },500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText("Loading . . .");
            }
        },1000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText("Loading . . . .");
            }
        },1500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },2000);

    }

}