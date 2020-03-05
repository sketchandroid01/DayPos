package com.daypos.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Commons {

    public static final String TAG = "TAG";


    public static String getFolderDirectory(){
        final String dir = android.os.Environment.getExternalStorageDirectory() + "/DayPOS";
        return dir;
    }


    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null && inputManager != null) {
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                inputManager.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }


    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }


    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static String convertDate1(String dates){
        String date = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date newDate = spf.parse(dates);

            spf= new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH);
            date = spf.format(newDate);

        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    public static String convertToTime(String dates){
        String date = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date newDate = spf.parse(dates);

            spf= new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            date = spf.format(newDate);

        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    public static String convertDateFormatBill(String dates){
        String date = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date newDate = spf.parse(dates);

            spf= new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH);
            date = spf.format(newDate);

        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }
}
