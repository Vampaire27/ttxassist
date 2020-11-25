package com.wwc2.ttxassist;

import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.wwc2.ttxassist.R;
import com.wwc2.ttxassist.yuv.BackYUVDispatch;
import com.wwc2.ttxassist.yuv.BaseYUVDispatch;
import com.wwc2.ttxassist.yuv.FrontYUVDispatch;
import com.wwc2.ttxassist.yuv.IYUVDataCallback;
import com.wwc2.ttxassist.yuv.LeftYUVDispatch;
import com.wwc2.ttxassist.yuv.RightYUVDispatch;


import androidx.annotation.Nullable;

import static com.wwc2.ttxassist.glrender.ByteFlowRender.IMAGE_FORMAT_I420;
import static com.wwc2.ttxassist.glrender.ByteFlowRender.IMAGE_FORMAT_NV12;
import static com.wwc2.ttxassist.glrender.ByteFlowRender.IMAGE_FORMAT_NV21;


public class YuvActivity extends BaseRenderActivity implements IYUVDataCallback {

    private String TAG = "YuvActivity";

    private BaseYUVDispatch mBaseYUVDispatch;

    private RelativeLayout mSurfaceViewRoot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv);
        initViews();
    }

    private void initViews() {

        mSurfaceViewRoot = (RelativeLayout) findViewById(R.id.surface_root);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mSurfaceViewRoot.addView(mGLSurfaceView, p);
        mByteFlowRender.init(mGLSurfaceView);
        mByteFlowRender.loadShaderFromAssetsFile(mCurrentShaderIndex, getResources());

        ViewTreeObserver treeObserver = mSurfaceViewRoot.getViewTreeObserver();
        treeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean  onPreDraw() {
                mSurfaceViewRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                mRootViewSize = new Size(mSurfaceViewRoot.getMeasuredWidth(), mSurfaceViewRoot.getMeasuredHeight());
                updateGLSurfaceViewSize(mRootViewSize);
                return true;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateTransformMatrix();
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
        stop();
        mBaseYUVDispatch = new FrontYUVDispatch(YuvActivity.this,this);
        mBaseYUVDispatch.start();

    }

    public void btn_yuv_extern_two(View v){
        stop();
        mBaseYUVDispatch = new FrontYUVDispatch(YuvActivity.this,this);
        mBaseYUVDispatch.start();

    }



    @Override
    public void inputYUV(int channel, byte[] nalu, int naluLength) {
        Log.d(TAG,"inputYUV....channel ="  +channel );
        //IMAGE_FORMAT_NV12 will show green color...
        mByteFlowRender.setRenderFrame(IMAGE_FORMAT_I420, nalu, 1280, 720);
        mByteFlowRender.requestRender();
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
        mByteFlowRender.unInit();

    }
}
