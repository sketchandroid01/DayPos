package com.daypos.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.container.DrawerData;
import com.daypos.fragments.home.ProductData;
import com.daypos.modifier.ModifierItemsData;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.myViewHolder> {

    private Context context;
    private ArrayList<CartData> cartDataArrayList;

    private static DecimalFormat df = new DecimalFormat("0.00");

    public CartAdapter(Context context, ArrayList<CartData> data) {
        this.context = context;
        this.cartDataArrayList = data;
    }

    @Override
    public CartAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.cart_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapter.myViewHolder holder, int position) {

        CartData cartData = cartDataArrayList.get(position);

        holder.tv_product_name.setText(cartData.getProduct_name());
        holder.tv_quantity.setText("X "+cartData.getQty());
        holder.tv_product_name.setText(cartData.getProduct_name());

        try {

            float price = Float.parseFloat(cartData.getPrice());
            int qty = Integer.parseInt(cartData.getQty());

            float total_price = price * qty;
            total_price = total_price + getModifier_price(cartData.getModifierItemsList(), cartData.getModifiers(), qty);

            holder.tv_calculate_price.setText(df.format(total_price));

        }catch (Exception e){
            e.printStackTrace();
        }

        getModifier_names(holder.tv_modifier_name, cartData.getModifiers(), cartData.getModifierItemsList());

        holder.linearProduct.setOnClickListener(v -> {
            mClickListenerEdit.onItemClickEdit(position, cartData);
        });

        holder.delete_iv.setOnClickListener(v -> {
            mClickListenerDetete.onItemClickDelete(position, cartData);
        });

    }

    @Override
    public int getItemCount() {
        return cartDataArrayList.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView delete_iv;
        TextView tv_product_name, tv_quantity, tv_calculate_price, tv_modifier_name;
        LinearLayout linearProduct;

        public myViewHolder(View itemView) {
            super(itemView);
            delete_iv = itemView.findViewById(R.id.delete_iv);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);
            tv_calculate_price = itemView.findViewById(R.id.tv_calculate_price);
            tv_modifier_name = itemView.findViewById(R.id.tv_modifier_name);
            linearProduct = itemView.findViewById(R.id.linearProduct);
        }
    }



    private ItemClickListenerEdit mClickListenerEdit;
    private ItemClickListenerDetete mClickListenerDetete;
    public void setClickListenerEdit(ItemClickListenerEdit itemClickListener) {
        this.mClickListenerEdit = itemClickListener;
    }

    public void setClickListenerDetete(ItemClickListenerDetete itemClickListener) {
        this.mClickListenerDetete = itemClickListener;
    }

    public interface ItemClickListenerEdit {
        void onItemClickEdit(int position, CartData cartData);
    }

    public interface ItemClickListenerDetete {
        void onItemClickDelete(int position, CartData cartData);
    }


    private void getModifier_names(TextView textView, String modifier,
                                   ArrayList<ModifierItemsData> arrayList){

        String[] array = modifier.split(",");
        StringBuilder sbString = new StringBuilder("");
        for(ModifierItemsData itemsData : arrayList){

            for (String ss : array) {
                if (ss.equals(itemsData.getId())) {
                    sbString.append(itemsData.getName()).append(", ");
                }
            }

        }
        String strList = sbString.toString();
        if( strList.length() > 0 )
            strList = strList.substring(0, strList.length() - 2);

        textView.setText(strList);

    }

    private float getModifier_price(ArrayList<ModifierItemsData> arrayList,
                                    String modifier, int qty){

        String[] array = modifier.split(",");
        float price = 0;
        for(ModifierItemsData itemsData : arrayList){

            for (String ss : array) {
                if (ss.equals(itemsData.getId())) {
                    price = price + (Float.parseFloat(itemsData.getPrice()) * qty);
                }
            }
        }

        return price;
    }

}


