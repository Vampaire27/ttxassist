package com.wwc2.ttxassist;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MesBroadCast extends BroadcastReceiver {
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
    public TTXService mTTXService;

    public MesBroadCast(Service serivce) {
        super();
        mTTXService = (TTXService) serivce;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == MESSAGE_RECEIVED_LIVE_ACTION){
            int start = intent.getIntExtra(START_MESSAGE,0);
            int type = intent.getIntExtra(TYPE_MESSAGE,0);
            int channel = intent.getIntExtra(CHANNEL_MESSAGE,0);
            int stream = intent.getIntExtra(STREAM_MESSAGE,0);


            Log.d(TAG," MESSAGE_RECEIVED_LIVE start= " +start + ",type =  "+ type + ", channel =" +channel);

        }

    }
}
