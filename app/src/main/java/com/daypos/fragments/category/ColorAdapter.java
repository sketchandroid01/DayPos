package com.daypos.fragments.category;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;

import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.myViewHolder> {

    private Context context;
    private ArrayList<String> mData;

    public ColorAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.mData = data;
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

        holder.rl_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_color;


        public myViewHolder(View itemView) {
            super(itemView);
            rl_color = itemView.findViewById(R.id.rl_color);
        }
    }



    private ItemClickListener mClickListener;
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(CategoryData categoryData);
    }

}


