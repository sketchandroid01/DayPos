package com.daypos.fragments.products;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daypos.R;
import com.daypos.barcodeLibs.FullScannerActivity;
import com.daypos.cart.CartActivity;
import com.daypos.fragments.home.ProductAdapter;
import com.daypos.fragments.home.ProductData;
import com.daypos.modifier.ModifierActivity;
import com.daypos.modifier.ModifierItemsData;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.CircleAnimationUtil;
import com.daypos.utils.Commons;
import com.daypos.utils.GlobalClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class SearchProductList extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        ProductAdapter.ItemClickListener,
        ProductAdapter.ItemClickListenerFav{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerview) RecyclerView recycler_view;
    @BindView(R.id.edt_search) EditText edt_search;
    @BindView(R.id.iv_search) ImageView iv_search;
    @BindView(R.id.iv_barcode_search) ImageView iv_barcode_search;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;


    public static TextView cart_counter;
    private RelativeLayout cart_relativeLayout;

    GlobalClass globalClass;
    private int start_index = 0;
    private int limit = 60;
    private String weight_qty = "0";
    private ArrayList<ProductData> productDataArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        ButterKnife.bind(this);

        initViews();

    }

    private void initViews(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);

        globalClass = (GlobalClass)getApplicationContext();
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        swipe_refresh_layout.setOnRefreshListener(this);

        getProductCategoryWise("all");

        iv_search.setOnClickListener(v -> {

            if (edt_search.getText().toString().trim().length() == 0){
                Toasty.info(getApplicationContext(),
                        "Enter search keyword",
                        Toast.LENGTH_SHORT, true).show();
                return;
            }

            searchProduct(edt_search.getText().toString());
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 1){
                    searchProduct(s.toString());
                }

                if (s.toString().length() == 0){
                    getProductCategoryWise("all");
                }
            }
        });

        iv_barcode_search.setOnClickListener(v -> {
            launchActivity(FullScannerActivity.class);
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cat, menu);

        MenuItem menuItem = menu.findItem(R.id.add_cat);

        MenuItemCompat.setActionView(menuItem, R.layout.cart_counter);
        cart_relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(menuItem);
        cart_counter = cart_relativeLayout.findViewById(R.id.tv_cart_counter);

        cart_counter.setText(globalClass.getCart_counter());
        cart_relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SearchProductList.this, CartActivity.class);
                startActivity(intent);

            }
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
    protected void onResume() {
        try {
            cart_counter.setText(globalClass.getCart_counter());
        }catch (Exception E){
            E.printStackTrace();
        }

        super.onResume();
    }

    @Override
    public void onRefresh() {
        getProductCategoryWise("all");
    }

    private void getProductCategoryWise(String category) {

        productDataArrayList = new ArrayList<>();

        String url = ApiConstant.filterProductCategoryWise;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("category_id", category);
        params.put("start", String.valueOf(start_index));
        params.put("limit", String.valueOf(limit));

        new PostDataParser(this, url, params, true,
                new PostDataParser.OnGetResponseListner() {
                    @Override
                    public void onGetResponse(JSONObject response) {
                        if (response != null) {

                            try {
                                int status = response.optInt("status");
                                String message = response.optString("message");
                                if (status == 1) {
                                    JSONArray item_list = response.getJSONArray("item_list");

                                    for (int i = 0; i < item_list.length(); i++){
                                        JSONObject object = item_list.getJSONObject(i);


                                        ProductData productData = new ProductData();
                                        productData.setId(object.optString("id"));
                                        productData.setName(object.optString("name"));
                                        productData.setPrice(object.optString("price"));
                                        productData.setSku(object.optString("sku"));
                                        productData.setBar_code(object.optString("bar_code"));
                                        productData.setSold_option(object.optString("sold_option"));

                                        if (object.optString("item_image").isEmpty()){
                                            productData.setImage("");
                                        }else {
                                            productData.setImage(ApiConstant.IMAGE_PATH
                                                    + object.optString("item_image"));
                                        }

                                        productData.setTaxes(object.optString("taxes"));
                                        productData.setItem_color(object.optString("item_color"));
                                        productData.setIs_modifier(object.optString("is_attribute"));
                                        productData.setIs_fav(object.optString("fav"));



                                        /// modifier
                                        productData.setIs_modifier(object.optString("is_modifire"));
                                        ArrayList<ModifierItemsData> modifierItemsDataArrayList = new ArrayList<>();
                                        if (productData.getIs_modifier().equals("1")){

                                            JSONArray modifire = object.getJSONArray("modifire");
                                            for (int j = 0; j < modifire.length(); j++){
                                                JSONObject object2 = modifire.getJSONObject(j);

                                                ModifierItemsData modifierItemsData = new ModifierItemsData();
                                                modifierItemsData.setId(object2.optString("id"));
                                                modifierItemsData.setName(object2.optString("modifier_option"));
                                                modifierItemsData.setPrice(object2.optString("price"));

                                                modifierItemsDataArrayList.add(modifierItemsData);

                                            }

                                        }
                                        productData.setModifierList(modifierItemsDataArrayList);


                                        productDataArrayList.add(productData);
                                    }

                                }


                                setProductData();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            swipe_refresh_layout.setRefreshing(false);

                        }
                    }
                });
    }

    private void setProductData(){

        ProductAdapter productAdapter =
                new ProductAdapter(SearchProductList.this, productDataArrayList);
        recycler_view.setAdapter(productAdapter);
        productAdapter.setClickListener(this);
        productAdapter.setClickListenerFav(this);

    }

    @Override
    public void onItemClick(ProductData productData, View view) {

        if (productData.getIs_modifier().equals("1")){
            Intent intent = new Intent(SearchProductList.this, ModifierActivity.class);
            intent.putExtra("datas", productData);
            startActivityForResult(intent, MODIFIER_REQUEST);
            weight_qty = "1";
            selected_product = productData;
            selected_view = view;
        }else {
            weight_qty = "1";
            makeFlyAnimation(view, productData.getId());
        }

    }

    private void makeFlyAnimation(View view, String id) {

        new CircleAnimationUtil().attachActivity(SearchProductList.this)
                .setTargetView(view)
                .setMoveDuration(500)
                .setDestView(cart_relativeLayout)
                .setAnimationListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //addItemToCart();

                        addToCart(id);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).startAnimation();


    }

    private void addToCart(String product_id) {

        String url = ApiConstant.add_to_cart;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("item_id", product_id);
        params.put("modifiers", modifires_ids);
        params.put("type", "1");
        params.put("weight_quantity", weight_qty);
        params.put("ticket_id", globalClass.getTicket_id());

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        String count = response.optString("count");

                        float fff = Float.parseFloat(count);
                        cart_counter.setText(""+(int)fff);

                        globalClass.setCart_counter(""+(int)fff);

                        /*Toasty.success(getApplicationContext(),
                                "Added",
                                Toast.LENGTH_SHORT, true).show();*/

                        modifires_ids = "";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void searchProduct(String search_key) {

        productDataArrayList = new ArrayList<>();

        String url = ApiConstant.search_item_list;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("search_keyword", search_key);
        params.put("bar_code", "");

        new PostDataParser(this, url, params, true,
                new PostDataParser.OnGetResponseListner() {
                    @Override
                    public void onGetResponse(JSONObject response) {
                        if (response != null) {

                            try {
                                int status = response.optInt("status");
                                String message = response.optString("message");
                                if (status == 1) {
                                    JSONArray item_list = response.getJSONArray("data");

                                    for (int i = 0; i < item_list.length(); i++){
                                        JSONObject object = item_list.getJSONObject(i);


                                        ProductData productData = new ProductData();
                                        productData.setId(object.optString("id"));
                                        productData.setName(object.optString("name"));
                                        productData.setPrice(object.optString("price"));
                                        productData.setSku(object.optString("sku"));
                                        productData.setBar_code(object.optString("bar_code"));
                                        productData.setSold_option(object.optString("sold_option"));


                                        if (object.optString("item_image").isEmpty()){
                                            productData.setImage("");
                                        }else {
                                            productData.setImage(ApiConstant.IMAGE_PATH
                                                    + object.optString("item_image"));
                                        }

                                        productData.setTaxes(object.optString("taxes"));
                                        productData.setItem_color(object.optString("item_color"));
                                        productData.setIs_modifier(object.optString("is_attribute"));
                                        productData.setIs_fav(object.optString("fav"));



                                        /// modifier
                                        productData.setIs_modifier(object.optString("is_modifire"));
                                        ArrayList<ModifierItemsData> modifierItemsDataArrayList = new ArrayList<>();
                                        if (productData.getIs_modifier().equals("1")){

                                            JSONArray modifire = object.getJSONArray("modifire");
                                            for (int j = 0; j < modifire.length(); j++){
                                                JSONObject object2 = modifire.getJSONObject(j);

                                                ModifierItemsData modifierItemsData = new ModifierItemsData();
                                                modifierItemsData.setId(object2.optString("id"));
                                                modifierItemsData.setName(object2.optString("modifier_option"));
                                                modifierItemsData.setPrice(object2.optString("price"));

                                                modifierItemsDataArrayList.add(modifierItemsData);

                                            }

                                        }
                                        productData.setModifierList(modifierItemsDataArrayList);



                                        productDataArrayList.add(productData);
                                    }

                                }

                                setProductData();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            swipe_refresh_layout.setRefreshing(false);

                        }
                    }
                });
    }


    @Override
    public void onItemClickFav(ProductData productData) {
        addOrRemoveFav(productData);
    }

    private void addOrRemoveFav(ProductData productData) {

        String url;
        if (productData.getIs_fav().equals("1")){
            url = ApiConstant.delete_favourite;
        }else {
            url = ApiConstant.add_to_favourite;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("item_id", productData.getId());

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        Toasty.success(getApplicationContext(),
                                message, Toast.LENGTH_SHORT, true).show();

                        getProductCategoryWise("all");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }


    ////////// goto barcode scanner ...
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }



    ////
    private static final int MODIFIER_REQUEST = 1231;
    private ProductData selected_product;
    private View selected_view;
    String modifires_ids = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == MODIFIER_REQUEST && resultCode == Activity.RESULT_OK) {

            modifires_ids = data.getStringExtra("ids");
            Log.d(Commons.TAG, "modifires_ids = "+modifires_ids);

            makeFlyAnimation(selected_view, selected_product.getId());
        }
    }

}
