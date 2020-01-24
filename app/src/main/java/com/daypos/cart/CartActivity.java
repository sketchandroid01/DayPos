package com.daypos.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.checkout.Checkout;
import com.daypos.fragments.customers.DialogAddCustomer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements
        View.OnClickListener {

    @BindView(R.id.recyclerview) RecyclerView recyclerview;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btn_checkout) Button btn_checkout;

    ArrayList<CartData> cartDataArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        initViews();

    }

    private void initViews(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);
        getSupportActionBar().setTitle("Cart (0)");


        recyclerview.setLayoutManager(new LinearLayoutManager(this));


        cartDataArrayList = new ArrayList<>();
        cartDataArrayList.add(new CartData());
        cartDataArrayList.add(new CartData());
        cartDataArrayList.add(new CartData());
        cartDataArrayList.add(new CartData());
        cartDataArrayList.add(new CartData());
        cartDataArrayList.add(new CartData());

        getSupportActionBar().setTitle("Cart ("+cartDataArrayList.size()+")");


        CartAdapter cartAdapter = new CartAdapter(CartActivity.this, cartDataArrayList);
        recyclerview.setAdapter(cartAdapter);


        btn_checkout.setOnClickListener(this);

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


}
