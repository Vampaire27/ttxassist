package com.wwc2.ttxassist;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import net.babelstar.gdispatch.service.TtxNetwork;

import androidx.annotation.Nullable;

public class TTXService extends Service {
    final static String TAG = "ttxService";

    private static final int AIDL_BYTE_PACK_LENGTH = 1024000;
    private TtxNetwork mNetBind;
    private boolean mBindSuc = false;
    private String mServer;
    private String mAccount;

    private Integer mChannel = 0;
    private Integer mNaluIndex = 0;
    private byte[] mTempNalubuf = new byte[AIDL_BYTE_PACK_LENGTH];
    private MesBroadCast mMesReviver;

    private AccStatusObserver mAccStatusObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");

        bindttx();
        if (mMesReviver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MesBroadCast.MESSAGE_RECEIVED_LIVE_ACTION);
            mMesReviver = new MesBroadCast(this);
            registerReceiver(mMesReviver, intentFilter);
        }

        mAccStatusObserver = new AccStatusObserver(
                new Handler(),TTXService.this);

        Uri uri_acc = Uri.parse("content://" + AccStatusObserver.AUTHORITY + "/"
                + AccStatusObserver.ACC_STATUS);
        TTXService.this.getContentResolver().
                registerContentObserver(uri_acc,
                        true, mAccStatusObserver);


    }


    public void bindttx(){
        //看门狗服务，会守护net.babelstar.gdispatch.remoteservice服务，当net.babelstar.gdispatch.remoteservice当机后，会再把net.babelstar.gdispatch.remoteservice重新启动
        Intent intentDeamon = new Intent("net.babelstar.gdispatch.dogservice");
        intentDeamon.setPackage("net.babelstar.gdispatch");
        startService(intentDeamon);
        //开始ttx网络服务，此服务主要与ttx cmsv6 server进行网络通信，进行音视频传输，位置上报等业务
        Intent intent = new Intent("net.babelstar.gdispatch.remoteservice");
        //执法仪版本用的包名
        intent.setPackage("net.babelstar.gdispatch");
        startService(intent);
        //intentDeamon.setPackage("net.babelstar.gdispatch");

        //当net.babelstar.gdispatch.remoteservice没有启动时，bindService调用可能会失败，请先手动启动CMSCruise APP程序，此APP启动时会创建好net.babelstar.gdispatch.remoteservice这个服务
        //注意，第一次绑定成功后，配置参数，会造成 net.babelstar.gdispatch.remoteservice服务挂掉，请重新运行 CMSCruise APP，再启用ttxnetdemo即可恢复正常
        bindService(intent, mServerConnection, BIND_AUTO_CREATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            String action = intent.getAction();
            if ("SYNC_ADDRESS_ACCOUNT".equals(action) &&  mNetBind != null) {
                String address =AppConfig.getInstance().getValue(TTXService.this,AppConfig.SERVICE_ADDRESS,"test.cmsv8.com");
                String count =AppConfig.getInstance().getValue(TTXService.this,AppConfig.USER_ACCOUNT,"1809690");

                try {
                    mNetBind.setServerAndAccount(address, count);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else {
                Log.d(TAG,"mNetBind == null");
            }
        }
        return ret;
    }

    public boolean ismBindSuc() {
        return mBindSuc;
    }

     public void startBindSerice(){
         Intent intent = new Intent("net.babelstar.gdispatch.remoteservice");
         //执法仪版本用的包名
         intent.setPackage("net.babelstar.gdispatch");
         startService(intent);
         bindService(intent, mServerConnection, BIND_AUTO_CREATE);
     }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        unregisterReceiver(mMesReviver);
        unbindService(mServerConnection);

        getContentResolver().unregisterContentObserver(mAccStatusObserver);
    }

    private ServiceConnection mServerConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBindSuc = false;
            mNetBind = null;
            bindDieClear();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder bind) {
            Log.d(TAG,"onServiceConnected TtxNetwork success!");
            mNetBind = TtxNetwork.Stub.asInterface(bind);
            try {
                //注意，第一次绑定成功后，配置参数，会造成 net.babelstar.gdispatch.remoteservice服务挂掉，请重新运行 CMSCruise APP，再启用ttxnetdemo即可恢复正常
                //设置通道数目，1表示1通道
                mNetBind.setChnCount(4);
                //设置app是否要自动操作摄像头
                mNetBind.setUsedCamera(false);
                mNetBind.SetAccStatus(true);
                mNetBind.IsAppPosition(false);
                //是否打开网络定位 默认true打开，false关闭
                mNetBind.IsNetWorkPosition(false);
                //后视镜  设置服务器IP和设备编号
                //120.26.98.110服务器上，测试可使用的设备编号有112233, 67706,67707,67708 请选择一个未在线的设备做测试，另不建议使用  112233这个编号来做测试
                //客户端信息  账号：szhsj 密码：000000 服务器：http://120.26.98.110
                //登录界面上有windows客户端下载，也可以下载windows客户端进行测试
           //
                String address =AppConfig.getInstance().getValue(TTXService.this,AppConfig.SERVICE_ADDRESS,"test.cmsv8.com");
                String count =AppConfig.getInstance().getValue(TTXService.this,AppConfig.USER_ACCOUNT,"1809690");

                mNetBind.setServerAndAccount(address, count);
                //执法仪
                //mNetBind.setServerAndAccount("39.104.57.38", "10005");
                //设置GPS上报间隔
                mNetBind.setGpsInterval(10);
                //设置不进行主码流和子码流录像，默认会进行通道1的码流录像
                mNetBind.setRecord(false, false);
                mNetBind.setVideoEncode(false);
                //设置不进行视频编码，如果输入264数据，则要调用 setVideoEncode(false)，
                //如果输入yuv数据，则要调用setVideoEncode(true)，默认为true
                //mNetBind.setVideoEncode(false);
                //设置预览大小，如果是直接送h264数据，则不需要调用initCameraPreview
                //hzy todo:
                //Camera.Size previewSize = mCamera.getPreviewSize();
				int width = 640;
				int height = 360;
                //配置通道1的预览大小
                mNetBind.initCameraPreview(0, width, height);
//				//如果配置成2个通道，则需要mNetBind.initCameraPreview(1，配置通道2的预览大小
               // mNetBind.initCameraPreview(1, previewSize.width, previewSize.height);
                //设置音频格式和视频类型 264，265
                //mNetBind.setMediaInfoEx(12, 0, 0,0);
				mNetBind.setMediaInfoEx(13, 0, 0, 0);
                //设置分辨率
                //doChangeResolution  最后一个参数，0是CIF，1是D1，2是720P，3是1080P
                //设置通道1子码流分辨率为D1
                //设置通道1主码流分辨率为720P
                mNetBind.doChangeResolution(0, 1, 1);
                mNetBind.doChangeResolution(0, 0, 2);
                mBindSuc = true;
                mServer = mNetBind.getServerIp();
                mAccount = mNetBind.getServerAccount();

                //设置是否使用回音消除
//				mNetBind.SetEchoParam(true, 109);
                // 设置设备录像路径 bIsSetRecPath:true 有效
                //mNetBind.SetRecPathEx(recPath, jpgPath, sdLogPath, bIsSetRecPath);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.d(TAG,"onBindingDied! start new bind");
            mBindSuc = false;
            mNetBind = null;
            bindDieClear();
            bindttx();
        }
    };


    protected  void inputH264Nalu(int channel, int stream, byte[] nalu, int naluLength) {
        //channel为通道号，0表示通道1，1表示通道2,
        //stream为码流类型，0为主码流，1为子码流
        //输入264数据时，要把  SPS, PPS, SEI, IDR 合并成一帧，或者把 SPS, PPS, IDR合并成一帧
        //264数据是以0x00, 0x00, 0x00, 0x01开头的数据
        ++ mNaluIndex;
        if (mBindSuc && mNetBind != null) {
            try {
                int totalLen = naluLength;
                int offset = 0;
                int packMaxLength = AIDL_BYTE_PACK_LENGTH;
                int packIndex = 0;
                int packCount = totalLen / packMaxLength;
                if ( (totalLen % packMaxLength) > 0 ) {
                    packCount += 1;
                }
                if (packCount == 1) {
                    mNetBind.inputH264Data(channel, stream, mNaluIndex, packIndex, packCount, nalu, offset, naluLength, naluLength);
                } else {
                    int packLength = 0;
                    while (offset < totalLen) {
                        if (offset + packMaxLength >= totalLen) {
                            packLength = totalLen - offset;
                        } else {
                            packLength = packMaxLength;
                        }
                        System.arraycopy(nalu, offset, mTempNalubuf, 0, packLength);
                        mNetBind.inputH264Data(channel, stream, mNaluIndex, packIndex, packCount, mTempNalubuf, offset, packLength, naluLength);
                        offset += packLength;
                        ++ packIndex;
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    protected  void sendAudioData(int channel, byte[] aac, int length){
        try {
            if (mBindSuc && mNetBind != null) {
                mNetBind.inputAacData(channel, aac, length);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void bindDieClear(){
        Log.d(TAG,"bindDieClear....");
        if (mMesReviver != null) {
            mMesReviver.clearRegister();
        }
    }

}
