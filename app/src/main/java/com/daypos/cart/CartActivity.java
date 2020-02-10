package com.daypos.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.checkout.Checkout;
import com.daypos.fragments.customers.DialogAddCustomer;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.GlobalClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class CartActivity extends AppCompatActivity implements
        View.OnClickListener, CartAdapter.ItemClickListener {

    @BindView(R.id.recyclerview) RecyclerView recyclerview;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btn_checkout) Button btn_checkout;

    ArrayList<CartData> cartDataArrayList;
    GlobalClass globalClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        initViews();

    }

    private void initViews(){

        globalClass = (GlobalClass) getApplicationContext();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);
        getSupportActionBar().setTitle("Cart");

        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        btn_checkout.setOnClickListener(this);

        getCartItems();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);

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

            case R.id.btn_checkout:

                Intent intent_checkout = new Intent(CartActivity.this, Checkout.class);
                startActivity(intent_checkout);

                break;


        }

    }

    private void dialogAddCustomer(){

        DialogAddCustomer dialogAddCustomer = new DialogAddCustomer(this);
        dialogAddCustomer.show();

    }


    private void getCartItems() {

        cartDataArrayList = new ArrayList<>();

        String url = ApiConstant.cart_item_list;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());

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
                            String name = object.optString("name");
                            String quantity = object.optString("quantity");
                            String price = object.optString("price");
                            String cost = object.optString("cost");


                            CartData cartData = new CartData();
                            cartData.setId(id);
                            cartData.setProduct_id(item_id);
                            cartData.setProduct_name(name);
                            cartData.setPrice(price);
                            cartData.setQty(quantity);
                            cartData.setMrp(cost);

                            cartDataArrayList.add(cartData);
                        }

                        getSupportActionBar().setTitle("Cart ("+cartDataArrayList.size()+")");

                        setCartData();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void setCartData(){

        CartAdapter cartAdapter = new CartAdapter(CartActivity.this, cartDataArrayList);
        recyclerview.setAdapter(cartAdapter);
        cartAdapter.setClickListener(this);
    }


    @Override
    public void onItemClick(int position, CartData cartData) {

    }
}
