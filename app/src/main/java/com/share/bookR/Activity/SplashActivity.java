package com.share.bookR.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.share.bookR.Constants;
import com.share.bookR.MainActivity;
import com.share.bookR.Authentication.MobileAthuntication;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                if (sharedPreferences.getBoolean(Constants.FIRST_OPEN,true))
                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                else if (FirebaseAuth.getInstance().getCurrentUser()!=null)
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, MobileAthuntication.class));
                finish();
            }
        }, 1000);
    }
}

