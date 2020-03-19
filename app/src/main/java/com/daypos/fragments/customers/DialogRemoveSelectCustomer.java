package com.daypos.fragments.customers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.daypos.R;
import com.daypos.utils.GlobalClass;

public class DialogRemoveSelectCustomer extends Dialog {

    private Context context;
    private GlobalClass globalClass;
    public int is_removed_customer = 0;

    public DialogRemoveSelectCustomer(@NonNull Context context) {

        super(context);
        this.context = context;
        globalClass = (GlobalClass) context.getApplicationContext();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_remove_customer);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setCancelable(false);

        initViews();

    }

    private void initViews(){

        is_removed_customer = 0;

        ImageView close = findViewById(R.id.close);
        Button btn_add_to_sale = findViewById(R.id.btn_add_to_sale);
        TextView edt_customer_name = findViewById(R.id.edt_customer_name);
        TextView edt_customer_phone = findViewById(R.id.edt_customer_phone);
        TextView edt_customer_email = findViewById(R.id.edt_customer_email);

        edt_customer_name.setText(globalClass.getCname());
        edt_customer_phone.setText(globalClass.getCphone());
        edt_customer_email.setText(globalClass.getCemail());

        close.setOnClickListener(v -> {
            dismiss();
        });

        btn_add_to_sale.setOnClickListener(v -> {
            globalClass.setCid("");
            globalClass.setCname("");
            globalClass.setCemail("");
            globalClass.setCphone("");

            is_removed_customer = 1;

            dismiss();
        });

    }



}
