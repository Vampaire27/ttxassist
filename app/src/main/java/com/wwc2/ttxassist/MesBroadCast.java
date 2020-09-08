package com.wwc2.ttxassist;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.util.ArrayMap;
import android.util.Log;

import com.wwc2.ttxassist.h264.BaseDispatch;
import com.wwc2.ttxassist.h264.IChannelDataCallback;
import com.wwc2.ttxassist.h264.LocalH264BackDispatch;
import com.wwc2.ttxassist.h264.LocalH264FrontDispatch;
import com.wwc2.ttxassist.h264.LocalH264LeftDispatch;
import com.wwc2.ttxassist.h264.LocalH264RightDispatch;
import com.wwc2.ttxassist.h264.Sutils;
import com.wwc2.ttxassist.mediacodec.AudioCodecManager;
import com.wwc2.ttxassist.mediacodec.IAudioDataCallback;
import com.wwc2.ttxassist.utils.LogUtils;



public class MesBroadCast extends BroadcastReceiver implements IChannelDataCallback , IAudioDataCallback {
    private static final String TAG = TTXService.TAG;
    public static final String MESSAGE_RECEIVED_LIVE_ACTION = "net.babelstar.MESSAGE_RECEIVED_LIVE";	//视频预览请求
    public static final String START_MESSAGE = "start";
    public static final String TYPE_MESSAGE = "type";
    public static final String CHANNEL_MESSAGE = "channel";
    public static final String STREAM_MESSAGE = "stream";
    //Type
    public static final int TTX_MEDIA_AV		= 1;		//实时视频
    public static final int TTX_MEDIA_TALKBACK	= 2;		//对讲
    public static final int TTX_MEDIA_LISTEN	= 3;		//监听

    public static final int TTX_START =1;
    public static final int TTX_END =0;
    public byte[] SPS = new byte[32];

//    public static final int TTX_START =1;
//    public static final int TTX_START =1;
//    public static final int TTX_START =1;
//    public static final int TTX_START =1;



    public TTXService mTTXService;
    private ArrayMap<Integer, BaseDispatch> mRegister = new ArrayMap<>();
    private AudioCodecManager mAudioMng;

    public MesBroadCast(Service serivce) {
        super();
        mTTXService = (TTXService) serivce;
        LogUtils.init(mTTXService);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == MESSAGE_RECEIVED_LIVE_ACTION) {
            int start = intent.getIntExtra(START_MESSAGE, 0);
            int type = intent.getIntExtra(TYPE_MESSAGE, 0);
            int channel = intent.getIntExtra(CHANNEL_MESSAGE, 0);
            int stream = intent.getIntExtra(STREAM_MESSAGE, 0);

            toDoMes(start,channel,type);
            Log.d(TAG, " MESSAGE_RECEIVED_LIVE start= " + start + ",type =  " + type + ", channel =" + channel);

        }
    }

        public void toDoMes(int start ,int channel,int type) {
             if(start == TTX_START){
                 BaseDispatch mBaseDispatch;
                 switch (channel) {
                     case 0:
                         if(mRegister.get(channel)== null) {
                             mBaseDispatch = new LocalH264FrontDispatch(this, mTTXService);
                             mBaseDispatch.start();  //data not come form PreViewCallback
                             mRegister.put(channel, mBaseDispatch);

                          }
                          if(mAudioMng == null){
                              mAudioMng = new AudioCodecManager(mTTXService);
                          }
                          mAudioMng.startRecord(this);
                         break;
                     case 1:
                         if(mRegister.get(channel) == null) {
                             mBaseDispatch = new LocalH264BackDispatch(this, mTTXService);
                             mBaseDispatch.start();  //data not come form PreViewCallback
                             mRegister.put(channel, mBaseDispatch);
                         }
                         break;
                     case 2:
                         if(mRegister.get(channel) == null) {
                             mBaseDispatch = new LocalH264LeftDispatch(this, mTTXService);
                             mBaseDispatch.start();  //data not come form PreViewCallback
                             mRegister.put(channel, mBaseDispatch);
                         }
                         break;
                     case 3:
                         if(mRegister.get(channel) == null) {
                             mBaseDispatch = new LocalH264RightDispatch(this, mTTXService);
                             mBaseDispatch.start();  //data not come form PreViewCallback
                             mRegister.put(channel, mBaseDispatch);
                         }
                         break;

                     default:
                         break;
                 }

             }else if(start == TTX_END){
                 BaseDispatch mBaseDispatch = mRegister.remove(channel);
                 if(mBaseDispatch != null) {
                     mBaseDispatch.destroy();
                 }
                 if(channel ==0){
                     mAudioMng.stopRecord();
                 }
             }
        }

    @Override
    public synchronized void inputH264Nalu(int channel, byte[] nalu, int naluLength) {

           if(isSps(nalu)){
               System.arraycopy(nalu, 0, SPS, 0, 32);
           }else if(isIDR(nalu)){
               byte[] send = Sutils.byteMerger(SPS,nalu);
//               String a= byteToHex(send,send.length);
//               Log.d(TAG,"1ns frame . =" + a );
//               LogUtils.log2FileOnlyhex(send,send.length);
               mTTXService.inputH264Nalu(channel,1,send,send.length);

           }else {
              // String a= byteToHex(nalu,naluLength);
//               Log.d(TAG,"2ns frame . =" + a );
//               LogUtils.log2FileOnlyhex(nalu,naluLength);
               mTTXService.inputH264Nalu(channel, 1, nalu, naluLength);
           }


    }

     public boolean isSps(byte data[]){
        if(data[0] ==0x00 &&
            data[1] ==0x00 &&
             data[2] ==0x00 &&
              data[3] == 0x01 &&
                data[4] == 0x67){
            return  true;
        }
        return false;
     }

     public boolean isIDR(byte data[]){
         if(data[0] ==0x00 &&
                 data[1] ==0x00 &&
                 data[2] ==0x00 &&
                 data[3] == 0x01 &&
                 data[4] == 0x65){
             return  true;
         }
         return false;
     }

    public String byteToHex(byte[] bytes,int size){
            String strHex = "";
            StringBuilder sb = new StringBuilder("");
            for (int n = 0; n < size; n++) {
                strHex = Integer.toHexString(bytes[n] & 0xFF);
                sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
            }
            return sb.toString().trim();
        }


    @Override
    public void audioAacData(int channel, byte[] aac, int length) {
        Log.d(TAG,"audioAacData . =" + aac );
        mTTXService.sendAudioData(channel,aac,length);
    }
}
