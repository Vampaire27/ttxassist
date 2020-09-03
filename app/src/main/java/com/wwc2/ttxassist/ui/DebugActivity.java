package com.wwc2.ttxassist.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wwc2.ttxassist.R;

import androidx.annotation.Nullable;

public class DebugActivity extends Activity {
    boolean isStop = true;
    Button mBt;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug);
        mBt= findViewById(R.id.mButton);
        mBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStop){
                    startTTxService();
                    mBt.setText(R.string.stop);
                }else{

                    mBt.setText(R.string.start);
                }
                isStop = !isStop;
            }
        });
    }


    public  void startTTxService() {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(DebugActivity.this.getPackageName(),
                DebugActivity.this.getPackageName() + ".TTXService");
        intent.setComponent(componentName);
        DebugActivity.this.startService(intent);

    }


    public  void stopTTxService() {
//        Intent intent = new Intent();
//        ComponentName componentName = new ComponentName(DebugActivity.this.getPackageName(),
//                DebugActivity.this.getPackageName() + "ui.ttxService");
//        intent.setComponent(componentName);
//        DebugActivity.this.startService(intent);

    }

}
