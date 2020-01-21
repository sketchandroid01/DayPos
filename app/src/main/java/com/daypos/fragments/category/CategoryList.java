package com.daypos.fragments.category;

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
import com.daypos.container.DrawerData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryList extends AppCompatActivity implements
        CategoryAdapter.ItemClickListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerview) RecyclerView recycler_view;
    @BindView(R.id.edt_search) EditText edt_search;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;


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


        recycler_view.setLayoutManager(new LinearLayoutManager(this));


        ArrayList<CategoryData> categoryDataArrayList = new ArrayList<>();

        CategoryData categoryData = new CategoryData();
        categoryDataArrayList.add(categoryData);
        categoryDataArrayList.add(categoryData);
        categoryDataArrayList.add(categoryData);
        categoryDataArrayList.add(categoryData);
        categoryDataArrayList.add(categoryData);
        categoryDataArrayList.add(categoryData);

        CategoryAdapter categoryAdapter = new CategoryAdapter(CategoryList.this,
                categoryDataArrayList);
        recycler_view.setAdapter(categoryAdapter);
        categoryAdapter.setClickListener(this);


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

    }



}
