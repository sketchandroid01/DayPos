package com.daypos.fragments.home;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daypos.R;
import com.daypos.cart.CartActivity;
import com.daypos.draftorder.AddDraftTicketActivity;
import com.daypos.draftorder.DraftListActivity;
import com.daypos.fragments.category.CategoryData;
import com.daypos.fragments.customers.CustomerSearchActivity;
import com.daypos.fragments.customers.DialogAddCustomer;
import com.daypos.fragments.customers.DialogRemoveSelectCustomer;
import com.daypos.fragments.products.SearchProductList;
import com.daypos.modifier.ModifierActivity;
import com.daypos.modifier.ModifierItemsData;
import com.daypos.modifier.WeightQtyActivity;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.CircleAnimationUtil;
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

public class Home extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        ProductAdapter.ItemClickListener,
        ProductAdapter.ItemClickListenerFav{

    private Unbinder unbinder;

    @BindView(R.id.recyclerview) RecyclerView recyclerview;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipe_refresh_layout;
    @BindView(R.id.iv_search) ImageView iv_search;
    @BindView(R.id.spinner_cat) Spinner spinner_cat;
    @BindView(R.id.rel_ticket) RelativeLayout rel_ticket;
    @BindView(R.id.tv_ticket_action) TextView tv_ticket_action;

    public static TextView cart_counter;
    private RelativeLayout cart_relativeLayout;

    private GlobalClass globalClass;
    private Preferense preferense;

    private ArrayList<CategoryData> categoryDataArrayList;
    private ArrayList<ProductData> productDataArrayList;
    private int start_index_offset = 0;
    private int limit = 20;
    private String category_id;
    private ProductAdapter productAdapter;
    Menu menu;

    private int last_scroll_position = 0;
    private static final int MODIFIER_REQUEST = 1231;
    private static final int WEIGHT_QTY_REQUEST = 5214;
    private ProductData selected_product;
    private View selected_view;


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
        this.menu = menu;

        MenuItem menuItem = menu.findItem(R.id.cart);


        MenuItemCompat.setActionView(menuItem, R.layout.cart_counter);
        cart_relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(menuItem);
        cart_counter = cart_relativeLayout.findViewById(R.id.tv_cart_counter);

        cart_counter.setText(globalClass.getCart_counter());
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

                if (!globalClass.getCid().isEmpty()){
                    DialogRemoveSelectCustomer dialogRemoveSelectCustomer =
                            new DialogRemoveSelectCustomer(getActivity());
                    dialogRemoveSelectCustomer.show();
                    dialogRemoveSelectCustomer.setOnDismissListener(dialog -> {
                        if (dialogRemoveSelectCustomer.is_removed_customer == 1){
                            MenuItem add_customer = menu.findItem(R.id.add_customer);
                            if (!globalClass.getCid().isEmpty()){
                                add_customer.setIcon(R.mipmap.icon_user_added);
                            }else {
                                add_customer.setIcon(R.mipmap.add_customer);
                            }
                        }
                    });

                }else {
                    Intent intent = new Intent(getActivity(), CustomerSearchActivity.class);
                    startActivity(intent);
                }

                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {

        try {

            MenuItem add_customer = menu.findItem(R.id.add_customer);
            if (!globalClass.getCid().isEmpty()){
                add_customer.setIcon(R.mipmap.icon_user_added);
            }else {
                add_customer.setIcon(R.mipmap.add_customer);
            }

            cart_counter.setText(globalClass.getCart_counter());

            setDraftText();

        }catch (Exception e){
            e.printStackTrace();
        }

        getCartItems();

        super.onResume();
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

        if (preferense.getString(Preferense.enable_open_ticket).equals("y")){
            rel_ticket.setVisibility(View.VISIBLE);
        }else {
            rel_ticket.setVisibility(View.VISIBLE);
        }


        iv_search.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchProductList.class);
            startActivity(intent);
        });

        rel_ticket.setOnClickListener(v -> {

            if (globalClass.getTicket_id().isEmpty()){
                if (Integer.parseInt(globalClass.getCart_counter()) == 0){
                    Intent intent = new Intent(getActivity(), DraftListActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), AddDraftTicketActivity.class);
                    startActivity(intent);
                }
            }else {
                globalClass.setTicket_id("");
                globalClass.setTicket_name("");
                getCartItems();
            }

        });

        initScrollListener();

    }

    @Override
    public void onRefresh() {
        start_index_offset = 0;
        last_scroll_position = 0;
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

                                }else {

                                    CategoryData categoryData = new CategoryData();

                                    categoryData.setId("all");
                                    categoryData.setName("All");
                                    categoryData.setColor("");
                                    categoryData.setItem_no("");
                                    categoryDataArrayList.add(categoryData);

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
        params.put("start", String.valueOf(start_index_offset));
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
                                        productData.setSold_option(object.optString("sold_option"));

                                        if (object.optString("item_image").isEmpty()){
                                            productData.setImage("");
                                        }else {
                                            productData.setImage(ApiConstant.IMAGE_PATH
                                                    + object.optString("item_image"));
                                        }
                                        productData.setTaxes(object.optString("taxes"));
                                        productData.setItem_color(object.optString("item_color"));
                                        productData.setIs_modifier(object.optString("is_attribute"));
                                        productData.setIs_fav(object.optString("fav"));


                                        /// modifier
                                        productData.setIs_modifier(object.optString("is_modifire"));
                                        ArrayList<ModifierItemsData> modifierItemsDataArrayList = new ArrayList<>();
                                        if (productData.getIs_modifier().equals("1")){

                                            JSONArray modifire = object.getJSONArray("modifire");
                                            for (int j = 0; j < modifire.length(); j++){
                                                JSONObject object2 = modifire.getJSONObject(j);

                                                ModifierItemsData modifierItemsData = new ModifierItemsData();
                                                modifierItemsData.setId(object2.optString("id"));
                                                modifierItemsData.setName(object2.optString("modifier_option"));
                                                modifierItemsData.setPrice(object2.optString("price"));

                                                modifierItemsDataArrayList.add(modifierItemsData);

                                            }

                                        }
                                        productData.setModifierList(modifierItemsDataArrayList);




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

        productAdapter = new ProductAdapter(getActivity(), productDataArrayList);
        recyclerview.setAdapter(productAdapter);
        productAdapter.setClickListener(this);
        productAdapter.setClickListenerFav(this);

        if (last_scroll_position != 0){
            recyclerview.scrollToPosition(last_scroll_position);
        }

    }


    boolean isLoading = false;
    private void initScrollListener() {

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                last_scroll_position = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {
                    if (linearLayoutManager != null &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition()
                                    == productDataArrayList.size() - 1) {
                        //bottom of list!

                        if (productDataArrayList.size() >= limit){
                            loadMore();
                            isLoading = true;
                        }

                    }
                }
            }
        });


    }


    private void loadMore() {

        productDataArrayList.add(null);
        productAdapter.notifyItemInserted(productDataArrayList.size() - 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                start_index_offset = productDataArrayList.size() - 1;

                loadMoreProductCategoryWise(category_id);
            }
        }, 1000);

    }

    private void loadMoreProductCategoryWise(String category) {

        String url = ApiConstant.filterProductCategoryWise;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("category_id", category);
        params.put("start", String.valueOf(start_index_offset));
        params.put("limit", String.valueOf(limit));

        new PostDataParser(getActivity(), url, params, false,
                new PostDataParser.OnGetResponseListner() {
                    @Override
                    public void onGetResponse(JSONObject response) {
                        if (response != null) {

                            try {
                                int status = response.optInt("status");
                                String message = response.optString("message");
                                if (status == 1) {

                                    productDataArrayList.remove(productDataArrayList.size() - 1);
                                    int scrollPosition = productDataArrayList.size();
                                    productAdapter.notifyItemRemoved(scrollPosition);


                                    JSONArray item_list = response.getJSONArray("item_list");

                                    for (int i = 0; i < item_list.length(); i++){
                                        JSONObject object = item_list.getJSONObject(i);


                                        ProductData productData = new ProductData();
                                        productData.setId(object.optString("id"));
                                        productData.setName(object.optString("name"));
                                        productData.setPrice(object.optString("price"));
                                        productData.setSku(object.optString("sku"));
                                        productData.setBar_code(object.optString("bar_code"));
                                        productData.setSold_option(object.optString("sold_option"));

                                        if (object.optString("item_image").isEmpty()){
                                            productData.setImage("");
                                        }else {
                                            productData.setImage(ApiConstant.IMAGE_PATH
                                                    + object.optString("item_image"));
                                        }
                                        productData.setTaxes(object.optString("taxes"));
                                        productData.setItem_color(object.optString("item_color"));
                                        productData.setIs_modifier(object.optString("is_attribute"));
                                        productData.setIs_fav(object.optString("fav"));


                                        /// modifier
                                        productData.setIs_modifier(object.optString("is_modifire"));
                                        ArrayList<ModifierItemsData> modifierItemsDataArrayList = new ArrayList<>();
                                        if (productData.getIs_modifier().equals("1")){

                                            JSONArray modifire = object.getJSONArray("modifire");
                                            for (int j = 0; j < modifire.length(); j++){
                                                JSONObject object2 = modifire.getJSONObject(j);

                                                ModifierItemsData modifierItemsData = new ModifierItemsData();
                                                modifierItemsData.setId(object2.optString("id"));
                                                modifierItemsData.setName(object2.optString("modifier_option"));
                                                modifierItemsData.setPrice(object2.optString("price"));

                                                modifierItemsDataArrayList.add(modifierItemsData);

                                            }

                                        }
                                        productData.setModifierList(modifierItemsDataArrayList);


                                        productDataArrayList.add(productData);
                                    }

                                }else {

                                    productDataArrayList.remove(productDataArrayList.size() - 1);
                                    int scrollPosition = productDataArrayList.size();
                                    productAdapter.notifyItemRemoved(scrollPosition);
                                }


                                productAdapter.notifyDataSetChanged();
                                isLoading = false;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            swipe_refresh_layout.setRefreshing(false);

                        }
                    }
                });
    }


    @Override
    public void onItemClick(ProductData productData, View view) {

        if (productData.getIs_modifier().equals("1")){
            Intent intent = new Intent(getActivity(), ModifierActivity.class);
            intent.putExtra("datas", productData);
            startActivityForResult(intent, MODIFIER_REQUEST);
            weight_qty = "1";
            selected_product = productData;
            selected_view = view;

        }else {

            ///WEIGHT_QTY_REQUEST
            if (productData.getSold_option().equals("2")){
                Intent intent = new Intent(getActivity(), WeightQtyActivity.class);
                intent.putExtra("datas", productData);
                startActivityForResult(intent, WEIGHT_QTY_REQUEST);

                selected_product = productData;
                selected_view = view;
            }else {
                weight_qty = "1";
                makeFlyAnimation(view, productData.getId());
            }

        }

    }

    private void makeFlyAnimation(View view, String id) {

        new CircleAnimationUtil().attachActivity(getActivity())
                .setTargetView(view)
                .setMoveDuration(500)
                .setDestView(cart_relativeLayout)
                .setAnimationListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                addToCart(id);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();


    }

    private void addToCart(String product_id) {

        String url = ApiConstant.add_to_cart;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("item_id", product_id);
        params.put("modifiers", modifires_ids);
        params.put("weight_quantity", weight_qty);
        params.put("type", "1");
        params.put("ticket_id", globalClass.getTicket_id());

        new PostDataParser(getActivity(), url, params, false, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        String count = response.optString("count");
                        float fff = Float.parseFloat(count);
                        cart_counter.setText(""+(int)fff);

                        globalClass.setCart_counter(""+(int)fff);

                        /*Toasty.success(getActivity(),
                                "Added",
                                Toast.LENGTH_SHORT, true).show();*/

                        modifires_ids = "";


                        setDraftText();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void getCartItems() {

        String url = ApiConstant.cart_item_list;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("ticket_id", globalClass.getTicket_id());

        new PostDataParser(getActivity(), url, params, false, response -> {

            if (response != null) {
                float total_qty = 0;
                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        JSONArray data = response.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);

                            String quantity = object.optString("quantity");

                            total_qty = total_qty + Float.parseFloat(quantity);
                        }

                        cart_counter.setText(""+(int)total_qty);

                        globalClass.setCart_counter(""+(int)total_qty);

                    }else {
                        cart_counter.setText(""+(int)total_qty);

                        globalClass.setCart_counter(""+(int)total_qty);
                    }

                    setDraftText();

                } catch (Exception e) {
                    e.printStackTrace();
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

                        getProductCategoryWise(category_id);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }


    private void setDraftText(){
        /*if (Integer.parseInt(globalClass.getCart_counter()) == 0){
            tv_ticket_action.setText("OPEN DRAFT");
        }else {
            tv_ticket_action.setText("SAVE");
        }*/

        if (!globalClass.getTicket_id().isEmpty()){
            if (Integer.parseInt(globalClass.getCart_counter()) == 0){
                tv_ticket_action.setText("OPEN TICKET");
            }else {
                tv_ticket_action.setText("SAVE   ("+globalClass.getTicket_name()+")");
            }
        }else {
            if (Integer.parseInt(globalClass.getCart_counter()) == 0){
                tv_ticket_action.setText("OPEN TICKET");
            }else {
                tv_ticket_action.setText("SAVE");
            }
        }
    }

    ///
    private String modifires_ids = "";
    private String weight_qty = "";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == MODIFIER_REQUEST && resultCode == Activity.RESULT_OK) {

            modifires_ids = data.getStringExtra("ids");
            Log.d(Commons.TAG, "modifires_ids = "+modifires_ids);

            makeFlyAnimation(selected_view, selected_product.getId());

        }if( requestCode == WEIGHT_QTY_REQUEST && resultCode == Activity.RESULT_OK) {

            weight_qty = data.getStringExtra("qty");
            Log.d(Commons.TAG, "weight_qty = "+weight_qty);

            makeFlyAnimation(selected_view, selected_product.getId());
        }
    }

}
