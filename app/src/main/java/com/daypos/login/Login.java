package com.daypos.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daypos.R;
import com.daypos.container.Container;
import com.daypos.registration.Registration;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

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
                finish();

               // checkValidate();

                break;

            case R.id.btn_register:

                intent = new Intent(Login.this, Registration.class);
                startActivity(intent);

                break;


        }

    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validateEmail() {
        String email = edt_username.getText().toString().trim();

        if (email.isEmpty()) {
            Toasty.info(getApplicationContext(),
                    "Enter your email id",
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_username);
            return false;
        }

        if (!isValidEmail(email)){
            Toasty.info(getApplicationContext(),
                    "Enter your valid email id",
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_username);
            return false;
        }

        return true;
    }



    private void checkValidate(){

        if (!validateEmail()){
            return;
        }

        if (edt_password.getText().toString().isEmpty()){
            Toasty.info(getApplicationContext(),
                    "Enter password",
                    Toast.LENGTH_SHORT, true).show();
            return;
        }


        String email = "admin@daypos.com";
        String pass = "123456";

        if (edt_username.getText().toString().equals(email)

                && edt_password.getText().toString().equals(pass)){


            Intent intent = new Intent(Login.this, Container.class);
            startActivity(intent);
            finish();

        }else {

            Toasty.error(getApplicationContext(),
                    "Invalid credential",
                    Toast.LENGTH_SHORT, true).show();
        }



    }
}
