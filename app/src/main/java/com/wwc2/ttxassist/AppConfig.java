package com.wwc2.ttxassist;

import android.content.Context;
import android.content.SharedPreferences;


public class AppConfig {


    private static final String USER_INFO = "user_info";

    public static final String USER_ACCOUNT = "account";
    public static final String SERVICE_ADDRESS = "address";

    public AppConfig( ) {
    }

    public void put(Context mContext, String key, String value){

        SharedPreferences pref = mContext.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public String getValue(Context mContext,String key, String value){
        SharedPreferences pref = mContext.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        return  pref.getString(key,value);
    }

    private static class SingletonHolder {
        //由JVM保证只初始化一次
        private static AppConfig instance = new AppConfig();
    }

    public static AppConfig getInstance(){
        return  SingletonHolder.instance;
    }
}
