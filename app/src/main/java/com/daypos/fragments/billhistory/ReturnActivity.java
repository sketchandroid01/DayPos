package com.daypos.fragments.billhistory;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
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
import es.dmoral.toasty.Toasty;

public class ReturnActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_total_value) TextView tv_total_value;
    @BindView(R.id.recyclerview) RecyclerView recyclerview;
    @BindView(R.id.btn_refund) Button btn_refund;


    private GlobalClass globalClass;
    private ArrayList<ProductData> productDataArrayList;
    private String order_id;
    private ReturnProductAdapter orderProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);
        ButterKnife.bind(this);


        iniViews();
    }

    private void iniViews(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);

        globalClass = (GlobalClass) getApplicationContext();
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            productDataArrayList = (ArrayList<ProductData>) bundle.getSerializable("array");

            tv_total_value.setText(bundle.getString("total"));
            order_id = bundle.getString("order_id");

            setData();
        }



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


    private void setData(){
        orderProductAdapter = new ReturnProductAdapter(ReturnActivity.this,
                productDataArrayList, btn_refund);
        recyclerview.setAdapter(orderProductAdapter);

        btn_refund.setOnClickListener(v -> {

            if (orderProductAdapter != null){
                if (orderProductAdapter.getReturnDataArrayList().size() > 0){

                    orderRefund();

                }
            }

        });
    }

    private String getReturnProductJson(){
        String json_s = "";
        try {
            JSONObject mainObj = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < orderProductAdapter.getReturnDataArrayList().size(); i++){

                ReturnData returnData = orderProductAdapter.getReturnDataArrayList().get(i);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("item_id", returnData.getItem_id());
                jsonObject.put("return_qty", returnData.getItem_qty());
                jsonObject.put("refund_amount", returnData.getRefund_price());

                jsonArray.put(jsonObject);

            }

            json_s = jsonArray.toString();
        }catch (Exception e){
            e.printStackTrace();
        }


        return json_s;
    }

    private void orderRefund() {

        productDataArrayList = new ArrayList<>();

        String url = ApiConstant.order_refund;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("order_id", order_id);
        params.put("return_product", getReturnProductJson());

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {
                        Toasty.success(getApplicationContext(),
                                message,
                                Toast.LENGTH_SHORT, true).show();
                        OrderDetailsActivity.activity.finish();
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

        });
    }

}
