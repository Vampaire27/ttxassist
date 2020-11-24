package com.wwc2.ttxassist;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wwc2.ttxassist.R;
import com.wwc2.ttxassist.yuv.BackYUVDispatch;
import com.wwc2.ttxassist.yuv.BaseYUVDispatch;
import com.wwc2.ttxassist.yuv.FrontYUVDispatch;
import com.wwc2.ttxassist.yuv.IYUVDataCallback;
import com.wwc2.ttxassist.yuv.LeftYUVDispatch;
import com.wwc2.ttxassist.yuv.RightYUVDispatch;
import com.wwc2.ttxassist.yuv.YUVDispatch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class YuvActivity extends AppCompatActivity implements IYUVDataCallback {

    private String TAG = "YuvActivity";

    private BaseYUVDispatch mBaseYUVDispatch;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv);
    }

    private void stop(){
        if(mBaseYUVDispatch != null){
            mBaseYUVDispatch.doTransactStop();
            mBaseYUVDispatch = null;
        }
    }

    public void onDrawYUVFrontClick(View v){
        stop();
        mBaseYUVDispatch = new FrontYUVDispatch(YuvActivity.this,this);
        mBaseYUVDispatch.start();

    }

    public void onDrawYUVBackClick(View v){
        stop();
        mBaseYUVDispatch = new BackYUVDispatch(YuvActivity.this,this);
        mBaseYUVDispatch.start();

    }


    public void onDrawYUVLeftClick(View v){
        stop();
        mBaseYUVDispatch = new LeftYUVDispatch(YuvActivity.this,this);
        mBaseYUVDispatch.start();

    }

    public void onDrawYUVRightClick(View v){
        stop();
        mBaseYUVDispatch = new RightYUVDispatch(YuvActivity.this,this);
        mBaseYUVDispatch.start();

    }

    public void btn_yuv_extern_one(View v){
        mBaseYUVDispatch = new FrontYUVDispatch(YuvActivity.this,this);
        mBaseYUVDispatch.start();

    }

    public void btn_yuv_extern_two(View v){
        mBaseYUVDispatch = new FrontYUVDispatch(YuvActivity.this,this);
        mBaseYUVDispatch.start();

    }



    @Override
    public void inputYUV(int channel, byte[] nalu, int naluLength) {
        Log.d(TAG,"inputYUV....channel ="  +channel );
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stop();
    }
}
