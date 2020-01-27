package com.daypos.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daypos.R;
import com.daypos.cart.CartActivity;
import com.daypos.fragments.customers.DialogAddCustomer;
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

public class Home extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.recyclerview) RecyclerView recyclerview;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipe_refresh_layout;
    @BindView(R.id.spinner_cat)
    Spinner spinner_cat;

    public static TextView cart_counter;


    private GlobalClass globalClass;

    private ArrayList<CategoryData> categoryDataArrayList;
    private int start_index = 1;
    private int limit = 20;

    public Home() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home, container, false);
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
        inflater.inflate(R.menu.main, menu);

        MenuItem menuItem = menu.findItem(R.id.cart);

        MenuItemCompat.setActionView(menuItem, R.layout.cart_counter);
        RelativeLayout relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(menuItem);
        cart_counter = relativeLayout.findViewById(R.id.tv_cart_counter);

        cart_counter.setText("0");
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);

            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_customer:

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

        globalClass = (GlobalClass) getActivity().getApplicationContext();

        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));


        ArrayList<ProductData> productDataArrayList = new ArrayList<>();
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());
        productDataArrayList.add(new ProductData());


        ProductAdapter productAdapter = new ProductAdapter(getActivity(), productDataArrayList);
        recyclerview.setAdapter(productAdapter);


        categoryDataArrayList = new ArrayList<>();




        getCategoryList();


    }

    private void getCategoryList() {

        String url = ApiConstant.category_list;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());

        new PostDataParser(getActivity(), url, params, true,
                new PostDataParser.OnGetResponseListner() {
                    @Override
                    public void onGetResponse(JSONObject response) {
                        if (response != null) {

                            try {
                                int status = response.optInt("status");
                                String message = response.optString("message");
                                if (status == 1) {
                                    JSONArray data = response.getJSONArray("data");

                                    for (int i = 0; i < data.length(); i++){
                                        JSONObject object = data.getJSONObject(i);

                                        CategoryData categoryData = new CategoryData();

                                        categoryData.setId(object.optString("id"));
                                        categoryData.setName(object.optString("category_name"));
                                        categoryData.setColor_code(object.optString("category_colour"));
                                        categoryData.setItem_no(object.optString("items"));

                                        categoryDataArrayList.add(categoryData);

                                    }

                                    CategorySpinnerAdapter categorySpinnerAdapter
                                            = new CategorySpinnerAdapter(getActivity(), categoryDataArrayList);
                                    spinner_cat.setAdapter(categorySpinnerAdapter);

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }



    private void getProductCategoryWise(String category) {

        String url = ApiConstant.filterProductCategoryWise;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("category_id", globalClass.getUserId());
        params.put("start", String.valueOf(start_index));
        params.put("limit", String.valueOf(limit));

        new PostDataParser(getActivity(), url, params, true,
                new PostDataParser.OnGetResponseListner() {
                    @Override
                    public void onGetResponse(JSONObject response) {
                        if (response != null) {

                            try {
                                int status = response.optInt("status");
                                String message = response.optString("message");
                                if (status == 1) {
                                    JSONArray data = response.getJSONArray("data");

                                    for (int i = 0; i < data.length(); i++){
                                        JSONObject object = data.getJSONObject(i);

                                        CategoryData categoryData = new CategoryData();

                                        categoryData.setId(object.optString("id"));
                                        categoryData.setName(object.optString("category_name"));
                                        categoryData.setColor_code(object.optString("category_colour"));
                                        categoryData.setItem_no(object.optString("items"));

                                        categoryDataArrayList.add(categoryData);

                                    }

                                    CategorySpinnerAdapter categorySpinnerAdapter
                                            = new CategorySpinnerAdapter(getActivity(), categoryDataArrayList);
                                    spinner_cat.setAdapter(categorySpinnerAdapter);

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }



}
