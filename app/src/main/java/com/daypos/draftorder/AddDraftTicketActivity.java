package com.daypos.draftorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.fragments.category.CategoryData;
import com.daypos.fragments.category.ColorAdapter;
import com.daypos.fragments.products.AddProduct;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.Commons;
import com.daypos.utils.GlobalClass;
import com.daypos.utils.Preferense;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class AddDraftTicketActivity extends AppCompatActivity implements
        View.OnClickListener,
        TicketListAdapter2.ItemClickListener{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.edt_ticket_name) EditText edt_ticket_name;
    @BindView(R.id.edt_comment) EditText edt_comment;
    @BindView(R.id.tv_save) TextView tv_save;
    @BindView(R.id.linear_list) LinearLayout linear_list;
    @BindView(R.id.recycler_view) RecyclerView recycler_view;


    private GlobalClass globalClass;
    private Preferense preferense;
    private ArrayList<TicketData> ticketDataArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ticket);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_x);

        tv_save.setOnClickListener(this);
        globalClass = (GlobalClass) getApplicationContext();
        preferense = new Preferense(this);

        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        linear_list.setVisibility(View.GONE);

        getTicket();

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

        if (v == tv_save){

            if (edt_ticket_name.getText().toString().trim().length() == 0){
                Toasty.info(getApplicationContext(),
                        "Enter ticket name",
                        Toast.LENGTH_SHORT, true).show();
                return;
            }

            addTicket();

        }

    }


    private void addTicket() {

        String url = ApiConstant.add_ticket;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("employee_id", preferense.getString(Preferense.employee_id));
        params.put("ticket_name", edt_ticket_name.getText().toString());
        params.put("comment", edt_comment.getText().toString());
        params.put("store_id", "");


        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        Commons.hideSoftKeyboard(AddDraftTicketActivity.this);

                        String ticket_id = response.optString("ticket_id");

                        convert_cart_to_draft(ticket_id);

                        /*Toasty.success(getApplicationContext(),
                                message,
                                Toast.LENGTH_SHORT, true).show();*/

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

    private void convert_cart_to_draft(String ticket_id) {

        String url = ApiConstant.convert_cart_to_draft;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("ticket_id", ticket_id);

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        globalClass.setTicket_id("");
                        globalClass.setTicket_name("");
                        globalClass.setCart_counter("0");
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


    /////
    private void getTicket() {

        ticketDataArrayList = new ArrayList<>();

        String url = ApiConstant.get_ticket;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("employee_id", preferense.getString(Preferense.employee_id));

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        /*JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++){
                            JSONObject object = data.getJSONObject(i);

                            TicketData ticketData = new TicketData();
                            ticketData.setTicket_id(object.optString("ticket_id"));
                            ticketData.setTicket_name(object.optString("ticket_name"));
                            ticketData.setComment(object.optString("comment"));
                            ticketData.setTotal_quantity(object.optString("total_quantity"));
                            ticketData.setTotal_amount(object.optString("total_amount"));
                            ticketData.setCreated_at(object.optString("created_at"));

                            ticketDataArrayList.add(ticketData);

                        }*/


                        JSONArray table_list = response.getJSONArray("table_list");
                        for (int i = 0; i < table_list.length(); i++){
                            JSONObject object = table_list.getJSONObject(i);

                            TicketData ticketData = new TicketData();
                            ticketData.setTicket_id(object.optString("id"));
                            ticketData.setTicket_name(object.optString("ticket_name"));
                            ticketData.setCreated_at(object.optString("created_at"));

                            ticketDataArrayList.add(ticketData);

                        }




                        setData();

                        if (ticketDataArrayList.size() > 0){
                            linear_list.setVisibility(View.VISIBLE);
                        }

                    }else {
                        linear_list.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void setData(){
        TicketListAdapter2 ticketListAdapter =
                new TicketListAdapter2(AddDraftTicketActivity.this, ticketDataArrayList);
        recycler_view.setAdapter(ticketListAdapter);
        ticketListAdapter.setClickListener(this);
    }

    @Override
    public void onItemClick(TicketData ticketData) {
        convert_cart_to_draft(ticketData.getTicket_id());
    }


}
