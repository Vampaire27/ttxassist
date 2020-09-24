package com.wwc2.ttxassist.h264;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

abstract public class LocalH264Dispatch extends BaseDispatch{

    private String TAG = "LocalH264Dispatch";

    private static final int BUFFER_SIZE = 4;

    private static final int STATUS_DATA_READY =1;

    private static final int STATUS_DATA_WRITE_FINISH =1;

    private static final int STATUS_DATA_WRITE_STOP =0Xff;

    private static final int byte_max = 100*1024;

    SocketThread mSocketThread;
    Context mCtx;

    private RandomAccessFile yuvFile;
    private MappedByteBuffer yuv_Map = null;
    private ParcelFileDescriptor mpfd;

    private static String controlDev ="/sys/devices/platform/wwc2_camera_combine/camera_action";

    public static final int MODE_WWC2_H264 =3;

    private FileInputStream syncFile;

    public LocalH264Dispatch(IChannelDataCallback callback, Context mCtx) {
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
        File mFile =new File(getH264Data());
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
        File mFile =new File(getH264Data());
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
            yuvFile =new RandomAccessFile(getH264Data(), "rw");
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

    private  boolean openSyncFile(){
        boolean ret = true;
        try {
            syncFile =new FileInputStream(getH264Sync());
        }catch (Exception e){
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


    private void closeSyncfile(){
        if(syncFile != null){
            try {
                syncFile.close();
                syncFile = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void doTransactStart(){
        //send start cmd.
        sendFpsAndBps();
        waitfinish();
        String action = MODE_WWC2_H264 +" " + getH264Start();
        Sutils.writeTextFile(action,controlDev);

        // open  data map ..
        if(!openMapFile()){
            Log.d(TAG, " openMapFile file fail ;name = "  + getH264Data() );
            return;
        }
        //open sync file
        if(!openSyncFile()){
            Log.d(TAG, " openSyncFile file fail;name = "  + getH264Sync() );
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
            int buffSize =0;

            while (true) {
               byte[] tmpbuf = new byte[byte_max];

                try {
                    buffSize =  getSyncSignal(); // wait the 1st data;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    yuvFile.seek(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    yuvFile.read(tmpbuf, 0, buffSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Log.d(TAG, " Socket Thread ..buffSize = " + buffSize );

                getRawDataCallback().inputH264Nalu(getChannelNumber(),tmpbuf,buffSize);

                if(isStop){
                    Log.d(TAG, " Socket Thread .is Interrupted.."  );

                    closeSyncfile();
                    closeMapFile();

                    String action = MODE_WWC2_H264 +" " + getH264Stop();
                    Sutils.writeTextFile(action,controlDev);

                    break;
                }


            }

        }

        private int getSyncSignal() throws IOException {

            byte[] buffer = new byte[BUFFER_SIZE];
            syncFile.read(buffer,0,4);

            int value = Sutils.byteArrayToInt(buffer,0);
            Log.e(TAG, "getSyncSignal size = " + value);
            return value;
        }

    }

    protected abstract String getH264Sync();
    protected abstract String getH264Data();
    protected abstract int getH264Start();
    protected abstract int getH264Stop();

    void sendFpsAndBps() {

        Log.e(TAG, "sendFpsAndBps do nothing !");
    }


}
