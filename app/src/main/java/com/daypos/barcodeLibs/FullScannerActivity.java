package com.daypos.barcodeLibs;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.daypos.R;
import com.daypos.cart.CartActivity;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.Commons;
import com.daypos.utils.GlobalClass;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class FullScannerActivity extends BaseScannerActivity implements
        MessageDialogFragment.MessageDialogListener,
        ZXingScannerView.ResultHandler,
        FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener {

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;

    public static TextView cart_counter;
    private RelativeLayout cart_relativeLayout;

    private GlobalClass globalClass;



    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if(state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }

        setContentView(R.layout.activity_barcode_scanner);
        setupToolbar();

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        setupFormats();
        contentFrame.addView(mScannerView);

        globalClass = (GlobalClass) getApplicationContext();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;


        menuItem = menu.add(Menu.NONE, R.id.add_cat, 0, R.string.goto_cart);
        //MenuItem menuItem_cart = menu.findItem(R.id.add_cat);
        MenuItem menuItem_cart = menuItem;
        MenuItemCompat.setActionView(menuItem_cart, R.layout.cart_counter);
        MenuItemCompat.setShowAsAction(menuItem_cart, MenuItem.SHOW_AS_ACTION_IF_ROOM);
        cart_relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(menuItem_cart);
        cart_counter = cart_relativeLayout.findViewById(R.id.tv_cart_counter);

        cart_counter.setText(globalClass.getCart_counter());
        cart_relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FullScannerActivity.this,
                        CartActivity.class);
                startActivity(intent);
                finish();

            }
        });



        if(mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);


        if(mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_flash:
                mFlash = !mFlash;
                if(mFlash) {
                    item.setTitle(R.string.flash_on);
                } else {
                    item.setTitle(R.string.flash_off);
                }
                mScannerView.setFlash(mFlash);
                return true;
            case R.id.menu_auto_focus:
                mAutoFocus = !mAutoFocus;
                if(mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on);
                } else {
                    item.setTitle(R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus(mAutoFocus);
                return true;
            case R.id.menu_formats:
                DialogFragment fragment = FormatSelectorDialogFragment.newInstance(this,
                        mSelectedIndices);
                fragment.show(getSupportFragmentManager(), "format_selector");
                return true;
            case R.id.menu_camera_selector:
                mScannerView.stopCamera();
                DialogFragment cFragment = CameraSelectorDialogFragment.newInstance(this, mCameraId);
                cFragment.show(getSupportFragmentManager(), "camera_selector");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();

            Log.d(Commons.TAG, "Scan result = "+rawResult.getText()
                    + ", Format = " + rawResult.getBarcodeFormat().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        addToCart(rawResult.getText());

       // mScannerView.resumeCameraPreview(this);

      //  showMessageDialog("Contents = " + rawResult.getText() + ", Format = " + rawResult.getBarcodeFormat().toString());
      //  showMessageDialog("Contents = " + rawResult.getText());
    }


    public void showMessageDialog(String message) {
        DialogFragment fragment = MessageDialogFragment
                .newInstance("Product added to cart", message, this);
        fragment.show(getSupportFragmentManager(), "scan_results");
    }

    public void closeMessageDialog() {
        closeDialog("scan_results");
    }

    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if(fragment != null) {
            fragment.dismiss();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Resume the camera
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    @Override
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if(mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            for(int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for(int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if(mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        closeMessageDialog();
        closeFormatsDialog();
    }


    private void addToCart(String barcode) {

        String url = ApiConstant.add_to_cart;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("item_id", "");
        params.put("bar_code", barcode);
        params.put("modifires", "");
        params.put("type", "2");

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        String count = response.optString("count");
                        cart_counter.setText(count);
                        globalClass.setCart_counter(count);

                       /* Toasty.success(getApplicationContext(),
                                "Added",
                                Toast.LENGTH_SHORT, true).show();*/


                    }else {

                        Toasty.error(getApplicationContext(),
                                message,
                                Toast.LENGTH_SHORT, true).show();
                    }

                    holdForCameraResume();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }


    private void holdForCameraResume(){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resumeCamera();
            }
        }, 1500);
    }

    private void resumeCamera(){
        mScannerView.resumeCameraPreview(this);
    }


}
