package com.daypos.receipt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.daypos.R;
import com.daypos.container.Container;
import com.daypos.fragments.billhistory.OrderDetails;
import com.daypos.fragments.customers.CustomerData;
import com.daypos.fragments.customers.DialogAddCustomer;
import com.daypos.fragments.customers.SearchCustomerAdapter;
import com.daypos.fragments.home.ProductData;
import com.daypos.fragments.settings.ShowMsg;
import com.daypos.localdb.DatabaseHelper;
import com.daypos.localdb.PrinterData;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.Commons;
import com.daypos.utils.GlobalClass;
import com.daypos.utils.Preferense;
import com.daypos.utils.PriceValueFilter;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class ReceiptActivity extends AppCompatActivity implements
        View.OnClickListener,
        ReceiveListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_paid_amount) TextView tv_paid_amount;
    @BindView(R.id.tv_change_amt) TextView tv_change_amt;
    @BindView(R.id.edt_emailid) EditText edt_emailid;
    @BindView(R.id.iv_send) ImageView iv_send;
    @BindView(R.id.linear_print) LinearLayout linear_print;
    @BindView(R.id.rel_new_sale) RelativeLayout rel_new_sale;


    private GlobalClass globalClass;
    private Preferense preferense;
    private DatabaseHelper databaseHelper;
    ProgressDialog progressDialog;
    PrinterData printerData;
    OrderDetails orderDetails;
    private ArrayList<ProductData> productDataArrayList;
    private static DecimalFormat df = new DecimalFormat("0.00");
    String order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        ButterKnife.bind(this);
        initViews();

    }


    private void initViews() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        iv_send.setOnClickListener(this);
        linear_print.setOnClickListener(this);
        rel_new_sale.setOnClickListener(this);


        databaseHelper = new DatabaseHelper(this);
        preferense = new Preferense(this);
        globalClass = (GlobalClass) getApplicationContext();
        printerData = databaseHelper.getActivePrinter();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            tv_paid_amount.setText(bundle.getString("pay_mat"));
            tv_change_amt.setText(bundle.getString("change_amt"));

            order_id = bundle.getString("order_id");

        }


        getOrdersDetailsList();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.iv_send:

                if (edt_emailid.getText().toString().trim().length() > 0){
                    sendEmailReceipt(edt_emailid.getText().toString());
                }

                break;

            case R.id.linear_print:

                if (databaseHelper.getActivePrinterLength() > 0){
                    runPrintReceiptSequence_EPSON();
                }else {
                    Toasty.info(getApplicationContext(),
                            "You are not selected any printer to print receipt.",
                            Toast.LENGTH_LONG, true).show();
                }

                break;

            case R.id.rel_new_sale:

                Intent intent = new Intent(ReceiptActivity.this, Container.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                break;


        }

    }


    private void sendEmailReceipt(String email) {

        String url = ApiConstant.email_invoice;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("order_id", order_id);
        params.put("email", email);
        params.put("store_id", "");

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        Toasty.success(getApplicationContext(),
                                message, Toast.LENGTH_LONG, true).show();

                    }else {
                        Toasty.error(getApplicationContext(),
                                message, Toast.LENGTH_LONG, true).show();
                    }

                    Commons.hideSoftKeyboard(ReceiptActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }



    /////////////
    private void getOrdersDetailsList() {

        productDataArrayList = new ArrayList<>();

        String url = ApiConstant.order_detail;

        HashMap<String, String> params = new HashMap<>();
        params.put("order_id", order_id);

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        orderDetails = new OrderDetails();
                        JSONObject bill_detail = response.getJSONObject("bill_detail");
                        orderDetails.setBill_no(bill_detail.optString("invoice"));
                        orderDetails.setTotal_amount(bill_detail.optString("total_amount"));
                        orderDetails.setPayment_type(bill_detail.optString("payment_mode"));
                        orderDetails.setDate(bill_detail.optString("created"));


                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++){
                            JSONObject object = data.getJSONObject(i);

                            String id = object.optString("id");
                            String item_id = object.optString("item_id");
                            String quantity = object.optString("quantity");
                            String price = object.optString("price");
                            String item_name = object.optString("item_name");

                            ProductData productData = new ProductData();
                            productData.setId(id);
                            productData.setItem_id(item_id);
                            productData.setName(item_name);
                            productData.setQty(quantity);
                            productData.setPrice(price);

                            productDataArrayList.add(productData);

                        }



                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }


    private Printer mEpsonPrinter = null;

    private boolean printData_EPSON() {
        if (mEpsonPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mEpsonPrinter.getStatus();
        dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            ShowMsg.showMsg(makeErrorMessage(status), this);
            try {
                mEpsonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mEpsonPrinter.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            // showException(e, "sendData", Invoice_New.this);
            try {
                mEpsonPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        } catch (UnsatisfiedLinkError ule) {
            Log.e("LoadJniLib", "Error: Could not load native library: "
                    + ule.getMessage());
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            mEpsonPrinter = new Printer(Printer.TM_M10, Printer.MODEL_ANK, this);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "Printer", this);
            return false;
        }
        catch (UnsatisfiedLinkError ule) {
            Log.e("LoadJniLib", "Error: Could not load native library: " + ule.getMessage());
        }

        mEpsonPrinter.setReceiveEventListener(this);

        return true;
    }

    private void finalizeObject() {
        if (mEpsonPrinter == null) {
            return;
        }

        mEpsonPrinter.clearCommandBuffer();
        mEpsonPrinter.setReceiveEventListener(null);
        mEpsonPrinter = null;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mEpsonPrinter == null) {
            return false;
        }

        try {
            mEpsonPrinter.connect(printerData.getLocation_path(), Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "connect", this);
            return false;
        }

        try {
            mEpsonPrinter.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", this);
            e.printStackTrace();
        }
        catch (UnsatisfiedLinkError ule) {
            Log.e("LoadJniLib", "Error: Could not load native library: "
                    + ule.getMessage());
        }

        if (isBeginTransaction == false) {
            try {
                mEpsonPrinter.disconnect();
            } catch (Epos2Exception e) {
                return false;
            }
        }

        return true;
    }

    private void disconnectPrinter() {
        if (mEpsonPrinter == null) {
            return;
        }
        try {
            mEpsonPrinter.endTransaction();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    // showException(e, "endTransaction", Invoice_New.this);
                }
            });
        }

        try {
            mEpsonPrinter.disconnect();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    // showException(e, "disconnect", Invoice_New.this);
                }
            });
        }

        finalizeObject();
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        }
        else if (status.getOnline() == Printer.FALSE) {
            return false;
        }
        else {
            //print available
        }

        return true;
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";
        //prefManager.resetThermalPrinter();

        if (status.getOnline() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    private void dispPrinterWarnings(PrinterStatusInfo status) {

        String warningsMsg = "";
        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += getString(R.string.handlingmsg_warn_battery_near_end);
        }

        Log.d("TAG", "Warning Msg = "+warningsMsg);

    }

    @Override
    public void onPtrReceive(final Printer printerObj, final int code,
                             final PrinterStatusInfo status,
                             final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {

                //  ShowMsg.showResult(code, makeErrorMessage(status), getApplicationContext());
                //  dispPrinterWarnings(status);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }


    private boolean runPrintReceiptSequence_EPSON() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Print processing...");
        progressDialog.show();

        if (!initializeObject()) {
            return false;
        }

        if (printerData.getPaper_width().contains("58")){
            createReceiptData58();
        }else {
            createReceiptData80();
        }


        if (!printData_EPSON()) {
            finalizeObject();
            return false;
        }

        progressDialog.dismiss();

        return true;
    }

    /// 58 mm print
    private boolean createReceiptData58() {
        String method = "";
        StringBuilder textData = new StringBuilder();
        if (mEpsonPrinter == null) {
            return false;
        }
        try {
            PrinterStatusInfo info = mEpsonPrinter.getStatus();
            int ii = info.getPaper();

            mEpsonPrinter.addTextSize(1, 2);
            mEpsonPrinter.addTextFont(Printer.FONT_A);

            mEpsonPrinter.addTextAlign(Printer.ALIGN_CENTER);

            textData.append(preferense.getString(Preferense.PREF_business));
            textData.append("\n");
            textData.append("\n");
            mEpsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


            mEpsonPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mEpsonPrinter.addTextSize(1, 1);

            textData.append("Cashier: "+preferense.getString(Preferense.PREF_name)+"\n");
            textData.append("POS: POS 1"+"\n");
            textData.append("--------------------------------\n");
            mEpsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


            for (int i = 0; i < productDataArrayList.size(); i++){
                ProductData productData = productDataArrayList.get(i);

                String p_name = productData.getName();

                int qty = Integer.parseInt(productData.getQty());
                float price = Float.parseFloat(productData.getPrice());
                float total_price = price * qty;

                textData.append(p_name);

                int differ = 32 - (p_name.length() + df.format(total_price).length());
                StringBuilder stringBuilder = new StringBuilder(differ);
                for (int j = 0; j < differ; j++){
                    stringBuilder.append(" ");
                }
                textData.append(stringBuilder.toString() + df.format(total_price)+"\n");
                textData.append(productData.getQty() + " x "+productData.getPrice()+"\n");
                textData.append("\n");

            }
            textData.append("\n");
            textData.append("--------------------------------\n");
            mEpsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            int differ = 32 - ("Total" + orderDetails.getTotal_amount()).length();
            StringBuilder stringBuilder = new StringBuilder(differ);
            for (int j = 0; j < differ; j++){
                stringBuilder.append(" ");
            }
            textData.append("Total" + stringBuilder.toString()
                    + orderDetails.getTotal_amount()+"\n");

            mEpsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


            differ = 32 - (orderDetails.getPayment_type() + orderDetails.getTotal_amount()).length();
            stringBuilder = new StringBuilder(differ);
            for (int j = 0; j < differ; j++){
                stringBuilder.append(" ");
            }
            textData.append(orderDetails.getPayment_type() + stringBuilder.toString()
                    + orderDetails.getTotal_amount()+"\n");


            textData.append("--------------------------------\n");
            mEpsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            String date_time = Commons.convertDateFormatBill(orderDetails.getDate());

            textData.append(date_time +"\n" + "#"+orderDetails.getBill_no()+"\n");
            textData.append("--------------------------------\n");

            textData.append("\n");
            textData.append("\n");

            mEpsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


            mEpsonPrinter.addCut(Printer.CUT_FEED);

        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg.showException(e, method, ReceiptActivity.this);
            return false;
        }

        textData = null;

        return true;
    }


    /// 80 mm print
    private boolean createReceiptData80() {
        String method = "";
        StringBuilder textData = new StringBuilder();
        if (mEpsonPrinter == null) {
            return false;
        }
        try {

            mEpsonPrinter.addTextSize(1, 2);
            mEpsonPrinter.addTextFont(Printer.FONT_A);

            mEpsonPrinter.addTextAlign(Printer.ALIGN_CENTER);

            textData.append(preferense.getString(Preferense.PREF_business));
            textData.append("\n");
            textData.append("\n");
            mEpsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


            mEpsonPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mEpsonPrinter.addTextSize(1, 1);

            textData.append("Cashier: "+preferense.getString(Preferense.PREF_name)+"\n");
            textData.append("POS: POS 1"+"\n");
            textData.append("------------------------------------------------\n");
            mEpsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


            for (int i = 0; i < productDataArrayList.size(); i++){
                ProductData productData = productDataArrayList.get(i);

                String p_name = productData.getName();

                int qty = Integer.parseInt(productData.getQty());
                float price = Float.parseFloat(productData.getPrice());
                float total_price = price * qty;

                textData.append(p_name);

                int differ = 48 - (p_name.length() + df.format(total_price).length());
                StringBuilder stringBuilder = new StringBuilder(differ);
                for (int j = 0; j < differ; j++){
                    stringBuilder.append(" ");
                }
                textData.append(stringBuilder.toString() + df.format(total_price)+"\n");
                textData.append(productData.getQty() + " x "+productData.getPrice()+"\n");
                textData.append("\n");

            }
            textData.append("\n");
            textData.append("------------------------------------------------\n");
            mEpsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            int differ = 48 - ("Total" + orderDetails.getTotal_amount()).length();
            StringBuilder stringBuilder = new StringBuilder(differ);
            for (int j = 0; j < differ; j++){
                stringBuilder.append(" ");
            }
            textData.append("Total" + stringBuilder.toString()
                    + orderDetails.getTotal_amount()+"\n");

            mEpsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


            differ = 48 - (orderDetails.getPayment_type() + orderDetails.getTotal_amount()).length();
            stringBuilder = new StringBuilder(differ);
            for (int j = 0; j < differ; j++){
                stringBuilder.append(" ");
            }
            textData.append(orderDetails.getPayment_type() + stringBuilder.toString()
                    + orderDetails.getTotal_amount()+"\n");


            textData.append("------------------------------------------------\n");
            mEpsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            String date_time = Commons.convertDateFormatBill(orderDetails.getDate());

            textData.append(date_time + "\n" + "#"+orderDetails.getBill_no()+"\n");
            textData.append("------------------------------------------------\n");

            textData.append("\n");
            textData.append("\n");

            mEpsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mEpsonPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            ShowMsg.showException(e, method, ReceiptActivity.this);
            return false;
        }

        textData = null;

        return true;
    }

}
