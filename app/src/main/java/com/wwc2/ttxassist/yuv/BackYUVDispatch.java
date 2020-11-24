package com.wwc2.ttxassist.yuv;

import android.content.Context;

import com.wwc2.ttxassist.h264.Sutils;


public class BackYUVDispatch extends BaseYUVDispatch{

    private String TAG = "BackYUVDispatch";


    private  final String YUV_DATA = "/sdcard/.ch1AlgoFrame";

    public static final int LOCAL_YUV_FONT_CMD = 31;



    public BackYUVDispatch(IYUVDataCallback callback, Context mCtx) {
        super(callback,mCtx);
    }

    @Override
    public int getChannelNumber() {
        return Sutils.LOCAL_H264_BACK_TYPE;
    }

    @Override
    protected String getYUVData() {
        return YUV_DATA;
    }

    @Override
    public int getYUVCMDNumber() {
        return LOCAL_YUV_FONT_CMD ;
    }

}
