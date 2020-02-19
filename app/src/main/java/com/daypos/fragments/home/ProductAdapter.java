package com.daypos.fragments.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daypos.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ProductData> productDataArrayList;


    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public ProductAdapter(Context context, ArrayList<ProductData> data) {
        this.context = context;
        this.productDataArrayList = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.product_item, parent, false);
            return new myViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading_product, parent, false);
            return new LoadingViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof myViewHolder) {
            setProductView((myViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }


    @Override
    public int getItemCount() {
        return productDataArrayList == null ? 0 : productDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return productDataArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }



    public class myViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_image, iv_image_clone;
        TextView tv_name, tv_sku, tv_price;
        RelativeLayout rl_color, rl_color_clone;
        ImageView iv_fav;

        public myViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_sku = itemView.findViewById(R.id.tv_sku);
            tv_price = itemView.findViewById(R.id.tv_price);
            rl_color = itemView.findViewById(R.id.rl_color);
            rl_color_clone = itemView.findViewById(R.id.rl_color_clone);
            iv_image_clone = itemView.findViewById(R.id.iv_image_clone);
            iv_fav = itemView.findViewById(R.id.iv_fav);
        }
    }


    private ItemClickListener mClickListener;
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(ProductData productData, View view);
    }


    private void setProductView(myViewHolder holder, int position){

        ProductData productData = productDataArrayList.get(position);

        holder.tv_name.setText(productData.getName());
        holder.tv_sku.setText(productData.getSku());
        holder.tv_price.setText(productData.getPrice());

        if (productData.getItem_color() == null
                || productData.getItem_color().equals(null)
                || productData.getItem_color().equals("null")
                || productData.getItem_color().isEmpty()
        ){
            if (!productData.getImage().isEmpty()){

                holder.iv_image.setVisibility(View.VISIBLE);
                holder.iv_image_clone.setVisibility(View.VISIBLE);
                holder.rl_color.setVisibility(View.GONE);
                holder.rl_color_clone.setVisibility(View.GONE);

                Glide.with(context)
                        .load(productData.getImage())
                        .placeholder(R.drawable.circle_green)
                        .fallback(R.drawable.circle_green)
                        .into(holder.iv_image);

                Glide.with(context)
                        .load(productData.getImage())
                        .placeholder(R.drawable.circle_green)
                        .fallback(R.drawable.circle_green)
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
                GradientDrawable bgShape1 = (GradientDrawable)holder.rl_color.getBackground();
                bgShape1.setColor(Color.parseColor(productData.getItem_color()));

                GradientDrawable bgShape2 = (GradientDrawable)holder.rl_color_clone.getBackground();
                bgShape2.setColor(Color.parseColor(productData.getItem_color()));
            }else {
                holder.rl_color.setBackground(context.getResources().getDrawable(R.drawable.circle_green));
                holder.rl_color_clone.setBackground(context.getResources().getDrawable(R.drawable.circle_green));
            }

        }


        if (productData.getIs_fav().equals("1")){
            holder.iv_fav.setImageResource(R.mipmap.icon_heart_red);
        }else {
            holder.iv_fav.setImageResource(R.mipmap.icon_heart_grey);
        }

        holder.iv_fav.setOnClickListener(v -> {
            mClickListenerFav.onItemClickFav(productData);
        });

    }

    ////

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder holder, int position) {
        //ProgressBar would be displayed
    }



    private ItemClickListenerFav mClickListenerFav;
    public void setClickListenerFav(ItemClickListenerFav itemClickListenerfav) {
        this.mClickListenerFav = itemClickListenerfav;
    }

    public interface ItemClickListenerFav {
        void onItemClickFav(ProductData productData);
    }

}


