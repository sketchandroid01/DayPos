package com.daypos.fragments.customers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.daypos.R;

import java.util.ArrayList;

public class SearchCustomerAdapter extends BaseAdapter implements Filterable {


    private static LayoutInflater inflater=null;
    private Context context;
    private ArrayList<CustomerData> _list_array;
    private ArrayList<CustomerData> originalvalue;

    public SearchCustomerAdapter(Context context_, ArrayList<CustomerData> array) {
        this.context = context_;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this._list_array = array;
        this.originalvalue = array;
    }

    ///////////////.....................filter definition................./////////////////////////////////


    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<CustomerData> results = new ArrayList<>();
                if (originalvalue == null)
                    originalvalue = _list_array;
                if (constraint != null) {
                    if (originalvalue != null && originalvalue.size() > 0) {
                        for (int i = 0; i < originalvalue.size(); i++) {
                            if (originalvalue.get(i).getName().toLowerCase()
                                    .contains(constraint.toString().toLowerCase())
                                    || originalvalue.get(i).getPhone().toLowerCase()
                                    .contains(constraint.toString().toLowerCase())
                                    || originalvalue.get(i).getEmail().toLowerCase()
                                    .contains(constraint.toString().toLowerCase())
                            ) {

                                results.add( originalvalue.get(i));
                            }
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                _list_array = (ArrayList<CustomerData>)results.values;

                if (_list_array != null){

                    notifyDataSetChanged();
                }else {

                    _list_array = new ArrayList<>();
                }


            }
        };
    }

    public int getCount() {
        return _list_array.size();
    }

    public Object getItem(int position) {
        return _list_array.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView= inflater.inflate(R.layout.autocomplete_textview, null, true);
        TextView tv_name=rowView.findViewById(R.id.tv_name);



        tv_name.setText(_list_array.get(position).getName()+" "
                + "\n" + _list_array.get(position).getPhone()
                + "\n" + _list_array.get(position).getEmail()
        ) ;



        return rowView;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }



}
