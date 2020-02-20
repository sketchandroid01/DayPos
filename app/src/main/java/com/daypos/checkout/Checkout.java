package com.daypos.checkout;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
    @BindView(R.id.tv_customer_info) TextView tv_customer_info;
    @BindView(R.id.iv_remove_customer) ImageView iv_remove_customer;
    @BindView(R.id.rel_customer)
    RelativeLayout rel_customer;


    GlobalClass globalClass;
    private ArrayList<CustomerData> customerDataArrayList;
    private CustomerData selected_customer;

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



}
