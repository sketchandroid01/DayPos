package com.daypos.fragments.customers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daypos.R;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.Commons;
import com.daypos.utils.GlobalClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

import static com.daypos.utils.Commons.isValidEmail;

public class DialogAddCustomer extends Dialog {

    private Context context;
    private GlobalClass globalClass;

    public DialogAddCustomer(@NonNull Context context) {

        super(context);
        this.context = context;
        globalClass = (GlobalClass) context.getApplicationContext();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_customer);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setCancelable(false);


        initViews();



    }

    private void initViews(){

        Button btn_close = findViewById(R.id.btn_close);
        Button btn_save = findViewById(R.id.btn_save);
        EditText edt_customer_name = findViewById(R.id.edt_customer_name);
        EditText edt_customer_phone = findViewById(R.id.edt_customer_phone);
        EditText edt_customer_email = findViewById(R.id.edt_customer_email);
        EditText edt_customer_number = findViewById(R.id.edt_customer_number);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_customer_name.getText().toString().trim().length() == 0){
                    Toasty.info(context,
                            "Enter customer name",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }
                if (edt_customer_phone.getText().toString().trim().length() == 0){
                    Toasty.info(context,
                            "Enter customer phone",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }
                if (edt_customer_email.getText().toString().trim().length() == 0){
                    Toasty.info(context,
                            "Enter customer email",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }
                if (!isValidEmail(edt_customer_email.getText().toString())){
                    Toasty.info(context,
                            "Enter valid email",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (edt_customer_number.getText().toString().trim().length() == 0){
                    Toasty.info(context,
                            "Enter customer code",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }


                addCustomer(edt_customer_name.getText().toString(),
                        edt_customer_email.getText().toString(),
                        edt_customer_phone.getText().toString(),
                        edt_customer_number.getText().toString());


            }
        });

    }



    private void addCustomer(String name, String email, String mobile, String c_no) {

        String url = ApiConstant.addEditCustomer;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("name", name);
        params.put("email", email);
        params.put("phone", mobile);
        params.put("customer_no", c_no);


        new PostDataParser(context, url, params, true, response -> {

            if (response != null) {
                try {

                    int status = response.getInt("status");
                    String message = response.getString("message");

                    if (status == 1) {

                        Toasty.success(context,
                                message,
                                Toast.LENGTH_SHORT, true).show();

                        Commons.hideSoftKeyboard((Activity) context);

                        dismiss();

                    }else {

                        Toasty.error(context,
                                message,
                                Toast.LENGTH_SHORT, true).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

}
