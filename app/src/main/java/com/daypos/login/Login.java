package com.daypos.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daypos.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity {

    @BindView(R.id.tv_forgot_password) TextView tv_forgot_password;
    @BindView(R.id.edt_username) EditText edt_username;
    @BindView(R.id.edt_password) EditText edt_password;
    @BindView(R.id.btn_login) Button btn_login;
    @BindView(R.id.btn_register) Button btn_register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


    }



}
