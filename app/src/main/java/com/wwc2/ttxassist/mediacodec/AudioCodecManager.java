package com.wwc2.ttxassist.mediacodec;


import android.content.Context;
import android.util.Log;

import com.wwc2.ttxassist.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class AudioCodecManager {
    private Context mCtx;
    AudioRecordEncoder mAudioRecordEncoder;


    public AudioCodecManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    public void startRecord(IAudioDataCallback lister){
        File file = new File(FileUtils.getAacFileDir(mCtx), FileUtils.getUUID32() + ".aac");
        Log.d("out file:", file.getAbsolutePath());
        mAudioRecordEncoder = new AudioRecordEncoder();
        if(lister != null) {
            mAudioRecordEncoder.setCodecDataLister(lister);
        }
        mAudioRecordEncoder.createAudio();
        try {
            mAudioRecordEncoder.createMediaCodec();
            mAudioRecordEncoder.start(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void stopRecord(){
        if (mAudioRecordEncoder != null) {
            mAudioRecordEncoder.stop();
            mAudioRecordEncoder = null;
        }
    }

}
