package com.daypos.draftorder;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.GlobalClass;
import com.daypos.utils.Preferense;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class DraftListActivity extends AppCompatActivity implements
        TicketListAdapter.ItemClickListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbar_title;
    @BindView(R.id.recycler_view) RecyclerView recycler_view;


    private static DecimalFormat df = new DecimalFormat("0.00");

    private GlobalClass globalClass;
    private Preferense preferense;

    private ArrayList<TicketData> ticketDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_list);
        ButterKnife.bind(this);
        actionViews();

    }


    private void actionViews(){

        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_x);

        globalClass = (GlobalClass) getApplicationContext();
        preferense = new Preferense(this);
        ticketDataArrayList = new ArrayList<>();



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

                        JSONArray data = response.getJSONArray("data");
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

                        }

                        setData();
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

    private void setData(){
        TicketListAdapter ticketListAdapter =
                new TicketListAdapter(DraftListActivity.this, ticketDataArrayList);
        recycler_view.setAdapter(ticketListAdapter);
        ticketListAdapter.setClickListener(this);
    }

    @Override
    public void onItemClick(TicketData ticketData) {
        globalClass.setTicket_id(ticketData.getTicket_id());
        globalClass.setTicket_name(ticketData.getTicket_name());
        finish();
    }


}
