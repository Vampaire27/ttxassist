package com.wwc2.ttxassist.mediacodec;

public interface IAudioDataCallback {

    void audioAacData(int channel, byte[] aac, int length) ;
}
