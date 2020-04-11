package com.daypos.cart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
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
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daypos.R;
import com.daypos.checkout.Checkout;
import com.daypos.fragments.customers.DialogAddCustomer;
import com.daypos.modifier.ModifierItemsData;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.Commons;
import com.daypos.utils.GlobalClass;
import com.daypos.utils.PriceValueFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class CartActivity extends AppCompatActivity implements
        View.OnClickListener,
        CartAdapter.ItemClickListenerDetete,
        CartAdapter.ItemClickListenerEdit,
        SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.recyclerview) RecyclerView recyclerview;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btn_checkout) Button btn_checkout;
    @BindView(R.id.btn_more_shop) Button btn_more_shop;
    @BindView(R.id.tv_total_price) TextView tv_total_price;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipe_refresh_layout;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

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

        swipe_refresh_layout.setVisibility(View.GONE);

        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        btn_checkout.setOnClickListener(this);
        btn_more_shop.setOnClickListener(this);
        swipe_refresh_layout.setOnRefreshListener(this);

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
                intent_checkout.putExtra("total_price", tv_total_price.getText().toString());
                startActivity(intent_checkout);

                break;

            case R.id.btn_more_shop:

                finish();

                break;

        }

    }

    @Override
    public void onRefresh() {
        getCartItems();
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
        params.put("ticket_id", globalClass.getTicket_id());

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {
                int total_qty = 0;
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
                            String modifiers = object.optString("modifiers");
                            String sold_option = object.optString("sold_option");
                            String weight_quantity = object.optString("weight_quantity");


                            total_qty = total_qty + (int) Float.parseFloat(weight_quantity);



                            CartData cartData = new CartData();
                            cartData.setId(id);
                            cartData.setProduct_id(item_id);
                            cartData.setProduct_name(name);
                            cartData.setPrice(price);
                            cartData.setQty(quantity);
                            cartData.setMrp(cost);
                            cartData.setModifiers(modifiers);
                            cartData.setSold_option(sold_option);


                            double qty = Double.parseDouble(cartData.getQty());

                            String[] array = modifiers.split(",");
                            /// modifier
                            double modifier_price = 0;
                            ArrayList<ModifierItemsData> modifierItemsDataArrayList = new ArrayList<>();
                            JSONArray item_modifire = object.getJSONArray("item_modifire");
                            for (int j = 0; j < item_modifire.length(); j++){
                                JSONObject object2 = item_modifire.getJSONObject(j);

                                ModifierItemsData modifierItemsData = new ModifierItemsData();
                                modifierItemsData.setId(object2.optString("id"));
                                modifierItemsData.setName(object2.optString("modifier_option"));
                                modifierItemsData.setPrice(object2.optString("price"));

                                modifierItemsDataArrayList.add(modifierItemsData);

                               for (String ss : array){
                                   if (ss.equals(object2.optString("id"))){
                                       modifier_price = modifier_price
                                               + (Float.parseFloat(object2.optString("price")) * qty);
                                   }
                               }

                            }
                            cartData.setModifierItemsList(modifierItemsDataArrayList);


                            cartDataArrayList.add(cartData);

                            double price_ = Float.parseFloat(cartData.getPrice());
                            double total_price = price_ * qty;
                            total_price = total_price + modifier_price;
                            total_cart_value = total_cart_value + total_price;
                        }

                        Collections.reverse(cartDataArrayList);


                        swipe_refresh_layout.setVisibility(View.VISIBLE);
                    }

                    globalClass.setCart_counter(""+total_qty);
                    getSupportActionBar().setTitle("Cart ("+total_qty+")");
                    setCartData();

                    swipe_refresh_layout.setRefreshing(false);
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

        scrollView.post(() -> {
            scrollView.fullScroll(View.FOCUS_DOWN);
        });

    }

    @Override
    public void onItemClickEdit(int position, CartData cartData) {

        if (cartData.getModifierItemsList().size() > 0){
            selected_cartdata = cartData;
            Intent intent = new Intent(CartActivity.this, CartModifierEditAct.class);
            intent.putExtra("datas", cartData);
            startActivityForResult(intent, MODIFIER_REQUEST);
        }else {
            editDialog(cartData);
        }

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


        if (cartData.getSold_option().equals("2")){

            edit_cart_quantity.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            edit_cart_quantity.setFilters(new InputFilter[]{new PriceValueFilter(3)});

            cart_plus_img.setVisibility(View.GONE);
            cart_minus_img.setVisibility(View.GONE);

            edit_cart_quantity.setText(cartData.getQty());
            edit_cart_quantity.setSelection(edit_cart_quantity.length());

        }else {
            float qty = Float.parseFloat(cartData.getQty());

            edit_cart_quantity.setText(""+(int)qty);
        }


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

            try {
                if (Float.parseFloat(edit_cart_quantity.getText().toString()) == 0){
                    Toasty.info(getApplicationContext(),
                            "Enter valid quantity",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
                Toasty.info(getApplicationContext(),
                        "Enter valid quantity",
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
        params.put("modifiers", modifires_ids);
        params.put("ticket_id", globalClass.getTicket_id());

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {
                int total_qty = 0;
                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        cartDataArrayList = new ArrayList<>();

                        JSONArray data = response.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++){
                            JSONObject object = data.getJSONObject(i);

                            String id = object.optString("id");
                            String item_id = object.optString("item_id");
                            String name = object.optString("name");
                            String quantity = object.optString("quantity");
                            String price = object.optString("price");
                            String cost = object.optString("cost");
                            String modifiers = object.optString("modifiers");
                            String sold_option = object.optString("sold_option");
                            String weight_quantity = object.optString("weight_quantity");

                            total_qty = total_qty + (int) Float.parseFloat(weight_quantity);


                            CartData cartData = new CartData();
                            cartData.setId(id);
                            cartData.setProduct_id(item_id);
                            cartData.setProduct_name(name);
                            cartData.setPrice(price);
                            cartData.setQty(quantity);
                            cartData.setMrp(cost);
                            cartData.setModifiers(modifiers);
                            cartData.setSold_option(sold_option);


                            double qty = Double.parseDouble(cartData.getQty());

                            String[] array = modifiers.split(",");
                            /// modifier
                            double modifier_price = 0;
                            ArrayList<ModifierItemsData> modifierItemsDataArrayList = new ArrayList<>();
                            JSONArray item_modifire = object.getJSONArray("item_modifire");
                            for (int j = 0; j < item_modifire.length(); j++){
                                JSONObject object2 = item_modifire.getJSONObject(j);

                                ModifierItemsData modifierItemsData = new ModifierItemsData();
                                modifierItemsData.setId(object2.optString("id"));
                                modifierItemsData.setName(object2.optString("modifier_option"));
                                modifierItemsData.setPrice(object2.optString("price"));

                                modifierItemsDataArrayList.add(modifierItemsData);

                                for (String ss : array){
                                    if (ss.equals(object2.optString("id"))){
                                        modifier_price = modifier_price
                                                + (Float.parseFloat(object2.optString("price")) * qty);
                                    }
                                }

                            }
                            cartData.setModifierItemsList(modifierItemsDataArrayList);


                            cartDataArrayList.add(cartData);


                            double price_ = Float.parseFloat(cartData.getPrice());

                            double total_price = price_ * qty;
                            total_price = total_price + modifier_price;
                            total_cart_value = total_cart_value + total_price;

                        }

                        Collections.reverse(cartDataArrayList);

                    }

                    globalClass.setCart_counter(""+total_qty);
                    getSupportActionBar().setTitle("Cart ("+total_qty+")");
                    setCartData();

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
        params.put("ticket_id", globalClass.getTicket_id());

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {
                int total_qty = 0;
                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        cartDataArrayList = new ArrayList<>();

                        JSONArray data = response.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++){
                            JSONObject object = data.getJSONObject(i);

                            String id = object.optString("id");
                            String item_id = object.optString("item_id");
                            String name = object.optString("name");
                            String quantity = object.optString("quantity");
                            String price = object.optString("price");
                            String cost = object.optString("cost");
                            String modifiers = object.optString("modifiers");
                            String sold_option = object.optString("sold_option");
                            String weight_quantity = object.optString("weight_quantity");

                            total_qty = total_qty + (int) Float.parseFloat(weight_quantity);

                            CartData cartData = new CartData();
                            cartData.setId(id);
                            cartData.setProduct_id(item_id);
                            cartData.setProduct_name(name);
                            cartData.setPrice(price);
                            cartData.setQty(quantity);
                            cartData.setMrp(cost);
                            cartData.setModifiers(modifiers);
                            cartData.setSold_option(sold_option);


                            double qty = Double.parseDouble(cartData.getQty());

                            String[] array = modifiers.split(",");

                            /// modifier
                            double modifier_price = 0;
                            ArrayList<ModifierItemsData> modifierItemsDataArrayList = new ArrayList<>();
                            JSONArray item_modifire = object.getJSONArray("item_modifire");
                            for (int j = 0; j < item_modifire.length(); j++){
                                JSONObject object2 = item_modifire.getJSONObject(j);

                                ModifierItemsData modifierItemsData = new ModifierItemsData();
                                modifierItemsData.setId(object2.optString("id"));
                                modifierItemsData.setName(object2.optString("modifier_option"));
                                modifierItemsData.setPrice(object2.optString("price"));

                                modifierItemsDataArrayList.add(modifierItemsData);

                                for (String ss : array){
                                    if (ss.equals(object2.optString("id"))){
                                        modifier_price = modifier_price
                                                + (Float.parseFloat(object2.optString("price")) * qty);
                                    }
                                }

                            }
                            cartData.setModifierItemsList(modifierItemsDataArrayList);


                            cartDataArrayList.add(cartData);


                            double price_ = Float.parseFloat(cartData.getPrice());
                            double total_price = price_ * qty;
                            total_price = total_price + modifier_price;
                            total_cart_value = total_cart_value + total_price;

                        }

                        Collections.reverse(cartDataArrayList);

                    }

                    globalClass.setCart_counter(""+total_qty);
                    getSupportActionBar().setTitle("Cart ("+total_qty+")");
                    setCartData();

                    if (cartDataArrayList.size() == 0){
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }



    ///
    private static final int MODIFIER_REQUEST = 1231;
    String modifires_ids = "";
    private CartData selected_cartdata;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == MODIFIER_REQUEST && resultCode == Activity.RESULT_OK) {

            modifires_ids = data.getStringExtra("ids");
            String qty = data.getStringExtra("qty");
            Log.d(Commons.TAG, "modifires_ids = "+modifires_ids);

            editCart(selected_cartdata.getId(), qty);
        }
    }
}
