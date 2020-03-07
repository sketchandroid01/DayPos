package com.daypos.fragments.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daypos.R;

import java.util.ArrayList;
import java.util.HashMap;

public class TMPrinterAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, String>> list;

    public TMPrinterAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        rowView = inflater.inflate(R.layout.list_at, null, true);

        TextView PrinterName = rowView.findViewById(R.id.PrinterName);
        TextView Target = rowView.findViewById(R.id.Target);

        PrinterName.setText(list.get(position).get("PrinterName"));
        Target.setText(list.get(position).get("Target"));


        TextView tv_msg = rowView.findViewById(R.id.tv_msg);
        //String s1 = String.valueOf(position + 1)  + ". ";
       // String s2 = "TM Printer set & Print";
        tv_msg.setText(list.get(position).get("PrinterName")
                + "\n" + list.get(position).get("Target"));

        return rowView;
    }


}
