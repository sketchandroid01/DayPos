package com.daypos.registration;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daypos.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class Registration extends AppCompatActivity implements
        View.OnClickListener {


    @BindView(R.id.edt_username) EditText edt_username;
    @BindView(R.id.edt_emailid) EditText edt_emailid;
    @BindView(R.id.edt_mobile) EditText edt_mobile;
    @BindView(R.id.edt_password) EditText edt_password;
    @BindView(R.id.edt_confirm_password) EditText edt_confirm_password;
    @BindView(R.id.btn_create) Button btn_create;
    @BindView(R.id.tv_goto_login) TextView tv_goto_login;
    @BindView(R.id.tv_privacy_policy) TextView tv_privacy_policy;
    @BindView(R.id.checkbox)
    CheckBox checkbox;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        OnClickViews();

    }

    private void OnClickViews(){

        tv_goto_login.setOnClickListener(this);

    }


    private boolean validateName() {
        if (edt_username.getText().toString().trim().isEmpty()) {
            Toasty.info(getApplicationContext(),
                    "Enter your name",
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_username);
            return false;
        }

        return true;
    }

    private boolean validateEmail() {
        String email = edt_emailid.getText().toString().trim();

        if (email.isEmpty()) {
            Toasty.info(getApplicationContext(),
                    "Enter your email id",
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_emailid);
            return false;
        }

        if (!isValidEmail(email)){
            Toasty.info(getApplicationContext(),
                    "Enter your valid email id",
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_emailid);
            return false;
        }

        return true;
    }

    private boolean validateMobile() {
        String mobile = edt_mobile.getText().toString().trim();

        if (mobile.isEmpty()) {
            Toasty.info(getApplicationContext(),
                    "Enter your valid email id",
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_mobile);
            return false;
        }

       /* if (mobile.length() < 10) {
            input_layout_mobile.setError(getString(R.string.err_msg_10digit_mobile));
            requestFocus(input_mobile);
            return false;
        }*/


        return true;
    }

    private boolean validatePassword() {

        String password = edt_password.getText().toString().trim();

        if (password.isEmpty()) {
            Toasty.info(getApplicationContext(),
                    getString(R.string.err_msg_password),
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_password);
            return false;
        }

        if (password.trim().length() < 6){
            Toasty.info(getApplicationContext(),
                    getString(R.string.err_msg_min_6_character_password),
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_password);
            return false;
        }

        return true;
    }

    private boolean validateConfirmPassword() {

        String password = edt_confirm_password.getText().toString().trim();

        if (password.isEmpty()) {
            Toasty.info(getApplicationContext(),
                    getString(R.string.err_msg_con_password),
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_confirm_password);
            return false;
        }

        if (!checkBothPassword()){
            return false;
        }

        return true;
    }

    private boolean checkBothPassword(){

        String password = edt_password.getText().toString().trim();
        String confirm_password = edt_confirm_password.getText().toString().trim();

        if (!password.equals(confirm_password)){
            Toasty.info(getApplicationContext(),
                    getString(R.string.err_msg_not_match),
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_confirm_password);
            return false;
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void checkValidate(){


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.tv_goto_login:
                finish();

                break;


        }

    }
}
