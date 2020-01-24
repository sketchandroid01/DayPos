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

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.edt_otp) EditText edt_otp;
    @BindView(R.id.edt_password) EditText edt_password;
    @BindView(R.id.edt_confirm_password) EditText edt_confirm_password;
    @BindView(R.id.btn_submit) Button btn_submit;

    @BindView(R.id.tv_resend_otp) TextView tv_resend_otp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);


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


        return true;
    }



    private void checkValidate(){

        if (!validateEmail()){
            return;
        }

       // forgotPassword(edt_username.getText().toString());
    }



    public void forgotPassword(String email) {


        String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String url = ApiConstant.change_password;

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
