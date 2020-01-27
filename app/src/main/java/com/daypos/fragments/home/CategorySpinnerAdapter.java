package com.daypos.fragments.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daypos.R;

import java.util.ArrayList;

public class CategorySpinnerAdapter extends BaseAdapter {

    private ArrayList<CategoryData> alertList;
    private LayoutInflater mInflater;

    public CategorySpinnerAdapter(Context context, ArrayList<CategoryData> results) {
        alertList = results;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return alertList.size();
    }

    @Override
    public Object getItem(int position) {
        return alertList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinner_item, null);
            holder = new ViewHolder();
            holder.spinnerValue = convertView.findViewById(R.id.tv_item);

            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.spinnerValue.setText(alertList.get(position).getName());
        return convertView;
    }




    static class ViewHolder {
        TextView spinnerValue; //spinner name
    }
}
