package com.wwc2.ttxassist.yuv;

public interface IYUVDataCallback {
     void inputYUV(int channel, byte[] nalu, int naluLength) ;
}
