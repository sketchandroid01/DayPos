package com.daypos.fragments.settings;

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
import com.daypos.fragments.category.CategoryData;
import com.daypos.localdb.PrinterData;

import java.util.ArrayList;

public class PrinterListAdapter extends RecyclerView.Adapter<PrinterListAdapter.myViewHolder> {

    private Context context;
    private ArrayList<PrinterData> mData;

    public PrinterListAdapter(Context context, ArrayList<PrinterData> data) {
        this.context = context;
        this.mData = data;
    }

    @Override
    public PrinterListAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.printer_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PrinterListAdapter.myViewHolder holder, final int position) {

        final PrinterData printerData = mData.get(position);


        holder.tv_name.setText(printerData.getPrinter_name());
        holder.tv_printer_model.setText(printerData.getLocation_path_name());

        if (printerData.getIs_print().equals("1")){
            holder.tv_is_print.setVisibility(View.VISIBLE);
        }else {
            holder.tv_is_print.setVisibility(View.GONE);
        }


        holder.itemView.setOnLongClickListener(v -> {
            mLongClickListener.onItemLongClick(printerData);
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_printer_model, tv_is_print;

        public myViewHolder(View itemView) {
            super(itemView);
            tv_printer_model = itemView.findViewById(R.id.tv_printer_model);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_is_print = itemView.findViewById(R.id.tv_is_print);

        }
    }



    private ItemClickListener mClickListener;
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(PrinterData printerData);
    }


    /// on long click ...
    private ItemLongClickListener mLongClickListener;
    public void setLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mLongClickListener = itemLongClickListener;
    }

    public interface ItemLongClickListener {
        void onItemLongClick(PrinterData printerData);
    }



}


