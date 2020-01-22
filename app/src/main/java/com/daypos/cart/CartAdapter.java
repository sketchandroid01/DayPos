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

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.myViewHolder> {

    private Context context;
    private ArrayList<CartData> cartDataArrayList;

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



    }

    @Override
    public int getItemCount() {
        return cartDataArrayList.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        TextView tv_title;

        public myViewHolder(View itemView) {
            super(itemView);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }



    private ItemClickListener mClickListener;
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(int position, DrawerData drawerData);
    }

}


