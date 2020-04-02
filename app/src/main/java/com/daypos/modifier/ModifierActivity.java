package com.daypos.modifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ModifierActivity extends AppCompatActivity implements
        ModifierAdapter.ItemOnChangeAction {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbar_title;
    @BindView(R.id.tv_save) TextView tv_save;
    @BindView(R.id.tv_modifier_name) TextView tv_modifier_name;
    @BindView(R.id.recycler_modifier) RecyclerView recycler_modifier;
    @BindView(R.id.cart_minus_img) ImageView cart_minus_img;
    @BindView(R.id.cart_plus_img) ImageView cart_plus_img;
    @BindView(R.id.edit_cart_quantity) EditText edit_cart_quantity;


    private ProductData productData;
    private ModifierAdapter modifierAdapter;
    private ArrayList<String> listPrices;

    private static DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_list);
        ButterKnife.bind(this);
        actionViews();

    }


    private void actionViews(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_x);

        listPrices = new ArrayList<>();
        recycler_modifier.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            productData = (ProductData) bundle.getSerializable("datas");


            toolbar_title.setText(productData.getName() + " " +productData.getPrice());

            modifierAdapter = new ModifierAdapter(ModifierActivity.this,
                    productData.getModifierList());
            recycler_modifier.setAdapter(modifierAdapter);
            modifierAdapter.setOnChangeAction(this);


            calculatePrice();
        }



        cart_minus_img.setOnClickListener(v -> {

            try {
                int count = Integer.parseInt(edit_cart_quantity.getText().toString());
                if (count >= 2){
                    count--;
                    edit_cart_quantity.setText(String.valueOf(count));
                    edit_cart_quantity.setSelection(edit_cart_quantity.length());
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            calculatePrice();

        });
        cart_plus_img.setOnClickListener(v -> {

            try {
                int count = Integer.parseInt(edit_cart_quantity.getText().toString());
                count++;
                edit_cart_quantity.setText(String.valueOf(count));
                edit_cart_quantity.setSelection(edit_cart_quantity.length());
            }catch (Exception e){
                e.printStackTrace();
            }

            calculatePrice();

        });
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

                        calculatePrice();

                    }else {
                        edit_cart_quantity.setText("0");
                        edit_cart_quantity.setSelection(edit_cart_quantity.length());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        tv_save.setOnClickListener(v -> {

            String ids = TextUtils.join(",", modifierAdapter.getListIds());

            Intent intent = new Intent();
            intent.putExtra("ids", ids);
            setResult(Activity.RESULT_OK, intent);
            finish();

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
    public void onChangeAction(ArrayList<String> listPrices) {
        this.listPrices = listPrices;

        calculatePrice();
    }

    private void calculatePrice(){

        int qty = Integer.parseInt(edit_cart_quantity.getText().toString());

        double p_price = Double.parseDouble(productData.getPrice());
        p_price = p_price * qty;

        double mod_price = 0;
        for (String pr : listPrices){
            double m_price = Double.parseDouble(pr);
            m_price = m_price * qty;

            mod_price = mod_price + m_price;
        }

        p_price = p_price + mod_price;

        toolbar_title.setText(productData.getName() + " " +df.format(p_price));

    }


}
