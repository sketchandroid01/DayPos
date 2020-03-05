package com.daypos.fragments.billhistory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daypos.R;
import com.daypos.fragments.home.ProductData;
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

public class OrderDetailsActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipe_refresh_layout;
    @BindView(R.id.tv_return_id) TextView tv_return_id;
    @BindView(R.id.tv_total_value) TextView tv_total_value;
    @BindView(R.id.tv_cashier_name) TextView tv_cashier_name;
    @BindView(R.id.tv_pos_name) TextView tv_pos_name;
    @BindView(R.id.recyclerview) RecyclerView recyclerview;
    @BindView(R.id.tv_total_value2) TextView tv_total_value2;
    @BindView(R.id.tv_payment_mode) TextView tv_payment_mode;
    @BindView(R.id.tv_total_value3) TextView tv_total_value3;
    @BindView(R.id.tv_date_time) TextView tv_date_time;
    @BindView(R.id.tv_bill_no) TextView tv_bill_no;



    private OrderDetails orderDetails;
    private GlobalClass globalClass;
    private ArrayList<ProductData> productDataArrayList;

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
        activity = OrderDetailsActivity.this;

        iniViews();
    }

    private void iniViews(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);

        globalClass = (GlobalClass) getApplicationContext();
        swipe_refresh_layout.setOnRefreshListener(this);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            orderDetails = (OrderDetails) bundle.getSerializable("datas");

            getSupportActionBar().setTitle(orderDetails.getBill_no());
            tv_total_value.setText(orderDetails.getTotal_amount());

            tv_bill_no.setText(orderDetails.getBill_no());
            tv_date_time.setText(Commons.convertDateFormatBill(orderDetails.getDate()));


            tv_total_value2.setText(orderDetails.getTotal_amount());
            tv_total_value3.setText(orderDetails.getTotal_amount());
            tv_payment_mode.setText(orderDetails.getPayment_type());
            tv_cashier_name.setText(orderDetails.getCashier());


            if (orderDetails.getIs_returned().equals("y")){
                tv_return_id.setText(orderDetails.getReturn_no());
                tv_return_id.setVisibility(View.VISIBLE);
            }else {
                tv_return_id.setVisibility(View.INVISIBLE);
            }
        }

        getOrdersDetailsList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refund, menu);


        MenuItem menuItem = menu.findItem(R.id.refund);
        MenuItemCompat.setActionView(menuItem, R.layout.item_refund);
        TextView refund = (TextView) MenuItemCompat.getActionView(menuItem);

        if (orderDetails != null){
            if (orderDetails.getIs_returned().equals("y")){
                menuItem.setVisible(false);
            }
        }


        refund.setOnClickListener(v -> {
            gotoReturnScreen();

        });

        return super.onCreateOptionsMenu(menu);
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
    public void onRefresh() {
        getOrdersDetailsList();
    }

    private void getOrdersDetailsList() {

        productDataArrayList = new ArrayList<>();

        String url = ApiConstant.order_detail;

        HashMap<String, String> params = new HashMap<>();
        params.put("order_id", orderDetails.getId());

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++){
                            JSONObject object = data.getJSONObject(i);

                            String id = object.optString("id");
                            String item_id = object.optString("item_id");
                            String quantity = object.optString("quantity");
                            String price = object.optString("price");
                            String item_name = object.optString("item_name");

                            ProductData productData = new ProductData();
                            productData.setId(id);
                            productData.setItem_id(item_id);
                            productData.setName(item_name);
                            productData.setQty(quantity);
                            productData.setPrice(price);

                            productDataArrayList.add(productData);

                        }

                        setData();

                    }

                    if (swipe_refresh_layout.isRefreshing()){
                        swipe_refresh_layout.setRefreshing(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void setData(){

        OrderProductAdapter orderProductAdapter =
                new OrderProductAdapter(OrderDetailsActivity.this,
                productDataArrayList);
        recyclerview.setAdapter(orderProductAdapter);
    }

    private void gotoReturnScreen(){

        Intent intent = new Intent(OrderDetailsActivity.this, ReturnActivity.class);
        intent.putExtra("array", productDataArrayList);
        intent.putExtra("order_id", orderDetails.getId());
        intent.putExtra("total", orderDetails.getTotal_amount());
        startActivity(intent);
    }

}
