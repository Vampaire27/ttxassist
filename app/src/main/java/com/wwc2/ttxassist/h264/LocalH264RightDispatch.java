package com.wwc2.ttxassist.h264;

import android.content.Context;

public class LocalH264RightDispatch extends LocalH264Dispatch{

    private String TAG = "LocalH264RightDispatch";

    private static final String H264_SOCKET = "h264StreamRightSocket";
    private static final String H264_FILE = "/proc/h264/stream_right";

    @Override
    public int getChannelNumber() {
        return Sutils.LOCAL_H264_RIGHT_TYPE;
    }

    public LocalH264RightDispatch(IChannelDataCallback callback, Context mCtx) {
        super(callback,mCtx);;
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
