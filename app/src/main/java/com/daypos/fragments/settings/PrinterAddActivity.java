package com.daypos.fragments.settings;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.fragments.products.AddProduct;
import com.daypos.localdb.DatabaseHelper;
import com.daypos.localdb.PrinterData;
import com.daypos.utils.Commons;
import com.daypos.utils.Preferense;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class PrinterAddActivity extends AppCompatActivity implements
        ReceiveListener {

    @BindView(R.id.edt_printer_name) EditText edt_printer_name;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.spinner_printer) Spinner spinner_printer;
    @BindView(R.id.spinner_epson_lan) Spinner spinner_epson_lan;
    @BindView(R.id.spinner_paper_width) Spinner spinner_paper_width;
    @BindView(R.id.switch_print) Switch switch_print;
    @BindView(R.id.linear_print_test) LinearLayout linear_print_test;
    @BindView(R.id.btn_select_printer)
    Button btn_select_printer;


    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 342;

    String Target, DeviceName, paper_size;
    private Preferense preferense;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_printer);
        ButterKnife.bind(this);

        initViews();

    }

    private void initViews(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create printer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);

        btn_select_printer.setVisibility(View.GONE);
        preferense = new Preferense(this);
        databaseHelper = new DatabaseHelper(this);

        linear_print_test.setOnClickListener(v -> {
            if (Target != null){
                runPrintReceiptSequence_EPSON();
            }else {
                Toasty.info(getApplicationContext(),
                        "Configure a printer",
                        Toast.LENGTH_SHORT, true).show();
            }
        });


        epsonConfig();

        locationPermission();

    }

    private void locationPermission(){
        List<String> permissionsList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(PrinterAddActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(PrinterAddActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) PrinterAddActivity.this,
                    permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cat, menu);

        MenuItem menuItem = menu.findItem(R.id.add_cat);
        MenuItemCompat.setActionView(menuItem, R.layout.item_save);
        TextView save = (TextView) MenuItemCompat.getActionView(menuItem);

        save.setOnClickListener(v -> {

            if (edt_printer_name.getText().toString().trim().isEmpty()){
                Toasty.info(getApplicationContext(),
                        "Enter printer name",
                        Toast.LENGTH_SHORT, true).show();
                return;
            }

            if (Target == null){
                Toasty.info(getApplicationContext(),
                        "Configure a printer",
                        Toast.LENGTH_SHORT, true).show();
                return;
            }

            PrinterData printerData = new PrinterData();
            printerData.setPrinter_name(edt_printer_name.getText().toString());
            printerData.setLocation_path(Target);
            printerData.setLocation_path_name(DeviceName);
            printerData.setPaper_width(paper_size);

            if (switch_print.isChecked()){
                printerData.setIs_print("1");
                databaseHelper.updateIsPrintColumn();
            }else {
                printerData.setIs_print("0");
            }


            databaseHelper.insertPinter(printerData);

            finish();

        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void epsonConfig(){

        ArrayAdapter<SpnModelsItem> nameAdapter = new ArrayAdapter<SpnModelsItem>(this, R.layout.simple_spinner_item);
        nameAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m10), Printer.TM_M10));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m30), Printer.TM_M30));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p20), Printer.TM_P20));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p60), Printer.TM_P60));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p60ii), Printer.TM_P60II));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p80), Printer.TM_P80));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t20), Printer.TM_T20));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t60), Printer.TM_T60));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t70), Printer.TM_T70));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t81), Printer.TM_T81));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t82), Printer.TM_T82));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t83), Printer.TM_T83));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t88), Printer.TM_T88));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t90), Printer.TM_T90));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t90kp), Printer.TM_T90KP));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_u220), Printer.TM_U220));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_u330), Printer.TM_U330));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_l90), Printer.TM_L90));
        nameAdapter.add(new SpnModelsItem(getString(R.string.printerseries_h6000), Printer.TM_H6000));
        spinner_printer.setAdapter(nameAdapter);


        ArrayAdapter<SpnModelsItem> modelAdapter = new ArrayAdapter<SpnModelsItem>(this, android.R.layout.simple_spinner_item);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelAdapter.add(new SpnModelsItem(getString(R.string.model_ank), Printer.MODEL_ANK));
        modelAdapter.add(new SpnModelsItem(getString(R.string.model_japanese), Printer.MODEL_JAPANESE));
        modelAdapter.add(new SpnModelsItem(getString(R.string.model_chinese), Printer.MODEL_CHINESE));
        modelAdapter.add(new SpnModelsItem(getString(R.string.model_taiwan), Printer.MODEL_TAIWAN));
        modelAdapter.add(new SpnModelsItem(getString(R.string.model_korean), Printer.MODEL_KOREAN));
        modelAdapter.add(new SpnModelsItem(getString(R.string.model_thai), Printer.MODEL_THAI));
        modelAdapter.add(new SpnModelsItem(getString(R.string.model_southasia), Printer.MODEL_SOUTHASIA));
        spinner_epson_lan.setAdapter(modelAdapter);

        try {
            com.epson.epos2.Log.setLogSettings(this,
                    com.epson.epos2.Log.PERIOD_TEMPORARY,
                    com.epson.epos2.Log.OUTPUT_STORAGE,
                    null, 0, 1,
                    com.epson.epos2.Log.LOGLEVEL_LOW);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "setLogSettings", this);
        }
        catch (UnsatisfiedLinkError ule) {
            Log.e("LoadJniLib", "Error: Could not load native library: " + ule.getMessage());
        }

        ArrayAdapter<String> paperSizeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item);
        paperSizeAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
        paperSizeAdapter.add("58 mm");
        paperSizeAdapter.add("80 mm");
        spinner_paper_width.setAdapter(paperSizeAdapter);



        spinner_printer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpnModelsItem spnModelsItem = (SpnModelsItem)parent.getItemAtPosition(position);

                Log.d(Commons.TAG, "spinner_printer = "+spnModelsItem.toString());
                if (spnModelsItem.toString().contains("Epson")){
                    btn_select_printer.setVisibility(View.VISIBLE);

                    initializeObject();

                }else {
                    btn_select_printer.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_paper_width.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                paper_size = (String) parent.getItemAtPosition(position);

                Log.d(Commons.TAG, "paper_size = "+paper_size);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_select_printer.setOnClickListener(v -> {

            Intent intent = new Intent(PrinterAddActivity.this,
                    EpsonDiscoveryActivity.class);
            startActivityForResult(intent, 121);
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
            mEpsonPrinter = new Printer(((SpnModelsItem) spinner_printer.getSelectedItem()).getModelConstant(),
                    ((SpnModelsItem) spinner_epson_lan.getSelectedItem()).getModelConstant(),
                    this);
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
            mEpsonPrinter.connect(Target, Printer.PARAM_DEFAULT);
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
            }
            catch (Epos2Exception e) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 121 && resultCode == RESULT_OK && data != null){

            Target = data.getStringExtra("Target");
            DeviceName = data.getStringExtra("PrinterName");

            Log.d(Commons.TAG, "Target = "+Target);
            Log.d(Commons.TAG, "DeviceName = "+DeviceName);

        }
    }



    private boolean runPrintReceiptSequence_EPSON() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Print processing...");
        progressDialog.show();

        if (!initializeObject()) {
            return false;
        }

        if (paper_size.contains("58")){
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
            mEpsonPrinter.addTextSize(1, 2);
            mEpsonPrinter.addTextFont(Printer.FONT_A);
            mEpsonPrinter.addTextAlign(Printer.ALIGN_CENTER);

            textData.append("Test Receipt");
            textData.append("\n");

            mEpsonPrinter.addTextSize(1, 1);
            textData.append("--------------------------------\n");


            textData.append(preferense.getString(Preferense.PREF_business));
            textData.append("\n");
            textData.append("\n");

            mEpsonPrinter.addText(textData.toString());

            mEpsonPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            ShowMsg.showException(e, method, getApplicationContext());
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

            textData.append("Test Receipt");
            textData.append("\n");

            mEpsonPrinter.addTextSize(1, 1);
            textData.append("------------------------------------------------\n");


            textData.append(preferense.getString(Preferense.PREF_business));
            textData.append("\n");
            textData.append("\n");

            mEpsonPrinter.addText(textData.toString());

            mEpsonPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            ShowMsg.showException(e, method, getApplicationContext());
            return false;
        }

        textData = null;

        return true;
    }

}
