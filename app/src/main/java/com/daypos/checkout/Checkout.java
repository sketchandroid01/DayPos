package com.daypos.checkout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.daypos.R;
import com.daypos.container.Container;
import com.daypos.fragments.customers.CustomerData;
import com.daypos.fragments.customers.DialogAddCustomer;
import com.daypos.fragments.customers.SearchCustomerAdapter;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.receipt.ReceiptActivity;
import com.daypos.utils.Commons;
import com.daypos.utils.GlobalClass;
import com.daypos.utils.Preferense;
import com.daypos.utils.PriceValueFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class Checkout extends AppCompatActivity implements View.OnClickListener {

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
    @BindView(R.id.radio_group)
    RadioGroup radio_group;


    private GlobalClass globalClass;
    private Preferense preferense;
    private ArrayList<CustomerData> customerDataArrayList;
    private String payment_method = "", coupon_code = "", feedback = "";
    private String refund_amount = "0.00", pay_amount;

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

        tv_cash.setOnClickListener(this);
        tv_creditcard.setOnClickListener(this);
        tv_debitcard.setOnClickListener(this);

        preferense = new Preferense(this);
        globalClass = (GlobalClass) getApplicationContext();
        customerDataArrayList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            tv_pay_amount.setText(bundle.getString("total_price"));
            tv_pay_amount2.setText(bundle.getString("total_price"));

        }

        rel_customer.setVisibility(View.GONE);
        rel_coupon_amt.setVisibility(View.GONE);
        rel_coupon_amt2.setVisibility(View.GONE);


        /// if select customer
        addToSelectCustomer();

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

                /*String sss = customerData.getName()+" "
                        + "\n" + customerData.getPhone()
                        + "\n" + customerData.getEmail();
                tv_customer_info.setText(sss);*/
               // rel_customer.setVisibility(View.VISIBLE);


                autocomplete_search.setText("");
                autocomplete_search.setSelection(autocomplete_search.length());

                globalClass.setCid(customerData.getId());
                globalClass.setCname(customerData.getName());
                globalClass.setCphone(customerData.getPhone());
                globalClass.setCemail(customerData.getEmail());

                addToSelectCustomer();

            }catch (Exception e){
                e.printStackTrace();
            }

        });

        iv_remove_customer.setOnClickListener(v -> {
            tv_customer_info.setText("");
            globalClass.setCid("");
            globalClass.setCname("");
            globalClass.setCphone("");
            globalClass.setCemail("");
            rel_customer.setVisibility(View.GONE);

        });

        iv_apply_coupon.setOnClickListener(v -> {

            if (globalClass.getCid().isEmpty()){
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


        radio_group.setOnCheckedChangeListener((group, checkedId) -> {

            int selectedId = group.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(selectedId);

            feedback = radioButton.getText().toString().toLowerCase();

            Log.d(Commons.TAG, "value = "+feedback);

        });

    }

    private void addToSelectCustomer(){

        if (!globalClass.getCid().isEmpty()){
            String sss = "";
            if (!globalClass.getCname().isEmpty()){
                sss = globalClass.getCname();
            }
            if (!globalClass.getCphone().isEmpty()){
                sss = sss + "\n" + globalClass.getCphone();
            }
            if (!globalClass.getCemail().isEmpty()){
                sss = sss + "\n" + globalClass.getCemail();
            }

            tv_customer_info.setText(sss);
            rel_customer.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.tv_cash:

                payment_method = "Cash";
                pay_amount = tv_pay_amount2.getText().toString();

                if (globalClass.getCid().isEmpty()){
                    Toasty.info(getApplicationContext(),
                            "Select customer",
                            Toast.LENGTH_LONG, true).show();
                    return;
                }

                dialogRefundAmount();

                break;

            case R.id.tv_creditcard:

                payment_method = "Credit Card";
                pay_amount = tv_pay_amount2.getText().toString();

                if (globalClass.getCid().isEmpty()){
                    Toasty.info(getApplicationContext(),
                            "Select customer",
                            Toast.LENGTH_LONG, true).show();
                    return;
                }

                checkout();

                break;

            case R.id.tv_debitcard:

                payment_method = "Debit Card";
                pay_amount = tv_pay_amount2.getText().toString();

                if (globalClass.getCid().isEmpty()){
                    Toasty.info(getApplicationContext(),
                            "Select customer",
                            Toast.LENGTH_LONG, true).show();
                    return;
                }

                checkout();

                break;
        }
    }

    private void dialogAddCustomer(){
        DialogAddCustomer dialogAddCustomer = new DialogAddCustomer(this);
        dialogAddCustomer.show();
        dialogAddCustomer.setOnDismissListener(dialog -> {
            if (dialogAddCustomer.is_add_customer == 1){
                addToSelectCustomer();
            }
        });
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
        params.put("user_id", globalClass.getUserId());
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

                        coupon_code = code;

                        tv_coupon_amt.setText(discount_amount);
                        tv_pay_amount2.setText(cart_amount_now);

                        rel_coupon_amt.setVisibility(View.VISIBLE);
                        rel_coupon_amt2.setVisibility(View.VISIBLE);

                    }else {

                        tv_coupon_amt.setText("0.00");
                        tv_pay_amount2.setText("0.00");

                        rel_coupon_amt.setVisibility(View.GONE);
                        rel_coupon_amt2.setVisibility(View.GONE);

                        Toasty.error(getApplicationContext(),
                                message, Toast.LENGTH_LONG, true).show();
                    }

                    Commons.hideSoftKeyboard(Checkout.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    float differ = 0;
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
        btn_done.setVisibility(View.GONE);

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
                        differ = value_received - Float.parseFloat(tv_order_total.getText().toString());

                        if (differ == 0){

                            tv_refund_value.setText(df.format(differ));
                            tv_due_refund.setText("Refund Amount");
                            tv_refund_value.setTextColor(getResources().getColor(R.color.green_light));

                            btn_done.setVisibility(View.VISIBLE);

                        }else if (differ > 0){

                            tv_refund_value.setText(df.format(differ));
                            tv_due_refund.setText("Refund Amount");
                            tv_refund_value.setTextColor(getResources().getColor(R.color.green_light));

                            btn_done.setVisibility(View.VISIBLE);

                        }else if (differ < 0){

                            differ = Math.abs(differ);

                            tv_refund_value.setText(df.format(differ));
                            tv_due_refund.setText("Due Amount");
                            tv_refund_value.setTextColor(getResources().getColor(R.color.color3));

                            btn_done.setVisibility(View.GONE);
                        }

                        refund_amount = tv_refund_value.getText().toString();

                    }else {

                        rl_refund.setVisibility(View.GONE);
                        btn_done.setVisibility(View.GONE);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    rl_refund.setVisibility(View.GONE);
                }

            }
        });

        btn_done.setOnClickListener(v -> {

            Commons.hideSoftKeyboard(Checkout.this);

            dialog_refund.dismiss();

            checkout();

        });


    }

    private void checkout() {

        String url = ApiConstant.check_out;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("employee_id", preferense.getString(Preferense.employee_id));
        params.put("cart_amount", tv_pay_amount.getText().toString());
        params.put("payment_mode", payment_method);
        params.put("customer_id", globalClass.getCid());
        params.put("discount_amount", tv_coupon_amt.getText().toString());
        params.put("coupon", coupon_code);
        params.put("note", edt_notes.getText().toString());
        params.put("feedback", feedback);


        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        globalClass.setCid("");
                        globalClass.setCname("");
                        globalClass.setCphone("");
                        globalClass.setCemail("");
                        globalClass.setCart_counter("0");

                        Commons.hideSoftKeyboard(Checkout.this);
                        /*Toasty.success(getApplicationContext(),
                                message, Toast.LENGTH_LONG, true).show();*/

                        Intent intent = new Intent(Checkout.this, ReceiptActivity.class);
                        intent.putExtra("pay_mat", pay_amount);
                        intent.putExtra("change_amt", refund_amount);
                        intent.putExtra("order_id", response.optString("order_id"));
                        startActivity(intent);
                        finish();

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


}
