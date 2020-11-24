package com.wwc2.ttxassist.yuv;

/**
 * 共享内存文件状态标志类
 */
public enum MemoryFileFlag {

    /**
     * 可写标志
     */
    CAN_WRITE((byte)0),
    /**
     * 可读标志
     */
    CAN_READ((byte)1);


    private byte flag;

    private MemoryFileFlag(byte flag) {
        this.flag = flag;
    }

    public byte getFlag() {
        return this.flag;
    }
}