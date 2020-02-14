package com.daypos.cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class CartActivity extends AppCompatActivity implements
        View.OnClickListener,
        CartAdapter.ItemClickListenerDetete,
        CartAdapter.ItemClickListenerEdit{

    @BindView(R.id.recyclerview) RecyclerView recyclerview;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btn_checkout) Button btn_checkout;
    @BindView(R.id.tv_total_price)
    TextView tv_total_price;

    ArrayList<CartData> cartDataArrayList;
    GlobalClass globalClass;
    double total_cart_value = 0;
    private static DecimalFormat df = new DecimalFormat("0.00");

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

        total_cart_value = 0;

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

                            double price_ = Float.parseFloat(cartData.getPrice());
                            int qty = Integer.parseInt(cartData.getQty());
                            double total_price = price_ * qty;
                            total_cart_value = total_cart_value + total_price;
                        }

                        globalClass.setCart_counter(""+cartDataArrayList.size());

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
        cartAdapter.setClickListenerEdit(this);
        cartAdapter.setClickListenerDetete(this);

        tv_total_price.setText(df.format(total_cart_value));
    }

    @Override
    public void onItemClickEdit(int position, CartData cartData) {
        editDialog(cartData);
    }

    @Override
    public void onItemClickDelete(int position, CartData cartData) {
        deleteProductDialog(cartData);
    }


    private void deleteProductDialog(CartData cartData){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Are you sure, you want to delete " +cartData.getProduct_name()+" ?");
        adb.setIcon(R.mipmap.alert_48);
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteCart(cartData.getId());

            } });
        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            } });
        adb.show();

    }

    private void editDialog(CartData cartData){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cart_edit, null);
        dialogBuilder.setView(dialogView);

        EditText edit_cart_quantity = dialogView.findViewById(R.id.edit_cart_quantity);
        ImageView cart_minus_img = dialogView.findViewById(R.id.cart_minus_img);
        ImageView cart_plus_img = dialogView.findViewById(R.id.cart_plus_img);
        Button btn_close = dialogView.findViewById(R.id.btn_close);
        Button btn_save = dialogView.findViewById(R.id.btn_save);
        edit_cart_quantity.setText(cartData.getQty());


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        cart_minus_img.setOnClickListener(v -> {

            try {
                int count = Integer.parseInt(edit_cart_quantity.getText().toString());
                if (count >= 2){
                    count--;
                    edit_cart_quantity.setText(String.valueOf(count));
                }
            }catch (Exception e){
                e.printStackTrace();
            }



        });
        cart_plus_img.setOnClickListener(v -> {

            try {
                int count = Integer.parseInt(edit_cart_quantity.getText().toString());
                count++;
                edit_cart_quantity.setText(String.valueOf(count));
            }catch (Exception e){
                e.printStackTrace();
            }

        });
        btn_close.setOnClickListener(v -> {

            alertDialog.dismiss();

        });
        btn_save.setOnClickListener(v -> {

            if (edit_cart_quantity.getText().toString().trim().length() == 0){
                Toasty.info(getApplicationContext(),
                        "Enter quantity",
                        Toast.LENGTH_SHORT, true).show();
                return;
            }

            editCart(cartData.getId(), edit_cart_quantity.getText().toString());

            alertDialog.dismiss();
        });

    }

    private void editCart(String cart_row_id, String quantity_) {

        cartDataArrayList = new ArrayList<>();

        total_cart_value = 0;

        String url = ApiConstant.cart_item_quantity_update;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("cart_row_id", cart_row_id);
        params.put("quantity", quantity_);

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


                            double price_ = Float.parseFloat(cartData.getPrice());
                            int qty = Integer.parseInt(cartData.getQty());
                            double total_price = price_ * qty;
                            total_cart_value = total_cart_value + total_price;

                        }

                        globalClass.setCart_counter(""+cartDataArrayList.size());

                        getSupportActionBar().setTitle("Cart ("+cartDataArrayList.size()+")");

                        setCartData();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void deleteCart(String cart_row_id) {

        cartDataArrayList = new ArrayList<>();

        total_cart_value = 0;

        String url = ApiConstant.cart_item_delete;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("cart_row_id", cart_row_id);

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


                            double price_ = Float.parseFloat(cartData.getPrice());
                            int qty = Integer.parseInt(cartData.getQty());
                            double total_price = price_ * qty;
                            total_cart_value = total_cart_value + total_price;

                        }
                        globalClass.setCart_counter(""+cartDataArrayList.size());
                        getSupportActionBar().setTitle("Cart ("+cartDataArrayList.size()+")");

                        setCartData();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }
}
