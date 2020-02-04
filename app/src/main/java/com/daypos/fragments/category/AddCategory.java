package com.daypos.fragments.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.fragments.products.AddProduct;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.Commons;
import com.daypos.utils.GlobalClass;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class AddCategory extends AppCompatActivity implements
        View.OnClickListener,
        ColorAdapter.ItemClickListener{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_colors) RecyclerView recycler_colors;
    @BindView(R.id.tv_save_cat) TextView tv_save_cat;
    @BindView(R.id.edt_cat_name) EditText edt_cat_name;
    @BindView(R.id.btn_create_item) Button btn_create_item;


    private GlobalClass globalClass;
    private String selected_color_code;

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
        btn_create_item.setOnClickListener(this);
        globalClass = (GlobalClass) getApplicationContext();

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

        ColorAdapter colorAdapter = new ColorAdapter(AddCategory.this, colorList);
        recycler_colors.setAdapter(colorAdapter);
        colorAdapter.setClickListener(this);



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

            if (edt_cat_name.getText().toString().trim().length() == 0){
                Toasty.info(getApplicationContext(),
                        "Enter category name",
                        Toast.LENGTH_SHORT, true).show();
                return;
            }

            addCategory();

        }else if (v == btn_create_item){

            Intent intent = new Intent(AddCategory.this, AddProduct.class);
            startActivity(intent);
        }

    }


    @Override
    public void onItemClick(String color_code) {
        selected_color_code = color_code;
    }



    private void addCategory() {

        String url = ApiConstant.addEditCategory;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("category_name", edt_cat_name.getText().toString());
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

                                    Commons.hideSoftKeyboard(AddCategory.this);

                                    finish();

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }

}
