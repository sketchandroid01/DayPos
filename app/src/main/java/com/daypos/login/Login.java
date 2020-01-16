package com.daypos.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daypos.R;
import com.daypos.container.Container;
import com.daypos.registration.Registration;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_forgot_password) TextView tv_forgot_password;
    @BindView(R.id.edt_username) EditText edt_username;
    @BindView(R.id.edt_password) EditText edt_password;
    @BindView(R.id.btn_login) Button btn_login;
    @BindView(R.id.btn_register) TextView btn_register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()){

            case R.id.btn_login:

                intent = new Intent(Login.this, Container.class);
                startActivity(intent);

                break;

            case R.id.btn_register:

                intent = new Intent(Login.this, Registration.class);
                startActivity(intent);

                break;


        }

    }
}
