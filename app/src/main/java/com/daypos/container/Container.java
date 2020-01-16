package com.daypos.container;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.abound.shopping.R;
import com.abound.shopping.data_models.CartDataModel;
import com.abound.shopping.data_models.CartProductData;
import com.abound.shopping.data_models.DiscountData;
import com.abound.shopping.fragments.AboutUs;
import com.abound.shopping.fragments.ContactUs;
import com.abound.shopping.fragments.FragOfferProduct;
import com.abound.shopping.fragments.HomePage;
import com.abound.shopping.fragments.UserInfo;
import com.abound.shopping.utils.ApiClient;
import com.abound.shopping.utils.GlobalClass;
import com.abound.shopping.utils.PrefManager;
import com.github.javiersantos.appupdater.AppUpdater;
import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidmads.updatehandler.app.UpdateHandler;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

import static com.abound.shopping.utils.PrefManager.PREF_email;
import static com.abound.shopping.utils.PrefManager.PREF_f_name;
import static com.abound.shopping.utils.PrefManager.PREF_phone_number;
import static com.abound.shopping.utils.PrefManager.cart_count;

public class Container extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    PrefManager prefManager;
    GlobalClass globalClass;

    LinearLayout linear_home, linear_user, linear_offer;
    RelativeLayout rel_my_cart;
    TextView cart_badge;

    private static final long MOVE_DEFAULT_TIME = 1000;
    private static final long FADE_DEFAULT_TIME = 300;
    private FragmentManager mFragmentManager;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        initViews();

        setToGlobal();


        AppUpdater appUpdater = new AppUpdater(Container.this);
        appUpdater.start();


        UpdateHandler updateHandler = new UpdateHandler(Container.this);
        updateHandler.start();

    }


    private void initViews(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.colorPrimaryDark));
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        prefManager = new PrefManager(this);
        globalClass = (GlobalClass) getApplicationContext();

        mFragmentManager = getSupportFragmentManager();

        transactFragment(new HomePage());


        belowBottonClick();

        getStoreInfo();

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;

            Toasty.info(Container.this, "Please click BACK again to exit",
                    Toast.LENGTH_SHORT, true).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }


    @Override
    protected void onResume() {

        try {

            cart_badge.setText(prefManager.getCartValue());

        }catch (Exception e){
            e.printStackTrace();
        }

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.bell) {
            return true;
        }

        else if (id == R.id.search) {

            Intent intent = new Intent(Container.this, ProductSearch.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()){

            case R.id.home:

                fragment = new HomePage();

                break;

            case R.id.category:

                Intent intent = new Intent(Container.this, CategoryMain.class);
                startActivity(intent);


                break;

            case R.id.my_account:

                fragment = new UserInfo();

                break;

            case R.id.about_us:

                fragment = new AboutUs();

                break;

            case R.id.contact_us:

                fragment = new ContactUs();

                break;

            case R.id.logout:

                dialogLogout();

                break;

            case R.id.help_line_no:

                dialogHelpLineNo();

                break;

        }

        transactFragment(fragment);

        getSupportActionBar().setTitle(item.getTitle());


        return true;
    }

    private void transactFragment(Fragment fragment){

        if (fragment != null) {

            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

        }

        drawer.closeDrawer(GravityCompat.START);
    }


    private void belowBottonClick(){

        linear_home = findViewById(R.id.linear_home);
        linear_user = findViewById(R.id.linear_user);
        linear_offer = findViewById(R.id.linear_offer);
        rel_my_cart = findViewById(R.id.rel_my_cart);
        cart_badge = findViewById(R.id.cart_badge);


        linear_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new HomePage();

                transactFragment(fragment);

                getSupportActionBar().setTitle("Home");
            }
        });

        linear_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new UserInfo();

                transactFragment(fragment);

                getSupportActionBar().setTitle("User's Section");

            }
        });

        linear_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new FragOfferProduct();

                transactFragment(fragment);

                getSupportActionBar().setTitle("Offers");

            }
        });

        rel_my_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Container.this, Cart.class);
                startActivity(intent);

            }
        });


    }




    public void dialogLogout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(Container.this);
        builder.setTitle("Abound");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("LOGOUT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        prefManager.clearPreference();
                        Intent intent = new Intent(Container.this, Login.class);
                        startActivity(intent);
                        finish();


                    }
                });

        builder.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    public void getStoreInfo(){

        String url = ApiClient.get_store_settings;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("store_name", ApiClient.Storename);

        // Log.d(globalClass.TAG , "get_store_settings- " + url);
        // Log.d(globalClass.TAG , "get_store_settings- " + params.toString());


        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                //  Log.d(globalClass.TAG, "get_store_settings- " + response.toString());
                if (response != null) {
                    try {


                        JSONObject result = response.getJSONObject("result");
                        int status = result.optInt("status");

                        if (status == 0){


                           // Toast.makeText(getApplicationContext(), "Some Error Occurred", Toast.LENGTH_LONG).show();


                        }else if (status == 1){


                            JSONObject obj_data = result.getJSONObject("data");

                            String base_currency = obj_data.optString("base_currency");

                            String sitename = obj_data.optString("sitename");


                            String acc_email = obj_data.optString("acc_email");
                            String phone = obj_data.optString("phone");
                            String gstin_number = obj_data.optString("gstin_number");
                            String apply_gst_tax = obj_data.optString("apply_gst_tax");
                            String all_taxes_include = obj_data.optString("all_taxes_include");
                            String loyalty_plugin_exist = obj_data.optString("loyalty_plugin_exist");

                            String get_default_store_from_multistore =
                                    obj_data.optString("get_default_store_from_multistore");

                            String mc_customer_plugin_exist =
                                    obj_data.optString("mc_customer_plugin_exist");


                            String voucher_plugin_exist =
                                    obj_data.optString("voucher_plugin_exist");



                            prefManager.saveStringData(PrefManager.default_store,
                                    get_default_store_from_multistore);

                            prefManager.saveStringData(PrefManager.apply_gst_tax,
                                    apply_gst_tax);

                            prefManager.saveStringData(PrefManager.all_taxes_include,
                                    all_taxes_include);

                            prefManager.saveStringData(PrefManager.mc_customer_plugin_exist,
                                    mc_customer_plugin_exist);

                            prefManager.saveStringData(PrefManager.loyalty_plugin_exist,
                                    loyalty_plugin_exist);

                            prefManager.saveStringData(PrefManager.voucher_plugin_exist,
                                    voucher_plugin_exist);


                            String appt_no = obj_data.optString("appt_no");
                            String street = obj_data.optString("street");
                            String city_name = obj_data.optString("city_name");
                            String state_name = obj_data.optString("state_name");
                            String country_name = obj_data.optString("country_name");
                            String postal_code = obj_data.optString("postal_code");


                            String store_full_address = "";

                            if (appt_no == null || appt_no.matches("null")){
                                // do nothing
                            }else {
                                if (appt_no.length() != 0){
                                    store_full_address = store_full_address + appt_no +", ";
                                }
                            }
                            if (street == null || street.matches("null")){
                                // do nothing
                            }else {
                                if (street.length() != 0){
                                    store_full_address = store_full_address + street +", ";
                                }
                            }
                            if (city_name == null || city_name.matches("null")){
                                // do nothing
                            }else {
                                if (city_name.length() != 0){
                                    store_full_address = store_full_address + city_name +", ";
                                }
                            }
                            if (state_name == null || state_name.matches("null")){
                                // do nothing
                            }else {
                                if (state_name.length() != 0){
                                    store_full_address = store_full_address + state_name+", ";
                                }
                            }
                            if (country_name == null || country_name.matches("null")){
                                // do nothing
                            }else {
                                if (country_name.length() != 0){
                                    store_full_address = store_full_address + country_name+", ";
                                }
                            }
                            if (postal_code == null || postal_code.matches("null")){
                                // do nothing
                            }else {
                                if (postal_code.length() != 0){
                                    store_full_address = store_full_address + postal_code;
                                }
                            }

                            // Log.e(globalClass.TAG, "store_full_address = "+store_full_address);

                            if (store_full_address.endsWith(", ")){
                               // store_full_address = removeLastChar(store_full_address);
                            }



                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(ApiClient.TAG, "failure = get_store_settings " + res);

            }

        });

    }

    //// load save data from shared pref ...

    private void setToGlobal(){

        GlobalClass globalClass = (GlobalClass) getApplicationContext();

        globalClass.setUser_name(prefManager.getSaveString(PREF_f_name));
        globalClass.setUser_email(prefManager.getSaveString(PREF_email));
        globalClass.setUser_mobile(prefManager.getSaveString(PREF_phone_number));


        try {
           String android_id = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            globalClass.setDevice_id(android_id);

        }catch (SecurityException e){
            e.printStackTrace();
        }


        getCartDetails();
    }

    private void getCartDetails(){

        String url = ApiClient.getCustomerCartDetailsByShipAddress;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put("store_name", ApiClient.Storename);
        params.put("customer_id", prefManager.getUserId());
        params.put("substore_id", prefManager.getSubstore_id());


       // Log.d(ApiClient.TAG , "getcart- " + url);
       // Log.d(ApiClient.TAG , "getcart- " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);

        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d(ApiClient.TAG, "getcart- " + response.toString());
                if (response != null) {
                    try {

                        CartDataModel cartDataModel = new CartDataModel();
                        ArrayList<CartProductData> productDataArrayList = new ArrayList<>();
                        CartProductData cartProductData;

                        double grand_total = 0.0;

                        JSONObject result = response.getJSONObject("result");
                        int status = result.optInt("status");

                        if (status == 0){


                        }else if (status == 1) {

                            JSONObject obj_data = result.getJSONObject("data");

                            JSONArray cart_data = obj_data.getJSONArray("cart_data");
                            int l1 = cart_data.length();
                            prefManager.saveStringData(cart_count, String.valueOf(l1));

                            cart_badge.setText(String.valueOf(l1));



                            for (int i = 0; i < cart_data.length(); i++){
                                JSONObject obj_cart_data = cart_data.getJSONObject(i);

                                double pro_amt = 0;

                                String id = obj_cart_data.optString("id");
                                String card_dtls_id = obj_cart_data.optString("card_dtls_id");
                                String cart_id = obj_cart_data.optString("cart_id");
                                String product_id = obj_cart_data.optString("product_id");
                                String catid = obj_cart_data.optString("catid");
                                String stock_id = obj_cart_data.optString("stock_id");
                                String cart_quantity = obj_cart_data.optString("cart_quantity");
                                String product_name = obj_cart_data.optString("product_name");
                                String stockdet = obj_cart_data.optString("stockdet");
                                String price = obj_cart_data.optString("price");
                                String special_price = obj_cart_data.optString("special_price");
                                String product_main_img = obj_cart_data.optString("product_main_img");
                                String image_path_thumb = obj_cart_data.optString("image_path_thumb");
                                String image_path = obj_cart_data.optString("image_path");
                                String attriblute_image = obj_cart_data.optString("attriblute_image");
                                String is_attr_exist = obj_cart_data.optString("is_attr_exist");
                                String available_qnty = obj_cart_data.optString("available_qnty");


                                cartProductData = new CartProductData();
                                cartProductData.setId(id);
                                cartProductData.setCard_dtls_id(card_dtls_id);
                                cartProductData.setProduct_id(product_id);
                                cartProductData.setCat_id(catid);
                                cartProductData.setStock_id(stock_id);
                                cartProductData.setName(product_name);
                                cartProductData.setPrice(price);
                                cartProductData.setSpecial_price(special_price);
                                cartProductData.setImage_path_thumb(image_path_thumb);
                                cartProductData.setAttriblute_image(attriblute_image);
                                cartProductData.setStockdet(stockdet);
                                cartProductData.setQty(cart_quantity);
                                cartProductData.setImage_path(image_path);
                                cartProductData.setProduct_main_img(product_main_img);
                                cartProductData.setAvailable_qnty(available_qnty);

                                JSONObject tax_details_data =
                                        obj_cart_data.getJSONObject("tax_details_data");
                                cartProductData.setTax_details_data(tax_details_data.toString());

                                String total_tax_percent = tax_details_data.optString("total_tax_percent");



                                double dis_A = 0;
                                double dis_P = 0;

                                ArrayList<DiscountData> discountDataArrayList = new ArrayList<>();
                                JSONArray all_discount_dtls = obj_cart_data.getJSONArray("all_discount_dtls");
                                int dis_len = all_discount_dtls.length();
                                for (int j = 0; j < all_discount_dtls.length(); j++) {
                                    JSONObject obj_discount = all_discount_dtls.getJSONObject(j);

                                    String discount_name = obj_discount.optString("discount_name");
                                    String discount_amount = obj_discount.optString("discount_amount");
                                    String discount_type = obj_discount.optString("discount_type");

                                    DiscountData discountData = new DiscountData();
                                    discountData.setStock_id(stock_id);
                                    discountData.setDiscount_name(discount_name);
                                    discountData.setDiscount_type(discount_type);
                                    discountData.setDiscount_amount(discount_amount);

                                    discountDataArrayList.add(discountData);


                                    if (discount_type.equals("P")){
                                        dis_P = dis_P + Double.parseDouble(discount_amount);
                                    }else {
                                        dis_A = dis_A + Double.parseDouble(discount_amount);
                                    }

                                }

                                cartProductData.setAll_discount_dtls(discountDataArrayList);


                                JSONArray tax_Details = tax_details_data.getJSONArray("tax_Details");
                                int l3 = tax_Details.length();
                                Log.d("kjhg", "tax_Details length = "+l3);
                                for (int j = 0; j < tax_Details.length(); j++) {
                                    JSONObject obj_tax_Details = tax_Details.getJSONObject(j);

                                    String tax_rate = obj_tax_Details.optString("tax_rate");
                                    String tax_name = obj_tax_Details.optString("tax_name");

                                }


                                //// calculation

                                if (dis_len == 0){

                                    if (Float.parseFloat(special_price) > 0){
                                        pro_amt = Double.parseDouble(special_price);
                                    }else {
                                        pro_amt = Double.parseDouble(price);
                                    }

                                }else {
                                    pro_amt = Double.parseDouble(price);
                                }


                                pro_amt = pro_amt * Float.parseFloat(cart_quantity);

                                double dis_amt = pro_amt * (dis_P/100);
                                dis_amt = dis_amt + dis_A;

                                pro_amt = pro_amt - dis_amt;


                                if (prefManager.getSaveString(PrefManager.all_taxes_include)
                                        .equals("N")){

                                    double tax = pro_amt *
                                            (Double.parseDouble(total_tax_percent)/100);

                                    pro_amt = pro_amt + tax;
                                }


                                grand_total = grand_total + pro_amt;

                                //////////////


                                productDataArrayList.add(cartProductData);

                            }


                            cartDataModel.setProductDataArrayList(productDataArrayList);

                            String total_cart_shipcost = obj_data.optString("total_cart_shipcost");


                            DecimalFormat df = new DecimalFormat("0.00");

                            cartDataModel.setCart_grand_total(df.format(grand_total));
                            cartDataModel.setTotal_cart_amount(df.format(grand_total));
                            cartDataModel.setTotal_cart_shipcost(total_cart_shipcost);


                            globalClass.setCartDataModel(cartDataModel);


                        }


                    } catch (Exception e) {
                        e.printStackTrace();


                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d("kjhg", "getcart- " + res);

            }

        });

    }

    public void dialogHelpLineNo(){

        AlertDialog.Builder builder = new AlertDialog.Builder(Container.this);
        builder.setTitle("Abound Retail");
        builder.setMessage(getApplicationContext().getResources()
                .getString(R.string.help_line_number));
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


}

