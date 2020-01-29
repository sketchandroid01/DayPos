package com.daypos.fragments.home;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daypos.R;
import com.daypos.cart.CartActivity;
import com.daypos.fragments.category.CategoryData;
import com.daypos.fragments.customers.DialogAddCustomer;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.CircleAnimationUtil;
import com.daypos.utils.GlobalClass;
import com.daypos.utils.Preferense;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        ProductAdapter.ItemClickListener{

    private Unbinder unbinder;

    @BindView(R.id.recyclerview) RecyclerView recyclerview;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipe_refresh_layout;
    @BindView(R.id.spinner_cat)
    Spinner spinner_cat;

    public static TextView cart_counter;
    RelativeLayout cart_relativeLayout;

    private GlobalClass globalClass;
    private Preferense preferense;

    private ArrayList<CategoryData> categoryDataArrayList;
    private ArrayList<ProductData> productDataArrayList;
    private int start_index = 1;
    private int limit = 20;
    private String category_id;

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
        cart_relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(menuItem);
        cart_counter = cart_relativeLayout.findViewById(R.id.tv_cart_counter);

        cart_counter.setText("0");
        cart_relativeLayout.setOnClickListener(new View.OnClickListener() {
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

        preferense = new Preferense(getActivity());
        preferense.setToGlobal();

        globalClass = (GlobalClass) getActivity().getApplicationContext();

        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipe_refresh_layout.setOnRefreshListener(this);


        categoryDataArrayList = new ArrayList<>();
        getCategoryList();

    }

    @Override
    public void onRefresh() {

        getProductCategoryWise(category_id);

    }

    private void getCategoryList() {
        categoryDataArrayList = new ArrayList<>();

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

                                    CategoryData categoryData = new CategoryData();

                                    categoryData.setId("all");
                                    categoryData.setName("All");
                                    categoryData.setColor("");
                                    categoryData.setItem_no("");
                                    categoryDataArrayList.add(categoryData);

                                    JSONArray data = response.getJSONArray("data");

                                    for (int i = 0; i < data.length(); i++){
                                        JSONObject object = data.getJSONObject(i);

                                        categoryData = new CategoryData();

                                        categoryData.setId(object.optString("id"));
                                        categoryData.setName(object.optString("category_name"));
                                        categoryData.setColor(object.optString("category_colour"));
                                        categoryData.setItem_no(object.optString("items"));

                                        categoryDataArrayList.add(categoryData);

                                    }

                                    CategorySpinnerAdapter categorySpinnerAdapter
                                            = new CategorySpinnerAdapter(getActivity(), categoryDataArrayList);
                                    spinner_cat.setAdapter(categorySpinnerAdapter);

                                }

                                spinnerAction();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }

    private void spinnerAction(){

        spinner_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category_id = categoryDataArrayList.get(position).getId();
                getProductCategoryWise(category_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private void getProductCategoryWise(String category) {

        productDataArrayList = new ArrayList<>();

        String url = ApiConstant.filterProductCategoryWise;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("category_id", category);
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
                                    JSONArray item_list = response.getJSONArray("item_list");

                                    for (int i = 0; i < item_list.length(); i++){
                                        JSONObject object = item_list.getJSONObject(i);


                                        ProductData productData = new ProductData();
                                        productData.setId(object.optString("id"));
                                        productData.setName(object.optString("name"));
                                        productData.setPrice(object.optString("price"));
                                        productData.setSku(object.optString("sku"));
                                        productData.setBar_code(object.optString("bar_code"));
                                        productData.setImage(object.optString("item_image"));
                                        productData.setTaxes(object.optString("taxes"));
                                        productData.setItem_color(object.optString("item_color"));
                                        productData.setIs_attribute(object.optString("is_attribute"));


                                        productDataArrayList.add(productData);
                                    }

                                }else {

                                }


                                setProductData();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            swipe_refresh_layout.setRefreshing(false);

                        }
                    }
                });
    }

    private void setProductData(){

        ProductAdapter productAdapter =
                new ProductAdapter(getActivity(), productDataArrayList);
        recyclerview.setAdapter(productAdapter);
        productAdapter.setClickListener(this);


    }

    @Override
    public void onItemClick(ProductData productData, View view) {

        makeFlyAnimation(view);

    }



    private void makeFlyAnimation(View view) {


        new CircleAnimationUtil().attachActivity(getActivity())
                .setTargetView(view)
                .setMoveDuration(1000)
                .setDestView(cart_relativeLayout)
                .setAnimationListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //addItemToCart();
                Toast.makeText(getActivity(), "Added",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();


    }

}
