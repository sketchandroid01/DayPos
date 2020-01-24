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
    private static final String PREF_name = "name";
    private static final String PREF_fname = "fname";
    private static final String PREF_lname = "lname";
    private static final String PREF_email = "user_email";
    private static final String PREF_phone_number = "phone_number";
    private static final String PREF_business = "business";
    private static final String PREF_id = "id";


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



    public void setToGlobal(){

        globalClass.setUserId(sharedPreferences.getString(PREF_id, ""));


    }





}
