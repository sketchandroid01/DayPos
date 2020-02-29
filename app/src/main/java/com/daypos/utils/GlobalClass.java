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
    private String cart_counter = "0";

    public String getUserId() {
        return "1";
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCart_counter() {
        return cart_counter;
    }

    public void setCart_counter(String cart_counter) {
        this.cart_counter = cart_counter;
    }

    /// selected customer id for billing ...
    private String cid = "";
    private String cname = "";
    private String cemail = "";
    private String cphone = "";

    public String getCid() {
        return cid;
    }

    public GlobalClass setCid(String cid) {
        this.cid = cid;
        return this;
    }

    public String getCname() {
        return cname;
    }

    public GlobalClass setCname(String cname) {
        this.cname = cname;
        return this;
    }

    public String getCemail() {
        return cemail;
    }

    public GlobalClass setCemail(String cemail) {
        this.cemail = cemail;
        return this;
    }

    public String getCphone() {
        return cphone;
    }

    public GlobalClass setCphone(String cphone) {
        this.cphone = cphone;
        return this;
    }
}
