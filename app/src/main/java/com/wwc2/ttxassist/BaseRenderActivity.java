package com.wwc2.ttxassist;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraCharacteristics;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.wwc2.ttxassist.glrender.GLByteFlowRender;
import com.wwc2.ttxassist.utils.CameraUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;



import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static com.wwc2.ttxassist.glrender.ByteFlowRender.IMAGE_FORMAT_RGBA;


public abstract class BaseRenderActivity extends AppCompatActivity {
    private static final String TAG = "BaseRenderActivity";
    protected static final int SHADER_NUM = 27;
    protected static final int LUT_A_SHADER_INDEX = 19;
    protected static final int LUT_B_SHADER_INDEX = 20;
    protected static final int LUT_C_SHADER_INDEX = 21;
    protected static final int LUT_D_SHADER_INDEX = 22;
    protected GLByteFlowRender mByteFlowRender;
    protected GLSurfaceView mGLSurfaceView;

    protected int mCurrentShaderIndex = 0;
    protected Size mRootViewSize, mScreenSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        mByteFlowRender = new GLByteFlowRender();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    protected boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void updateTransformMatrix() {
            mByteFlowRender.setTransformMatrix(0, 0);

    }

    public void updateGLSurfaceViewSize(Size previewSize) {
        Size fitSize = null;
        fitSize = CameraUtil.getFitInScreenSize(previewSize.getWidth(), previewSize.getHeight(), getScreenSize().getWidth(), getScreenSize().getHeight());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mGLSurfaceView
                .getLayoutParams();
        params.width = fitSize.getWidth();
        params.height = fitSize.getHeight();
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.CENTER_HORIZONTAL);

        mGLSurfaceView.setLayoutParams(params);
    }

    public Size getScreenSize() {
        if (mScreenSize == null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            mScreenSize = new Size(displayMetrics.widthPixels, displayMetrics.heightPixels);
        }
        return mScreenSize;
    }

    public void loadRGBAImage(int resId, int index) {
        InputStream is = this.getResources().openRawResource(resId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                int bytes = bitmap.getByteCount();
                ByteBuffer buf = ByteBuffer.allocate(bytes);
                bitmap.copyPixelsToBuffer(buf);
                byte[] byteArray = buf.array();
                mByteFlowRender.loadLutImage(index, IMAGE_FORMAT_RGBA, bitmap.getWidth(), bitmap.getHeight(), byteArray);
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
