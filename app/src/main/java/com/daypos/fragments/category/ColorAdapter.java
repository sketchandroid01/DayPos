package com.daypos.fragments.category;

import android.content.Context;
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

import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.myViewHolder> {

    private Context context;
    private ArrayList<String> mData;
    private ArrayList<Boolean> booleanArrayList;

    public ColorAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.mData = data;


        booleanArrayList = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++){
            if (i == 0){
                booleanArrayList.add(true);
            }else {
                booleanArrayList.add(false);
            }
        }
    }

    private void setBooleanData(){
        booleanArrayList = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++){
            booleanArrayList.add(false);
        }

    }

    @Override
    public ColorAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.color_circle_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ColorAdapter.myViewHolder holder, final int position) {

        GradientDrawable bgShape = (GradientDrawable)holder.rl_color.getBackground();
        bgShape.setColor(Color.parseColor(mData.get(position)));

        if (booleanArrayList.get(position)) {
            holder.iv_check.setVisibility(View.VISIBLE);
        }else {
            holder.iv_check.setVisibility(View.GONE);
        }

        holder.rl_color.setOnClickListener(v -> {
            mClickListener.onItemClick(mData.get(position));
            setBooleanData();
            booleanArrayList.set(position, true);
            notifyDataSetChanged();
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rl_color;
        private ImageView iv_check;


        public myViewHolder(View itemView) {
            super(itemView);
            rl_color = itemView.findViewById(R.id.rl_color);
            iv_check = itemView.findViewById(R.id.iv_check);
        }
    }



    private ItemClickListener mClickListener;
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(String color_code);
    }

}


