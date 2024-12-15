package com.ismail.note.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.ismail.note.R;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 5000; // 5000 milliseconds = 5 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    // Using a Handler to delay the transition to the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity after the delay
                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                // Finish the splash activity so the user can't return to it
                finish();
            }
        }, SPLASH_DELAY);
    }
}