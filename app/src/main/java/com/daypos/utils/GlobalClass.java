package com.daypos.utils;

import android.content.Context;
import android.text.TextUtils;

import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import static com.android.volley.VolleyLog.TAG;

public class GlobalClass extends MultiDexApplication {

    // git pull

    // https://github.com/SouravAndroid/DayPos

    // https://github.com/sketchandroid01/DayPos



    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context context;
    // public DbHelper myDbHelper;

    private static GlobalClass mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        GlobalClass.context = getApplicationContext();
    }
    public static Context getAppContext() {
        return GlobalClass.context;
    }

    public static synchronized GlobalClass getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    ///// data..

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



}
