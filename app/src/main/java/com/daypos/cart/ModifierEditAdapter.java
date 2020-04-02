package com.daypos.cart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.modifier.ModifierItemsData;
import com.daypos.utils.Commons;

import java.util.ArrayList;

public class ModifierEditAdapter extends RecyclerView.Adapter<ModifierEditAdapter.myViewHolder> {

    private Context context;
    private ArrayList<ModifierItemsData> mData;
    private ArrayList<String> listIds;
    private ArrayList<String> listPrices;
    private ArrayList<Boolean> booleanArrayList;
    private String modifiers;

    public ModifierEditAdapter(Context context, ArrayList<ModifierItemsData> data,
                               String modifiers) {
        this.context = context;
        this.mData = data;
        this.modifiers = modifiers;
        listIds = new ArrayList<>();
        listPrices = new ArrayList<>();
        booleanArrayList = new ArrayList<>();
        initData();
    }

    private void initData(){
        String[] array = modifiers.split(",");

        for (int i = 0; i < mData.size(); i++) {

            boolean isExits = false;
            for (String ss : array) {
                if (ss.equals(mData.get(i).getId())) {
                    listPrices.add(mData.get(i).getPrice());
                    listIds.add(mData.get(i).getId());
                    isExits = true;
                    break;
                }
            }

            booleanArrayList.add(isExits);
        }

       // Log.d(Commons.TAG, "listIds = "+listIds);
       // Log.d(Commons.TAG, "listPrices = "+listPrices);
    }

    @Override
    public ModifierEditAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.modifier_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ModifierEditAdapter.myViewHolder holder, final int position) {

        final ModifierItemsData modifierItemsData = mData.get(position);

        holder.tv_name.setText(modifierItemsData.getName());
        holder.tv_price.setText(modifierItemsData.getPrice());

        if (booleanArrayList.get(position)){
            holder.rel_background.setBackgroundColor(context.getResources().getColor(R.color.light_red));
        }else {
            holder.rel_background.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        }


        holder.itemView.setOnClickListener(v -> {

            if (booleanArrayList.get(position)){
                booleanArrayList.set(position, false);

                listIds.remove(modifierItemsData.getId());
                listPrices.remove(modifierItemsData.getPrice());
            }else {
                booleanArrayList.set(position, true);

                listIds.add(modifierItemsData.getId());
                listPrices.add(modifierItemsData.getPrice());
            }

            notifyDataSetChanged();

            Log.d(Commons.TAG, "listIds = "+listIds);
            Log.d(Commons.TAG, "listPrices = "+listPrices);

            mOnChangeAction.onChangeAction(listPrices);

        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rel_background;
        TextView tv_name, tv_price;

        public myViewHolder(View itemView) {
            super(itemView);
            rel_background = itemView.findViewById(R.id.rel_background);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_price = itemView.findViewById(R.id.tv_price);

        }
    }


    public ArrayList<String> getListIds() {
        return listIds;
    }

    private ItemClickListener mClickListener;
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(ModifierItemsData modifierItemsData);
    }


    ////
    private ItemOnChangeAction mOnChangeAction;
    public void setOnChangeAction(ItemOnChangeAction itemOnChangeAction) {
        this.mOnChangeAction = itemOnChangeAction;
    }

    public interface ItemOnChangeAction {
        void onChangeAction(ArrayList<String> listPrices);
    }




}


