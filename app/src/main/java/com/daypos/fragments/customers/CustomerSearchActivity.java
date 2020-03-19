package com.daypos.fragments.customers;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.GlobalClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerSearchActivity extends AppCompatActivity implements
        View.OnClickListener, CustomerAdapter.ItemClickListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view) RecyclerView recycler_view;
    @BindView(R.id.tv_add_customer) TextView tv_add_customer;
    @BindView(R.id.edt_search) EditText edt_search;
    @BindView(R.id.iv_search)
    ImageView iv_search;

    private ArrayList<CustomerData> customerDataArrayList;
    private GlobalClass globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_customer);
        ButterKnife.bind(this);

        initViews();
    }



    private void initViews() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add customer to sale");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);

        iv_search.setOnClickListener(this);
        tv_add_customer.setOnClickListener(this);
        globalClass = (GlobalClass) getApplicationContext();

        recycler_view.setLayoutManager(new LinearLayoutManager(this));


        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 1){
                    searchCustomerList(s.toString());
                }
                if (s.toString().length() == 0){
                    getTopCustomer();
                }
            }
        });

        getTopCustomer();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return (super.onOptionsItemSelected(menuItem));
    }


    @Override
    public void onClick(View v) {

        if (v == tv_add_customer){

            DialogAddCustomer dialogAddCustomer =
                    new DialogAddCustomer(CustomerSearchActivity.this);
            dialogAddCustomer.show();
            dialogAddCustomer.setOnDismissListener(dialog -> {
                if (dialogAddCustomer.is_add_customer == 1){
                    getTopCustomer();
                }
            });

        }else if (v == iv_search){

            edt_search.requestFocus();

        }
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


                        setCustomerData();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void setCustomerData(){

        CustomerAdapter customerAdapter = new CustomerAdapter(CustomerSearchActivity.this,
                customerDataArrayList);
        recycler_view.setAdapter(customerAdapter);
        customerAdapter.setClickListener(this);

    }

    @Override
    public void onItemClick(CustomerData customerData) {

        DialogSelectCustomer dialogSelectCustomer =
                new DialogSelectCustomer(CustomerSearchActivity.this, customerData);
        dialogSelectCustomer.show();

        dialogSelectCustomer.setOnDismissListener(dialog -> {
            if (dialogSelectCustomer.is_set_customer == 1){
                finish();
            }
        });
    }

    private void getTopCustomer() {

        String url = ApiConstant.top_customer;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());

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

                            String id = object.optString("customer_id");
                            String name = object.optString("customer_name");
                            String email = object.optString("customer_email");
                            String phone = object.optString("customer_phone");

                            CustomerData customerData = new CustomerData();
                            customerData.setId(id);
                            customerData.setName(name);
                            customerData.setEmail(email);
                            customerData.setPhone(phone);

                            customerDataArrayList.add(customerData);
                        }

                        setCustomerData();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }


}
