package com.daypos.fragments.products;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.fragments.category.ColorAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddProduct extends AppCompatActivity implements
        View.OnClickListener,
        ColorAdapter.ItemClickListener{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_colors) RecyclerView recycler_colors;
    @BindView(R.id.tv_save_cat) TextView tv_save_cat;
    @BindView(R.id.radio_color) RadioButton radio_color;
    @BindView(R.id.radio_image) RadioButton radio_image;
    @BindView(R.id.linear_select_image) LinearLayout linear_select_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        initViews();
    }



    private void initViews() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Product");
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

        ColorAdapter colorAdapter = new ColorAdapter(AddProduct.this, colorList);
        recycler_colors.setAdapter(colorAdapter);
        colorAdapter.setClickListener(this);


        radio_color.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    recycler_colors.setVisibility(View.VISIBLE);
                    linear_select_image.setVisibility(View.GONE);
                }

            }
        });

        radio_image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    recycler_colors.setVisibility(View.GONE);
                    linear_select_image.setVisibility(View.VISIBLE);
                }

            }
        });

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


    @Override
    public void onItemClick(String color_code) {

    }
}
