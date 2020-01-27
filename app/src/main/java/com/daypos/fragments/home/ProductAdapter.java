package com.daypos.fragments.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.container.DrawerData;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.myViewHolder> {

    private Context context;
    private ArrayList<ProductData> productDataArrayList;

    public ProductAdapter(Context context, ArrayList<ProductData> data) {
        this.context = context;
        this.productDataArrayList = data;
    }

    @Override
    public ProductAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.product_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.myViewHolder holder, int position) {

        ProductData productData = productDataArrayList.get(position);



    }

    @Override
    public int getItemCount() {
        return productDataArrayList.size();
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
        void onItemClick(ProductData productData);
    }

}


