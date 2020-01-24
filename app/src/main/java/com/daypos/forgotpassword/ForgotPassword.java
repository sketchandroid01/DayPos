package com.daypos.forgotpassword;

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
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.edt_username) EditText edt_username;
    @BindView(R.id.btn_submit) Button btn_submit;
    @BindView(R.id.btn_back_to_login) TextView btn_back_to_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);


        btn_submit.setOnClickListener(this);
        btn_back_to_login.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_submit:

                checkValidate();

                break;

            case R.id.btn_back_to_login:

                finish();

                break;


        }

    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

        forgotPassword(edt_username.getText().toString());
    }



    public void forgotPassword(String email) {


        String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String url = ApiConstant.change_password;

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        new PostDataParser(ForgotPassword.this, url, params, true,
                new PostDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {
                if (response != null) {

                    try {
                        int status = response.optInt("status");
                        String message = response.optString("message");
                        if (status == 1) {


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
