package com.wwc2.ttxassist.h264;

import android.content.Context;


public class LocalH264FrontDispatch extends LocalH264Dispatch{

    private String TAG = "LocalH264FrontDispatch";


    private static final String H264_SOCKET = "h264StreamFrontSocket";
    private static final String H264_FILE = "/proc/h264/stream_front";

    public LocalH264FrontDispatch(IChannelDataCallback callback, Context mCtx) {
        super(callback,mCtx);
    }

    @Override
    public int getChannelNumber() {
        return Sutils.LOCAL_H264_FONT_TYPE;
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
