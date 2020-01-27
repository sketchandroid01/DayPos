package com.daypos.login;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
import com.daypos.forgotpassword.ForgotPassword;
import com.daypos.network.ApiConstant;
import com.daypos.network.GetDataParser;
import com.daypos.network.PostDataParser;
import com.daypos.registration.Registration;
import com.daypos.utils.Preferense;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_forgot_password) TextView tv_forgot_password;
    @BindView(R.id.edt_username) EditText edt_username;
    @BindView(R.id.edt_password) EditText edt_password;
    @BindView(R.id.btn_login) Button btn_login;
    @BindView(R.id.btn_register) TextView btn_register;

    private Preferense preferense;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        preferense = new Preferense(this);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        tv_forgot_password.setOnClickListener(this);


        String email = "admin@loyverse.com";
        String pass = "123456";

       // edt_username.setText(email);
       // edt_password.setText(pass);

    }

    @Override
    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()){

            case R.id.btn_login:

                checkValidate();

                break;

            case R.id.btn_register:

                intent = new Intent(Login.this, Registration.class);
                startActivity(intent);

                break;


            case R.id.tv_forgot_password:

                intent = new Intent(Login.this, ForgotPassword.class);
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


        userLogin(edt_username.getText().toString(), edt_password.getText().toString());
    }



    public void userLogin(String email, String pass) {


        String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String url = ApiConstant.login;

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", pass);
        params.put("device_type", "android");
        params.put("fcm_token", "funky");
        params.put("device_id", device_id);


        new PostDataParser(Login.this, url, params, true,
                new PostDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {
                if (response != null) {

                    try {
                        int status = response.optInt("status");
                        String message = response.optString("message");
                        if (status == 1) {

                            JSONObject data = response.getJSONObject("data");

                            JSONObject user_details = data.getJSONObject("user_details");

                            preferense.setPref_logInStatus(true);

                            preferense.saveString(Preferense.PREF_id,
                                    data.optString("user_id"));
                            preferense.saveString(Preferense.PREF_name,
                                    data.optString("user_name"));
                            preferense.saveString(Preferense.PREF_phone_number,
                                    data.optString("user_phone"));
                            preferense.saveString(Preferense.PREF_image,
                                    data.optString("user_image"));


                            preferense.saveString(Preferense.PREF_email,
                                    user_details.optString("email"));
                            preferense.saveString(Preferense.Pref_Pin,
                                    user_details.optString("emp_pin"));
                            preferense.saveString(Preferense.PREF_business,
                                    user_details.optString("business_name"));




                            preferense.setToGlobal();

                            Intent intent = new Intent(Login.this, Container.class);
                            startActivity(intent);
                            finish();


                        } else {

                            Toasty.info(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_SHORT, true).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}


/*
 {
 	"status": 1,
 	"message": "Login Sucessful",
 	"data": {
 		"user_id": "2",
 		"user_name": "Arayan Khanna",
 		"user_phone": "9775370208",
 		"user_image": "https:\/\/lab-5.sketchdemos.com\/PHP-WEB-SERVICES\/P-1029-DAYPOS\/uploads\/user\/images.png",
 		"user_details": {
 			"id": "2",
 			"user_id": "1",
 			"name": "Arayan Khanna",
 			"fname": "",
 			"lname": "",
 			"email": "admin@loyverse.com",
 			"password": "7c4a8d09ca3762af61e59520943dc26494f8941b",
 			"hash": "e41a54377944eca26a6b84d6c63f2e57bf172ec1",
 			"phone": "9775370208",
 			"mobile": "",
 			"role_id": "3",
 			"emp_pin": "9874",
 			"image": "images.png",
 			"created_date": null,
 			"modified_date": "2020-01-25 11:54:32",
 			"ip_address": "10.10.0.1",
 			"last_login": "2020-01-25 11:47:06",
 			"user_agent": "Mozilla\/5.0 (X11; Linux x86_64) AppleWebKit\/537.36 (KHTML, like Gecko) Chrome\/73.0.3683.75 Safari\/537.36",
 			"online_status": "1",
 			"invite_to_back_office": "1",
 			"store_availability": "1",
 			"store_ids": "6,4,2",
 			"pos_status": "1",
 			"back_office_status": null,
 			"device_type": "android",
 			"fcm_token": "sfdgdfhfj",
 			"device_id": "45566",
 			"otp": "0",
 			"business_name": "dayPos online store"
 		}
 	}
 }

        */
