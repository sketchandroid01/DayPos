package com.daypos.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daypos.R;

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

    public Home() {}

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
