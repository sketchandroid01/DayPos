package com.daypos.fragments.billhistory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daypos.R;
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
import butterknife.Unbinder;

public class FragBillHistory extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        OrdersListAdapter.ViewClickListener{

    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipe_refresh_layout;
    @BindView(R.id.edt_search)
    EditText edt_search;

    private GlobalClass globalClass;
    private Preferense preferense;

    private ArrayList<Orders> ordersArrayList;
    private ArrayList<ListItem> mListItem;

    public FragBillHistory() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_billhistory, container, false);
        unbinder = ButterKnife.bind(this, view);

        viewsAction();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        getOrdersList();
        super.onResume();
    }

    private void viewsAction(){

        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        globalClass = (GlobalClass) getActivity().getApplicationContext();
        preferense = new Preferense(getActivity());

        swipe_refresh_layout.setOnRefreshListener(this);

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


    }

    @Override
    public void onRefresh() {
        getOrdersList();
    }

    private void getOrdersList() {

        swipe_refresh_layout.setRefreshing(true);

        ordersArrayList = new ArrayList<>();

        String url = ApiConstant.order_list;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("employee_id", preferense.getString(Preferense.employee_id));

        new PostDataParser(getActivity(), url, params, false, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        JSONArray data = response.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++){
                            JSONObject object = data.getJSONObject(i);


                            String date = object.optString("date");

                            Orders orders = new Orders();
                            orders.setDate(Commons.convertDate1(date));

                            ArrayList<OrderDetails> list = new ArrayList<>();
                            JSONArray orderlist = object.getJSONArray("orderlist");
                            for (int j = 0; j < orderlist.length(); j++){
                                JSONObject object1 = orderlist.getJSONObject(j);

                                String id = object1.optString("id");
                                String customer_id = object1.optString("customer_id");
                                String user_id = object1.optString("user_id");
                                String total_item = object1.optString("total_item");
                                String created = object1.optString("created");
                                String total_amount = object1.optString("total_amount");
                                String coupon_name = object1.optString("coupon_name");
                                String discount_amount = object1.optString("discount_amount");
                                String payment_mode = object1.optString("payment_mode");
                                String invoice = object1.optString("invoice");
                                String is_return = object1.optString("is_return");
                                String return_id = object1.optString("return_id");
                                String cashier = object1.optString("cashier");

                                OrderDetails orderDetails = new OrderDetails();
                                orderDetails.setId(id);
                                orderDetails.setDate(created);
                                orderDetails.setTime(Commons.convertToTime(created));
                                orderDetails.setPayment_type(payment_mode);
                                orderDetails.setTotal_amount(total_amount);
                                orderDetails.setTotal_item(total_item);
                                orderDetails.setBill_no(invoice);
                                orderDetails.setIs_returned(is_return);
                                orderDetails.setReturn_no(return_id);
                                orderDetails.setCashier(cashier);

                                list.add(orderDetails);
                            }

                            orders.setOrderDetailsArrayList(list);

                            ordersArrayList.add(orders);
                        }

                        setData();

                    }

                    if (swipe_refresh_layout.isRefreshing()){
                        swipe_refresh_layout.setRefreshing(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }




    private void setData(){


        mListItem = new ArrayList<>();
        for (Orders orders : ordersArrayList){

            HeaderItem header = new HeaderItem();
            header.setHeader(orders.getDate());
            mListItem.add(header);

            for (OrderDetails orderDetails : orders.getOrderDetailsArrayList()) {
                ChildItem childItem = new ChildItem();
                childItem.setOrderDetails(orderDetails);
                mListItem.add(childItem);

            }

        }

        OrdersListAdapter ordersListAdapter = new OrdersListAdapter(getActivity(), mListItem);
        recycler_view.setAdapter(ordersListAdapter);
        ordersListAdapter.setViewClickListener(this);

    }

    @Override
    public void onClicked(OrderDetails orderDetails) {

        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
        intent.putExtra("datas", orderDetails);
        startActivity(intent);

    }



}
