package com.daypos.fragments.customers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.daypos.utils.GlobalClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class Customers extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        CustomerAdapter.ItemClickListener {

    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipe_refresh_layout;
    @BindView(R.id.edt_search)
    EditText edt_search;


    private ArrayList<CustomerData> customerDataArrayList;
    private GlobalClass globalClass;

    public Customers() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_customer, container, false);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cat, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_cat:

                dialogAddCustomer();

                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogAddCustomer(){

        DialogAddCustomer dialogAddCustomer = new DialogAddCustomer(getActivity());
        dialogAddCustomer.show();
    }


    private void viewsAction(){

        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        globalClass = (GlobalClass) getActivity().getApplicationContext();
        customerDataArrayList = new ArrayList<>();

        swipe_refresh_layout.setOnRefreshListener(this);


        getCustomerList();

    }


    @Override
    public void onRefresh() {
        getCustomerList();
    }

    private void getCustomerList() {

        customerDataArrayList = new ArrayList<>();

        String url = ApiConstant.customer_list_by_userID;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());

        new PostDataParser(getActivity(), url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        JSONArray data = response.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++){
                            JSONObject object = data.getJSONObject(i);

                            String id = object.optString("id");
                            String name = object.optString("name");
                            String image = object.optString("image");
                            String customerId = object.optString("customerId");
                            String email = object.optString("email");
                            String phone = object.optString("phone");
                            String purchase_amount = object.optString("purchase_amount");
                            String points_balance = object.optString("points_balance");
                            String note = object.optString("note");
                            String address = object.optString("address");

                            CustomerData customerData = new CustomerData();
                            customerData.setId(id);
                            customerData.setName(name);
                            customerData.setEmail(email);
                            customerData.setPhone(phone);
                            customerData.setCustomer_id(customerId);

                            customerDataArrayList.add(customerData);
                        }


                        setCustomerData();
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


    private void setCustomerData(){

        CustomerAdapter customerAdapter = new CustomerAdapter(getActivity(),
                customerDataArrayList);
        recycler_view.setAdapter(customerAdapter);
        customerAdapter.setClickListener(this);

    }

    @Override
    public void onItemClick(CustomerData customerData) {

        DialogEditCustomer dialogEditCustomer =
                new DialogEditCustomer(getActivity(), customerData);
        dialogEditCustomer.show();

        dialogEditCustomer.setOnDismissListener(dialog -> {
            if (dialogEditCustomer.isEdited == 1){
                getCustomerList();
            }
        });

    }
}
