package com.daypos.draftorder;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TicketListAdapter extends RecyclerView.Adapter<TicketListAdapter.myViewHolder> {

    private Context context;
    private ArrayList<TicketData> mData;
    private Calendar eCal;

    public TicketListAdapter(Context context, ArrayList<TicketData> data) {
        this.context = context;
        this.mData = data;
        eCal = Calendar.getInstance();

    }



    @Override
    public TicketListAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.draft_ticket_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TicketListAdapter.myViewHolder holder, final int position) {

        final TicketData ticketData = mData.get(position);

        holder.tv_name.setText(ticketData.getTicket_name());
        holder.tv_price.setText(ticketData.getTotal_amount());

        differDates(holder.tv_time, ticketData.getCreated_at());

        holder.itemView.setOnClickListener(v -> {
            mClickListener.onItemClick(ticketData);
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_time, tv_price;

        public myViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_time = itemView.findViewById(R.id.tv_time);

        }
    }



    private ItemClickListener mClickListener;
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(TicketData ticketData);
    }


    ////
    private ItemOnChangeAction mOnChangeAction;
    public void setOnChangeAction(ItemOnChangeAction itemOnChangeAction) {
        this.mOnChangeAction = itemOnChangeAction;
    }

    public interface ItemOnChangeAction {
        void onChangeAction(ArrayList<String> listPrices);
    }

    ////// difference dates ...
    private SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private void differDates(TextView textView, String date){

        String ss = "";

        try {

            Date oldDate = dateFormat.parse(date);
            System.out.println(oldDate);

            Date currentDate = eCal.getTime();
            System.out.println(currentDate);

            long diff = currentDate.getTime() - oldDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;


            if (oldDate.before(currentDate)) {

                Log.e("oldDate", "is previous date");
                Log.e("Difference: ", " seconds: " + seconds
                        + " minutes: " + minutes
                        + " hours: " + hours
                        + " days: " + days);


                ss =  "Just now";
                if (seconds > 0){
                    ss =  "Few seconds ago";
                    if (minutes > 0){
                        ss = minutes + " minutes ago";
                        if (hours > 0){
                            ss = hours + " hours "
                                    + minutes + " minutes ago";
                            if (days > 0){
                                ss = days + " days "
                                        + hours + " hours "
                                        + minutes + " minutes ago";
                            }

                        }

                    }

                }

            }

            // Log.e("toyBornTime", "" + toyBornTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        textView.setText(ss);
    }

}


