package com.daypos.fragments.products;

import android.content.Intent;
import android.os.Bundle;
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
import com.daypos.fragments.category.AddCategory;
import com.daypos.fragments.home.ProductAdapter;
import com.daypos.fragments.home.ProductData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductList extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerview) RecyclerView recycler_view;
    @BindView(R.id.edt_search) EditText edt_search;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        ButterKnife.bind(this);

        initViews();

    }

    private void initViews(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);


        recycler_view.setLayoutManager(new LinearLayoutManager(this));


        ArrayList<ProductData> productDataArrayList = new ArrayList<>();
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());


        ProductAdapter productAdapter = new ProductAdapter(ProductList.this, productDataArrayList);
        recycler_view.setAdapter(productAdapter);



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



}
