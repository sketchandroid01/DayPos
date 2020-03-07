package com.daypos.fragments.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.daypos.R;
import com.daypos.utils.Commons;
import com.daypos.utils.Preferense;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.epson.eposprint.Print;

import java.util.ArrayList;
import java.util.HashMap;


public class EpsonDiscoveryActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Context mContext = null;
    private ArrayList<HashMap<String, String>> mPrinterList = null;
    private FilterOption mFilterOption = null;
    private TMPrinterAdapter printerListAdapter = null;

    ListView list;

    Preferense prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discovery);
        mContext = this;
        prefManager = new Preferense(this);

        Button button = findViewById(R.id.btnRestart);
        button.setOnClickListener(this);

        mPrinterList = new ArrayList<HashMap<String, String>>();
        printerListAdapter = new TMPrinterAdapter(this, mPrinterList);

        list = findViewById(R.id.listView_printerlist);
        list.setAdapter(printerListAdapter);
        list.setOnItemClickListener(this);

        mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);

        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Commons.TAG, "start" +e);
            ShowMsg.showException(e, "start", mContext);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    break;
                }
            }
        }

        mFilterOption = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRestart:
                restartDiscovery();
                break;

            default:
                // Do nothing
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();

        HashMap<String, String> item  = mPrinterList.get(position);
        intent.putExtra("Target", item.get("Target"));
        intent.putExtra("PrinterName", item.get("PrinterName"));

        //globalClass.setOpenDeviceName(item.get("Target"));

        prefManager.saveThermalPrinterEpson(item.get("Target"), Print.DEVTYPE_USB);

        setResult(RESULT_OK, intent);

        finish();
    }

    private void restartDiscovery() {
        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    ShowMsg.showException(e, "stop", mContext);
                    return;
                }
            }
        }

        mPrinterList.clear();
        printerListAdapter.notifyDataSetChanged();

        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "stop", mContext);
        }
    }

    private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("PrinterName", deviceInfo.getDeviceName());
                    item.put("Target", deviceInfo.getTarget());
                    mPrinterList.add(item);

                    Log.d(Commons.TAG, "mPrinterList = "+mPrinterList);
                    printerListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

}
