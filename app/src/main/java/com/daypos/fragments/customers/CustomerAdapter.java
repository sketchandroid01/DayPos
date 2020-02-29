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
                .inflate(R.layout.customers_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerAdapter.myViewHolder holder, int position) {

        CustomerData customerData = customerDataArrayList.get(position);

        /*if (position%2 == 0){
            holder.linear_main.setBackgroundColor(context.getResources().getColor(R.color.sky_light));
        }else {
            holder.linear_main.setBackgroundColor(context.getResources().getColor(R.color.white));
        }*/

        holder.customer_name.setText(customerData.getName());
        holder.tv_total_value.setText("0.00");
        holder.tv_balance.setText("0.00");
        holder.tv_total_orders.setText("0");


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
        TextView customer_name, tv_total_orders, tv_total_value, tv_balance;

        public myViewHolder(View itemView) {
            super(itemView);
            linear_main = itemView.findViewById(R.id.linear_main);
            customer_name = itemView.findViewById(R.id.customer_name);
            tv_total_orders = itemView.findViewById(R.id.tv_total_orders);
            tv_total_value = itemView.findViewById(R.id.tv_total_value);
            tv_balance = itemView.findViewById(R.id.tv_balance);
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


