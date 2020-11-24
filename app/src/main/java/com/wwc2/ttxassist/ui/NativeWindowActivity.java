package com.wwc2.ttxassist.ui;

import android.content.Intent;

import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wwc2.ttxassist.AppConfig;
import com.wwc2.ttxassist.R;
import com.wwc2.ttxassist.fourCamera.Config;
import com.wwc2.ttxassist.fourCamera.FourCameraProxy;
import com.wwc2.ttxassist.h264.BaseDispatch;
import com.wwc2.ttxassist.h264.LocalH264BackDispatch;
import com.wwc2.ttxassist.h264.LocalH264ExternOneDispatch;
import com.wwc2.ttxassist.h264.LocalH264ExternTwoDispatch;
import com.wwc2.ttxassist.h264.LocalH264FrontDispatch;
import com.wwc2.ttxassist.h264.IChannelDataCallback;
import com.wwc2.ttxassist.h264.LocalH264LeftDispatch;
import com.wwc2.ttxassist.h264.LocalH264RightDispatch;
import com.wwc2.ttxassist.utils.H264Utils;
import com.wwc2.ttxassist.ui.h264decode.PlayerActivity;
import com.wwc2.ttxassist.YuvActivity;

public class NativeWindowActivity extends AppCompatActivity implements IChannelDataCallback{
    /**
     * 根容器
     */
    private String TAG = "NativeWindowActivity";

    private ViewGroup mRootLayer;

    private TextView mFileName;


    private SurfaceView mSurfaceView;

    private ArrayMap<Integer, BaseDispatch> mRegister = new ArrayMap<>();

    private int mCurrentShow;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_window);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupView();
    }

    private void setupView() {
        mRootLayer = (ViewGroup) findViewById(R.id.root_layer);

        mFileName = (TextView) findViewById(R.id.fileName);
        mSurfaceView = new SurfaceView(this);
        mRootLayer.addView(mSurfaceView);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    public void onDrawFrontClick(View view) {
        mCurrentShow = AppConfig.FRONT;
        setCameraData(mCurrentShow);
    }

    public void onDrawBackClick(View view) {
        mCurrentShow = AppConfig.BACK;
        setCameraData(mCurrentShow);
    }

    public void onDrawLeftClick(View view) {
        mCurrentShow = AppConfig.LEFT;
        setCameraData(mCurrentShow);
    }

    public void onDrawRightClick(View view) {
        mCurrentShow = AppConfig.RIGHT;
        setCameraData(mCurrentShow);
    }
    public void onDrawExternOneClick(View view) {
        mCurrentShow = AppConfig.EXTERN_ONE;
        setCameraData(mCurrentShow);
    }

    public void onDrawExternTwoClick(View view) {
        mCurrentShow = AppConfig.EXTERN_TWO;
        setCameraData(mCurrentShow);
    }

    public void onPlayClick(View view){
        clearRegister();
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("file", H264Utils.getLogFileName());
        startActivity(intent);
    }

    public void onTakePhone(View view) {
        String mPath = FourCameraProxy.getInstance().TakePhoto(Config.FRONT_CAPTURE);
        TextView mText  = findViewById(R.id.photo_path);
        mText.setText(mPath);
    }


    public void entryYUV(View view) {
        clearRegister();
        Intent intent = new Intent(this, YuvActivity.class);
        startActivity(intent);
    }



    public void setCameraData(int channel){

            BaseDispatch mBaseDispatch;
            switch (channel) {
                case AppConfig.FRONT:
                    if (mRegister.get(channel) == null) {
                        mBaseDispatch = new LocalH264FrontDispatch(this, NativeWindowActivity.this);
                        mBaseDispatch.start();  //data not come form PreViewCallback
                        mRegister.put(channel, mBaseDispatch);
                        H264Utils.setLogToFilName("font.h264");
                    }
                    break;
                case AppConfig.BACK:
                    if (mRegister.get(channel) == null) {
                        mBaseDispatch = new LocalH264BackDispatch(this, NativeWindowActivity.this);
                        mBaseDispatch.start();  //data not come form PreViewCallback
                        mRegister.put(channel, mBaseDispatch);
                        H264Utils.setLogToFilName("back.h264");
                    }
                    break;
                case AppConfig.LEFT:
                    if (mRegister.get(channel) == null) {
                        mBaseDispatch = new LocalH264LeftDispatch(this, NativeWindowActivity.this);
                        mBaseDispatch.start();  //data not come form PreViewCallback
                        mRegister.put(channel, mBaseDispatch);
                        H264Utils.setLogToFilName("left.h264");
                    }
                    break;
                case AppConfig.RIGHT:
                    if (mRegister.get(channel) == null) {
                        mBaseDispatch = new LocalH264RightDispatch(this, NativeWindowActivity.this);
                        mBaseDispatch.start();  //data not come form PreViewCallback
                        mRegister.put(channel, mBaseDispatch);
                        H264Utils.setLogToFilName("right.h264");
                    }
                    break;
                case AppConfig.EXTERN_ONE:
                    if (mRegister.get(channel) == null) {
                        mBaseDispatch = new LocalH264ExternOneDispatch(this, NativeWindowActivity.this);
                        mBaseDispatch.start();  //data not come form PreViewCallback
                        mRegister.put(channel, mBaseDispatch);
                        H264Utils.setLogToFilName("extern1.h264");
                    }
                    break;
                case AppConfig.EXTERN_TWO:
                    if (mRegister.get(channel) == null) {
                        mBaseDispatch = new LocalH264ExternTwoDispatch(this, NativeWindowActivity.this);
                        mBaseDispatch.start();  //data not come form PreViewCallback
                        mRegister.put(channel, mBaseDispatch);
                        H264Utils.setLogToFilName("extern2.h264");
                    }
                    break;

                default:
                    break;
            }
        mFileName.setText(H264Utils.getLogFilePath() + H264Utils.getLogFileName());
    }

    @Override
    public void inputH264Nalu(int channel, byte[] nalu, int naluLength) {
               //String a= byteToHex(nalu,naluLength);
               //Log.d(TAG,"2ns frame . =" + a );
        Log.d(TAG,"mCurrentShow . =" + mCurrentShow );
        Log.d(TAG,"mCurrentShow . =" + mCurrentShow );
             if(channel == mCurrentShow) {
                 H264Utils.log2FileOnlyhex(nalu, nalu.length);
             }
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearRegister();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearRegister();
    }

    public void clearRegister(){
        Log.d(TAG,"clearRegister ...mRegister.size ="  + mRegister.size());

        for(BaseDispatch mDisptach:mRegister.values()) {
            mDisptach.destroy();
        }

        mRegister.clear();
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

}
