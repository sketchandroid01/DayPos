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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class Home extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;

    public static TextView cart_counter;

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


    }



}
