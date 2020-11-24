package com.wwc2.ttxassist.yuv;

import android.content.Context;

import com.wwc2.ttxassist.h264.Sutils;


public class RightYUVDispatch extends BaseYUVDispatch{

    private String TAG = "LeftYUVDispatch";


    private  final String YUV_DATA = "/sdcard/.ch3AlgoFrame";

    public static final int LOCAL_YUV_FONT_CMD = 33;



    public RightYUVDispatch(IYUVDataCallback callback, Context mCtx) {
        super(callback,mCtx);
    }

    @Override
    public int getChannelNumber() {
        return Sutils.LOCAL_H264_RIGHT_TYPE;
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
