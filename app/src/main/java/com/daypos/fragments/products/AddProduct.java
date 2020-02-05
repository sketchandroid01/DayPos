package com.daypos.fragments.products;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.fragments.category.ColorAdapter;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.Commons;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class AddProduct extends AppCompatActivity implements
        View.OnClickListener,
        ColorAdapter.ItemClickListener{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_colors) RecyclerView recycler_colors;
    @BindView(R.id.tv_save_cat) TextView tv_save_cat;
    @BindView(R.id.radio_color) RadioButton radio_color;
    @BindView(R.id.radio_image) RadioButton radio_image;
    @BindView(R.id.linear_select_image) LinearLayout linear_select_image;
    @BindView(R.id.edt_product_name) EditText edt_product_name;
    @BindView(R.id.edt_selling_price) EditText edt_selling_price;
    @BindView(R.id.edt_sku) EditText edt_sku;
    @BindView(R.id.edt_barcode) EditText edt_barcode;
    @BindView(R.id.spinner_category) Spinner spinner_category;
    @BindView(R.id.spinner_unit) Spinner spinner_unit;
    @BindView(R.id.iv_gallery) ImageView iv_gallery;
    @BindView(R.id.iv_camera) ImageView iv_camera;


    private String selected_color_code;

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
        iv_camera.setOnClickListener(this);
        iv_gallery.setOnClickListener(this);

        selected_color_code = "#c2c2c2";
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

        ColorAdapter colorAdapter = new ColorAdapter(AddProduct.this,
                colorList, selected_color_code);
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




    private void addProduct() {

        String url = ApiConstant.addEditCategory;

        HashMap<String, String> params = new HashMap<>();

        params.put("category_colour", selected_color_code);
        params.put("category_id", "");

        new PostDataParser(this, url, params, true,
                new PostDataParser.OnGetResponseListner() {
                    @Override
                    public void onGetResponse(JSONObject response) {
                        if (response != null) {

                            try {
                                int status = response.optInt("status");
                                String message = response.optString("message");
                                if (status == 1) {

                                    Commons.hideSoftKeyboard(AddProduct.this);

                                    finish();

                                }else {

                                    Toasty.error(getApplicationContext(),
                                            message,
                                            Toast.LENGTH_SHORT, true).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }
}
