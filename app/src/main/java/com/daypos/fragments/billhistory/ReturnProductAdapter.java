package com.daypos.fragments.billhistory;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.cart.CartData;
import com.daypos.fragments.home.ProductData;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ReturnProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ProductData> productDataArrayList;
    private Button btn_refund;

    private static DecimalFormat df = new DecimalFormat("0.00");

    private ArrayList<ReturnData> returnDataArrayList;
    private ArrayList<Boolean> booleanArrayList;
    private int selected_position;

    public ReturnProductAdapter(Context context, ArrayList<ProductData> itemList,
                                Button btn_refund) {
        this.context = context;
        this.productDataArrayList = itemList;
        this.btn_refund = btn_refund;

        returnDataArrayList = new ArrayList<>();

        initBoolean();
    }

    private void initBoolean(){
        booleanArrayList = new ArrayList<>();
        for (int i = 0; i < productDataArrayList.size(); i++){
            booleanArrayList.add(false);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name, tv_calculate_price, tv_qty, tv_return_qty;
        private CheckBox checkbox;

        public MyViewHolder(View view) {
            super(view);
            tv_product_name = view.findViewById(R.id.tv_product_name);
            tv_calculate_price = view.findViewById(R.id.tv_calculate_price);
            tv_qty = view.findViewById(R.id.tv_qty);
            tv_return_qty = view.findViewById(R.id.tv_return_qty);
            checkbox = view.findViewById(R.id.checkbox);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@Nullable ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.return_product_item, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@Nullable RecyclerView.ViewHolder viewHolder,
                                 final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        holder.tv_return_qty.setVisibility(View.GONE);
        ProductData productData = productDataArrayList.get(position);

        holder.tv_product_name.setText(productData.getName());

        int qty = Integer.parseInt(productData.getQty());
        float price = Float.parseFloat(productData.getPrice());

        holder.tv_qty.setText(" x "+productData.getQty());

        float total_price = price * qty;
        holder.tv_calculate_price.setText(df.format(total_price));

        //holder.tv_return_qty


        holder.checkbox.setChecked(booleanArrayList.get(position));

        holder.itemView.setOnClickListener(v -> {

            if (booleanArrayList.get(position)){
                removeFromArray(productData.getItem_id());
                booleanArrayList.set(position, false);
                notifyDataSetChanged();
            }else {
                selected_position = position;
                addToArray(productData.getItem_id(), productData.getQty(),
                        productData.getPrice());
            }


        });

        isExitsReturnList(productData.getItem_id(), holder.tv_return_qty);

    }

    @Override
    public int getItemCount() {
        return productDataArrayList.size();
    }


    private void addToArray(String item_id, String item_qty, String item_price){

        if (Integer.parseInt(item_qty) > 1){

            returnQtyDialog(item_id, item_qty, item_price);

        }else {
            ReturnData returnData = new ReturnData();
            returnData.setItem_id(item_id);
            returnData.setItem_price(item_price);
            returnData.setItem_qty(item_qty);
            returnData.setRefund_price(item_price);
            returnDataArrayList.add(returnData);
            booleanArrayList.set(selected_position, true);
            notifyDataSetChanged();
            setRefundAmount();
        }

    }

    private void removeFromArray(String item_id){
        for (int i = 0; i < returnDataArrayList.size(); i++){
            ReturnData returnData = returnDataArrayList.get(i);
            if (item_id.equals(returnData.getItem_id())){
                returnDataArrayList.remove(i);
                break;
            }
        }

        setRefundAmount();
    }

    private void isExitsReturnList(String item_id, TextView textView){
        for (int i = 0; i < returnDataArrayList.size(); i++){

            ReturnData returnData = returnDataArrayList.get(i);
            if (item_id.equals(returnData.getItem_id())){
                textView.setText("Refund  x "+returnData.getItem_qty());
                textView.setVisibility(View.VISIBLE);
                break;
            }
        }

    }

    private void setRefundAmount(){

        float total = 0;
        for (int i = 0; i < returnDataArrayList.size(); i++){
            ReturnData returnData = returnDataArrayList.get(i);
            float price = Float.parseFloat(returnData.getItem_price());
            int qty = Integer.parseInt(returnData.getItem_qty());

            float ppp = price * qty;
            total = total + ppp;
        }

        btn_refund.setText("Refund  "+df.format(total));

    }

    public ArrayList<ReturnData> getReturnDataArrayList(){
        return returnDataArrayList;
    }



    private void returnQtyDialog(String item_id, String item_qty, String item_price){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_return_qty, null);
        dialogBuilder.setView(dialogView);

        EditText edit_cart_quantity = dialogView.findViewById(R.id.edit_cart_quantity);
        ImageView cart_minus_img = dialogView.findViewById(R.id.cart_minus_img);
        ImageView cart_plus_img = dialogView.findViewById(R.id.cart_plus_img);
        Button btn_close = dialogView.findViewById(R.id.btn_close);
        Button btn_save = dialogView.findViewById(R.id.btn_save);

        edit_cart_quantity.setText(item_qty);


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        cart_minus_img.setOnClickListener(v -> {

            try {
                int count = Integer.parseInt(edit_cart_quantity.getText().toString());
                if (count >= 2){
                    count--;
                    edit_cart_quantity.setText(String.valueOf(count));
                }
            }catch (Exception e){
                e.printStackTrace();
            }



        });
        cart_plus_img.setOnClickListener(v -> {

            try {
                int count = Integer.parseInt(edit_cart_quantity.getText().toString());
                count++;

                if (count <= Integer.parseInt(item_qty)){
                    edit_cart_quantity.setText(String.valueOf(count));
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        });
        btn_close.setOnClickListener(v -> {

            alertDialog.dismiss();

        });
        btn_save.setOnClickListener(v -> {

            if (edit_cart_quantity.getText().toString().trim().length() == 0){
                Toasty.info(context,
                        "Enter quantity",
                        Toast.LENGTH_SHORT, true).show();
                return;
            }

            float refund_price = Float.parseFloat(item_price)
                    * Integer.parseInt(edit_cart_quantity.getText().toString());

            ReturnData returnData = new ReturnData();
            returnData.setItem_id(item_id);
            returnData.setItem_price(item_price);
            returnData.setItem_qty(edit_cart_quantity.getText().toString());
            returnData.setRefund_price(df.format(refund_price));
            returnDataArrayList.add(returnData);


            booleanArrayList.set(selected_position, true);
            notifyDataSetChanged();

            setRefundAmount();

            alertDialog.dismiss();

        });

    }


    /////////////

    private ViewClickListener mViewClickListener;

    public interface ViewClickListener {
        void onClicked(ProductData productData);
    }

    public void setViewClickListener (ViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }
}