package com.daypos.fragments.category;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daypos.R;
import com.daypos.container.DrawerData;
import com.daypos.fragments.products.ProductList;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.GlobalClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryList extends AppCompatActivity implements
        CategoryAdapter.ItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerview) RecyclerView recycler_view;
    @BindView(R.id.edt_search) EditText edt_search;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;


    ArrayList<CategoryData> categoryDataArrayList;
    GlobalClass globalClass;
    CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);

        initViews();

    }

    private void initViews(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);


        globalClass = (GlobalClass) getApplicationContext();

        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        swipe_refresh_layout.setOnRefreshListener(this);


        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                filter(s.toString());
            }
        });

    }

    @Override
    protected void onResume() {
        getCategoryList();
        super.onResume();
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

                Intent intent = new Intent(CategoryList.this, AddCategory.class);
                startActivity(intent);


                break;

        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onItemClick(CategoryData categoryData) {

        Intent intent = new Intent(CategoryList.this, ProductList.class);
        intent.putExtra("product_data", categoryData);
        startActivity(intent);


    }

    @Override
    public void onRefresh() {
        getCategoryList();
    }

    private void getCategoryList() {
        categoryDataArrayList = new ArrayList<>();

        String url = ApiConstant.category_list;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());

        new PostDataParser(this, url, params, true,
                new PostDataParser.OnGetResponseListner() {
                    @Override
                    public void onGetResponse(JSONObject response) {
                        if (response != null) {

                            try {
                                int status = response.optInt("status");
                                String message = response.optString("message");
                                if (status == 1) {

                                    CategoryData categoryData = new CategoryData();

                                    JSONArray data = response.getJSONArray("data");

                                    for (int i = 0; i < data.length(); i++){
                                        JSONObject object = data.getJSONObject(i);

                                        categoryData = new CategoryData();

                                        categoryData.setId(object.optString("id"));
                                        categoryData.setName(object.optString("category_name"));
                                        categoryData.setColor(object.optString("category_colour"));
                                        categoryData.setItem_no(object.optString("items"));

                                        categoryDataArrayList.add(categoryData);

                                    }

                                   setCategoryData();

                                }


                                if (swipe_refresh_layout.isRefreshing()){
                                    swipe_refresh_layout.setRefreshing(false);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }


    private void setCategoryData(){

        categoryAdapter = new CategoryAdapter(CategoryList.this, categoryDataArrayList);
        recycler_view.setAdapter(categoryAdapter);
        categoryAdapter.setClickListener(this);
    }


    private void filter(String text) {
        ArrayList<CategoryData> filterdNames = new ArrayList<>();
        for (CategoryData categoryData : categoryDataArrayList) {
            if (categoryData.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(categoryData);
            }
        }
        categoryAdapter.filterList(filterdNames);
    }

}
