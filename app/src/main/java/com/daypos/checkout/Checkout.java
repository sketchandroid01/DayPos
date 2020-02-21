package com.daypos.checkout;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.daypos.R;
import com.daypos.fragments.customers.CustomerData;
import com.daypos.fragments.customers.DialogAddCustomer;
import com.daypos.fragments.customers.SearchCustomerAdapter;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.Commons;
import com.daypos.utils.GlobalClass;
import com.daypos.utils.PriceValueFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

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
    @BindView(R.id.tv_customer_info) TextView tv_customer_info;
    @BindView(R.id.tv_coupon_amt) TextView tv_coupon_amt;
    @BindView(R.id.tv_pay_amount2) TextView tv_pay_amount2;
    @BindView(R.id.iv_remove_customer) ImageView iv_remove_customer;
    @BindView(R.id.rel_customer) RelativeLayout rel_customer;
    @BindView(R.id.rel_coupon_amt) RelativeLayout rel_coupon_amt;
    @BindView(R.id.rel_coupon_amt2) RelativeLayout rel_coupon_amt2;


    private GlobalClass globalClass;
    private ArrayList<CustomerData> customerDataArrayList;
    private CustomerData selected_customer = null;

    private static DecimalFormat df = new DecimalFormat("0.00");

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

        globalClass = (GlobalClass) getApplicationContext();
        customerDataArrayList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            tv_pay_amount.setText(bundle.getString("total_price"));

        }

        rel_customer.setVisibility(View.GONE);
        rel_coupon_amt.setVisibility(View.GONE);
        rel_coupon_amt2.setVisibility(View.GONE);


        autocomplete_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() >= 1 && s.toString().length() < 6){

                    searchCustomerList(s.toString());
                }

            }
        });


        autocomplete_search.setOnItemClickListener((parent, view, position, id) -> {

            try {

                CustomerData customerData = (CustomerData)parent.getItemAtPosition(position);

                String sss = customerData.getName()+" "
                        + "\n" + customerData.getPhone()
                        + "\n" + customerData.getEmail();

                tv_customer_info.setText(sss);

                rel_customer.setVisibility(View.VISIBLE);

                autocomplete_search.setText("");
                autocomplete_search.setSelection(autocomplete_search.length());

                selected_customer = customerData;

            }catch (Exception e){
                e.printStackTrace();
            }

        });

        iv_remove_customer.setOnClickListener(v -> {
            tv_customer_info.setText("");
            selected_customer = null;
            rel_customer.setVisibility(View.GONE);

        });

        iv_apply_coupon.setOnClickListener(v -> {

            if (selected_customer == null){
                Toasty.info(getApplicationContext(),
                        "Select customer",
                        Toast.LENGTH_SHORT, true).show();
                return;
            }

            if (edt_coupon_code.getText().toString().trim().length() == 0){
                Toasty.info(getApplicationContext(),
                        "Enter coupon code",
                        Toast.LENGTH_SHORT, true).show();
                return;
            }

            applyCoupon(edt_coupon_code.getText().toString());

        });

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

    private void searchCustomerList(String keyword) {

        String url = ApiConstant.customer_by_name_mobile;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("name", keyword);

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        customerDataArrayList = new ArrayList<>();

                        JSONArray data = response.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++){
                            JSONObject object = data.getJSONObject(i);

                            String id = object.optString("id");
                            String name = object.optString("name");
                            String image = object.optString("image");
                            String customerId = object.optString("customerId");
                            String email = object.optString("email");
                            String phone = object.optString("phone");
                            String purchase_amount = object.optString("purchase_amount");
                            String points_balance = object.optString("points_balance");
                            String note = object.optString("note");
                            String address = object.optString("address");

                            CustomerData customerData = new CustomerData();
                            customerData.setId(id);
                            customerData.setName(name);
                            customerData.setEmail(email);
                            customerData.setPhone(phone);
                            customerData.setCustomer_id(customerId);

                            customerDataArrayList.add(customerData);
                        }


                        autocomplete_search.setThreshold(1);
                        autocomplete_search.setTextColor(Color.BLACK);

                        SearchCustomerAdapter searchCustomerAdapter =
                                new SearchCustomerAdapter(Checkout.this, customerDataArrayList);
                        autocomplete_search.setAdapter(searchCustomerAdapter);
                        searchCustomerAdapter.notifyDataSetChanged();
                        autocomplete_search.showDropDown();

                    }


                    Commons.hideSoftKeyboard(Checkout.this);



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void applyCoupon(String code) {

        String url = ApiConstant.check_coupon;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", selected_customer.getId());
        params.put("cart_amount", tv_pay_amount.getText().toString());
        params.put("coupon", code);

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        String discount_amount = response.optString("discount_amount");
                        String cart_amount_now = response.optString("cart_amount_now");


                        tv_coupon_amt.setText(discount_amount);
                        tv_pay_amount2.setText(cart_amount_now);

                        rel_coupon_amt.setVisibility(View.VISIBLE);
                        rel_coupon_amt2.setVisibility(View.VISIBLE);

                    }else {

                        Toasty.error(getApplicationContext(),
                                message, Toast.LENGTH_LONG, true).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }


    private void dialogRefundAmount(){

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Checkout.this);
        View dialogView = inflater.inflate(R.layout.cash_refund_dialog, null);
        dialogBuilder.setView(dialogView);
        AlertDialog dialog_refund = dialogBuilder.create();
        dialog_refund.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog_refund.show();

        EditText edt_received_cash = dialogView.findViewById(R.id.tv_received_cash_value);
        TextView tv_order_total = dialogView.findViewById(R.id.tv_order_total);
        TextView tv_refund_value = dialogView.findViewById(R.id.tv_refund_value);
        TextView tv_due_refund = dialogView.findViewById(R.id.tv_due_refund);
        Button btn_done = dialogView.findViewById(R.id.btn_done);
        RelativeLayout rl_refund = dialogView.findViewById(R.id.rl_refund);
        rl_refund.setVisibility(View.GONE);

        tv_order_total.setText(tv_pay_amount2.getText().toString());


        edt_received_cash.setInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL
                | InputType.TYPE_NUMBER_FLAG_SIGNED);

        edt_received_cash.setFilters(new InputFilter[]{new PriceValueFilter(2)});
        edt_received_cash.setSelection(edt_received_cash.getText().length());

        edt_received_cash.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {

                    if (s.toString().length() > 0){

                        rl_refund.setVisibility(View.VISIBLE);

                        float value_received = Float.parseFloat(s.toString());
                        float differ = value_received - Float.parseFloat(tv_order_total.getText().toString());


                        if (differ == 0){

                            tv_refund_value.setText(df.format(differ));
                            tv_due_refund.setText("Refund Amount");
                            tv_refund_value.setTextColor(getResources().getColor(R.color.green_light));


                        }else if (differ > 0){

                            tv_refund_value.setText(df.format(differ));
                            tv_due_refund.setText("Refund Amount");
                            tv_refund_value.setTextColor(getResources().getColor(R.color.green_light));


                        }else if (differ < 0){

                            tv_refund_value.setText(df.format(differ));
                            tv_due_refund.setText("Due Amount");
                            tv_refund_value.setTextColor(getResources().getColor(R.color.color3));

                        }


                    }else {

                        rl_refund.setVisibility(View.GONE);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    rl_refund.setVisibility(View.GONE);
                }


            }
        });


    }

}
