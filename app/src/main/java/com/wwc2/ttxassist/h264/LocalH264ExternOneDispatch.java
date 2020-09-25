package com.wwc2.ttxassist.h264;

import android.content.Context;

public class LocalH264ExternOneDispatch extends LocalH264Dispatch{

    private String TAG = "LocalH264BackDispatch";

    private  final String H264_SYNC = "/dev/wwc2_hsch4_sync";
    private  final String H264_DATA = "/sdcard/.h264StreamCh4";

    public  final int MODE_WWC2_H264_START = 54;
    public  final int MODE_WWC2_H264_STOP = 104;

    public LocalH264ExternOneDispatch(IChannelDataCallback callback, Context mCtx) {
        super(callback,mCtx);
    }

     @Override
     public int getChannelNumber() {
         return Sutils.LOCAL_H264_EXTERN_ONE;
     }

    @Override
    protected String getH264Sync() {
        return H264_SYNC;
    }

    @Override
    protected String getH264Data() {
        return H264_DATA;
    }

    @Override
    protected int getH264Start() {
        return MODE_WWC2_H264_START;
    }

    @Override
    protected int getH264Stop() {
        return MODE_WWC2_H264_STOP;
    }

}
