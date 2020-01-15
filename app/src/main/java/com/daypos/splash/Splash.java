package com.daypos.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.daypos.R;
import com.daypos.login.Login;

public class Splash extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        threadTogo();
    }



    public void threadTogo(){

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                Intent intent = new Intent(Splash.this, Login.class);
                startActivity(intent);

            }

        }, SPLASH_DISPLAY_LENGTH);

    }


}
