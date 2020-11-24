package com.wwc2.ttxassist.yuv;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.wwc2.ttxassist.h264.BaseDispatch;
import com.wwc2.ttxassist.h264.IChannelDataCallback;
import com.wwc2.ttxassist.h264.Sutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

abstract public class BaseYUVDispatch extends YUVDispatch{

    private String TAG = "LocalYUVDispatch";

    private static final int DATA_SIZE = 640*720*3;
    private static final int HEADER_SIZE = 1 ;

    private static final int DATA_HEADER_MAX = DATA_SIZE + HEADER_SIZE; // =1280*720*1.5+1


    SocketThread mSocketThread;
    Context mCtx;

    private RandomAccessFile yuvFile;
    private MappedByteBuffer yuv_Map = null;
    private ParcelFileDescriptor mpfd;

    private static String controlDev ="/sys/devices/platform/wwc2_camera_combine/camera_action";

    public static final int MODE_WWC2_YUV_START =1;
    public static final int MODE_WWC2_YUV_STOP =0;


    public BaseYUVDispatch(IYUVDataCallback callback, Context mCtx) {
        super(callback);
        this.mCtx =mCtx;
    }

    @Override
    public void start() {
        doTransactStart();
    }

    @Override
    public void destroy() {
        doTransactStop();
    }

    @Override
    public void dispatchData(byte[] data) {

    }

    private void waitfinish(){
        File mFile =new File(getYUVData());
        int cnt =30;
        while (mFile.exists() && cnt > 0){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cnt--;
        }
    }

    private  boolean openMapFile(){
        boolean ret = true;
        //wait .3
        File mFile =new File(getYUVData());
        int cnt =200;
        while (!mFile.exists() && cnt > 0){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cnt--;
        }
        Log.d(TAG, " wait time cnt = "  +  cnt );

        if(cnt <= 0){
            ret  = false;
            return ret;
        }

        try {
            yuvFile =new RandomAccessFile(getYUVData(), "rw");
            FileChannel fc = yuvFile.getChannel();
            //size=该映射文件的总大小
            yuv_Map = fc.map(FileChannel.MapMode.READ_WRITE, 0, yuvFile.length());
            Log.d(TAG, "    map.length() = "  +  yuvFile.length() );

        }catch (FileNotFoundException e){
            e.printStackTrace();
            ret = false;
        } catch (IOException e) {
            e.printStackTrace();
            ret = false;
        }
        return  ret;
    }


    private  void closeMapFile(){
        if(mpfd !=null){
            try {
                mpfd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (yuvFile != null) {
            try {
                yuvFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(yuv_Map != null){
            yuv_Map.clear();
        }
        mpfd= null;
        yuv_Map = null;
        yuvFile= null;
    }


    public void doTransactStart(){
        //send start cmd.
        sendFpsAndBps();
        waitfinish();
        String action = getYUVCMDNumber() + " " +MODE_WWC2_YUV_START;
        Sutils.writeTextFile(action,controlDev);

        // open  data map ..
        if(!openMapFile()){
            Log.d(TAG, " openMapFile file fail ;name = "  + getYUVData() );
            return;
        }

        mSocketThread = new SocketThread();
        mSocketThread.start();

    }

    public void doTransactStop(){
        Log.d(TAG, " do TransactStop = ---------------" );
        if(mSocketThread != null ){
            mSocketThread.stopThread();
            mSocketThread = null;
        }
    }

    class SocketThread extends Thread {
        private boolean isStop =false;
        public void stopThread(){
           isStop =true;
       }


        public SocketThread( ) {
            super();
        }

        @Override
        public void run() {
            super.run();
            byte[] tmpbuf = new byte[DATA_HEADER_MAX];
            byte[] headerBuff = new byte[HEADER_SIZE];
            setWirtable(headerBuff);

            try {
                yuvFile.seek(DATA_SIZE);
                yuvFile.write(headerBuff);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {



                try {
                    yuvFile.seek(DATA_SIZE);
                    yuvFile.read(headerBuff, 0, HEADER_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(canRead(headerBuff)) {
                    Log.d(TAG, " canRead DATA... " );
                    try {
                        yuvFile.seek(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        yuvFile.read(tmpbuf, 0, DATA_SIZE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setWirtable(headerBuff);

                    try {
                        yuvFile.seek(DATA_SIZE);
                        yuvFile.write(headerBuff);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getRawDataCallback().inputYUV(getChannelNumber(), tmpbuf, DATA_SIZE);

                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(isStop){
                    Log.d(TAG, " Socket Thread .is Interrupted.."  );
                    closeMapFile();
                    String action = getYUVCMDNumber() + " " +MODE_WWC2_YUV_STOP;
                    Sutils.writeTextFile(action,controlDev);
                    break;
                }

            }

        }


    }



    protected abstract String getYUVData();

    protected abstract int getYUVCMDNumber();

    void sendFpsAndBps() {

        Log.e(TAG, "sendFpsAndBps do nothing !");
    }


    /**
     * 判断是否可读
     *
     * @param header
     * @return
     */
    public static boolean canRead(byte[] header) {
        if (header == null) {
            return false;
        }
        return header[0] == MemoryFileFlag.CAN_READ.getFlag();
    }


    /**
     * 设置为可写
     *
     * @param header
     * @return
     */
    public static boolean setWirtable(byte[] header) {
        if (header == null ) {
            return false;
        }
        header[0] = MemoryFileFlag.CAN_WRITE.getFlag();
        return true;
    }


}
