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

public class DialogSelectCustomer extends Dialog {

    private Context context;
    private GlobalClass globalClass;
    public int is_set_customer = 0;
    private CustomerData customerData;

    public DialogSelectCustomer(@NonNull Context context, CustomerData customerData) {

        super(context);
        this.context = context;
        globalClass = (GlobalClass) context.getApplicationContext();
        this.customerData = customerData;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_customer);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setCancelable(false);

        initViews();

    }

    private void initViews(){

        is_set_customer = 0;

        Button btn_close = findViewById(R.id.btn_close);
        Button btn_add_to_sale = findViewById(R.id.btn_add_to_sale);
        TextView edt_customer_name = findViewById(R.id.edt_customer_name);
        TextView edt_customer_phone = findViewById(R.id.edt_customer_phone);
        TextView edt_customer_email = findViewById(R.id.edt_customer_email);

        edt_customer_name.setText(customerData.getName());
        edt_customer_phone.setText(customerData.getPhone());
        edt_customer_email.setText(customerData.getEmail());

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        btn_add_to_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                globalClass.setCid(customerData.getId());
                globalClass.setCname(customerData.getName());
                globalClass.setCemail(customerData.getEmail());
                globalClass.setCphone(customerData.getPhone());

                is_set_customer = 1;

                dismiss();
            }
        });

    }




    /// not use
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

                        String customer_id = response.optString("customer_id");

                        globalClass.setCid(customer_id);
                        globalClass.setCname(name);
                        globalClass.setCemail(email);
                        globalClass.setCphone(mobile);

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
