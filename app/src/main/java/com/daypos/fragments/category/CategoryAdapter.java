package com.daypos.fragments.category;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.container.DrawerData;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.myViewHolder> {

    private Context context;
    private ArrayList<CategoryData> mData;

    public CategoryAdapter(Context context, ArrayList<CategoryData> data) {
        this.context = context;
        this.mData = data;
    }

    @Override
    public CategoryAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.category_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.myViewHolder holder, final int position) {

        final CategoryData categoryData = mData.get(position);


        if (categoryData.getColor().isEmpty()){
            GradientDrawable bgShape = (GradientDrawable)holder.rl_color.getBackground();
            bgShape.setColor(Color.parseColor("#c2c2c2"));
        }else {
            if (categoryData.getColor().length() == 7){
                GradientDrawable bgShape = (GradientDrawable)holder.rl_color.getBackground();
                bgShape.setColor(Color.parseColor(categoryData.getColor()));
            }

        }



        holder.tv_name.setText(categoryData.getName());
        holder.tv_item_no.setText(categoryData.getItem_no() + " items");


        holder.itemView.setOnClickListener(v -> {
            mClickListener.onItemClick(categoryData);
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_color;
        TextView tv_name, tv_item_no;

        public myViewHolder(View itemView) {
            super(itemView);
            rl_color = itemView.findViewById(R.id.rl_color);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_item_no = itemView.findViewById(R.id.tv_item_no);

        }
    }



    private ItemClickListener mClickListener;
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(CategoryData categoryData);
    }



    public void filterList(ArrayList<CategoryData> filterdData) {
        this.mData = filterdData;
        notifyDataSetChanged();
    }

}


