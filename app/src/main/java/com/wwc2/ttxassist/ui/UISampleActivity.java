package com.wwc2.ttxassist.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import com.wwc2.ttxassist.R;

public class UISampleActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
    }

    public void onBasisClick(View view) {
        Intent intent = new Intent(this, NativeWindowActivity.class);
        startActivity(intent);
    }

}
