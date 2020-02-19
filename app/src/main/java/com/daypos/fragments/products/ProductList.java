package com.daypos.fragments.products;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daypos.R;
import com.daypos.fragments.category.CategoryData;
import com.daypos.fragments.home.ProductAdapter;
import com.daypos.fragments.home.ProductData;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.GlobalClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductList extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        ProductAdapter.ItemClickListener,
        ProductAdapter.ItemClickListenerFav{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerview) RecyclerView recycler_view;
    @BindView(R.id.edt_search) EditText edt_search;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;

    GlobalClass globalClass;
    private int start_index = 0;
    private int limit = 50;
    private ArrayList<ProductData> productDataArrayList;
    private CategoryData categoryData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        ButterKnife.bind(this);

        initViews();

    }

    private void initViews(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);

        globalClass = (GlobalClass)getApplicationContext();
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        swipe_refresh_layout.setOnRefreshListener(this);


        getProductCategoryWise("all");



        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cat, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.add_cat:

                Intent intent = new Intent(ProductList.this, AddProduct.class);
                startActivity(intent);


                break;

        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onRefresh() {

        if (categoryData == null){
            getProductCategoryWise("all");
        }else {
            getProductCategoryWise(categoryData.getId());
        }



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

                                        if (object.optString("item_image").isEmpty()){
                                            productData.setImage("");
                                        }else {
                                            productData.setImage(ApiConstant.IMAGE_PATH
                                                    + object.optString("item_image"));
                                        }


                                        productData.setTaxes(object.optString("taxes"));
                                        productData.setItem_color(object.optString("item_color"));
                                        productData.setIs_attribute(object.optString("is_attribute"));


                                        productDataArrayList.add(productData);
                                    }

                                }else {

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
                new ProductAdapter(ProductList.this, productDataArrayList);
        recycler_view.setAdapter(productAdapter);
        productAdapter.setClickListener(this);
        productAdapter.setClickListenerFav(this);

    }

    @Override
    public void onItemClick(ProductData productData, View view) {

    }

    @Override
    public void onItemClickFav(ProductData productData) {

    }

    private void searchProduct(String search_key) {

        productDataArrayList = new ArrayList<>();

        String url = ApiConstant.filterProductCategoryWise;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("search_keyword", search_key);

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
                                        productData.setImage(object.optString("item_image"));
                                        productData.setTaxes(object.optString("taxes"));
                                        productData.setItem_color(object.optString("item_color"));
                                        productData.setIs_attribute(object.optString("is_attribute"));


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

}
