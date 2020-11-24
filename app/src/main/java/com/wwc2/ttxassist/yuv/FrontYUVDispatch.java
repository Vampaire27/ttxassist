package com.wwc2.ttxassist.yuv;

import android.content.Context;

import com.wwc2.ttxassist.h264.IChannelDataCallback;
import com.wwc2.ttxassist.h264.LocalH264Dispatch;
import com.wwc2.ttxassist.h264.Sutils;


public class FrontYUVDispatch extends BaseYUVDispatch{

    private String TAG = "FrontYUVDispatch";


    private  final String YUV_DATA = "/sdcard/.ch0AlgoFrame";

    public static final int LOCAL_YUV_FONT_CMD = 30;



    public FrontYUVDispatch(IYUVDataCallback callback, Context mCtx) {
        super(callback,mCtx);
    }

    @Override
    public int getChannelNumber() {
        return Sutils.LOCAL_H264_FONT_TYPE;
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
