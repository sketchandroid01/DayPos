package com.daypos.fragments.category;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddCategory extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_colors) RecyclerView recycler_colors;
    @BindView(R.id.tv_save_cat) TextView tv_save_cat;
    @BindView(R.id.switch_parent) Switch switch_parent;
    @BindView(R.id.edt_parent_name)
    EditText edt_parent_name;
    @BindView(R.id.edt_cat_name) EditText edt_cat_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        ButterKnife.bind(this);

        initViews();
    }



    private void initViews() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);

        tv_save_cat.setOnClickListener(this);

        ArrayList<String> colorList = new ArrayList<>();
        colorList.add("#c2c2c2");
        colorList.add("#f26522");
        colorList.add("#ed1c24");
        colorList.add("#a864a8");
        colorList.add("#00bff3");
        colorList.add("#57d7df");
        colorList.add("#f5989d");
        colorList.add("#00a651");

        recycler_colors.setLayoutManager(new GridLayoutManager(this, 4));

        ColorAdapter colorAdapter = new ColorAdapter(AddCategory.this, colorList);
        recycler_colors.setAdapter(colorAdapter);




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
    public void onClick(View v) {

        if (v == tv_save_cat){


        }

    }
}
