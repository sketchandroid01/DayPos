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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daypos.R;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.Commons;
import com.daypos.utils.GlobalClass;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

import static com.daypos.utils.Commons.isValidEmail;

public class DialogEditCustomer extends Dialog {

    private Context context;
    private GlobalClass globalClass;
    private CustomerData customerData;
    public int isEdited = 0;

    public DialogEditCustomer(@NonNull Context context, CustomerData customerData) {

        super(context);
        this.context = context;
        this.customerData = customerData;
        globalClass = (GlobalClass) context.getApplicationContext();

        isEdited = 0;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_customer);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setCancelable(false);


        initViews();

    }

    private void initViews(){

        TextView tv_title = findViewById(R.id.tv_title);
        Button btn_close = findViewById(R.id.btn_close);
        Button btn_save = findViewById(R.id.btn_save);
        Button btn_edit = findViewById(R.id.btn_edit);
        EditText edt_customer_name = findViewById(R.id.edt_customer_name);
        EditText edt_customer_phone = findViewById(R.id.edt_customer_phone);
        EditText edt_customer_email = findViewById(R.id.edt_customer_email);

        edt_customer_name.setText(customerData.getName());
        edt_customer_phone.setText(customerData.getPhone());
        edt_customer_email.setText(customerData.getEmail());


        btn_edit.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.GONE);

        edt_customer_name.setEnabled(false);
        edt_customer_phone.setEnabled(false);
        edt_customer_email.setEnabled(false);


        tv_title.setText("Update Customer");

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
                /*if (edt_customer_phone.getText().toString().trim().length() == 0){
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
                }*/
                if (!edt_customer_email.getText().toString().isEmpty()
                        && !isValidEmail(edt_customer_email.getText().toString())){
                    Toasty.info(context,
                            "Enter valid email",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }

                /*if (edt_customer_number.getText().toString().trim().length() == 0){
                    Toasty.info(context,
                            "Enter customer code",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }*/


                addCustomer(customerData.getId(), edt_customer_name.getText().toString(),
                        edt_customer_email.getText().toString(),
                        edt_customer_phone.getText().toString());


            }
        });

        btn_edit.setOnClickListener(v -> {
            if (btn_save.getVisibility() == View.GONE){
                btn_edit.setVisibility(View.GONE);
                btn_save.setVisibility(View.VISIBLE);

                edt_customer_name.setEnabled(true);
                edt_customer_phone.setEnabled(true);
                edt_customer_email.setEnabled(true);

                edt_customer_name.setSelection(edt_customer_name.length());
            }
        });
    }



    private void addCustomer(String id, String name, String email, String mobile) {

        String url = ApiConstant.addEditCustomer;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("customer_id", id);
        params.put("name", name);
        params.put("email", email);
        params.put("phone", mobile);


        new PostDataParser(context, url, params, true, response -> {

            if (response != null) {
                try {

                    int status = response.getInt("status");
                    String message = response.getString("message");

                    if (status == 1) {

                        Toasty.success(context, message,
                                Toast.LENGTH_SHORT, true).show();

                        Commons.hideSoftKeyboard((Activity) context);

                        isEdited = 1;

                        dismiss();

                    }else {

                        Toasty.error(context, message,
                                Toast.LENGTH_SHORT, true).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

}
