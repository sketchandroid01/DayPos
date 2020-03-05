package com.daypos.fragments.billhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.fragments.home.ProductData;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ProductData> productDataArrayList;

    private static DecimalFormat df = new DecimalFormat("0.00");

    public OrderProductAdapter(Context context, ArrayList<ProductData> itemList) {
        this.context = context;
        this.productDataArrayList = itemList;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name, tv_calculate_price, tv_qty;

        public MyViewHolder(View view) {
            super(view);
            tv_product_name = view.findViewById(R.id.tv_product_name);
            tv_calculate_price = view.findViewById(R.id.tv_calculate_price);
            tv_qty = view.findViewById(R.id.tv_qty);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@Nullable ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orderd_product_item, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@Nullable RecyclerView.ViewHolder viewHolder,
                                 final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;

        ProductData productData = productDataArrayList.get(position);

        holder.tv_product_name.setText(productData.getName());

        int qty = Integer.parseInt(productData.getQty());
        float price = Float.parseFloat(productData.getPrice());

        holder.tv_qty.setText(productData.getQty() + " x "+productData.getPrice());

        float total_price = price * qty;
        holder.tv_calculate_price.setText(df.format(total_price));

    }

    @Override
    public int getItemCount() {
        return productDataArrayList.size();
    }



    /////////////

    private ViewClickListener mViewClickListener;

    public interface ViewClickListener {
        void onClicked(ProductData productData);
    }

    public void setViewClickListener (ViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }
}