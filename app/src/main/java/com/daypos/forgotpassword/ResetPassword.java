package com.daypos.forgotpassword;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.daypos.login.Login;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.edt_otp) EditText edt_otp;
    @BindView(R.id.edt_password) EditText edt_password;
    @BindView(R.id.edt_confirm_password) EditText edt_confirm_password;
    @BindView(R.id.btn_submit) Button btn_submit;

    @BindView(R.id.tv_resend_otp) TextView tv_resend_otp;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            email = bundle.getString("email");
        }


        btn_submit.setOnClickListener(this);
        tv_resend_otp.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_submit:

                checkValidate();

                break;

            case R.id.tv_resend_otp:

                 forgotPassword(email);

                break;


        }

    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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


    private void checkValidate(){

        if (edt_otp.getText().toString().isEmpty()){
            Toasty.info(getApplicationContext(),
                    "Enter otp",
                    Toast.LENGTH_SHORT, true).show();
            return;
        }

        if (edt_otp.getText().toString().trim().length() < 4){
            Toasty.info(getApplicationContext(),
                    "Enter 4-digit otp",
                    Toast.LENGTH_SHORT, true).show();
            return;
        }

        if (!validatePassword()){
            return;
        }

        if (!validateConfirmPassword()){
            return;
        }



        resetPassword(edt_otp.getText().toString(), edt_password.getText().toString());
    }



    public void forgotPassword(String email) {

        String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String url = ApiConstant.forgotPassword;

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        new PostDataParser(ResetPassword.this, url, params, true,
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
                                    Toast.LENGTH_SHORT, true).show();
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

    public void resetPassword(String otp, String password) {


        String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String url = ApiConstant.resetPassword;

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("otp", otp);
        params.put("password", password);

        new PostDataParser(ResetPassword.this, url, params, true,
                new PostDataParser.OnGetResponseListner() {
                    @Override
                    public void onGetResponse(JSONObject response) {
                        if (response != null) {

                            try {
                                int status = response.optInt("status");
                                String message = response.optString("message");
                                if (status == 1) {

                                    dialogResetPassword(message);

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

    private void dialogResetPassword(String msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
        builder.setTitle("DayPos");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(ResetPassword.this, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}


