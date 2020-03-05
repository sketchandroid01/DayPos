package com.daypos.fragments.billhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;

import java.util.ArrayList;

public class OrdersListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ListItem> mItems;


    public OrdersListAdapter(Context context, ArrayList<ListItem> itemList) {
        this.context = context;
        this.mItems = itemList;

    }


    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        TextView tv_header;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            tv_header = itemView.findViewById(R.id.tv_header);

        }
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_total_value, tv_time, tv_bill_no, tv_return_id;
        private ImageView iv_payment_type;

        public OrderViewHolder(View view) {
            super(view);
            tv_total_value = view.findViewById(R.id.tv_total_value);
            tv_time = view.findViewById(R.id.tv_time);
            tv_bill_no = view.findViewById(R.id.tv_bill_no);
            tv_return_id = view.findViewById(R.id.tv_return_id);
            iv_payment_type = view.findViewById(R.id.iv_payment_type);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@Nullable ViewGroup parent, int viewType) {

        if (viewType == ListItem.TYPE_HEADER) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_bill_header, parent, false);
            return new HeaderViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_bill_child, parent, false);
            return new OrderViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@Nullable RecyclerView.ViewHolder viewHolder,
                                 final int position) {

        int type = getItemViewType(position);
        if (type == ListItem.TYPE_HEADER) {
            HeaderItem header = (HeaderItem) mItems.get(position);
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

            holder.tv_header.setText(header.getHeaderName());

        } else {
            ChildItem childItem = (ChildItem) mItems.get(position);
            OrderViewHolder holder = (OrderViewHolder) viewHolder;

            setOwnerData(holder, childItem.getOrderDetails());
        }


    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }


    private void setOwnerData(OrderViewHolder holder, OrderDetails orderDetails){

        holder.tv_time.setText(orderDetails.getTime());
        holder.tv_total_value.setText(orderDetails.getTotal_amount());

        holder.tv_bill_no.setText(orderDetails.getBill_no());

        if (orderDetails.getIs_returned().equals("y")){
            holder.tv_return_id.setVisibility(View.VISIBLE);
            holder.tv_return_id.setText(orderDetails.getReturn_no());
        }else {
            holder.tv_return_id.setVisibility(View.GONE);
        }

        if (orderDetails.getPayment_type().equalsIgnoreCase("Cash")){
            holder.iv_payment_type.setImageResource(R.mipmap.icon_cash);
        }else {
            holder.iv_payment_type.setImageResource(R.drawable.ic_card_black);
        }


        holder.itemView.setOnClickListener(v -> {
            mViewClickListener.onClicked(orderDetails);
        });
    }


    /////////////

    private ViewClickListener mViewClickListener;

    public interface ViewClickListener {
        void onClicked(OrderDetails orderDetails);
    }

    public void setViewClickListener (ViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }
}