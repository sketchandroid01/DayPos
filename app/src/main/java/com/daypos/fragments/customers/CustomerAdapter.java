package com.daypos.fragments.customers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.myViewHolder> {

    private Context context;
    private ArrayList<CustomerData> customerDataArrayList;

    public CustomerAdapter(Context context, ArrayList<CustomerData> data) {
        this.context = context;
        this.customerDataArrayList = data;
    }

    @Override
    public CustomerAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.customer_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerAdapter.myViewHolder holder, int position) {

        CustomerData customerData = customerDataArrayList.get(position);

        holder.tv_name.setText(customerData.getName());

        holder.tv_email.setVisibility(View.GONE);

        if (customerData.getEmail().isEmpty()){
            if (customerData.getPhone().isEmpty()){
                holder.tv_email.setText(customerData.getEmail());
            }else {
                holder.tv_email.setText(customerData.getPhone());
                holder.tv_email.setVisibility(View.VISIBLE);
            }

        }else {
            holder.tv_email.setVisibility(View.VISIBLE);
            if (customerData.getPhone().isEmpty()){
                holder.tv_email.setText(customerData.getEmail());
            }else {
                holder.tv_email.setText(customerData.getEmail()+", "+customerData.getPhone());
            }
        }



        holder.itemView.setOnClickListener(v -> {
            mClickListener.onItemClick(customerData);
        });


    }

    @Override
    public int getItemCount() {
        return customerDataArrayList.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linear_main;
        TextView tv_name, tv_email;

        public myViewHolder(View itemView) {
            super(itemView);
            linear_main = itemView.findViewById(R.id.linear_main);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_email = itemView.findViewById(R.id.tv_email);

        }
    }



    private ItemClickListener mClickListener;
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(CustomerData customerData);
    }

}


