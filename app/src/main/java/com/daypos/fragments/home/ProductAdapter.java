package com.daypos.fragments.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daypos.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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

        holder.tv_name.setText(productData.getName());
        holder.tv_sku.setText(productData.getSku());
        holder.tv_price.setText(productData.getPrice());

        if (productData.getItem_color().isEmpty()){
            if (!productData.getImage().isEmpty()){

                holder.iv_image.setVisibility(View.VISIBLE);
                holder.iv_image_clone.setVisibility(View.VISIBLE);
                holder.rl_color.setVisibility(View.GONE);
                holder.rl_color_clone.setVisibility(View.GONE);

                Glide.with(context)
                        .load(productData.getImage())
                        .placeholder(R.drawable.circle_green)
                        .into(holder.iv_image);

                Glide.with(context)
                        .load(productData.getImage())
                        .placeholder(R.drawable.circle_green)
                        .into(holder.iv_image_clone);

                holder.itemView.setOnClickListener(v -> {
                    mClickListener.onItemClick(productData, holder.iv_image_clone);
                });


            }else {
                holder.iv_image.setVisibility(View.GONE);
                holder.iv_image_clone.setVisibility(View.GONE);
                holder.rl_color.setVisibility(View.VISIBLE);
                holder.rl_color_clone.setVisibility(View.VISIBLE);

                holder.itemView.setOnClickListener(v -> {
                    mClickListener.onItemClick(productData, holder.rl_color_clone);
                });

            }

        }else {
            holder.iv_image.setVisibility(View.GONE);
            holder.iv_image_clone.setVisibility(View.GONE);
            holder.rl_color.setVisibility(View.VISIBLE);
            holder.rl_color_clone.setVisibility(View.VISIBLE);

            holder.itemView.setOnClickListener(v -> {
                mClickListener.onItemClick(productData, holder.rl_color_clone);
            });

            if (productData.getItem_color().length() == 7){
                GradientDrawable bgShape = (GradientDrawable)holder.rl_color.getBackground();
                bgShape.setColor(Color.parseColor(productData.getItem_color()));
            }

        }



    }

    @Override
    public int getItemCount() {
        return productDataArrayList.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_image, iv_image_clone;
        TextView tv_name, tv_sku, tv_price;
        RelativeLayout rl_color, rl_color_clone;

        public myViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_sku = itemView.findViewById(R.id.tv_sku);
            tv_price = itemView.findViewById(R.id.tv_price);
            rl_color = itemView.findViewById(R.id.rl_color);
            rl_color_clone = itemView.findViewById(R.id.rl_color_clone);
            iv_image_clone = itemView.findViewById(R.id.iv_image_clone);
        }
    }



    private ItemClickListener mClickListener;
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(ProductData productData, View view);
    }


}


