package com.daypos.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

public class Util {

    private static NetworkInfo networkInfo;

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // test for connection for WIFI
        if (networkInfo != null
                && networkInfo.isAvailable()
                && networkInfo.isConnected()) {
            // String networkstate = String.valueOf(networkInfo.getExtraInfo());
            return true;
        }

        networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // test for connection for Mobile
        return networkInfo != null
                && networkInfo.isAvailable()
                && networkInfo.isConnected();

    }


    public JSONObject getjsonobject(String responce) {
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(responce);
        } catch (Exception e) {

        }
        return jobj;
    }

}
