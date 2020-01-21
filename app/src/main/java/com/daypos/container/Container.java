package com.daypos.container;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.fragments.category.CategoryList;
import com.daypos.fragments.home.Home;
import com.daypos.login.Login;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


public class Container extends AppCompatActivity implements
        DrawerAdapter.ItemClickListener {

    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.recycler_view) RecyclerView recycler_view;
    @BindView(R.id.rl_logout) RelativeLayout rl_logout;
    @BindView(R.id.toolbar) Toolbar toolbar;




    private static final long MOVE_DEFAULT_TIME = 1000;
    private static final long FADE_DEFAULT_TIME = 300;
    private FragmentManager mFragmentManager;
    public static TextView cart_counter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        ButterKnife.bind(this);
        initViews();



    }


    private void initViews(){

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

    }


    private void setDrawerData(){

        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<DrawerData> drawerDataArrayList = new ArrayList<>();

        DrawerData drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.ic_launcher);
        drawerData.setTitle("Home");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.ic_launcher);
        drawerData.setTitle("Customers");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.ic_launcher);
        drawerData.setTitle("Category");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.ic_launcher);
        drawerData.setTitle("Products");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.ic_launcher);
        drawerData.setTitle("Bill History");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.ic_launcher);
        drawerData.setTitle("Help");
        drawerDataArrayList.add(drawerData);

        drawerData = new DrawerData();
        drawerData.setIcon(R.mipmap.ic_launcher);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        MenuItem menuItem = menu.findItem(R.id.cart);

        MenuItemCompat.setActionView(menuItem, R.layout.cart_counter);
        RelativeLayout relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(menuItem);
        cart_counter = relativeLayout.findViewById(R.id.tv_cart_counter);

        cart_counter.setText("5");
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position, DrawerData drawerData) {

        Fragment fragment = null;
        Intent intent = null;

        switch (position){

            case 0:

                break;

            case 1:

                break;

            case 2:

                intent = new Intent(Container.this, CategoryList.class);
                startActivity(intent);

                break;




        }


        transactFragment(fragment);
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



    public void dialogLogout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(Container.this);
        builder.setTitle("Abound");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("LOGOUT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(Container.this, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    private Drawable buildCounterDrawable(int count, int backgroundImageId){

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.cart_counter, null);
        view.setBackgroundResource(backgroundImageId);
        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.rel1);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.tv_cart_counter);
            textView.setText("" + count);
        }
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return new BitmapDrawable(getResources(), bitmap);
    }


}

