package com.daypos.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.container.DrawerData;
import com.daypos.fragments.home.ProductData;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.myViewHolder> {

    private Context context;
    private ArrayList<CartData> cartDataArrayList;

    private static DecimalFormat df = new DecimalFormat("0.00");

    public CartAdapter(Context context, ArrayList<CartData> data) {
        this.context = context;
        this.cartDataArrayList = data;
    }

    @Override
    public CartAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.cart_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapter.myViewHolder holder, int position) {

        CartData cartData = cartDataArrayList.get(position);

        holder.tv_product_name.setText(cartData.getProduct_name());
        holder.tv_quantity.setText("X "+cartData.getQty());
        holder.tv_product_name.setText(cartData.getProduct_name());

        try {

            float price = Float.parseFloat(cartData.getPrice());
            int qty = Integer.parseInt(cartData.getQty());

            float total_price = price * qty;

            holder.tv_calculate_price.setText(df.format(total_price));

        }catch (Exception e){
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(v -> {
            mClickListener.onItemClick(position, cartData);
        });

    }

    @Override
    public int getItemCount() {
        return cartDataArrayList.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView delete_iv;
        TextView tv_product_name, tv_quantity, tv_calculate_price;

        public myViewHolder(View itemView) {
            super(itemView);
            delete_iv = itemView.findViewById(R.id.delete_iv);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);
            tv_calculate_price = itemView.findViewById(R.id.tv_calculate_price);
        }
    }



    private ItemClickListener mClickListener;
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(int position, CartData cartData);
    }

}


