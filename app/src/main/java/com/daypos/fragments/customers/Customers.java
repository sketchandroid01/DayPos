package com.daypos.fragments.customers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

public class Customers extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipe_refresh_layout;
    @BindView(R.id.edt_search)
    EditText edt_search;


    public Customers() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_customer, container, false);
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
        inflater.inflate(R.menu.menu_cat, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_cat:

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

        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<CustomerData> customerDataArrayList = new ArrayList<>();
        customerDataArrayList.add(new CustomerData());
        customerDataArrayList.add(new CustomerData());
        customerDataArrayList.add(new CustomerData());
        customerDataArrayList.add(new CustomerData());
        customerDataArrayList.add(new CustomerData());
        customerDataArrayList.add(new CustomerData());
        customerDataArrayList.add(new CustomerData());

        CustomerAdapter customerAdapter = new CustomerAdapter(getActivity(),
                customerDataArrayList);
        recycler_view.setAdapter(customerAdapter);


    }



}
