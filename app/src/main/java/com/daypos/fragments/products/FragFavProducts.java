package com.daypos.fragments.products;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daypos.R;
import com.daypos.fragments.home.ProductAdapter;
import com.daypos.fragments.home.ProductData;
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
import es.dmoral.toasty.Toasty;

public class FragFavProducts extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        ProductAdapter.ItemClickListener,
        ProductAdapter.ItemClickListenerFav{

    private Unbinder unbinder;

    @BindView(R.id.recyclerview) RecyclerView recycler_view;
    @BindView(R.id.edt_search) EditText edt_search;
    @BindView(R.id.iv_search)
    ImageView iv_search;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;

    private GlobalClass globalClass;
    private Preferense preferense;

    private ArrayList<ProductData> productDataArrayList;
    private int start_index = 0;
    private int limit = 60;


    public FragFavProducts() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_product_list, container, false);
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
    public void onRefresh() {

        getFavProductList();

    }

    @Override
    public void onResume() {

        getFavProductList();

        super.onResume();
    }

    private void viewsAction(){

        globalClass = (GlobalClass)getActivity().getApplicationContext();
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipe_refresh_layout.setOnRefreshListener(this);

        edt_search.setHint("Search from favourite");
        iv_search.setOnClickListener(v -> {

            if (edt_search.getText().toString().trim().length() == 0){
                Toasty.info(getActivity(),
                        "Enter search keyword",
                        Toast.LENGTH_SHORT, true).show();
                return;
            }

            searchProduct(edt_search.getText().toString());

            Commons.hideSoftKeyboard(getActivity());
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().trim().length() > 1){
                    searchProduct(s.toString());
                }

                if (s.toString().length() == 0){
                    getFavProductList();
                }

            }
        });

    }

    private void getFavProductList() {

        productDataArrayList = new ArrayList<>();

        String url = ApiConstant.favourite_item_list;

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
                                    JSONArray all_data = response.getJSONArray("all_data");

                                    for (int i = 0; i < all_data.length(); i++){
                                        JSONObject object = all_data.getJSONObject(i);


                                        ProductData productData = new ProductData();
                                        productData.setId(object.optString("id"));
                                        productData.setName(object.optString("name"));
                                        productData.setPrice(object.optString("price"));
                                        productData.setSku(object.optString("sku"));
                                        productData.setBar_code(object.optString("bar_code"));

                                        if (object.optString("item_image").isEmpty()){
                                            productData.setImage("");
                                        }else {
                                            productData.setImage(ApiConstant.IMAGE_PATH
                                                    + object.optString("item_image"));
                                        }

                                        productData.setTaxes(object.optString("taxes"));
                                        productData.setItem_color(object.optString("item_color"));
                                        productData.setIs_modifier(object.optString("is_attribute"));
                                        productData.setCategory_id(object.optString("category_id"));
                                        productData.setSold_option(object.optString("sold_option"));
                                        productData.setIs_fav("1");


                                        productDataArrayList.add(productData);
                                    }

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
        recycler_view.setAdapter(productAdapter);
        productAdapter.setClickListener(this);
        productAdapter.setClickListenerFav(this);

    }

    @Override
    public void onItemClick(ProductData productData, View view) {

       // Intent intent = new Intent(getActivity(), EditProduct.class);
       // intent.putExtra("datas", productData);
       // startActivity(intent);

    }

    private void searchProduct(String search_key) {

        productDataArrayList = new ArrayList<>();

        String url = ApiConstant.searchToFavorits;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("keyword", search_key);


        new PostDataParser(getActivity(), url, params, true,
                new PostDataParser.OnGetResponseListner() {
                    @Override
                    public void onGetResponse(JSONObject response) {
                        if (response != null) {

                            try {
                                int status = response.optInt("status");
                                String message = response.optString("message");
                                if (status == 1) {
                                    JSONArray item_list = response.getJSONArray("data");

                                    for (int i = 0; i < item_list.length(); i++){
                                        JSONObject object = item_list.getJSONObject(i);


                                        ProductData productData = new ProductData();
                                        productData.setId(object.optString("id"));
                                        productData.setName(object.optString("name"));
                                        productData.setPrice(object.optString("price"));
                                        productData.setSku(object.optString("sku"));
                                        productData.setBar_code(object.optString("bar_code"));
                                        if (object.optString("item_image").isEmpty()){
                                            productData.setImage("");
                                        }else {
                                            productData.setImage(ApiConstant.IMAGE_PATH
                                                    + object.optString("item_image"));
                                        }
                                        productData.setTaxes(object.optString("taxes"));
                                        productData.setItem_color(object.optString("item_color"));
                                        productData.setIs_modifier(object.optString("is_attribute"));
                                        productData.setIs_fav("1");

                                        productDataArrayList.add(productData);
                                    }

                                }

                                setProductData();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            swipe_refresh_layout.setRefreshing(false);

                            Commons.hideSoftKeyboard(getActivity());
                        }
                    }
                });
    }

    @Override
    public void onItemClickFav(ProductData productData) {
        addOrRemoveFav(productData);
    }

    private void addOrRemoveFav(ProductData productData) {

        String url;
        if (productData.getIs_fav().equals("1")){
            url = ApiConstant.delete_favourite;
        }else {
            url = ApiConstant.add_to_favourite;
        }


        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("item_id", productData.getId());


        new PostDataParser(getActivity(), url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        Toasty.success(getActivity(),
                                message, Toast.LENGTH_SHORT, true).show();

                        getFavProductList();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }
}
