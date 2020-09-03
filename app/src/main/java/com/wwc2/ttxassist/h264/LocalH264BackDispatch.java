package com.wwc2.ttxassist.h264;

import android.content.Context;

public class LocalH264BackDispatch extends LocalH264Dispatch{

    private String TAG = "LocalH264BackDispatch";

    private static final String H264_SOCKET = "h264StreamBackSocket";
    private static final String H264_FILE = "/proc/h264/stream_back";

    public LocalH264BackDispatch(IChannelDataCallback callback, Context mCtx) {
        super(callback,mCtx);
    }

     @Override
     public int getChannelNumber() {
         return Sutils.LOCAL_H264_BACK_TYPE;
     }

    @Override
    protected String getH264Socket() {
        return H264_SOCKET;
    }

    @Override
    protected String getH264File() {
        return H264_FILE;
    }

}
