package com.wwc2.ttxassist.h264;

import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;


import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class BaseDispatch {


    IChannelDataCallback mRawDataCallback;

    static final byte sps[]= {0x00,0x00,0x00,0x01,0x67,0x64,0x00,0x29,
            (byte)0xac,(byte)0x1b,0x1a,(byte)0x80,(byte)0xa0,(byte)0x2f,(byte)0xf9,0x66,
            (byte)0xa0,(byte)0xa0,0x40,0x40,(byte)0xf0,(byte)0x88,0x46,(byte)0xe0};

    static final byte pps[]={0x00,0x00,0x00,0x01,0x68,(byte)0xea,0x43,(byte)0xcb};


    public BaseDispatch(IChannelDataCallback mRawDataCallback) {
        this.mRawDataCallback = mRawDataCallback;

    }

    public abstract  void dispatchData(byte[] data);
    public abstract void start();
    public abstract  void destroy();

    protected IChannelDataCallback getRawDataCallback(){
        return mRawDataCallback;
    }

    abstract public int getChannelNumber();


}
