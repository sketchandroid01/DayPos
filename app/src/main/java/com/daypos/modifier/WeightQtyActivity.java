package com.daypos.modifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.fragments.home.ProductData;
import com.daypos.utils.Commons;
import com.daypos.utils.PriceValueFilter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeightQtyActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbar_title;
    @BindView(R.id.tv_save) TextView tv_save;
    @BindView(R.id.edit_cart_quantity) EditText edit_cart_quantity;

    private ProductData productData;

    private static DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_qty);
        ButterKnife.bind(this);
        actionViews();

    }


    private void actionViews(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_x);

        edit_cart_quantity.setFilters(new InputFilter[]{new PriceValueFilter(3)});

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            productData = (ProductData) bundle.getSerializable("datas");

            toolbar_title.setText(productData.getName());



        }


        edit_cart_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    if (s.toString().length() > 0){

                        int qty = Integer.parseInt(edit_cart_quantity.getText().toString());


                    }else {
                        //edit_cart_quantity.setText("0");
                        //edit_cart_quantity.setSelection(edit_cart_quantity.length());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        tv_save.setOnClickListener(v -> {

            Commons.hideSoftKeyboard(WeightQtyActivity.this);

            Intent intent = new Intent();
            intent.putExtra("qty", edit_cart_quantity.getText().toString());
            setResult(Activity.RESULT_OK, intent);
            finish();

        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Commons.hideSoftKeyboard(WeightQtyActivity.this);
                finish();
                break;

        }
        return (super.onOptionsItemSelected(menuItem));
    }



}
