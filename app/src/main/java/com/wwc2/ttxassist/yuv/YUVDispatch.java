package com.wwc2.ttxassist.yuv;


public abstract class YUVDispatch {


    IYUVDataCallback mRawDataCallback;


    public YUVDispatch(IYUVDataCallback mRawDataCallback) {
        this.mRawDataCallback = mRawDataCallback;
    }

    public abstract  void dispatchData(byte[] data);
    public abstract void start();
    public abstract  void destroy();

    protected IYUVDataCallback getRawDataCallback(){
        return mRawDataCallback;
    }

    abstract public int getChannelNumber();

}
