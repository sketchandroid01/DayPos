package com.daypos.container;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;

import java.util.ArrayList;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.myViewHolder> {

    private Context context;
    private ArrayList<DrawerData> mData;

    public DrawerAdapter(Context context, ArrayList<DrawerData> data) {
        this.context = context;
        this.mData = data;
    }

    @Override
    public DrawerAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.drawer_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DrawerAdapter.myViewHolder holder, int position) {

        DrawerData drawerData = mData.get(position);

        holder.iv_icon.setImageResource(drawerData.getIcon());
        holder.tv_title.setText(drawerData.getTitle());




        holder.itemView.setOnClickListener(v -> {

            mClickListener.onItemClick(position, drawerData);

        });



    }

    @Override
    public int getItemCount() {
        return mData.size();
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


