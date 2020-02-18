package com.daypos.checkout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.daypos.R;
import com.daypos.fragments.customers.DialogAddCustomer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Checkout extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_pay_amount) TextView tv_pay_amount;
    @BindView(R.id.tv_cash) TextView tv_cash;
    @BindView(R.id.tv_creditcard) TextView tv_creditcard;
    @BindView(R.id.tv_debitcard) TextView tv_debitcard;
    @BindView(R.id.autocomplete_search) AutoCompleteTextView autocomplete_search;
    @BindView(R.id.edt_coupon_code) EditText edt_coupon_code;
    @BindView(R.id.edt_notes) EditText edt_notes;
    @BindView(R.id.iv_apply_coupon) ImageView iv_apply_coupon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);
        initViews();

    }


    private void initViews() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Checkout");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            tv_pay_amount.setText(bundle.getString("total_price"));


        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_customer, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.add_customer:

                dialogAddCustomer();

                break;

        }
        return (super.onOptionsItemSelected(menuItem));
    }


    private void dialogAddCustomer(){

        DialogAddCustomer dialogAddCustomer = new DialogAddCustomer(this);
        dialogAddCustomer.show();

    }



}
