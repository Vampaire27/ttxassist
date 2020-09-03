package com.wwc2.ttxassist.h264;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Sutils {



    public static final int DEF_H264_FRAME_RATE = 100; //0.1HZ
    public static final int DEF_H264_BIT_RATE =160; // 1KB.


    public static final int LOCAL_H264_FONT_TYPE = 0;
    public static final int LOCAL_H264_BACK_TYPE = 1;
    public static final int LOCAL_H264_LEFT_TYPE = 2;
    public static final int LOCAL_H264_RIGHT_TYPE = 3;


    public static int byteArrayToInt(byte[] valueBuf, int offset) {
        ByteBuffer converter = ByteBuffer.wrap(valueBuf);
        converter.order(ByteOrder.nativeOrder());
        return converter.getInt(offset);
    }

    public static byte[] intToByteArray(int value) {
        ByteBuffer converter = ByteBuffer.allocate(4);
        converter.order(ByteOrder.nativeOrder());
        converter.putInt(value);
        return converter.array();
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

}
