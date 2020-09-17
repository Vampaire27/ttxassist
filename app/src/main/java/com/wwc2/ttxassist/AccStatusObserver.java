package com.wwc2.ttxassist;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class AccStatusObserver extends ContentObserver {
    private String TAG = "AccStatusObserver";
    private TTXService mTTXService;
    private String oldAcc = "";

    public static final String AUTHORITY = "com.wwc2.main.provider.logic";
    public static final String ACC_STATUS = "acc_status";

    public AccStatusObserver(Handler handler, TTXService mTTXService) {
        super(handler);
        this.mTTXService = mTTXService;
    }


    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        Uri uri_acc = Uri.parse("content://" + AUTHORITY + "/" + ACC_STATUS);
        String strAcc = mTTXService.getContentResolver().getType(uri_acc);
        if(oldAcc.equals(strAcc)){
            Log.d(TAG,"observer acc change 重复,return!!");
            return;
        }
        Log.d(TAG,"observer acc change to oldAcc=" + oldAcc+ "---strAcc=" + strAcc);
        oldAcc = strAcc;
        switch (oldAcc) {
            case "true":
                if(!mTTXService.ismBindSuc()){
                    mTTXService.bindttx();
                }
                break;
            case "false":
                mTTXService.bindDieClear();
                break;
            default:break;
        }

    }
}
