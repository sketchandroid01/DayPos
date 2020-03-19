package com.daypos.container;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daypos.R;
import com.daypos.fragments.billhistory.FragBillHistory;
import com.daypos.fragments.category.CategoryList;
import com.daypos.fragments.customers.Customers;
import com.daypos.fragments.home.Home;
import com.daypos.fragments.products.FragFavProducts;
import com.daypos.fragments.products.FragProductList;
import com.daypos.fragments.settings.FragSettings;
import com.daypos.login.Login;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.GlobalClass;
import com.daypos.utils.Preferense;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;


public class Container extends AppCompatActivity implements
        DrawerAdapter.ItemClickListener {

    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.recycler_view) RecyclerView recycler_view;
    @BindView(R.id.rl_logout) RelativeLayout rl_logout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.imageView) CircleImageView imageView;
    @BindView(R.id.username) TextView username;
    @BindView(R.id.store_name) TextView store_name;
    @BindView(R.id.tv_version) TextView tv_version;


    private Preferense preferense;
    private GlobalClass globalClass;


    private static final long MOVE_DEFAULT_TIME = 1000;
    private static final long FADE_DEFAULT_TIME = 300;
    private FragmentManager mFragmentManager;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        ButterKnife.bind(this);
        initViews();



    }

    private void initViews(){

        preferense = new Preferense(this);
        preferense.setToGlobal();
        globalClass = (GlobalClass) getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.colorPrimaryDark));
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mFragmentManager = getSupportFragmentManager();
        transactFragment(new Home());

        setDrawerData();

        rl_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogLogout();
            }
        });



        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;
            int versionCode = pInfo.versionCode;

            Log.d("TAG", "versionName = "+versionName);
            Log.d("TAG", "versionCode = "+versionCode);

            tv_version.setText("V: "+versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Glide.with(this)
                .load(preferense.getString(Preferense.PREF_image))
                .into(imageView);
        username.setText(preferense.getString(Preferense.PREF_name));
        store_name.setText(preferense.getString(Preferense.PREF_business));

    }


    private void setDrawerData(){

        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<DrawerData> drawerDataArrayList = new ArrayList<>();

        DrawerData drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.icon_home);
        drawerData.setTitle("Home");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.icon_customers);
        drawerData.setTitle("Customers");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.icon_category);
        drawerData.setTitle("Category");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.icon_products);
        drawerData.setTitle("Products");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.icon_fav);
        drawerData.setTitle("Favourites");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.icon_bill_hist);
        drawerData.setTitle("Bill History");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.icon_setting);
        drawerData.setTitle("Settings");
        drawerDataArrayList.add(drawerData);


        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.icon_help);
        drawerData.setTitle("Help");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.icon_update);
        drawerData.setTitle("Check for Update");
        drawerDataArrayList.add(drawerData);


        DrawerAdapter drawerAdapter = new DrawerAdapter(Container.this, drawerDataArrayList);
        recycler_view.setAdapter(drawerAdapter);
        drawerAdapter.setClickListener(this);

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

        }catch (Exception e){
            e.printStackTrace();
        }
        super.onResume();
    }


    @Override
    public void onItemClick(int position, DrawerData drawerData) {
        Fragment fragment = null;
        Intent intent = null;
        switch (position){
            case 0:
                getSupportActionBar().setTitle(drawerData.getTitle());
                fragment = new Home();
                transactFragment(fragment);
                break;

            case 1:
                getSupportActionBar().setTitle(drawerData.getTitle());
                fragment = new Customers();
                transactFragment(fragment);
                break;

            case 2:
                intent = new Intent(Container.this, CategoryList.class);
                intentClass(intent);
                break;

            case 3:
                getSupportActionBar().setTitle(drawerData.getTitle());
                fragment = new FragProductList();
                transactFragment(fragment);
                break;

            case 4:
                getSupportActionBar().setTitle(drawerData.getTitle());
                fragment = new FragFavProducts();
                transactFragment(fragment);
                break;

            case 5:
                getSupportActionBar().setTitle(drawerData.getTitle());
                fragment = new FragBillHistory();
                transactFragment(fragment);
                break;

            case 6:
                getSupportActionBar().setTitle(drawerData.getTitle());
                fragment = new FragSettings();
                transactFragment(fragment);
                break;

        }
        drawer.closeDrawer(GravityCompat.START);

    }

    private void transactFragment(final Fragment fragment){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fragment != null) {
                    FragmentTransaction ft = mFragmentManager.beginTransaction();
                    ft.replace(R.id.container, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            }
        }, FADE_DEFAULT_TIME);
    }

    private void intentClass(final Intent intent){
        if (intent != null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            }, FADE_DEFAULT_TIME);
        }
    }

    public void dialogLogout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(Container.this);
        builder.setTitle("DayPos");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logoutApi();
                    }
                });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    private void logoutApi() {

        String url = ApiConstant.logout;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("employee_id", preferense.getString(Preferense.employee_id));

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        preferense.clearData();

                        Intent intent = new Intent(Container.this, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }



}

