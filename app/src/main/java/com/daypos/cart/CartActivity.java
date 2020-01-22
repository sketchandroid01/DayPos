package com.daypos.cart;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview) RecyclerView recyclerview;
    @BindView(R.id.toolbar) Toolbar toolbar;

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




    private void dialogAddCustomer(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_customer, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        Button btn_close = dialogView.findViewById(R.id.btn_close);
        Button btn_save = dialogView.findViewById(R.id.btn_save);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });


    }


}
