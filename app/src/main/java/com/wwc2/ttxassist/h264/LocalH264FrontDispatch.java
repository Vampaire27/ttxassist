package com.wwc2.ttxassist.h264;

import android.content.Context;


public class LocalH264FrontDispatch extends LocalH264Dispatch{

    private String TAG = "LocalH264FrontDispatch";


    private  final String H264_SYNC = "/dev/wwc2_hsf_sync";
    private  final String H264_DATA = "/sdcard/.h264StreamFront";

    public  final int MODE_WWC2_H264_START =50;
    public  final int MODE_WWC2_H264_STOP =100;


    public LocalH264FrontDispatch(IChannelDataCallback callback, Context mCtx) {
        super(callback,mCtx);
    }

    @Override
    public int getChannelNumber() {
        return Sutils.LOCAL_H264_FONT_TYPE;
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
