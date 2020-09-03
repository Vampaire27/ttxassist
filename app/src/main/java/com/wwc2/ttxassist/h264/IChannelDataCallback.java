package com.wwc2.ttxassist.h264;

public interface IChannelDataCallback {
     void inputH264Nalu(int channel, byte[] nalu, int naluLength) ;
}
