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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.fragments.home.Home;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class Container extends AppCompatActivity implements
        DrawerAdapter.ItemClickListener {

    DrawerLayout drawer;
    RecyclerView recycler_view;

    private static final long MOVE_DEFAULT_TIME = 1000;
    private static final long FADE_DEFAULT_TIME = 300;
    private FragmentManager mFragmentManager;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        initViews();



    }


    private void initViews(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.colorPrimaryDark));
        }


        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<DrawerData> drawerDataArrayList = new ArrayList<>();
        drawerDataArrayList.add(new DrawerData());
        drawerDataArrayList.add(new DrawerData());
        drawerDataArrayList.add(new DrawerData());
        drawerDataArrayList.add(new DrawerData());
        drawerDataArrayList.add(new DrawerData());
        drawerDataArrayList.add(new DrawerData());

        DrawerAdapter drawerAdapter = new DrawerAdapter(Container.this, drawerDataArrayList);
        recycler_view.setAdapter(drawerAdapter);
        drawerAdapter.setClickListener(this);




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mFragmentManager = getSupportFragmentManager();



        transactFragment(new Home());


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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position, DrawerData drawerData) {

        Fragment fragment = null;

        switch (position){

            case 0:

                break;

            case 1:

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




}

