package com.daypos.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferense {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private GlobalClass globalClass;

    private static final String PREFS_NAME = "daypos_prefs";


    private static final String PREF_logInStatus = "logInStatus";
    private static final String PREF_firstlogin = "firstlogin";
    public static final String PREF_name = "name";
    public static final String PREF_email = "user_email";
    public static final String PREF_image = "user_image";
    public static final String PREF_phone_number = "phone_number";
    public static final String PREF_business = "business";
    public static final String PREF_id = "id";
    public static final String Pref_Pin = "pincode";
    public static final String employee_id = "employee_id";


    public Preferense(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        globalClass = (GlobalClass) context.getApplicationContext();

    }

    public void saveFirstLogin(boolean val){
        editor.putBoolean(PREF_firstlogin, val);
        editor.commit();
    }

    public boolean isFirstLogin(){
        return sharedPreferences.getBoolean(PREF_firstlogin, true);
    }

    public boolean isLogin(){
        return sharedPreferences.getBoolean(PREF_logInStatus, false);
    }

    public void setPref_logInStatus(boolean val){
        editor.putBoolean(PREF_logInStatus, val);
        editor.commit();
    }

    public void saveString(String key, String val){
        editor.putString(key, val);
        editor.commit();
    }


    public String getString(String key){
        return sharedPreferences.getString(key, "");
    }

    public void setToGlobal(){
        globalClass.setUserId(sharedPreferences.getString(PREF_id, ""));

    }


    public void clearData(){
        editor.clear();
        editor.commit();
    }



}
