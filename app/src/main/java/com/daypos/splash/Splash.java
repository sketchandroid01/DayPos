package com.daypos.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.daypos.R;
import com.daypos.container.Container;
import com.daypos.login.Login;
import com.daypos.utils.Preferense;

public class Splash extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000;

    private Preferense preferense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferense = new Preferense(this);

        threadTogo();
    }



    public void threadTogo(){

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                if (preferense.isLogin()){

                    Intent intent = new Intent(Splash.this, Container.class);
                    startActivity(intent);
                    finish();

                }else {

                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    finish();
                }

            }

        }, SPLASH_DISPLAY_LENGTH);

    }


}
