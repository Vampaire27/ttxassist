package com.wwc2.ttxassist;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class TTXApplication  extends Application {
    private String TAG = "TTXApplication";
    Context mContext;
    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
        super.onCreate();
        mContext = getApplicationContext();
        startTTxService();
    }

    public  void startTTxService() {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                mContext.getPackageName() + ".TTXService");
        intent.setComponent(componentName);
        mContext.startService(intent);
    }

}
