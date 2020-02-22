package com.daypos.registration;

import android.os.Bundle;
import android.provider.Settings;
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
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class Registration extends AppCompatActivity implements
        View.OnClickListener {


    @BindView(R.id.edt_first_name) EditText edt_first_name;
    @BindView(R.id.edt_last_name) EditText edt_last_name;
    @BindView(R.id.edt_emailid) EditText edt_emailid;
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
        btn_create.setOnClickListener(this);

    }


    private boolean validateFName() {
        if (edt_first_name.getText().toString().trim().isEmpty()) {
            Toasty.info(getApplicationContext(),
                    "Enter your first name",
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_first_name);
            return false;
        }

        return true;
    }

    private boolean validateLName() {
        if (edt_last_name.getText().toString().trim().isEmpty()) {
            Toasty.info(getApplicationContext(),
                    "Enter your last name",
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_last_name);
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

    /*private boolean validateMobile() {
        String mobile = edt_mobile.getText().toString().trim();

        if (mobile.isEmpty()) {
            Toasty.info(getApplicationContext(),
                    "Enter your valid email id",
                    Toast.LENGTH_SHORT, true).show();
            requestFocus(edt_mobile);
            return false;
        }

       *//* if (mobile.length() < 10) {
            input_layout_mobile.setError(getString(R.string.err_msg_10digit_mobile));
            requestFocus(input_mobile);
            return false;
        }*//*


        return true;
    }*/

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

        if (!validateFName()){
            return;
        }
        if (!validateLName()){
            return;
        }
        if (!validateEmail()){
            return;
        }

        if (!validatePassword()){
            return;
        }
        if (!validateConfirmPassword()){
            return;
        }


        if (!checkbox.isChecked()){
            Toasty.info(getApplicationContext(),
                    "Please agree privacy policy",
                    Toast.LENGTH_SHORT, true).show();
            return;
        }

        userSignUp();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.tv_goto_login:

                finish();

                break;

            case R.id.btn_create:

                checkValidate();

                break;

        }

    }


    public void userSignUp() {


        String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String url = ApiConstant.SignUp;

        HashMap<String, String> params = new HashMap<>();
        params.put("fname", edt_first_name.getText().toString());
        params.put("lname", edt_last_name.getText().toString());
        params.put("email", edt_emailid.getText().toString());
        params.put("password", edt_password.getText().toString());
        params.put("rpassword", edt_confirm_password.getText().toString());

        if (checkbox.isChecked()){
            params.put("agree", "true");
        }else {
            params.put("agree", "");
        }

        params.put("device_type", "android");
        params.put("fcm_token", "funky");
        params.put("device_id", device_id);


        new PostDataParser(Registration.this, url, params, true,
                new PostDataParser.OnGetResponseListner() {
                    @Override
                    public void onGetResponse(JSONObject response) {
                        if (response != null) {

                            try {
                                int status = response.optInt("status");
                                String message = response.optString("message");
                                if (status == 1) {

                                    Toasty.success(getApplicationContext(),
                                            message,
                                            Toast.LENGTH_LONG, true).show();
                                    finish();

                                } else {

                                    Toasty.info(getApplicationContext(),
                                            message,
                                            Toast.LENGTH_LONG, true).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }
}
