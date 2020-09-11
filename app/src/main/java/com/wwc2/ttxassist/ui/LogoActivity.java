package com.wwc2.ttxassist.ui;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wwc2.ttxassist.AppConfig;
import com.wwc2.ttxassist.R;

import androidx.appcompat.app.AppCompatActivity;


public class LogoActivity extends AppCompatActivity {
     private String TAG =  "LogoActivity";
     private String ACTION_ADDRESS_ACCOUNT =  "SYNC_ADDRESS_ACCOUNT";
    private AutoCompleteTextView serviceAdress;
    private AutoCompleteTextView etUserAccount;
    private Button btnLogin;
    private String userName;
    private String userPassword;
    private InputMethodManager imm;
    private Context mContext;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_activty);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mContext= LogoActivity.this;
        init();
    }

    private void init(){
        serviceAdress = (AutoCompleteTextView) findViewById(R.id.et_userName);
        etUserAccount = (AutoCompleteTextView) findViewById(R.id.et_password);
        ImageView unameClear = (ImageView) findViewById(R.id.iv_unameClear);
        ImageView pwdClear = (ImageView) findViewById(R.id.iv_pwdClear);
        String value =AppConfig.getInstance().getValue(this,AppConfig.SERVICE_ADDRESS, null);
        if(value != null){
            serviceAdress.setText(value);
        }

        value =AppConfig.getInstance().getValue(this,AppConfig.USER_ACCOUNT, null);
        if(value != null){
            etUserAccount.setText(value);
        }

        EditTextClearTools.addClearListener(serviceAdress,unameClear);
        EditTextClearTools.addClearListener(etUserAccount,pwdClear);

        serviceAdress.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView editText, int actionId, KeyEvent event) {
                        if (actionId != EditorInfo.IME_ACTION_NONE) {

                            imm.hideSoftInputFromWindow(
                                    editText.getWindowToken(), 0);
                            String text = editText.getText().toString();
                            Log.d(TAG,"serviceAdress =" + text);

                            return true;
                        }
                        return false;
                    }
                });

        etUserAccount.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView editText, int actionId, KeyEvent event) {
                        if (actionId != EditorInfo.IME_ACTION_NONE) {

                            imm.hideSoftInputFromWindow(
                                    editText.getWindowToken(), 0);
                            String text = editText.getText().toString();
                            Log.d(TAG,"etUserAccount =" + TAG);
                            return true;
                        }
                        return false;
                    }
                });


        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });
    }


    public void saveUserInfo(){

        String ip = serviceAdress.getText().toString();
        String account = etUserAccount.getText().toString();
        Log.d(TAG,"service Address ip:" + ip +"account :" + account);
        if(ip != null && account != null ) {
            AppConfig.getInstance().put(this,AppConfig.SERVICE_ADDRESS,ip.trim());
            AppConfig.getInstance().put(this,AppConfig.USER_ACCOUNT,account.trim());
            Toast.makeText(this, "设置成功", Toast.LENGTH_LONG).show();
            startTTxServiceWithSyncAccount();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public  void startTTxServiceWithSyncAccount() {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(LogoActivity.this.getPackageName(),
                LogoActivity.this.getPackageName() + ".TTXService");
        intent.setComponent(componentName);
        intent.setAction(ACTION_ADDRESS_ACCOUNT);
        LogoActivity.this.startService(intent);

    }
}
