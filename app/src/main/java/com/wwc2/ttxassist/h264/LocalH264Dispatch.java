package com.wwc2.ttxassist.h264;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileNotFoundException;
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


    public LocalH264Dispatch(IChannelDataCallback callback, Context mCtx) {
        super(callback);
        this.mCtx =mCtx;
    }

    @Override
    public void start() {
        mSocketThread = new SocketThread();
        mSocketThread.start();
    }

    @Override
    public void destroy() {
        mSocketThread.stopThread();
        mSocketThread = null;
    }

    @Override
    public void dispatchData(byte[] data) {

    }


    class SocketThread extends Thread {

        private LocalSocket mSocket;
        private OutputStream mOutputStream;
        private InputStream mInputStream;
        private RandomAccessFile yuvFile;
        private MappedByteBuffer yuv_Map = null;
        private ParcelFileDescriptor mpfd;
        private String mapFileName;
        private boolean isStop =false;

       public void stopThread(){
           isStop =true;
       }
        public SocketThread( ) {
            super();
        }

        public void openMapFile(){
            try {
                yuvFile =new RandomAccessFile(getH264File(), "rw");
                FileChannel fc = yuvFile.getChannel();
                //size=该映射文件的总大小
               // yuv_Map = fc.map(FileChannel.MapMode.READ_WRITE, 0, yuvFile.length());
                mpfd = ParcelFileDescriptor.dup(yuvFile.getFD());
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
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

        @Override
        public void run() {
            super.run();

            int buffSize =0;
            byte[] tmpbuf = new byte[byte_max];
            byte[] spspps = new byte[32];
            try {
                openSocketLocked();
            } catch (IOException e) {
                Log.d(TAG, " open Socket fail!"  );
                return;
            }

            sendFpsAndBps();

            try {
                buffSize =  getBuffSize(); //wait the 1st data;
            } catch (IOException e) {
                e.printStackTrace();
            }

            openMapFile();


            Log.d(TAG, " Socket Thread ..buffSize = " + buffSize );
            try {
                yuvFile.seek(0);
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                yuvFile.read(spspps, 0, buffSize);
            } catch (IOException e) {
                e.printStackTrace();
            }

            sendResponse(STATUS_DATA_WRITE_FINISH);

            try {
                buffSize = getBuffSize();
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] IDR = new byte[buffSize];
            try {
                yuvFile.seek(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                yuvFile.read(IDR, 0, buffSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendResponse(STATUS_DATA_WRITE_FINISH);
            getRawDataCallback().inputH264Nalu(getChannelNumber(),Sutils.byteMerger(spspps,IDR),buffSize);

            try {
                buffSize = getBuffSize();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {

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

                Log.d(TAG, " Socket Thread ..buffSize = " + buffSize );

                getRawDataCallback().inputH264Nalu(getChannelNumber(),tmpbuf,buffSize);

                if(isStop){
                    Log.d(TAG, " Socket Thread .is Interrupted.."  );
                    sendResponse(STATUS_DATA_WRITE_STOP);
                    closeSocketLocked();
                    closeMapFile();
                    break;
                }

                sendResponse(STATUS_DATA_WRITE_FINISH);
                try {
                    buffSize = getBuffSize();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }


        private void openSocketLocked() throws IOException {
            try {
                LocalSocketAddress address = new LocalSocketAddress(getH264Socket(),
                        LocalSocketAddress.Namespace.ABSTRACT);

                mInputStream = null;
                mSocket = new LocalSocket();
                mSocket.connect(address);

                mOutputStream = mSocket.getOutputStream();
                mInputStream = mSocket.getInputStream();
            } catch (IOException ioe) {
                closeSocketLocked();
                throw ioe;
            }
        }

        private int listenToSocket() throws IOException {
            byte[] buffer = new byte[BUFFER_SIZE];
            mInputStream.read(buffer);
            return buffer[0];
        }

        private int getBuffSize() throws IOException {
            int total_size= 0;
            int read_size= 0;
            byte[] buffer = new byte[BUFFER_SIZE];

            while(true){ //need read 4 byte.
                read_size = mInputStream.read(buffer,total_size,4-total_size);
                total_size += read_size;
                if(total_size == 4) {
                    break;
                }
            }

            int value = Sutils.byteArrayToInt(buffer,0);
            return value;
        }




        private void closeSocketLocked() {
            try {
                if (mOutputStream != null) {
                    mOutputStream.close();
                    mOutputStream = null;
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed closing output stream: " + e);
            }

            try {
                if (mSocket != null) {
                    mSocket.close();
                    mSocket = null;
                }
            } catch (IOException ex) {
                Log.e(TAG, "Failed closing socket: " + ex);
            }
        }

        /** Call to stop listening on the socket and exit the thread. */
        void stopListening() {
            synchronized (this) {
                closeSocketLocked();
            }
        }

        void sendResponse(int msg) {
            synchronized (this) {
                if (mOutputStream != null) {
                    try {
                        mOutputStream.write(msg);
                    } catch (IOException ex) {
                        Log.e(TAG, "Failed to write response:", ex);
                    }
                }
            }
        }


        private int getValidFPS(){

            return Sutils.DEF_H264_FRAME_RATE;
        }

        private int getValidBPS(){
            return Sutils.DEF_H264_BIT_RATE;
        }

        void sendFpsAndBps() {

            synchronized (this) {
                byte[] fps  = Sutils.intToByteArray(getValidFPS());
                byte[] bps  = Sutils.intToByteArray(getValidBPS());
                if (mOutputStream != null) {
                    try {
                        mOutputStream.write(fps);
                        mOutputStream.write(bps);
                    } catch (IOException ex) {
                        Log.e(TAG, "Failed to write response:", ex);
                    }
                }
            }
        }

    }

    protected abstract String getH264Socket();
    protected abstract String getH264File();

     private void readSpsPpsData(byte[] data){

     }

}
