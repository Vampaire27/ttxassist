package com.wwc2.ttxassist.yuv;

import android.content.Context;

import com.wwc2.ttxassist.h264.Sutils;


public class LeftYUVDispatch extends BaseYUVDispatch{

    private String TAG = "LeftYUVDispatch";


    private  final String YUV_DATA = "/sdcard/.ch2AlgoFrame";

    public static final int LOCAL_YUV_FONT_CMD = 32;



    public LeftYUVDispatch(IYUVDataCallback callback, Context mCtx) {
        super(callback,mCtx);
    }

    @Override
    public int getChannelNumber() {
        return Sutils.LOCAL_H264_LEFT_TYPE;
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
